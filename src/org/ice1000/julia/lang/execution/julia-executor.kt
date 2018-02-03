package org.ice1000.julia.lang.execution

import com.intellij.execution.*
import com.intellij.execution.configurations.*
import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.process.*
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ToggleAction
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.DumbAware
import org.ice1000.julia.lang.toYesNo
import java.nio.charset.Charset

class JuliaCommandLineState(
	private val configuration: JuliaRunConfiguration,
	env: ExecutionEnvironment) : RunProfileState {
	private val consoleBuilder = TextConsoleBuilderFactory
		.getInstance()
		.createBuilder(env.project,
			SearchScopeProvider.createSearchScope(env.project, env.runProfile))

	override fun execute(executor: Executor, runner: ProgramRunner<*>): ExecutionResult {
		val params = mutableListOf<String>()
		with(configuration) {
			params += juliaExecutable
			params += "--check-bounds=${checkBoundsOption.toYesNo()}"
			params += "--history-file=${historyOption.toYesNo()}"
			params += "--inline=${inlineOption.toYesNo()}"
			params += "--color=${colorOption.toYesNo()}"
			params += "--math-mode=${if (unsafeFloatOption) "fast" else "ieee"}"
			params += "--handle-signals=${handleSignalOption.toYesNo()}"
			params += "--startup-file=${startupFileOption.toYesNo()}"
			params += "--optimize=$optimizationLevel"
			params += "--compile=$jitCompiler"
			params += "--depwarn=$deprecationWarning"
			params += "--code-coverage=$codeCoverage"
			params += "--track-allocation=$trackAllocation"
			params += configuration.additionalOptions.split(' ').filter(String::isNotBlank)
			params += configuration.targetFile
			params += configuration.programArgs.split(' ').filter(String::isNotBlank)
		}
		val handler = OSProcessHandler(GeneralCommandLine(params).also {
			it.withCharset(Charset.forName("UTF-8"))
			it.withWorkDirectory(configuration.workingDir)
		})
		ProcessTerminatedListener.attach(handler)
		handler.startNotify()
		val console = consoleBuilder.console
		console.print("${handler.commandLine}\n", ConsoleViewContentType.NORMAL_OUTPUT)
		console.attachToProcess(handler)
		return DefaultExecutionResult(console, handler, PauseOutputAction(console, handler))
	}

	private class PauseOutputAction(private val console: ConsoleView, private val handler: ProcessHandler) :
		ToggleAction(
			ExecutionBundle.message("run.configuration.pause.output.action.name"),
			null, AllIcons.Actions.Pause), DumbAware {
		override fun isSelected(event: AnActionEvent) = console.isOutputPaused
		override fun setSelected(event: AnActionEvent, flag: Boolean) {
			console.isOutputPaused = flag
			ApplicationManager.getApplication().invokeLater { update(event) }
		}

		override fun update(event: AnActionEvent) {
			super.update(event)
			when {
				!handler.isProcessTerminated -> event.presentation.isEnabled = true
				!console.canPause() or !console.hasDeferredOutput() -> event.presentation.isEnabled = false
				else -> {
					event.presentation.isEnabled = true
					console.performWhenNoDeferredOutput { update(event) }
				}
			}
		}
	}
}
