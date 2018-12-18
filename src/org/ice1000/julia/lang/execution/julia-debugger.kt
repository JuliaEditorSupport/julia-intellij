package org.ice1000.julia.lang.execution

import com.intellij.execution.ExecutionResult
import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.ui.ExecutionConsole
import com.intellij.javascript.debugger.execution.DebuggableProgramRunner
import com.intellij.notification.*
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.xdebugger.*
import com.intellij.xdebugger.breakpoints.XLineBreakpointTypeBase
import com.intellij.xdebugger.evaluation.EvaluationMode
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider
import org.ice1000.julia.lang.*
import org.jetbrains.debugger.DebuggableRunConfiguration
import java.net.InetSocketAddress

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

	override fun startPausing() {
		Notifications.Bus.notify(
			Notification("org.ice1000.julia.lang.execution.debug.invalid.notification",
				JuliaBundle.message("julia.debug.title"),
				"Pausing unsupported!",
				NotificationType.ERROR))
		super.startPausing()
	}

	// outputs
	override fun createConsole(): ExecutionConsole = executionResult?.executionConsole ?: super.createConsole()

	// listener to runner exit
	override fun doGetProcessHandler(): ProcessHandler? {
		return env.project.getUserData(JULIA_DEBUG_PROCESS_HANDLER_KEY)
	}
	override fun stop() {
		session.stop()
		processHandler.destroyProcess()
	}
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