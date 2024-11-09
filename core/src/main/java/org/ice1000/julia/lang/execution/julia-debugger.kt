package org.ice1000.julia.lang.execution

import com.google.gson.Gson
import com.intellij.execution.ExecutionResult
import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.ui.ExecutionConsole
import com.intellij.javascript.debugger.execution.DebuggableProgramRunner
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.terminal.TerminalExecutionConsole
import com.intellij.util.io.createFile
import com.intellij.util.io.exists
import com.intellij.xdebugger.*
import com.intellij.xdebugger.breakpoints.XLineBreakpointTypeBase
import com.intellij.xdebugger.evaluation.EvaluationMode
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider
import com.intellij.xdebugger.frame.XExecutionStack
import com.intellij.xdebugger.frame.XStackFrame
import com.intellij.xdebugger.frame.XSuspendContext
import com.intellij.xdebugger.impl.frame.XStackFrameContainerEx
import org.ice1000.julia.lang.JULIA_DEBUG_FILE_KEY
import org.ice1000.julia.lang.JULIA_DEBUG_PROCESS_HANDLER_KEY
import org.ice1000.julia.lang.JuliaFileType
import org.ice1000.julia.lang.module.JuliaDebugValue
import org.ice1000.julia.lang.module.JuliaVariableStackFrame
import org.ice1000.julia.lang.module.juliaSettings
import org.jetbrains.debugger.DebuggableRunConfiguration
import org.jetbrains.debugger.SourceInfo
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.nio.file.Paths

/**
 * this feature is not a Joke!
 * @author zxj5470
 * @date 2018/09/22
 */
val Project.breakpoints
	get() = XDebuggerManager.getInstance(this)
		.breakpointManager
		.getBreakpoints(JuliaLineBreakpointType::class.java)

class JuliaDebugProcess(socketAddress: InetSocketAddress,
												session: XDebugSession,
												val executionResult: ExecutionResult?,
												val env: ExecutionEnvironment) : XDebugProcess(session) {
	companion object LoggerHolder {
		val LOG = Logger.getInstance(JuliaDebugProcess::class.java)
	}

	override fun getEditorsProvider(): XDebuggerEditorsProvider = JuliaEditorsProvider()
	lateinit var socket: ServerSocket
	val debugStack = JuliaDebugExecutionStack(emptyList())

	override fun sessionInitialized() {
		super.sessionInitialized()
		val filePath = env.getUserData(JULIA_DEBUG_FILE_KEY) ?: return

		ApplicationManager.getApplication().executeOnPooledThread {
			socket = ServerSocket(0)
			val port = socket.localPort
			WriteCommandAction.runWriteCommandAction(env.project) {
				val debuggerFile = Paths.get(PathManager.getPluginsPath(), "julia-intellij", "IntelliJDebugger.jl").apply {
					if (!exists()) createFile()
				}.toFile()
				debuggerFile.writeBytes(javaClass.getResource("IntelliJDebugger.jl").readBytes())
				debuggerFile.appendText("\n_intellij_debug_port=$port\n")
				processHandler.includeFileToProcess(debuggerFile.absolutePath)
				processHandler.includeFileToProcess(filePath)
			}

			while (true) {
				try {
					val connectionSocket = socket.accept()
					val inFromClient = BufferedReader(InputStreamReader(connectionSocket.getInputStream()))
					val text = inFromClient.readLine()
					val debugData = try {
						Gson().fromJson(text, JuliaDebugFrameValue::class.java)
					} catch (e: Exception) {
						e.printStackTrace()
						null
					} ?: continue

					val sdkHomeCache = env.project.juliaSettings.settings.basePath
					val projectDir = env.project.guessProjectDir() ?: break
					val fileSystem = projectDir.fileSystem

					val topFrame = debugData.next
					val currentFile = fileSystem.findFileByPath(topFrame.file)
						?: fileSystem.findFileByPath(Paths.get(sdkHomeCache).resolve(topFrame.file).toString())
					val currentPosition = currentFile?.let { SourceInfo(it, topFrame.line - 1) }

					val list = debugData.frames.map { stack ->
						val virtualFile = fileSystem.findFileByPath(stack.stack.file)
							?: fileSystem.findFileByPath(Paths.get(sdkHomeCache).resolve(stack.stack.file).toString())
						val source = virtualFile?.let { SourceInfo(virtualFile, line = stack.stack.line - 1, functionName = stack.stack.function) }
						stack.vars.map {
							JuliaDebugValue(it.name, it.type, it.value, sourceInfo = source)
						}
					}
					if (list.isEmpty() || list.first().isEmpty()) continue
					list.first().first().sourceInfo = currentPosition
					val top = JuliaVariableStackFrame(env.project, list.first())
					val stackFrame = JuliaDebugExecutionStack(list.map { JuliaVariableStackFrame(env.project, it) })
					session.setCurrentStackFrame(stackFrame, top)
					session.updateExecutionPosition()
				} catch (e: Throwable) {
					LOG.error(e)
				}
			}
		}
		pause()
	}

	private fun pause() {
		val project = env.project
		val breakpoints = project.breakpoints
		ApplicationManager.getApplication().invokeLater {
			session.breakpointReached(breakpoints.first(), null, JuliaDebugSuspendContext(debugStack))
		}
	}

	override fun startPausing() {
		pause()
	}

	// outputs
	override fun createConsole(): ExecutionConsole = executionResult?.executionConsole ?: super.createConsole()

	// listener to runner exit
	override fun doGetProcessHandler(): ProcessHandler? {
		return env.project.getUserData(JULIA_DEBUG_PROCESS_HANDLER_KEY)
	}

	override fun startForceStepInto(context: XSuspendContext?) {
		processHandler.sendCommandToProcess("s")
		pause()
	}

	override fun startStepOver(context: XSuspendContext?) {
		processHandler.sendCommandToProcess("nc")
		context ?: return
		session.positionReached(context)
	}

	override fun startStepInto(context: XSuspendContext?) {
		processHandler.sendCommandToProcess("sg")
		pause()
	}

	override fun resume(context: XSuspendContext?) {
		val filePath = env.getUserData(JULIA_DEBUG_FILE_KEY) ?: return
		processHandler.includeFileToProcess(filePath)
		pause()
	}

	override fun startStepOut(context: XSuspendContext?) {
		processHandler.sendCommandToProcess("finish")
		pause()
	}

	override fun stop() {
		processHandler.sendCommandToProcess("exit()")
//		session.stop()
//		processHandler.destroyProcess()
//		if (::socket.isInitialized)
//			socket.close()
	}
}

class JuliaDebugSuspendContext(private val active: JuliaDebugExecutionStack) : XSuspendContext() {
	override fun getActiveExecutionStack() = active
}

class JuliaDebugRunner : DebuggableProgramRunner() {
	override fun canRun(executorId: String, profile: RunProfile): Boolean {
		return DefaultDebugExecutor.EXECUTOR_ID == executorId && profile is DebuggableRunConfiguration && profile.canRun(executorId, profile)
	}

	override fun getRunnerId(): String = "JuliaDebugRunner"
}

class JuliaEditorsProvider : XDebuggerEditorsProvider() {
	override fun createDocument(project: Project, expression: XExpression, sourcePosition: XSourcePosition?, mode: EvaluationMode): Document {
		return EditorFactory.getInstance().createDocument(expression.expression)
	}

	override fun getFileType(): FileType = JuliaFileType
}

/**
 * Julia Debug LineBreakpoint
 */
class JuliaLineBreakpointType : XLineBreakpointTypeBase(ID, NAME, JuliaEditorsProvider()) {
	override fun canPutAt(file: VirtualFile, line: Int, project: Project): Boolean = file.fileType === JuliaFileType

	companion object {
		private const val ID = "julia-line"
		private const val NAME = "julia-line-breakpoint"
	}
}

fun ProcessHandler.includeFileToProcess(filePath: String) =
	sendCommandToProcess("""include("${filePath.toUnixPath()}")""")

fun String.toUnixPath() = if (SystemInfo.isWindows) this.replace('\\', '/') else this

fun ProcessHandler.sendCommandToProcess(command: String) {
	val processInputOS = processInput ?: return
	val bytes = ("$command\n").toByteArray()
	processInputOS.write(bytes)
	processInputOS.flush()
}

class JuliaDebugTerminalExecutionConsole(env: ExecutionEnvironment, handler: ProcessHandler) : TerminalExecutionConsole(env.project, handler)

class JuliaDebugExecutionStack(private val stackFrameList: List<XStackFrame>) : XExecutionStack("JuliaStack") {
	private var topFrame: XStackFrame? = null

	init {
		if (stackFrameList.isNotEmpty())
			topFrame = stackFrameList[0]
	}

	override fun getTopFrame() = topFrame

	override fun computeStackFrames(i: Int, xStackFrameContainer: XExecutionStack.XStackFrameContainer) {
		val stackFrameContainerEx = xStackFrameContainer as XStackFrameContainerEx
		stackFrameContainerEx.addStackFrames(stackFrameList, topFrame, true)
	}
}

data class JuliaDebugFrameValue(var next: Next,
																var frames: List<Frames>) {
	data class Next(var line: Int,
									var file: String,
									var expr: String)

	data class Frames(var stack: Stack,
										var vars: List<Vars>) {
		data class Stack(var line: Int,
										 var function: String,
										 var file: String)

		data class Vars(var name: String,
										var value: String,
										var type: String)
	}
}
