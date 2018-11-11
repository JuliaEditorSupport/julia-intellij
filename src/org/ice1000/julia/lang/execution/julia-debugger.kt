package org.ice1000.julia.lang.execution

import com.intellij.execution.configurations.*
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.execution.runners.AsyncProgramRunner
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.ui.RunContentDescriptor
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
import org.ice1000.julia.lang.JuliaBundle
import org.ice1000.julia.lang.JuliaFileType
import org.jetbrains.concurrency.AsyncPromise
import org.jetbrains.concurrency.Promise

/**
 * this feature is a Joke!
 * @author zxj5470
 * @date 2018/09/22
 */
class JuliaDebugProcess(session: XDebugSession) : XDebugProcess(session) {
	override fun getEditorsProvider(): XDebuggerEditorsProvider {
		return JuliaEditorsProvider()
	}
}

class JuliaDebugRunner : AsyncProgramRunner<RunnerSettings>() {
	override fun canRun(executorId: String, profile: RunProfile): Boolean {
		if (executorId != DefaultDebugExecutor.EXECUTOR_ID || profile !is JuliaRunConfiguration) return false
		return true
	}

	override fun execute(environment: ExecutionEnvironment, state: RunProfileState): Promise<RunContentDescriptor?> {
		val promise = AsyncPromise<RunContentDescriptor?>()
		XDebuggerManager.getInstance(environment.project)
			.startSession(environment, object : XDebugProcessStarter() {
				override fun start(session: XDebugSession): XDebugProcess =
					JuliaDebugProcess(session).apply {
						// FIXME: never fix it. How delicious!
						Notifications.Bus.notify(
							Notification("org.ice1000.julia.lang.execution.debug.invalid.notification",
								JuliaBundle.message("julia.debug.title"),
								JuliaBundle.message("julia.debug.title.invalid.content"),
								NotificationType.ERROR))
						stop()
					}
			})
		return promise
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
	override fun canPutAt(file: VirtualFile, line: Int, project: Project): Boolean {
		return file.fileType === JuliaFileType
	}

	companion object {
		private const val ID = "julia-line"
		private const val NAME = "julia-line-breakpoint"
	}
}