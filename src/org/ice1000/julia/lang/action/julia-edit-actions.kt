package org.ice1000.julia.lang.action

import com.google.common.util.concurrent.UncheckedTimeoutException
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.JBColor
import com.intellij.ui.ScrollPaneFactory
import com.intellij.util.ui.JBUI
import icons.JuliaIcons
import org.ice1000.julia.lang.*
import org.ice1000.julia.lang.module.juliaSettings
import java.awt.Dimension
import java.util.concurrent.TimeUnit
import javax.swing.JLabel
import javax.swing.JTextArea

class TryEvaluate {
	private class InvalidJuliaSdkException(val path: String) : RuntimeException()

	private var textLimit = 320
	private var timeLimit = 2500L

	fun tryEval(editor: Editor, text: String, project: Project?) {
		try {
			val builder = StringBuilder()
			var juliaExe = ""
			var juliaVersion = ""
			project?.juliaSettings?.settings?.let {
				juliaExe = it.exePath
				juliaVersion = it.version
				textLimit = it.tryEvaluateTextLimit
				timeLimit = it.tryEvaluateTimeLimit
			}
			val (stdout, stderr) = executeJulia(juliaExe, text, timeLimit)
			builder.appendln(JuliaBundle.message("julia.messages.try-eval.version-text", juliaVersion))
			if (stdout.isNotEmpty()) {
				builder.appendln(JuliaBundle.message("julia.messages.try-eval.stdout"))
				stdout.forEach { builder.appendln(it) }
			}
			if (stderr.isNotEmpty()) {
				builder.appendln(JuliaBundle.message("julia.messages.try-eval.stderr"))
				stderr.forEach { builder.appendln(it) }
			}
			if (stderr.isNotEmpty()) showPopupWindow(builder.toString(), editor, 0xE20911, 0xC20022)
			else showPopupWindow(builder.toString(), editor, 0x0013F9, 0x000CA1)
		} catch (e: UncheckedTimeoutException) {
			showPopupWindow(JuliaBundle.message("julia.messages.try-eval.timeout"), editor, 0xEDC209, 0xC26500)
		} catch (e: Throwable) {
			val cause = e.cause ?: e
			if (cause is InvalidJuliaSdkException) showPopupWindow(JuliaBundle.message(
				"julia.messages.try-eval.invalid-path", cause.path), editor, 0xEDC209, 0xC26500)
			else showPopupWindow(JuliaBundle.message(
				"julia.messages.try-eval.exception", e.javaClass.simpleName, e.message.orEmpty()), editor, 0xE20911, 0xC20022)
		}
	}

	private fun showPopupWindow(result: String, editor: Editor, color: Int, colorDark: Int) {
		val relativePoint = JBPopupFactory.getInstance().guessBestPopupLocation(editor)
		if (result.length < textLimit)
			ApplicationManager.getApplication().invokeLater {
				JBPopupFactory.getInstance()
					.createHtmlTextBalloonBuilder(result, JuliaIcons.JULIA_BIG_ICON, JBColor(color, colorDark), null)
					.setFadeoutTime(8000)
					.setHideOnAction(true)
					.createBalloon()
					.show(relativePoint, Balloon.Position.below)
			}
		else
			ApplicationManager.getApplication().invokeLater {
				JBPopupFactory.getInstance()
					.createComponentPopupBuilder(JBUI.Panels.simplePanel()
						.addToTop(JLabel(JuliaIcons.JULIA_BIG_ICON))
						.addToCenter(ScrollPaneFactory.createScrollPane(JTextArea(result).apply {
							toolTipText = JuliaBundle.message("julia.messages.try-eval.overflowed-text", textLimit)
							lineWrap = true
							wrapStyleWord = true
							isEditable = false
						}))
						.apply {
							preferredSize = Dimension(500, 500)
							border = JBUI.Borders.empty(10, 5, 5, 5)
						}, null)
					.setRequestFocus(true)
					.setResizable(true)
					.setMovable(true)
					.setCancelOnClickOutside(true)
					.createPopup()
					.show(relativePoint)
			}
	}
}

class JuliaTryEvaluateAction : AnAction(
	JuliaBundle.message("julia.actions.try-eval.name"),
	JuliaBundle.message("julia.actions.try-eval.description"),
	JuliaIcons.JULIA_BIG_ICON), DumbAware {
	private val core = TryEvaluate()
	override fun actionPerformed(e: AnActionEvent) {
		val editor = e.getData(CommonDataKeys.EDITOR) ?: return
		core.tryEval(editor, editor.selectionModel.selectedText ?: return, e.getData(CommonDataKeys.PROJECT))
	}

	override fun update(e: AnActionEvent) {
		e.presentation.isEnabledAndVisible = e.getData(CommonDataKeys.VIRTUAL_FILE)?.fileType == JuliaFileType
	}
}

class JuliaDocumentFormatAction : AnAction(
	JuliaBundle.message("julia.actions.doc-format.name"),
	JuliaBundle.message("julia.actions.doc-format.description"),
	JuliaIcons.JULIA_BIG_ICON), DumbAware, Disposable {
	private var process: Process? = null
	override fun dispose() {
		process?.let {
			it.outputStream.close()
			it.destroy()
		}
	}

	override fun update(e: AnActionEvent) {
		e.presentation.isEnabledAndVisible = e.getData(CommonDataKeys.VIRTUAL_FILE)?.fileType == JuliaFileType
	}

	override fun actionPerformed(e: AnActionEvent) {
		val project = e.project ?: return
		val settings = project.juliaSettings.settings
		val file = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
		val path = file.path.trimEnd(' ', '!', '/')
		FileDocumentManager.getInstance().let { it.getDocument(file)?.let(it::saveDocument) }
		ProgressManager.getInstance().runProcessWithProgressSynchronously({
			val process = process ?: Runtime.getRuntime().exec("${settings.exePath} -q").also {
				//language=Julia
				it.outputStream.let {
					it.write("using DocumentFormat: format\n".toByteArray())
					it.flush()
				}
				process = it
			}
			process.waitFor(5, TimeUnit.SECONDS)
			//language=Julia
			process.outputStream.let {
				it.write("open(\"$path\", \"w\") do f; write(f, format(readstring(f))) end\n".toByteArray())
				it.flush()
			}
			file.refresh(false, false)
		}, JuliaBundle.message("julia.messages.doc-format.running"), false, project)
		FileDocumentManager.getInstance().reloadFiles(file)
	}
}
