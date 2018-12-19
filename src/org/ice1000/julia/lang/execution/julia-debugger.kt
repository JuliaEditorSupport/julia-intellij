package org.ice1000.julia.lang.execution

import com.intellij.execution.ExecutionResult
import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.ui.*
import com.intellij.icons.AllIcons
import com.intellij.javascript.debugger.execution.DebuggableProgramRunner
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.terminal.TerminalExecutionConsole
import com.intellij.ui.ColoredTextContainer
import com.intellij.ui.SimpleTextAttributes
import com.intellij.util.Processor
import com.intellij.util.containers.ContainerUtil
import com.intellij.xdebugger.*
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import com.intellij.xdebugger.breakpoints.XLineBreakpointTypeBase
import com.intellij.xdebugger.evaluation.*
import com.intellij.xdebugger.frame.*
import com.intellij.xdebugger.impl.frame.XStackFrameContainerEx
import org.ice1000.julia.lang.*
import org.ice1000.julia.lang.action.errorNotification
import org.jetbrains.debugger.DebuggableRunConfiguration
import java.net.InetSocketAddress
import java.nio.file.Paths

/**
 * this feature is a Joke!
 * @author zxj5470
 * @date 2018/09/22
 */
class JuliaDebugProcess(socketAddress: InetSocketAddress,
												session: XDebugSession,
												val executionResult: ExecutionResult?,
												val env: ExecutionEnvironment) : XDebugProcess(session) {
	override fun getEditorsProvider(): XDebuggerEditorsProvider = JuliaEditorsProvider()

	val debugStack = JuliaDebugExecutionStack(emptyList())
	val breakpoints
		get() = XDebuggerManager.getInstance(session.project)
			.breakpointManager
			.getBreakpoints(JuliaLineBreakpointType::class.java)

	override fun sessionInitialized() {
		super.sessionInitialized()
		val filePath = env.getUserData(JULIA_DEBUG_FILE_KEY).let { Paths.get(it) } ?: return
		processHandler.sendCommandToProcess("""include("$filePath")""")
		pause()
	}

	private fun pause() {
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
		env.project.getUserData(JULIA_DEBUG_PROCESS_HANDLER_KEY)?.sendCommandToProcess("s")
		pause()
	}

	override fun startStepOver(context: XSuspendContext?) {
		env.project.getUserData(JULIA_DEBUG_PROCESS_HANDLER_KEY)?.sendCommandToProcess("nc")
		pause()
	}

	override fun startStepInto(context: XSuspendContext?) {
		env.project.getUserData(JULIA_DEBUG_PROCESS_HANDLER_KEY)?.sendCommandToProcess("sg")
		pause()
	}

	override fun resume(context: XSuspendContext?) {
		env.project.getUserData(JULIA_DEBUG_PROCESS_HANDLER_KEY)?.sendCommandToProcess("finish")
	}

	override fun startStepOut(context: XSuspendContext?) {
		env.project.getUserData(JULIA_DEBUG_PROCESS_HANDLER_KEY)?.sendCommandToProcess("finish")
	}

	override fun stop() {
		session.stop()
		processHandler.destroyProcess()
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

fun ProcessHandler.sendCommandToProcess(command: String) {
	val processInputOS = processInput ?: return
	val bytes = ("$command\n").toByteArray()
	processInputOS.write(bytes)
	processInputOS.flush()
}

class JuliaDebugTerminalExecutionConsole(env: ExecutionEnvironment, handler: ProcessHandler) : TerminalExecutionConsole(env.project, handler)

class JuliaDebugExecutionStack(private val stackFrameList: List<XStackFrame>) : XExecutionStack("JuliaStack") {
	private var _topFrame: XStackFrame? = null

	val stackFrames: Array<XStackFrame>
		get() = stackFrameList.toTypedArray()

	init {
		if (stackFrameList.isNotEmpty())
			_topFrame = stackFrameList[0]
	}

	override fun getTopFrame() = _topFrame

	fun setTopFrame(frame: XStackFrame) {
		_topFrame = frame
	}

	override fun computeStackFrames(i: Int, xStackFrameContainer: XExecutionStack.XStackFrameContainer) {
		val stackFrameContainerEx = xStackFrameContainer as XStackFrameContainerEx
		stackFrameContainerEx.addStackFrames(stackFrameList, topFrame, true)
	}
}