package org.ice1000.julia.lang.action

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorTextField
import com.intellij.util.ui.JBUI
import icons.JuliaIcons
import org.ice1000.julia.lang.*
import org.ice1000.julia.lang.editing.unicode.JuliaUnicodeFileType
import org.ice1000.julia.lang.module.JuliaSettings
import org.ice1000.julia.lang.module.juliaSettings

class JuliaTryEvaluateAction : JuliaAction(
	JuliaBundle.message("julia.actions.try-eval.name"),
	JuliaBundle.message("julia.actions.try-eval.description")), DumbAware {
	private val core = TryEvaluate()
	override fun actionPerformed(e: AnActionEvent) {
		val editor = e.getData(CommonDataKeys.EDITOR) ?: return
		core.tryEval(editor, editor.selectionModel.selectedText ?: return, e.getData(CommonDataKeys.PROJECT))
	}
}

class JuliaUnicodeInputAction : JuliaAction("TODO", "TODO") { // TODO
	private companion object {
		private val unicodeList = listOf(
			"alpha" to "α", "beta" to "β", "gamma" to "γ", "delta" to "δ", "epsilon" to "ϵ"
		)
	}

	override fun actionPerformed(e: AnActionEvent) {
		val editor = e.getData(CommonDataKeys.EDITOR) ?: return
		JBPopupFactory.getInstance()
			.createComponentPopupBuilder(JBUI.Panels.simplePanel()
				.addToCenter(EditorTextField("", editor.project, JuliaUnicodeFileType).apply {
					setOneLineMode(true)
				}), null)
			.setMovable(true)
			.setAlpha(0.8F)
			.setAdText("LaTeX style unicode input") // TODO l18n
			.setRequestFocus(true)
			.createPopup()
			.show(JBPopupFactory.getInstance().guessBestPopupLocation(editor))
	}
}

class JuliaDocumentFormatAction : JuliaAction(
	JuliaBundle.message("julia.actions.doc-format.name"),
	JuliaBundle.message("julia.actions.doc-format.description")), DumbAware {
	override fun actionPerformed(e: AnActionEvent) {
		val project = e.project ?: return
		val settings = project.juliaSettings.settings
		val file = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
		ProgressManager.getInstance().runProcessWithProgressSynchronously({
			ApplicationManager.getApplication().run { invokeAndWait { runReadAction { read(file, settings, project) } } }
		}, JuliaBundle.message("julia.messages.doc-format.running"), false, project)
	}

	private fun read(file: VirtualFile, settings: JuliaSettings, project: Project) {
		val content = file.inputStream.reader().readText().replace(Regex.fromLiteral("\"\\")) {
			when (it.value) {
				"\"" -> "\\\""
				"\\" -> "\\\\"
				else -> it.value
			}
		}
		//language=Julia
		val (stdout, stderr) = executeJulia("${settings.exePath} -q",
			"""using DocumentFormat: format
println(format($JULIA_DOC_SURROUNDING$content$JULIA_DOC_SURROUNDING))
exit()
""",
			50000L)
		ApplicationManager.getApplication().let {
			it.invokeAndWait {
				it.runWriteAction { write(stderr, project, file, stdout) }
				LocalFileSystem.getInstance().refreshFiles(listOf(file))
			}
		}
	}

	private fun write(stderr: List<String>, project: Project, file: VirtualFile, stdout: List<String>) {
		if (stderr.isNotEmpty()) Messages.showDialog(
			project,
			stderr.joinToString("\n"),
			JuliaBundle.message("julia.messages.doc-format.error"),
			arrayOf(JuliaBundle.message("julia.yes")),
			0, JuliaIcons.JOJO_ICON)
		else file.getOutputStream(this).bufferedWriter().use {
			it.append(stdout.joinToString("\n"))
			it.flush()
		}
	}
}
