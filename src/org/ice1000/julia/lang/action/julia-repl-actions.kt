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
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.keymap.KeymapUtil
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.JBColor
import icons.JuliaIcons
import org.ice1000.julia.lang.*
import org.ice1000.julia.lang.module.juliaSettings
import java.awt.Font
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.util.concurrent.ConcurrentHashMap

abstract class JuliaSendCodeToReplAction(
	text: String?,
	description: String?) : JuliaAction(text, description) {
	override fun actionPerformed(e: AnActionEvent) {
		val project = e.getData(CommonDataKeys.PROJECT)
			?: return errorNotification(message = JuliaBundle.message("julia.actions.project-not-found"))
		val juliaExe = project.juliaSettings.settings.exePath
		if (juliaExe.isEmpty()) return errorNotification(project, JuliaBundle.message("julia.actions.repl.no-exe"))

		WriteCommandAction.runWriteCommandAction(project) {
			var runner = project.getUserData(JULIA_REPL_RUNNER_KEY)
			if (runner == null || runner.processHandler?.isProcessTerminated.orFalse()) {
				runner = JuliaReplRunner(GeneralCommandLine(juliaExe), project, JuliaBundle.message("julia.actions.repl.name"), juliaExe)
					.apply {
						project.putUserData(JULIA_REPL_RUNNER_KEY, this)
						initAndRun()
					}
			}
			runner.executor.sendCommandToProcess(code(e) ?: return@runWriteCommandAction)
		}
	}

	abstract fun code(e: AnActionEvent): String?
}

class JuliaIncludeRunFileToReplAction : JuliaSendCodeToReplAction(
	JuliaBundle.message("julia.actions.repl.run-include.name"),
	JuliaBundle.message("julia.actions.repl.run-include.description")), DumbAware {
	override fun code(e: AnActionEvent): String? {
		val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return null
		FileDocumentManager
			.getInstance()
			.getDocument(virtualFile)
			?.let(FileDocumentManager.getInstance()::saveDocument)
		return """include("${virtualFile.path}")"""
	}
}

class JuliaSendSelectionToReplAction : JuliaSendCodeToReplAction(
	JuliaBundle.message("julia.actions.repl.run-selected.name"),
	JuliaBundle.message("julia.actions.repl.run-selected.description")), DumbAware {
	override fun code(e: AnActionEvent): String? {
		val editor = e.getData(CommonDataKeys.EDITOR) ?: return null
		return editor.selectionModel.selectedText
	}

	override fun update(e: AnActionEvent) {
		super.update(e)
		e.presentation.isEnabled = e.getData(CommonDataKeys.EDITOR)?.selectionModel?.selectedText != null
	}
}

fun errorNotification(project: Project? = null, message: String) {
	Notifications.Bus.notify(Notification(
		REPL_ERROR_TAG,
		JuliaBundle.message("julia.actions.repl.error-title"),
		message,
		NotificationType.ERROR), project)
}

class JuliaReplAction : JuliaAction(
	JuliaBundle.message("julia.actions.repl.name"),
	JuliaBundle.message("julia.actions.repl.description")) {
	override fun actionPerformed(e: AnActionEvent) {
		val project = e.project
			?: return errorNotification(message = JuliaBundle.message("julia.actions.project-not-found"))
		val juliaExe = project.juliaSettings.settings.exePath
		if (juliaExe.isEmpty()) return errorNotification(project, JuliaBundle.message("julia.actions.repl.no-exe"))
		JuliaReplRunner(GeneralCommandLine(juliaExe), project, JuliaBundle.message("julia.actions.repl.name"), juliaExe)
			.apply {
				project.putUserData(JULIA_REPL_RUNNER_KEY, this)
				initAndRun()
			}
	}
}

class JuliaReplRunner(
	private val cmdLine: GeneralCommandLine,
	myProject: Project,
	title: String,
	path: String?
) : AbstractConsoleRunnerWithHistory<LanguageConsoleView>(myProject, title, path) {
	override fun initAndRun() {
		super.initAndRun()
		useSciMode()
	}

	fun useSciMode() {
		WriteCommandAction.runWriteCommandAction(project) {
			val jlFile = FileUtil.createTempFile("IntelliJ", ".jl")
			val pyFile = FileUtil.createTempFile("backend_interagg", ".py")
			jlFile.writeBytes(javaClass.getResource("IntelliJ.jl").readBytes())
			pyFile.writeBytes(javaClass.getResource("backend_interagg.py").readBytes())
			executor.sendCommandToProcess("""include("${jlFile.absolutePath}")""", false)
		}
	}

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
		cmdLine.withJuliaSciMode(project)
		return object : ColoredProcessHandler(cmdLine
			.withCharset(Charsets.UTF_8)
			.withWorkDirectory(project.basePath)) {}
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
				e.project ?: return errorNotification(null, JuliaBundle.message("julia.actions.project-not-found"))

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

fun GeneralCommandLine.withJuliaSciMode(project: Project) = this
	.apply {
		environment[JULIA_INTELLIJ_PLOT_PORT] = project.getUserData(JULIA_SCI_PORT_KEY)
			?: return@apply
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

	fun sendCommandToProcess(command: String, showCommand: Boolean = true) {
		val processHandler = runner.processHandler
		val processInputOS = processHandler.processInput
			?: return errorNotification(runner.project, "Error")
		val bytes = ("$command\n").toByteArray()

		if (showCommand) {
			val historyDocumentRange = runner.historyUpdater.printNewCommandInHistory(command)
			val commandHistory = runner.commandHistory
			commandHistory.addEntry(CommandHistory.Entry(command, historyDocumentRange))
			runner.commandHistory.entryProcessed()
		}
		processInputOS.write(bytes)
		processInputOS.flush()
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
	val PLACEHOLDER_COLOR: JBColor = JBColor.LIGHT_GRAY
}