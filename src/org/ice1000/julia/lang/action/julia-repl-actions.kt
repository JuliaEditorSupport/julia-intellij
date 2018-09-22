package org.ice1000.julia.lang.action

import com.intellij.codeInsight.lookup.LookupManager
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.console.*
import com.intellij.execution.process.ColoredProcessHandler
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.runners.AbstractConsoleRunnerWithHistory
import com.intellij.notification.*
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.ex.util.EditorUtil
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.keymap.KeymapUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.JBColor
import icons.JuliaIcons
import org.ice1000.julia.lang.*
import org.ice1000.julia.lang.module.juliaGlobalSettings
import java.awt.Font
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.util.concurrent.ConcurrentHashMap

fun errorNotification(project: Project?, message: String) {
	val errorTag = "Julia REPL ERROR"
	val errorTitle = "Julia REPL Configuration Error"
	Notifications.Bus.notify(Notification(errorTag, errorTitle, message, NotificationType.ERROR), project)
}

class JuliaReplAction : JuliaAction(
	JuliaBundle.message("julia.actions.repl.name"),
	JuliaBundle.message("julia.actions.repl.description")) {
	override fun actionPerformed(e: AnActionEvent) {
		val project = e.project ?: return errorNotification(null, "Project not found")
		val juliaExe = juliaGlobalSettings.knownJuliaExes.firstOrNull()
			?: return errorNotification(null, "No Julia Executable, please configure it.")
		JuliaReplRunner(juliaExe.let { GeneralCommandLine(it) },
			project, "Julia REPL", juliaExe
		).initAndRun()
	}
}

class JuliaReplRunner(
	private val cmdLine: GeneralCommandLine,
	myProject: Project,
	title: String,
	path: String?
) : AbstractConsoleRunnerWithHistory<LanguageConsoleView>(myProject, title, path) {

	private val consoleMap: MutableMap<VirtualFile, JuliaReplRunner> = ConcurrentHashMap()
	fun getConsoleByVirtualFile(virtualFile: VirtualFile) = consoleMap[virtualFile]
	fun putVirtualFileToConsole(virtualFile: VirtualFile, console: JuliaReplRunner) = consoleMap.put(virtualFile, console)
	fun removeConsole(virtualFile: VirtualFile) = consoleMap.remove(virtualFile)

	val executor = CommandExecutor(this)
	val commandHistory = CommandHistory()
	val historyUpdater = HistoryUpdater(this)

	override fun createExecuteActionHandler() = object : ProcessBackedConsoleExecuteActionHandler(processHandler, false) {
		override fun runExecuteAction(consoleView: LanguageConsoleView) = executor.executeCommand()
	}

	override fun getConsoleIcon() = JuliaIcons.JULIA_BIG_ICON
	override fun createProcess(): Process? = cmdLine.createProcess()
	override fun createProcessHandler(process: Process?): OSProcessHandler {
		val consoleFile = consoleView.virtualFile
		putVirtualFileToConsole(consoleFile, this)
		return ColoredProcessHandler(cmdLine
			.withCharset(Charsets.UTF_8)
			.withWorkDirectory(cmdLine.workDirectory))
	}

	override fun createConsoleView(): LanguageConsoleView {
		val builder = LanguageConsoleBuilder()

		val consoleView = builder.gutterContentProvider(object : BasicGutterContentProvider() {
			override fun beforeEvaluate(editor: Editor) = Unit
		}).build(project, JuliaLanguage.INSTANCE)
		consoleView.prompt = "julia> "
		val consoleEditor = consoleView.consoleEditor
		setupPlaceholder(consoleEditor)

		val historyKeyListener = HistoryKeyListener(project, consoleEditor, commandHistory)
		consoleEditor.contentComponent.addKeyListener(historyKeyListener)
		commandHistory.listeners.add(historyKeyListener)

		val executeAction = object : AnAction() {
			override fun actionPerformed(e: AnActionEvent) {
				e.project ?: return errorNotification(null, "Cannot find project")

				val virtualFile = consoleView.virtualFile
				val console = getConsoleByVirtualFile(virtualFile) ?: return
				console.executor.executeCommand()
			}
		}
		executeAction.registerCustomShortcutSet(KeyEvent.VK_ENTER, KeyEvent.SHIFT_DOWN_MASK, consoleView.consoleEditor.component)

		return consoleView
	}

	override fun createConsoleExecAction(consoleExecuteActionHandler: ProcessBackedConsoleExecuteActionHandler) = ConsoleExecuteAction(consoleView, consoleExecuteActionHandler, JULIA_REPL_EMPTY_ACTION_ID, consoleExecuteActionHandler)
	override fun finishConsole() {
		removeConsole(consoleView.virtualFile)
		super.finishConsole()
	}

	private fun setupPlaceholder(editor: EditorEx) {
		val executeCommandAction = ActionManager.getInstance().getAction(JULIA_REPL_EMPTY_ACTION_ID)
		val executeCommandActionShortcutText = KeymapUtil.getFirstKeyboardShortcutText(executeCommandAction)
		editor.setPlaceholder("<$executeCommandActionShortcutText> to execute")
		editor.setShowPlaceholderWhenFocused(false)

		val placeholderAttrs = TextAttributes()
		placeholderAttrs.foregroundColor = ReplColors.PLACEHOLDER_COLOR
		placeholderAttrs.fontType = Font.ITALIC
		editor.setPlaceholderAttributes(placeholderAttrs)
	}
}

class CommandExecutor(private val runner: JuliaReplRunner) {
	fun executeCommand() = WriteCommandAction.runWriteCommandAction(runner.project) {
		val commandText = getTrimmedCommandText()
		if (commandText.isEmpty()) return@runWriteCommandAction

		sendCommandToProcess(commandText)
	}

	private fun getTrimmedCommandText(): String {
		val consoleView = runner.consoleView
		val document = consoleView.editorDocument
		return document.text.trim()
	}

	private fun sendCommandToProcess(command: String) {
		val processHandler = runner.processHandler
		val processInputOS = processHandler.processInput ?: return errorNotification(null, "Error")
		val charset = Charsets.UTF_8
		val bytes = ("$command\n").toByteArray(charset)
		processInputOS.write(bytes)
		processInputOS.flush()
		val historyDocumentRange = runner.historyUpdater.printNewCommandInHistory(command)
		val commandHistory = runner.commandHistory
		commandHistory.addEntry(CommandHistory.Entry(command, historyDocumentRange))
		runner.commandHistory.entryProcessed()
	}
}


class CommandHistory {
	class Entry(
		val entryText: String,
		val rangeInHistoryDocument: TextRange
	)

	private val entries = arrayListOf<Entry>()
	var processedEntriesCount: Int = 0

	val listeners = arrayListOf<HistoryUpdateListener>()

	operator fun get(i: Int) = entries[i]

	fun addEntry(entry: Entry) {
		entries.add(entry)
		listeners.forEach { it.onNewEntry(entry) }
	}

	fun entryProcessed() {
		processedEntriesCount++
	}

	val size: Int
		get() = entries.size
}

interface HistoryUpdateListener {
	fun onNewEntry(entry: CommandHistory.Entry)
}

class HistoryUpdater(private val runner: JuliaReplRunner) {
	private val consoleView: LanguageConsoleImpl by lazy { runner.consoleView as LanguageConsoleImpl }

	fun printNewCommandInHistory(trimmedCommandText: String): TextRange {
		val historyEditor = consoleView.historyViewer
		addLineBreakIfNeeded(historyEditor)
		val startOffset = historyEditor.document.textLength
		val endOffset = startOffset + trimmedCommandText.length
		addCommandTextToHistoryEditor(trimmedCommandText)
		historyEditor.scrollingModel.scrollVertically(endOffset)
		return TextRange(startOffset, endOffset)
	}

	private fun addCommandTextToHistoryEditor(trimmedCommandText: String) {
		val consoleEditor = consoleView.consoleEditor
		val consoleDocument = consoleEditor.document
		consoleDocument.setText(trimmedCommandText)
		LanguageConsoleImpl.printWithHighlighting(consoleView, consoleEditor, TextRange(0, consoleDocument.textLength))
		consoleView.flushDeferredText()
		consoleDocument.setText("")
	}

	private fun addLineBreakIfNeeded(historyEditor: EditorEx) {
		val historyDocument = historyEditor.document
		val historyText = historyDocument.text
		val textLength = historyText.length

		if (!historyText.endsWith('\n')) {
			historyDocument.insertString(textLength, "\n")
			historyDocument.insertString(textLength + 1, "\n")
		} else if (!historyText.endsWith("\n\n")) {
			historyDocument.insertString(textLength, "\n")
		}
	}
}

class HistoryKeyListener(
	private val project: Project, private val consoleEditor: EditorEx, private val history: CommandHistory
) : KeyAdapter(), HistoryUpdateListener {
	private var historyPos = 0
	private var prevCaretOffset = -1
	private var unfinishedCommand = ""

	override fun onNewEntry(entry: CommandHistory.Entry) {
		// reset history positions
		historyPos = history.size
		prevCaretOffset = -1
		unfinishedCommand = ""
	}

	private enum class HistoryMove {
		UP, DOWN
	}

	override fun keyReleased(e: KeyEvent) {
		when (e.keyCode) {
			KeyEvent.VK_UP -> moveHistoryCursor(HistoryMove.UP)
			KeyEvent.VK_DOWN -> moveHistoryCursor(HistoryMove.DOWN)
			KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT -> prevCaretOffset = consoleEditor.caretModel.offset
		}
	}

	private fun moveHistoryCursor(move: HistoryMove) {
		if (history.size == 0) return
		if (LookupManager.getInstance(project).activeLookup != null) return

		val caret = consoleEditor.caretModel
		val document = consoleEditor.document

		val curOffset = caret.offset
		val curLine = document.getLineNumber(curOffset)
		val totalLines = document.lineCount
		val isMultiline = totalLines > 1

		when (move) {
			HistoryMove.UP -> {
				if (curLine != 0 || (isMultiline && prevCaretOffset != 0)) {
					prevCaretOffset = curOffset
					return
				}

				if (historyPos == history.size) {
					unfinishedCommand = document.text
				}

				historyPos = Math.max(historyPos - 1, 0)
				WriteCommandAction.runWriteCommandAction(project) {
					document.setText(history[historyPos].entryText)
					EditorUtil.scrollToTheEnd(consoleEditor)
					prevCaretOffset = 0
					caret.moveToOffset(0)
				}
			}
			HistoryMove.DOWN -> {
				if (historyPos == history.size) return

				if (curLine != totalLines - 1 || (isMultiline && prevCaretOffset != document.textLength)) {
					prevCaretOffset = curOffset
					return
				}

				historyPos = Math.min(historyPos + 1, history.size)
				WriteCommandAction.runWriteCommandAction(project) {
					document.setText(if (historyPos == history.size) unfinishedCommand else history[historyPos].entryText)
					prevCaretOffset = document.textLength
					EditorUtil.scrollToTheEnd(consoleEditor)
				}
			}
		}
	}
}

object ReplColors {
//	val HISTORY_GUTTER_COLOR: JBColor = JBColor(Gray.xF2, Gray.x41)
//	val EDITOR_GUTTER_COLOR: JBColor = JBColor(Gray.xCF, Gray.x31)
	val PLACEHOLDER_COLOR: JBColor = JBColor.LIGHT_GRAY
}