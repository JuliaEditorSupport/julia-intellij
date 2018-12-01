package org.ice1000.julia.lang.module

import com.intellij.ide.plugins.PluginManager
import com.intellij.notification.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.util.PlatformUtils
import org.ice1000.julia.lang.*
import org.jetbrains.annotations.Nls
import java.io.DataInputStream
import java.io.IOException
import java.net.ServerSocket

class JuliaProjectComponent(private val project: Project) : ProjectComponent {
	var isNightlyNotificationShown = false
	lateinit var socket: ServerSocket
	@Volatile
	private var hold = true

	override fun getComponentName() = "JuliaProjectComponent"
	override fun projectOpened() {
		super.projectOpened()
		val isNightly = PluginManager.getPlugin(PluginId.getId(JULIA_PLUGIN_ID))?.run { '-' in version }.orFalse()
		if (isNightly and !isNightlyNotificationShown) {
			isNightlyNotificationShown = true
			notify(
				JuliaBundle.message("julia.messages.notify.nightly.title"),
				JuliaBundle.message("julia.messages.notify.nightly.content"))
		}
		// other IDEs cannot verify settings...
		if (project.withJulia) {
			if (!validateJulia(project.juliaSettings.settings) && PlatformUtils.isIntelliJ()) {
				notify(
					JuliaBundle.message("julia.messages.notify.invalid-julia.title"),
					JuliaBundle.message("julia.messages.notify.invalid-julia.content"),
					NotificationType.WARNING)
			}
			val useSciView = true
			if (useSciView) {
				socket = ServerSocket(0)
				project.putUserData(JULIA_SCI_PORT_KEY, socket.localPort.toString())
				ApplicationManager.getApplication().executeOnPooledThread {
					while (this.hold) {
						waitAndHandle()
					}
				}
			}
		}
	}

	private fun waitAndHandle() {
		val socketAccept = socket.accept()
		try {
			val inputStream = socketAccept.getInputStream()
			val data = DataInputStream(inputStream)

			try {
				val width = data.readInt()
				val flag = data.readInt()
				val size = data.readInt()
				val raw = ByteArray(size)
				data.readFully(raw)
				val filename = "myplot.png"
				val imageVirtualFile = ImageVirtualFile(filename, width, raw)
				val figure = ImageFigure(imageVirtualFile, project, flag)
				JuliaSciToolWindow.getInstance(project).addFigure(figure)
				val toolWindow = ToolWindowManager.getInstance(project).getToolWindow(JULIA_SCI_VIEW_ID)
				if (toolWindow != null) {
					ApplicationManager.getApplication().invokeLater {
						toolWindow.show(null as Runnable?)
						val content = toolWindow.contentManager.getContent(1)
						if (content != null) {
							toolWindow.contentManager.setSelectedContent(content)
						}
					}
					return
				}
			} catch (e: IOException) {
				e.printStackTrace()
				return
			} finally {
				data.close()
			}
		} finally {
			if (!socketAccept.isClosed) {
				socketAccept.close()
			}
		}
	}

	/** 好想把函数名写成 hugify 。。。 */
	private fun notify(@Nls title: String, @Nls content: String, type: NotificationType = NotificationType.INFORMATION) {
		val notification = NotificationGroup(
			JuliaBundle.message("julia.messages.notify.group"),
			NotificationDisplayType.STICKY_BALLOON,
			true)
			.createNotification(title, content, type, NotificationListener.URL_OPENING_LISTENER)
		Notifications.Bus.notify(notification, project)
	}
}

val Project.withJulia: Boolean
	get() = this.baseDir.getChildrenWithDepth(4).any { it.name.endsWith(".jl") }

fun VirtualFile.getChildrenWithDepth(depth: Int): Sequence<VirtualFile> {
	if (depth == 0) return emptySequence()
	return children.asSequence() + children.asSequence().flatMap { it.getChildrenWithDepth(depth - 1) }
}
