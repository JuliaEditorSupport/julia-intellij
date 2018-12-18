package org.ice1000.julia.lang.execution

import com.intellij.execution.ExecutionResult
import com.intellij.execution.TaskExecutor
import com.intellij.execution.configurations.*
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.execution.process.*
import com.intellij.execution.runners.*
import com.intellij.execution.ui.ExecutionConsole
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.javascript.debugger.execution.DebuggableProgramRunner
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.Processor
import com.intellij.util.concurrency.AppExecutorUtil
import com.intellij.util.containers.ContainerUtil
import com.intellij.xdebugger.*
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import com.intellij.xdebugger.breakpoints.XLineBreakpointTypeBase
import com.intellij.xdebugger.evaluation.EvaluationMode
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider
import com.intellij.xdebugger.frame.*
import com.intellij.xdebugger.impl.XDebugSessionImpl
import com.intellij.xdebugger.impl.frame.XStackFrameContainerEx
import com.pty4j.PtyProcess
import org.ice1000.julia.lang.JuliaFileType
import org.jetbrains.concurrency.Promise
import org.jetbrains.concurrency.resolvedPromise
import org.jetbrains.debugger.DebuggableRunConfiguration
import java.io.OutputStream
import java.net.InetSocketAddress
import java.util.concurrent.Future

/**
 * this feature is a Joke!
 * @author zxj5470
 * @date 2018/09/22
 */
class JuliaDebugProcess(socketAddress: InetSocketAddress,
												session: XDebugSession,
												val executionResult: ExecutionResult?,
												environment: ExecutionEnvironment) : XDebugProcess(session) {
	override fun getEditorsProvider(): XDebuggerEditorsProvider = JuliaEditorsProvider()
	override fun stop() {
		session.stop()
		processHandler.destroyProcess()
	}

	override fun createConsole(): ExecutionConsole = executionResult?.executionConsole ?: super.createConsole()

	fun processBreakpoint(processor: Processor<XLineBreakpoint<*>>) {
		ApplicationManager.getApplication().runReadAction {
			val breakpoints = XDebuggerManager.getInstance(session.project)
				.breakpointManager
				.getBreakpoints(JuliaLineBreakpointType::class.java)
			ContainerUtil.process(breakpoints, processor)
		}
	}

	private fun getBreakpoint(file: VirtualFile, line: Int): XLineBreakpoint<*>? {
		var bp: XLineBreakpoint<*>? = null
		processBreakpoint(Processor {
			val pos = it.sourcePosition
			if (file == pos?.file && line == pos.line) {
				bp = it
			}
			true
		})
		return bp
	}

}

class JuliaDebugRunner : AsyncProgramRunner<RunnerSettings>() {
	override fun canRun(executorId: String, profile: RunProfile): Boolean {
		return DefaultDebugExecutor.EXECUTOR_ID == executorId && profile is DebuggableRunConfiguration && profile.canRun(executorId, profile)
	}

	override fun execute(environment: ExecutionEnvironment, state: RunProfileState): Promise<RunContentDescriptor?> {
		FileDocumentManager.getInstance().saveAllDocuments()
		if (state is DebuggableRunProfileState) {
			return state.execute(-1)
				.then {
					it?.let {
						RunContentBuilder(it, environment).showRunContent(environment.contentToReuse)
					}
				}
		} else {
			return resolvedPromise(showRunContent(state.execute(environment.executor, this), environment))
		}
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