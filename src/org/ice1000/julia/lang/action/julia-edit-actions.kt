package org.ice1000.julia.lang.action

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.CharFilter
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.popup.*
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.textCompletion.TextCompletionProvider
import com.intellij.util.textCompletion.TextFieldWithCompletion
import icons.JuliaIcons
import org.ice1000.julia.lang.*
import org.ice1000.julia.lang.module.JuliaSettings
import org.ice1000.julia.lang.module.juliaSettings
import java.awt.event.KeyEvent

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
	private companion object CompletionHolder {
		private const val unicodeFile = "org/ice1000/julia/lang/unicode-list.txt"
		private val unicodeList by lazy {
			JuliaUnicodeInputAction::class.java.classLoader.getResource(unicodeFile)
				.readText()
				.split('\n')
				.map { str ->
					val (a, b) = str.split(' ')
					LookupElementBuilder.create(b)
						.withLookupString(a)
						.withPresentableText(a)
						.withIcon(JuliaIcons.JULIA_BIG_ICON)
				}
		}

		private object UnicodeCompletionProvider : TextCompletionProvider {
			override fun getAdvertisement() = "LaTeX unicode"
			override fun getPrefix(text: String, offset: Int) = text.take(offset)
			override fun acceptChar(c: Char) = CharFilter.Result.ADD_TO_PREFIX
			override fun applyPrefixMatcher(result: CompletionResultSet, prefix: String) =
				result.withPrefixMatcher(PlainPrefixMatcher(prefix))

			override fun fillCompletionVariants(
				parameters: CompletionParameters,
				prefix: String,
				result: CompletionResultSet) {
				unicodeList.forEach(result::addElement)
				result.stopHere()
			}
		}
	}

	override fun actionPerformed(e: AnActionEvent) {
		val editor = e.getData(CommonDataKeys.EDITOR) ?: return
		val project = e.project ?: return
		val field = TextFieldWithCompletion(project, UnicodeCompletionProvider, "", true, true, true)
		var popup: JBPopup? = null
		popup = JBPopupFactory.getInstance()
			.createComponentPopupBuilder(field, null)
			.setMovable(true)
			.setAlpha(0.2F)
			.setKeyEventHandler {
				if (it.keyCode == KeyEvent.VK_ENTER) popup?.cancel()
				false
			}
			.setAdText("LaTeX style unicode input") // TODO l18n
			.createPopup()
		popup.addListener(object : JBPopupListener.Adapter() {
			override fun onClosed(event: LightweightWindowEvent?) {
				CommandProcessor.getInstance().executeCommand(project, {
					ApplicationManager.getApplication().runWriteAction {
						editor.document.insertString(editor.caretModel.offset, field.text)
						editor.caretModel.moveCaretRelatively(1, 0, false, false, true)
					}
				}, null, null)
			}
		})
		popup.show(JBPopupFactory.getInstance().guessBestPopupLocation(editor))
		field.requestFocus()
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
