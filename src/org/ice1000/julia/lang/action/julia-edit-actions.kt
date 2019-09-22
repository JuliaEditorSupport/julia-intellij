package org.ice1000.julia.lang.action

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.PlainPrefixMatcher
import com.intellij.codeInsight.lookup.CharFilter
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.codeInsight.lookup.LookupManager
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.ui.popup.JBPopupListener
import com.intellij.openapi.ui.popup.LightweightWindowEvent
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.textCompletion.TextCompletionProvider
import com.intellij.util.textCompletion.TextFieldWithCompletion
import com.intellij.util.ui.JBUI
import icons.JuliaIcons
import org.apache.commons.lang.StringEscapeUtils
import org.ice1000.julia.lang.JuliaBundle
import org.ice1000.julia.lang.execution.toUnixPath
import org.ice1000.julia.lang.module.juliaGlobalSettings
import org.ice1000.julia.lang.module.juliaSettings
import org.ice1000.julia.lang.module.languageServer
import org.ice1000.julia.lang.orFalse
import java.awt.event.KeyEvent
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.swing.JLabel

class JuliaTryEvaluateAction : JuliaAction(
	JuliaBundle.message("julia.actions.try-eval.name"),
	JuliaBundle.message("julia.actions.try-eval.description")), DumbAware {
	private val core = TryEvaluate()
	override fun actionPerformed(e: AnActionEvent) {
		val editor = e.getData(CommonDataKeys.EDITOR) ?: return
		core.tryEval(editor, editor.selectionModel.selectedText ?: return, e.getData(CommonDataKeys.PROJECT))
	}
}

/**
 * see wiki
 * [Unicode-input](https://github.com/ice1000/julia-intellij/wiki/Unicode-input)
 */
class JuliaUnicodeInputAction : JuliaAction(
	JuliaBundle.message("julia.actions.unicode-input.text"),
	JuliaBundle.message("julia.actions.unicode-input.description")), DumbAware {
	companion object CompletionHolder {
		private const val unicodeFile = "org/ice1000/julia/lang/unicode-list.txt"
		private val unicodeList: List<LookupElementBuilder> by lazy {
			JuliaUnicodeInputAction::class.java.classLoader.getResource(unicodeFile)
				.readText()
				.lines()
				.mapNotNull { str ->
					if (str.isBlank()) return@mapNotNull null
					val (a, b) = str.split(' ')
					lookupElementBuilder(a, b)
						.withCaseSensitivity(false)
						.withTailText(" $b", true)
				} + listOf("nolinebreak" to '\u2060')
				.map { (a, b) -> lookupElementBuilder(a, b) }
		}

		private fun lookupElementBuilder(a: String, b: Any) = LookupElementBuilder.create(b)
			.withLookupString(a)
			.withPresentableText(a)
			.withIcon(JuliaIcons.JULIA_BIG_ICON)

		private object UnicodeCompletionProvider : TextCompletionProvider, DumbAware {
			override fun getAdvertisement() = JuliaBundle.message("julia.actions.unicode-input.provider.ad")
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

		fun actionInvoke(editor: Editor?, project: Project) {
			val field = TextFieldWithCompletion(project, UnicodeCompletionProvider, "", true, true, true)
			var popup: JBPopup? = null
			popup = JBPopupFactory.getInstance()
				.createComponentPopupBuilder(JBUI.Panels.simplePanel(field)
					.addToLeft(JLabel("\\")), null)
				.setMovable(true)
				.setAlpha(0.1F)
				.setKeyEventHandler {
					if (it.keyCode == KeyEvent.VK_ENTER) {
						// `LookUp` is the unique popup for a project.
						if (LookupManager.getInstance(project).activeLookup != null) {
							false
						} else {
							popup?.cancel()
							true
						}
					} else
						false
				}
				.setAdText(JuliaBundle.message("julia.actions.unicode-input.popup.ad"))
				.setRequestFocus(true)
				.createPopup()
			popup.addListener(object : JBPopupListener {
				// due to that this function is not `default` in older idea versions
				override fun beforeShown(event: LightweightWindowEvent) = Unit

				override fun onClosed(event: LightweightWindowEvent) {
					CommandProcessor.getInstance().executeCommand(project, {
						if (null != editor) ApplicationManager.getApplication().runWriteAction {
							val inserted = field.text
								.let {
								when (it) {
									"''", "\"\"" -> it.replaceFirst(it[0], '\\')
									else -> it.replace("\'", "\\'").replace("\"", "\\'")
								}
							}
							editor.document.insertString(editor.caretModel.offset, inserted)
							editor.caretModel.moveCaretRelatively(inserted.length, 0, false, false, true)
						}
					}, null, null)
				}
			})
			if (editor != null) popup.show(JBPopupFactory.getInstance().guessBestPopupLocation(editor))
			else popup.showInFocusCenter()
			field.requestFocus()
		}
	}

	override fun actionPerformed(e: AnActionEvent) {
		val presentation = e.presentation
		if (!presentation.isEnabled) return
		val editor = CommonDataKeys.EDITOR.getData(e.dataContext) ?: return
		val project = e.project ?: return
		actionInvoke(editor, project)
	}

	override fun update(e: AnActionEvent) {
		e.presentation.isEnabledAndVisible =
			(juliaGlobalSettings.globalUnicodeInput && CommonDataKeys.EDITOR.getData(e.dataContext) != null) ||
				(fileType(e) && e.project?.run { juliaSettings.settings.unicodeEnabled }.orFalse())
	}
}

class JuliaDocumentFormatAction : JuliaAction(
	JuliaBundle.message("julia.actions.doc-format.name"),
	JuliaBundle.message("julia.actions.doc-format.description")), DumbAware {
	override fun actionPerformed(e: AnActionEvent) {
		val project = e.project ?: return
		val file = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
		// TODO (idk why the progress is invisible)
		ProgressManager.getInstance().runProcessWithProgressSynchronously({
			ApplicationManager.getApplication().run { invokeAndWait { runReadAction { read(file, project) } } }
		}, JuliaBundle.message("julia.messages.doc-format.running"), false, project)
	}

	private fun read(file: VirtualFile, project: Project) {
		FileDocumentManager
			.getInstance()
			.getDocument(file)
			?.let(FileDocumentManager.getInstance()::saveDocument)
		//language=Julia
		val code = """using DocumentFormat: format
try
    read("${file.path.toUnixPath()}",String) |> format
catch e
    println("__INTELLIJ__"*repr(e))
end
"""
		var stdout = ""
		val executor = Executors.newCachedThreadPool()
		val future = executor.submit {
			try {
				ReadAction.compute<String?, Throwable> {
					project.languageServer.sendCommand(code)?.also { stdout = it }
				}
			} catch (e: Exception) {
				e.printStackTrace()
			}
		}
		try {
			future.get(10000, TimeUnit.MILLISECONDS)
			ApplicationManager.getApplication().let {
				it.invokeAndWait {
					it.runWriteAction { write(project, file, stdout) }
					LocalFileSystem.getInstance().refreshFiles(listOf(file))
				}
			}
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}

	private fun write(project: Project, file: VirtualFile, stdout: String) {
		val errInfo = stdout.substringAfter("__INTELLIJ__")
		if (errInfo !== stdout) ApplicationManager.getApplication().invokeLater {
			Messages.showDialog(
				project,
				errInfo,
				JuliaBundle.message("julia.messages.doc-format.error"),
				arrayOf(JuliaBundle.message("julia.yes")),
				0, JuliaIcons.JOJO_ICON)
		} else if (errInfo.isNotEmpty()) {
			file.getOutputStream(this).bufferedWriter().use {
				it.append(StringEscapeUtils.unescapeJava(stdout.trim('"')))
				it.flush()
			}
		}
	}
}