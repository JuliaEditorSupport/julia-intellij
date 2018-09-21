package org.ice1000.julia.lang.execution

import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.ExecutionBundle
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.configurations.SearchScopeProvider
import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.process.*
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.ui.ConsoleView
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ToggleAction
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.DumbAware
import org.ice1000.julia.lang.forceRun
import org.ice1000.julia.lang.module.compareVersion
import org.ice1000.julia.lang.module.juliaSettings
import org.ice1000.julia.lang.toYesNo

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
			val settings = project.juliaSettings.settings
			params += juliaExecutable
			params += "--check-bounds=${checkBoundsOption.toYesNo()}"
			params += "--history-file=${historyOption.toYesNo()}"
			params += "--inline=${inlineOption.toYesNo()}"
			params += "--color=${colorOption.toYesNo()}"
			params += "--math-mode=${if (unsafeFloatOption) "fast" else "ieee"}"
			params += "--handle-signals=${handleSignalOption.toYesNo()}"
			params += "--startup-file=${startupFileOption.toYesNo()}"
			// julia#9384, a bug of 0.4.x
			forceRun {
				if (compareVersion(settings.version, "0.5.0") >= 0)
					params += "--optimize=$optimizationLevel"
			}
			params += "--compile=$jitCompiler"
			params += "--depwarn=$deprecationWarning"
			params += "--code-coverage=$codeCoverage"
			params += "--track-allocation=$trackAllocation"
			if (launchReplOption) params += "-i"
			if (systemImageOption) {
				params += "--sysimage"
				params += systemImage
			}
			params += additionalOptions.split(' ', '\n').filter(String::isNotBlank)
			params += targetFile
			params += programArgs.split(' ', '\n').filter(String::isNotBlank)
		}

		val chooseColorful: (GeneralCommandLine) -> OSProcessHandler =
			if (configuration.colorOption) ::ColoredProcessHandler
			else ::OSProcessHandler
		val handler = GeneralCommandLine(params)
			// Thanks to intellij-rust plugin again!
			// Explicitly use UTF-8.
			// Even though default system encoding is usually not UTF-8 on windows,
			// most Julia programs are UTF-8 only.
			.withCharset(Charsets.UTF_8)
			.withWorkDirectory(configuration.workingDir)
			.let(chooseColorful)
		ProcessTerminatedListener.attach(handler)
		val console = consoleBuilder.console
		console.attachToProcess(handler)
		handler.startNotify()
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
				!console.canPause() || !console.hasDeferredOutput() -> event.presentation.isEnabled = false
				else -> {
					event.presentation.isEnabled = true
					console.performWhenNoDeferredOutput { update(event) }
				}
			}
		}
	}
}
