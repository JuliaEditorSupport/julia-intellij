package org.ice1000.julia.lang.action

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.CharFilter
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.popup.*
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.textCompletion.TextCompletionProvider
import com.intellij.util.textCompletion.TextFieldWithCompletion
import com.intellij.util.ui.JBUI
import icons.JuliaIcons
import org.ice1000.julia.lang.*
import org.ice1000.julia.lang.module.JuliaSettings
import org.ice1000.julia.lang.module.juliaSettings
import java.awt.event.KeyEvent
import javax.swing.JLabel

/**
 * 快速运行选定代码的Action
 */
class JuliaTryEvaluateAction : JuliaAction(
	JuliaBundle.message("julia.actions.try-eval.name"),
	JuliaBundle.message("julia.actions.try-eval.description")), DumbAware {
	private val core = TryEvaluate()
	override fun actionPerformed(e: AnActionEvent) {
		val editor = e.getData(CommonDataKeys.EDITOR) ?: return		//获得Editor
		core.tryEval(editor, editor.selectionModel.selectedText ?: return, e.getData(CommonDataKeys.PROJECT))		//执行部分代码运行
	}
}

/**
 * 就是那个\alpha + Tab就会变成α的玩意
 */
class JuliaUnicodeInputAction : JuliaAction(
	JuliaBundle.message("julia.actions.unicode-input.text"),
	JuliaBundle.message("julia.actions.unicode-input.description")), DumbAware {
	companion object CompletionHolder {
		private const val unicodeFile = "org/ice1000/julia/lang/unicode-list.txt"		//对应表路径
		private val unicodeList: List<LookupElementBuilder> by lazy {		//估计就是把unicodeFile的内容转化为LookupElementBuilder对象吧
			JuliaUnicodeInputAction::class.java.classLoader.getResource(unicodeFile)		//获取Resource(对应表
				.readText()		//读取文本
				.split('\n')		//以\n分割
				.mapNotNull { str ->		//map遍历
					if (str.isBlank()) return@mapNotNull null  //如果内容是空白的, 就返回null(相当于continue?
					val (a, b) = str.split(' ')		//如果不为空, 就以空格分割一次, 左边的是alpha, 右边的是α 这样的一个对应关系
					lookupElementBuilder(a, b)		//create一个LookupElementBuilder
						.withCaseSensitivity(false)
						.withTailText(" $b", true)		//设置文本末尾(大概就是 onj.toString()								String 的这个String了
				} + listOf("nolinebreak" to '\u2060')		//介个大概是个特例
				.map { (a, b) -> lookupElementBuilder(a, b) }
		}

		/**
		 * create一个LookupElementBuilder, 然后进行一些杂七杂八的操作
		 */
		private fun lookupElementBuilder(a: String, b: Any) = LookupElementBuilder.create(b)
			.withLookupString(a)
			.withPresentableText(a)
			.withIcon(JuliaIcons.JULIA_BIG_ICON)		//设置图标

		/**
		 * 好像是按下\就会弹出的那个灰色框框
		 */
		private object UnicodeCompletionProvider : TextCompletionProvider, DumbAware {
			override fun getAdvertisement() = JuliaBundle.message("julia.actions.unicode-input.provider.ad")		//灰色框框下面的那个提示语
			override fun getPrefix(text: String, offset: Int) = text.take(offset)		//获取前缀?
			override fun acceptChar(c: Char) = CharFilter.Result.ADD_TO_PREFIX		//不懂
			override fun applyPrefixMatcher(result: CompletionResultSet, prefix: String) =		//同不懂QAQ
				result.withPrefixMatcher(PlainPrefixMatcher(prefix))

			override fun fillCompletionVariants(
				parameters: CompletionParameters,
				prefix: String,
				result: CompletionResultSet) {
				unicodeList.forEach(result::addElement)
				result.stopHere()
			}
		}


		/**
		 * 看名字就知道是干嘛的了
		 * 执行这个Action
		 * @param editor 当前的editor
		 * @param project 当前的project(不能editor.project吗。。
		 */
		fun actionInvoke(editor: Editor, project: Project) {
			val field = TextFieldWithCompletion(project, UnicodeCompletionProvider, "", true, true, true)
			var popup: JBPopup? = null		//弹窗对象
			popup = JBPopupFactory.getInstance()		//制造弹窗和一堆设置
				.createComponentPopupBuilder(JBUI.Panels.simplePanel(field)
					.addToLeft(JLabel("\\")), null)		//框框前面的东西
				.setMovable(true)
				.setAlpha(0.1F)		//透明度(我只能看得懂这个了。。
				.setKeyEventHandler {
					if (it.keyCode == KeyEvent.VK_ENTER) popup?.cancel()
					false
				}
				.setAdText(JuliaBundle.message("julia.actions.unicode-input.popup.ad"))		//加点广告
				.createPopup()		//制造~
			popup.addListener(object : JBPopupListener.Adapter() {		//添加监听(对不起, 实在不懂
				override fun onClosed(event: LightweightWindowEvent?) {
					CommandProcessor.getInstance().executeCommand(project, {
						ApplicationManager.getApplication().runWriteAction {
							editor.document.insertString(editor.caretModel.offset, field.text)
							editor.caretModel.moveCaretRelatively(field.text.length, 0, false, false, true)
						}
					}, null, null)
				}
			})
			popup.show(JBPopupFactory.getInstance().guessBestPopupLocation(editor))
			field.requestFocus()
		}
	}

	/**
	 * 执行Action
	 */
	override fun actionPerformed(e: AnActionEvent) {
		val editor = e.getData(CommonDataKeys.EDITOR) ?: return
		val project = e.project ?: return
		actionInvoke(editor, project)
	}

	/**
	 * 更新?
	 */
	override fun update(e: AnActionEvent) {
		e.presentation.isEnabledAndVisible = fileType(e) and e.project?.run { juliaSettings.settings.unicodeEnabled }.orFalse()
	}
}

/**
 * 格式化文档的Action
 */
class JuliaDocumentFormatAction : JuliaAction(
	JuliaBundle.message("julia.actions.doc-format.name"),
	JuliaBundle.message("julia.actions.doc-format.description")), DumbAware {
	override fun actionPerformed(e: AnActionEvent) {
		val project = e.project ?: return		//当前Project
		val settings = project.juliaSettings.settings		//Project里的settings
		val file = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return		//获得File
		ProgressManager.getInstance().runProcessWithProgressSynchronously({		//不懂
			ApplicationManager.getApplication().run { invokeAndWait { runReadAction { read(file, settings, project) } } }
		}, JuliaBundle.message("julia.messages.doc-format.running"), false, project)
	}

	/**
	 * 读取?
	 */
	private fun read(file: VirtualFile, settings: JuliaSettings, project: Project) {
		val content = file.inputStream.reader().readText().replace(Regex.fromLiteral("\"\\")) {
			when (it.value) {		//这个琪露诺都能看明白
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
