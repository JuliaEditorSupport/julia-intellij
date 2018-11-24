@file:Suppress("UndesirableClassUsage")

package org.ice1000.julia.lang.module

import com.google.common.base.Preconditions
import com.intellij.icons.AllIcons.Actions
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileChooser.FileChooserFactory
import com.intellij.openapi.fileChooser.FileSaverDescriptor
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.ex.FileEditorProviderManager
import com.intellij.openapi.fileEditor.impl.EditorTabbedContainer.DockableEditor
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.IdeFocusManager
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.testFramework.BinaryLightVirtualFile
import com.intellij.ui.JBColor
import com.intellij.ui.SimpleColoredComponent
import com.intellij.ui.awt.RelativePoint
import com.intellij.ui.awt.RelativeRectangle
import com.intellij.ui.content.ContentFactory.SERVICE
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
import com.intellij.ui.tabs.impl.DefaultEditorTabsPainter
import com.intellij.ui.tabs.impl.JBEditorTabs
import com.intellij.ui.tabs.impl.JBTabsImpl
import com.intellij.ui.tabs.impl.TabLabel
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import java.awt.*
import java.awt.event.MouseEvent
import java.awt.image.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import javax.imageio.ImageIO
import javax.swing.Icon
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

class JuliaSciToolWindow(private val d: Project) : JPanel(BorderLayout()), DumbAware {
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
		this.tabs = JuliaSciToolWindow.MyTabs(this.d)
		this.tabs.tabsPosition = JBTabsPosition.right
		this.tabs.setPopupGroup(DefaultActionGroup(SaveAsFileAction(), CloseAllPlotsAction()), "unknown", true)
		this.tabs.isTabDraggingEnabled = true
		this.tabs.addListener(object : TabsListener {
			override fun tabRemoved(tabToRemove: TabInfo) {
				this@JuliaSciToolWindow.map.remove(tabToRemove)
				val jComponent = tabToRemove.component
				if (jComponent is Disposable) {
					Disposer.dispose(jComponent as Disposable)
				}

			}
		})
		this.add(this.tabs)
	}

	fun init(toolWindow: ToolWindow) {
		val var4 = SERVICE.getInstance()
		val var5 = var4.createContent(this, "Plots", false)
		var5.isCloseable = false
		toolWindow.contentManager.addContent(var5)
		if (this.dockContainer == null) {
			this.dockContainer = MyDockContainer(toolWindow)
			Disposer.register(this.d, this.dockContainer!!)
			DockManager.getInstance(this.d).register(this.dockContainer)
		}
	}

	fun addDockableContentFigureFactory(factory: DockableContentFigureFactory) {
		this.list.add(factory)
	}

	fun addFigure(figure: Figure) {
		val tabInfo = figure.getTabInfo()
		tabInfo.setTabLabelActions(DefaultActionGroup(ClosePlotAction(tabInfo)), "unknown")
		if (figure is WithDockableContent) {
			tabInfo.dragOutDelegate = MyDragOutDelegate(figure as WithDockableContent)
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

	inner class MyDockContainer constructor(private val a: ToolWindow) : DockContainer {

		override fun getAcceptArea(): RelativeRectangle = RelativeRectangle(this.a.component)

		override fun getAcceptAreaFallback(): RelativeRectangle = this.acceptArea

		override fun getContentResponse(content: DockableContent<*>, point: RelativePoint): ContentResponse =
			if (this.factory(content) != null) ContentResponse.ACCEPT_MOVE else ContentResponse.DENY

		override fun getContainerComponent(): JComponent = this.a.component

		override fun add(content: DockableContent<*>, dropTarget: RelativePoint) {
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
		override fun showNotify() {}
		override fun hideNotify() {}
		private fun factory(content: DockableContent<*>): DockableContentFigureFactory? {
			return this@JuliaSciToolWindow.list.firstOrNull { it.isApplicable(content) }
		}
	}

	inner class MyDragOutDelegate constructor(private val a: WithDockableContent) : DragOutDelegate {
		private var dragSession: DragSession? = null

		override fun dragOutStarted(mouseEvent: MouseEvent, info: TabInfo) {
			val tabInfo = info.previousSelection
			info.isHidden = true
			if (tabInfo != null) {
				this@JuliaSciToolWindow.tabs.select(tabInfo, true)
			}

			this.dragSession = this.getDockManager().createDragSession(mouseEvent, this.a.createDockableContent())
		}

		private fun getDockManager(): DockManager {
			return DockManager.getInstance(this@JuliaSciToolWindow.d)
		}

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
			if (this.dragSession != null) {
				this.dragSession!!.cancel()
			}

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
			val virtualFileWrapper = fileSaverDialog.save(project.baseDir, "myplot")

			try {
				if (virtualFileWrapper != null) {
					val file = virtualFileWrapper.file
					Files.write(Paths.get(file.path), bytes)
				}
			} catch (e: IOException) {
				JuliaSciToolWindow.logger.warn("Failed to save image " + e.message)
			}
		}
	}

	private inner class CloseAllPlotsAction constructor() : AnAction("Close All Plots", "Close all plot", null as Icon?) {

		override fun actionPerformed(e: AnActionEvent) {
			this@JuliaSciToolWindow.tabs.removeAllTabs()
		}
	}

	private inner class ClosePlotAction constructor(private val a: TabInfo) : AnAction("Close Plot", "Close selected plot", Actions.Close) {

		override fun actionPerformed(e: AnActionEvent) {
			this@JuliaSciToolWindow.tabs.removeTab(this.a)
		}
	}

	private class MyTabLabel constructor(tabs: JBTabsImpl, info: TabInfo) : TabLabel(tabs, info) {
		init {
			val jComponent = this.labelComponent
			if (jComponent is SimpleColoredComponent) {
				jComponent.isIconOnTheRight = true
			}

		}

		override fun getPreferredSize(): Dimension {
			return Dimension(JBUI.scale(100), JBUI.scale(80))
		}
	}

	private class MyTabs constructor(project: Project) : JBEditorTabs(project, ActionManager.getInstance(), IdeFocusManager.findInstance(), project) {
		init {
			this.myDefaultPainter = object : DefaultEditorTabsPainter(this) {
				override fun getBackgroundColor(): Color {
					return JBColor.LIGHT_GRAY
				}
			}
		}

		override fun createTabLabel(info: TabInfo): TabLabel {
			return JuliaSciToolWindow.MyTabLabel(this, info)
		}
	}

	companion object {
		private val h = 80
		private val logger = Logger.getInstance(JuliaSciToolWindow::class.java)
		@JvmStatic
		fun getInstance(project: Project): JuliaSciToolWindow {
			return ServiceManager.getService(project, JuliaSciToolWindow::class.java) as JuliaSciToolWindow
		}
	}
}

class ImageFigure @JvmOverloads constructor(imageVirtualFile: ImageVirtualFile, private val project: Project, private val key: Any? = null) : Figure, WithDockableContent {
	private val tabInfo: TabInfo

	private fun a(file: ImageVirtualFile, project: Project): TabInfo {
		val var2 = ImageFigure.MyPlotPanel(file, project)
		val var3 = TabInfo(var2)
		var3.tabColor = UIUtil.getPanelBackground()
		val var4 = file.image
		val var5 = FigureUtil.fit(var4!!, 64, 48)
		var3.icon = ImageIcon(var5)
		var3.text = " "
		return var3
	}

	init {
		this.tabInfo = a(imageVirtualFile, project)
	}

	override fun getTabInfo(): TabInfo {
		return this.tabInfo
	}

	override fun hasSearchKey(): Boolean {
		return this.key != null
	}

	override fun getSearchKey(): Any {
		Preconditions.checkState(this.hasSearchKey(), "Search key is not defined")
		return this.key!!
	}

	override fun createDockableContent(): DockableContent<*> {
		val image = JBTabsImpl.getComponentImage(this.tabInfo)
		val text = Presentation(this.tabInfo.text)
		val dimension = this.tabInfo.component.preferredSize
		val imageVirtualFile = (this.tabInfo.component as ImageFigure.MyPlotPanel).getImageVirtualFile()
		val imageVirtualFileCopy = ImageVirtualFile.makeCopy(imageVirtualFile)
		return DockableEditor(this.project, image, imageVirtualFileCopy, text, dimension, false)
	}

	class MyPlotPanel constructor(val a: ImageVirtualFile, project: Project) : JPanel(BorderLayout()), WithBinaryContent, Disposable {
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
			Disposer.dispose(this.a)
			Disposer.dispose(this.fileEditor!!)
		}
	}
}

class ImageVirtualFile : BinaryLightVirtualFile, Disposable {
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

	override fun dispose() {
		this.image = null
	}

	companion object {
		@JvmStatic
		fun makeCopy(virtualFile: ImageVirtualFile): ImageVirtualFile {
			return ImageVirtualFile(virtualFile.name, virtualFile.image!!)
		}
	}
}

/**
 * decompile from [com.jetbrains.scientific.figures.FigureUtil]
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
	fun fit(image: Image, width: Int, height: Int): Image {
		return image.getScaledInstance(JBUI.scale(width), JBUI.scale(height), 4)
	}

	@JvmStatic
	fun componentToByteArray(component: JComponent): ByteArray {
		return if (component is WithBinaryContent) {
			component.getBytes()
		} else {
			componentImage(component).toByteArray()
		}
	}

	@JvmName("toByteArrayExt")
	private fun RenderedImage.toByteArray() = FigureUtil.toByteArray(this)

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