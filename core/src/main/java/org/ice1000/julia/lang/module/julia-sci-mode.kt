/*
 *     Julia language support plugin for Intellij-based IDEs.
 *     Copyright (C) 2024 julia-intellij contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

@file:Suppress("UndesirableClassUsage")

package org.ice1000.julia.lang.module

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.intellij.execution.console.LanguageConsoleImpl
import com.intellij.execution.impl.ConsoleViewUtil
import com.intellij.execution.ui.ObservableConsoleView
import com.intellij.icons.AllIcons.Actions
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.colors.EditorColors
import com.intellij.openapi.editor.colors.EditorColorsScheme
import com.intellij.openapi.fileChooser.FileChooserFactory
import com.intellij.openapi.fileChooser.FileSaverDescriptor
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.ex.FileEditorProviderManager
import com.intellij.openapi.fileEditor.impl.DockableEditor
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.testFramework.BinaryLightVirtualFile
import com.intellij.ui.ColoredTextContainer
import com.intellij.ui.JBColor
import com.intellij.ui.SimpleColoredComponent
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.awt.RelativePoint
import com.intellij.ui.awt.RelativeRectangle
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.docking.DockContainer
import com.intellij.ui.docking.DockContainer.ContentResponse
import com.intellij.ui.docking.DockContainer.Listener
import com.intellij.ui.docking.DockManager
import com.intellij.ui.docking.DockableContent
import com.intellij.ui.docking.DragSession
import com.intellij.ui.tabs.JBTabsPosition
import com.intellij.ui.tabs.TabInfo
import com.intellij.ui.tabs.TabInfo.DragOutDelegate
import com.intellij.ui.tabs.TabsListener
import com.intellij.ui.tabs.impl.*
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import com.intellij.xdebugger.XSourcePosition
import com.intellij.xdebugger.frame.*
import com.intellij.xdebugger.impl.frame.XStandaloneVariablesView
import icons.JuliaIcons
import kotlinx.coroutines.CoroutineScope
import org.ice1000.julia.lang.*
import org.ice1000.julia.lang.execution.JuliaEditorsProvider
import org.jetbrains.debugger.SourceInfo
import org.jetbrains.rpc.LOG
import java.awt.*
import java.awt.event.MouseEvent
import java.awt.image.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JComponent
import javax.swing.JPanel
import kotlin.collections.Map.Entry

interface Figure {
	fun getTabInfo(): TabInfo
	fun getSearchKey(): Any
	fun hasSearchKey(): Boolean
}

interface WithBinaryContent {
	fun getBytes(): ByteArray
}

interface WithDockableContent {
	fun createDockableContent(): DockableContent<*>
}

interface DockableContentFigureFactory {
	fun isApplicable(content: DockableContent<*>): Boolean
	fun createFigure(content: DockableContent<*>): Figure
}

class JuliaSciToolWindow(private val project: Project, cs: CoroutineScope) : JPanel(BorderLayout()),
	DumbAware {
	private val tabs: JBEditorTabs
	private val map: MutableMap<TabInfo, Any>
	var lastPlotIndex: Int = 0
		private set
	private var dockContainer: MyDockContainer? = null
	private val list: MutableList<DockableContentFigureFactory>

	init {
		this.map = HashMap()
		this.lastPlotIndex = 0
		this.list = ArrayList()
		this.tabs = MyTabs(this.project, cs)
		this.tabs.setPopupGroup(DefaultActionGroup(SaveAsFileAction(), CloseAllPlotsAction()), "unknown", true)
		// this.tabs.isTabDraggingEnabled = true
		this.tabs.addListener(object : TabsListener {
			override fun tabRemoved(tabToRemove: TabInfo) {
				this@JuliaSciToolWindow.map.remove(tabToRemove)
				val jComponent = tabToRemove.component as? Disposable ?: return
				Disposer.dispose(jComponent)
			}
		})
		this.add(this.tabs)
	}

	fun init(toolWindow: ToolWindow) {
		val sciPanel = ContentFactory.getInstance()
		val plotsContent = sciPanel.createContent(this, JuliaBundle.message("julia.modules.sci-mode.plots.title"), false)
		plotsContent.isCloseable = false

		val stackFrame = JuliaVariableStackFrame(project)
		val view = JuliaVariablesView(project, stackFrame)
		project.putUserData(JULIA_SCI_DATA_KEY, view)
		val dataContent = sciPanel.createContent(view.panel, JuliaBundle.message("julia.modules.sci-mode.data.title"), false)

		toolWindow.contentManager.addContent(dataContent)
		toolWindow.contentManager.addContent(plotsContent)

		if (dockContainer == null) {
			dockContainer = MyDockContainer(toolWindow)
			val dockContainer = dockContainer?:return
			val disposer = plotsContent.disposer
			if(disposer!=null)
			{
				Disposer.tryRegister(disposer, dockContainer)
				DockManager.getInstance(this.project).register(dockContainer, disposer)
			}
		}
	}

	fun addFigure(figure: Figure) {
		val tabInfo = figure.getTabInfo()
		tabInfo.setTabLabelActions(DefaultActionGroup(ClosePlotAction(tabInfo)), "unknown")
		if (figure is WithDockableContent) {
			tabInfo.setDragOutDelegate(MyDragOutDelegate(figure as WithDockableContent))
		}

		ApplicationManager.getApplication().invokeLater {
			var tabInfo1: TabInfo? = null
			if (figure.hasSearchKey()) {
				tabInfo1 = this.getTabInfo(figure.getSearchKey())
			}

			if (tabInfo1 == null) {
				this.tabs.addTab(tabInfo)
				++this.lastPlotIndex
			} else {
				val index = this.tabs.getIndexOf(tabInfo1)
				this.tabs.removeTab(tabInfo1)
				this.tabs.addTab(tabInfo, index)
			}

			this.tabs.select(tabInfo, true)
		}
	}

	private fun getTabInfo(value: Any): TabInfo? {
		val iterator = this.map.entries.iterator()
		var entry: Entry<*, *>
		do {
			if (!iterator.hasNext()) {
				return null
			}
			entry = iterator.next()
		} while (entry.value != value)
		return entry.key as TabInfo
	}

	inner class MyDockContainer(private val a: ToolWindow) : DockContainer, Disposable {
		override fun getAcceptArea(): RelativeRectangle = RelativeRectangle(this.a.component)
		override fun getAcceptAreaFallback(): RelativeRectangle = this.acceptArea
		override fun getContainerComponent(): JComponent = this.a.component

		override fun getContentResponse(content: DockableContent<*>, point: RelativePoint): ContentResponse =
			if (this.factory(content) != null) ContentResponse.ACCEPT_MOVE else ContentResponse.DENY

		override fun add(content: DockableContent<*>, dropTarget: RelativePoint?) {
			val figure = this.factory(content)!!.createFigure(content)
			this@JuliaSciToolWindow.addFigure(figure)
		}

		override fun closeAll() {}
		override fun addListener(listener: Listener, parent: Disposable) {}
		override fun isEmpty(): Boolean = false
		override fun startDropOver(content: DockableContent<*>, point: RelativePoint): Image? = null
		override fun processDropOver(content: DockableContent<*>, point: RelativePoint): Image? = null
		override fun resetDropOver(content: DockableContent<*>) {}
		override fun isDisposeWhenEmpty(): Boolean = false
		override fun dispose() {}
		private fun factory(content: DockableContent<*>): DockableContentFigureFactory? {
			return this@JuliaSciToolWindow.list.firstOrNull { it.isApplicable(content) }
		}
	}

	inner class MyDragOutDelegate(private val a: WithDockableContent) : DragOutDelegate {
		private var dragSession: DragSession? = null

		override fun dragOutStarted(mouseEvent: MouseEvent, info: TabInfo) {
			info.isHidden = true
			this.dragSession = this.getDockManager().createDragSession(mouseEvent, this.a.createDockableContent())
			val tabInfo = info.previousSelection ?: return
			this@JuliaSciToolWindow.tabs.select(tabInfo, true)
		}

		private fun getDockManager(): DockManager = DockManager.getInstance(this@JuliaSciToolWindow.project)

		override fun processDragOut(event: MouseEvent, source: TabInfo) {
			this.dragSession!!.process(event)
		}

		override fun dragOutFinished(event: MouseEvent, source: TabInfo) {
			this@JuliaSciToolWindow.tabs.removeTab(source)
			this.dragSession!!.process(event)
			this.dragSession = null
		}

		override fun dragOutCancelled(source: TabInfo) {
			source.isHidden = false
			this.dragSession?.cancel()
			this.dragSession = null
		}
	}

	private inner class SaveAsFileAction : AnAction("Save as File") {

		override fun actionPerformed(e: AnActionEvent) {
			val tabInfo = this@JuliaSciToolWindow.tabs.selectedInfo ?: return
			val project = e.project ?: return
			val bytes = FigureUtil.componentToByteArray(tabInfo.component)

			val fileSaverDescriptor = FileSaverDescriptor("Select File to Save Plot", "", "png")
			val fileSaverDialog = FileChooserFactory.getInstance().createSaveFileDialog(fileSaverDescriptor, project)
			val virtualFileWrapper = fileSaverDialog.save(project.guessProjectDir(), "myplot")

			try {
				if (virtualFileWrapper != null) {
					val file = virtualFileWrapper.file
					Files.write(Paths.get(file.path), bytes)
				}
			} catch (e: IOException) {
				logger.warn("Failed to save image " + e.message)
			}
		}
	}

	private inner class CloseAllPlotsAction : AnAction("Close All Plots", "Close all plot", JuliaIcons.JULIA_BIG_ICON) {

		override fun actionPerformed(e: AnActionEvent) {
			this@JuliaSciToolWindow.tabs.removeAllTabs()
		}
	}

	private inner class ClosePlotAction(private val a: TabInfo) :
		AnAction("Close Plot", "Close selected plot", Actions.Close) {

		override fun actionPerformed(e: AnActionEvent) {
			this@JuliaSciToolWindow.tabs.removeTab(this.a)
		}
	}

	private class MyTabLabel(tabs: JBTabsImpl, info: TabInfo) : TabLabel(tabs, info) {
		init {
			val jComponent = this.labelComponent as? SimpleColoredComponent
			jComponent?.isIconOnTheRight = true
		}

		override fun getPreferredSize(): Dimension = Dimension(JBUI.scale(100), JBUI.scale(80))
	}

	private class MyTabs(project: Project, cs: CoroutineScope) :
		JBEditorTabs(project, project, cs, TabListOptions(tabPosition = JBTabsPosition.right)) {

		override fun createTabPainterAdapter(): TabPainterAdapter {
			return DefaultTabPainterAdapter(object : JBDefaultTabPainter() {
				override fun getBackgroundColor(): Color = JBColor.LIGHT_GRAY
			})
		}

		override fun createTabLabel(info: TabInfo): TabLabel = MyTabLabel(this, info)
	}

	companion object {
		private val logger = Logger.getInstance(JuliaSciToolWindow::class.java)
		@JvmStatic
		fun getInstance(project: Project): JuliaSciToolWindow =
			ServiceManager.getService(project, JuliaSciToolWindow::class.java) as JuliaSciToolWindow
	}
}

class ImageFigure @JvmOverloads constructor(imageVirtualFile: ImageVirtualFile, private val project: Project, private val key: Any? = null) : Figure, WithDockableContent {
	private val tabInfo: TabInfo

	private fun a(file: ImageVirtualFile, project: Project): TabInfo {
		val plotPanel = MyPlotPanel(file, project)
		val var3 = TabInfo(plotPanel)
		var3.setTabColor(UIUtil.getPanelBackground())
		val var4 = file.image
		val var5 = FigureUtil.fit(var4!!, 64, 48)
		var3.setIcon(ImageIcon(var5))
		var3.setText(" ")
		return var3
	}

	init {
		tabInfo = a(imageVirtualFile, project)
	}

	override fun getTabInfo(): TabInfo = this.tabInfo
	override fun hasSearchKey(): Boolean = this.key != null
	override fun getSearchKey(): Any {
		// TODO use contract
		if (!this.hasSearchKey()) throw RuntimeException("Search key is not defined")
		return this.key!!
	}

	override fun createDockableContent(): DockableContent<*> {
		val image = JBTabsImpl.getComponentImage(this.tabInfo)
		val text = Presentation(this.tabInfo.text)
		val dimension = this.tabInfo.component.preferredSize
		val imageVirtualFile = (this.tabInfo.component as MyPlotPanel).getImageVirtualFile()
		val imageVirtualFileCopy = ImageVirtualFile.makeCopy(imageVirtualFile)
		return DockableEditor(image, imageVirtualFileCopy, text, dimension, false, false)
	}

	class MyPlotPanel(val a: ImageVirtualFile, project: Project) : JPanel(BorderLayout()), WithBinaryContent, Disposable {
		private var fileEditor: FileEditor? = null

		init {
			ApplicationManager.getApplication().invokeLater {
				val var4 = FileEditorProviderManager.getInstance().getProvider("images")
				if (var4 != null) {
					this.fileEditor = var4.createEditor(project, this.a)
					Disposer.register(project, this.fileEditor!!)
					this.add(this.fileEditor!!.component)
				}
			}
			this.background = UIUtil.getEditorPaneBackground()
		}

		fun getImageVirtualFile(): ImageVirtualFile = this.a
		override fun getBytes(): ByteArray = this.a.content
		override fun dispose() {
			Disposer.dispose(this.fileEditor!!)
		}
	}
}

class ImageVirtualFile : BinaryLightVirtualFile {
	var image: BufferedImage? = null

	constructor(simpleFilename: String, width: Int, raw: ByteArray) : super(simpleFilename) {
		this.image = FigureUtil.fromRawBytes(width, raw)
		this.setContent(FigureUtil.toByteArray(this.image!!))
	}

	private constructor(simpleFilename: String, image: BufferedImage) : super(simpleFilename) {
		this.image = image
		this.setContent(FigureUtil.toByteArray(this.image!!))
	}

	private fun setContent(bytes: ByteArray?) {
		if (bytes != null) {
			ApplicationManager.getApplication().invokeLater {
				ApplicationManager.getApplication().runWriteAction {
					try {
						this.setBinaryContent(bytes)
					} catch (e: IOException) {
					}
				}
			}
		}
	}

	companion object {
		@JvmStatic
		fun makeCopy(virtualFile: ImageVirtualFile): ImageVirtualFile {
			return ImageVirtualFile(virtualFile.name, virtualFile.image!!)
		}
	}
}

/**
 * decompile from PyCharm Professional [com.jetbrains.scientific.figures.FigureUtil]
 */
object FigureUtil {
	@JvmStatic
	fun componentImage(component: Component, width: Int = component.width, height: Int = component.height): BufferedImage {
		val image = BufferedImage(width, height, 2)
		val graphics2D = image.createGraphics()
		component.paintAll(graphics2D)
		graphics2D.dispose()
		return image
	}

	@JvmStatic
	fun fit(image: Image, width: Int, height: Int): Image = image.getScaledInstance(JBUI.scale(width), JBUI.scale(height), 4)

	@JvmStatic
	fun componentToByteArray(component: JComponent): ByteArray {
		return if (component is WithBinaryContent) component.getBytes() else componentImage(component).toByteArray()
	}

	@JvmName("toByteArrayExt")
	private fun RenderedImage.toByteArray() = toByteArray(this)

	fun toByteArray(image: RenderedImage): ByteArray {
		val output = ByteArrayOutputStream()
		try {
			ImageIO.write(image, "png", output)
			output.flush()
			val bytes = output.toByteArray()
			output.close()
			return bytes
		} catch (e: IOException) {
			throw IllegalArgumentException("Failed to convert image to byte array", e)
		}
	}

	@JvmStatic
	fun fromRawBytes(width: Int, raw: ByteArray): BufferedImage {
		val wid = raw.size / 3 / width
		val dataBufferByte = DataBufferByte(raw, raw.size)
		val model = ComponentSampleModel(0, width, wid, 3, width * 3, intArrayOf(0, 1, 2))
		val raster = Raster.createRaster(model, dataBufferByte, null as Point?)
		val bufferedImage = BufferedImage(width, wid, 1)
		bufferedImage.data = raster
		return bufferedImage
	}
}

class JuliaSciToolWindowFactory : ToolWindowFactory, DumbAware {
	override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
		JuliaSciToolWindow.getInstance(project).init(toolWindow)
	}
}

class JuliaConsoleView(project: Project, title: String) : LanguageConsoleImpl(project, title, JuliaLanguage.INSTANCE), ObservableConsoleView {
	var lastModified: Long = 0L
	private val myPyHighlighter: JuliaHighlighter
	private val myScheme: EditorColorsScheme

	init {
		historyViewer.putUserData(ConsoleViewUtil.EDITOR_IS_CONSOLE_HISTORY_VIEW, true)
		setUpdateFoldingsEnabled(false)
		myPyHighlighter = JuliaHighlighter
		myScheme = consoleEditor.colorsScheme
	}

	override fun createCenterComponent(): JComponent {
		val centerComponent = super.createCenterComponent()
		historyViewer.settings.additionalLinesCount = 0
		historyViewer.settings.isUseSoftWraps = false
		consoleEditor.gutterComponentEx.background = consoleEditor.backgroundColor
		consoleEditor.gutterComponentEx.revalidate()
		consoleEditor.colorsScheme.setColor(EditorColors.GUTTER_BACKGROUND, consoleEditor.backgroundColor)
		return centerComponent
	}
}

class JuliaVariablesView(project: Project, stackFrame: JuliaVariableStackFrame) : XStandaloneVariablesView(project, JuliaEditorsProvider(), stackFrame)

class JuliaVariableStackFrame(val project: Project, val list: List<JuliaDebugValue>? = null) : XStackFrame() {
	override fun computeChildren(node: XCompositeNode) {
		if (this.list != null) {
			val childrenList = XValueChildrenList()
			list.forEach(childrenList::add)
			node.addChildren(childrenList, true)
		} else {
			val list = project.getUserData(JULIA_VAR_LIST_KEY) ?: return super.computeChildren(node)
			val childrenList = XValueChildrenList()
			list.forEach(childrenList::add)
			node.addChildren(childrenList, true)
		}
	}

	override fun getSourcePosition(): XSourcePosition? {
		list ?: return super.getSourcePosition()
		return list.first().sourceInfo
	}

	override fun customizePresentation(component: ColoredTextContainer) {
		val position = sourcePosition
		if (position == null)
			super.customizePresentation(component)
		else {
			component.append(position.file.name, SimpleTextAttributes.REGULAR_ATTRIBUTES)
			component.append(":${position.line + 1}", SimpleTextAttributes.REGULAR_ATTRIBUTES)
			val icon = if (position.file.path.contains("julia/share"))
				JuliaIcons.JULIA_BIG_ICON
			else
				JuliaIcons.JULIA_ICON
			component.setIcon(icon)
		}
	}
}

class JuliaDebugValue(name: String,
											var type: String = "",
											var value: String = "",
											var container: Boolean = false,
											var parent: JuliaDebugValue? = null,
											var sourceInfo: SourceInfo? = null) : XNamedValue(name) {
	override fun computeSourcePosition(navigatable: XNavigatable) {
		navigatable.setSourcePosition(sourceInfo)
	}

	override fun computePresentation(node: XValueNode, place: XValuePlace) {
		var valuePresentation = value
		val icon =
			when {
				type == "function" -> JuliaIcons.JULIA_FUNCTION_ICON
				type.contains(ARRAY_TYPE) -> {
					try {
						valuePresentation = json.parse(value).asJsonArray.toString()
					} catch (e: Exception) {
						LOG.error(e.message + "when parsing: $value")
					}
					JuliaIcons.JULIA_VARIABLE_ICON
				}
				else -> JuliaIcons.JULIA_VARIABLE_ICON
			}
		// if type is not empty, presentation is `{$type}`, otherwise it won't show bracket pairs.
		val typePresentation = if (type.isEmpty() || type == ARRAY_ITEM_TYPE) null else type
		node.setPresentation(icon, typePresentation, valuePresentation, container)
	}

	override fun computeChildren(node: XCompositeNode) {
		val childrenList = XValueChildrenList()
		when {
			type.contains("EnvDict") || type.contains("Dict{") -> {
				try {
					json.parse(value).asJsonObject.apply {
						keySet().forEach { key ->
							childrenList.add(
								JuliaDebugValue(
									name = key,
									value = this[key].asString,
									parent = this@JuliaDebugValue))
						}
					}
				} catch (e: Exception) {
					LOG.error(e.message + "when parsing: $value")
				}
			}
			type.contains(ARRAY_TYPE) -> {
				try {
					json.parse(value).asJsonArray.forEachIndexed { index, it ->
						val name =
							if (type.endsWith(",2}")) "[Row ${index + 1}]"
							else "[${index + 1}]"
						arrays(it, childrenList, name)
					}
				} catch (e: Exception) {
					LOG.error(e.message + "when parsing: $value")
				}
			}
			type.contains(ARRAY_ITEM_TYPE) -> {
				try {
					json.parse(value).asJsonArray.forEachIndexed { index, it ->
						// Do we need to add `Row` for High dimensional arrays?
						val name = "[${index + 1}]"
						arrays(it, childrenList, name)
					}
				} catch (e: Exception) {
					LOG.error(e.message + "when parsing: $value")
				}
			}
		}
		node.addChildren(childrenList, true)
	}

	private fun arrays(it: JsonElement, childrenList: XValueChildrenList, name: String) {
		childrenList.add(
			JuliaDebugValue(
				name = name,
				type = ARRAY_ITEM_TYPE,
				value = it.toString(),
				container = it.isJsonArray,
				parent = this@JuliaDebugValue))
	}
}

private val json = JsonParser()
const val ARRAY_ITEM_TYPE = "_Intellij_ArrayItem"
const val ARRAY_TYPE = "Array{"
