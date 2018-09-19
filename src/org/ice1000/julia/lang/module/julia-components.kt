package org.ice1000.julia.lang.module

import com.intellij.ide.plugins.PluginManager
import com.intellij.notification.*
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project
import com.intellij.util.PlatformUtils
import org.ice1000.julia.lang.*
import org.jetbrains.annotations.Nls

class JuliaProjectComponent(private val project: Project) : ProjectComponent {
	var isNightlyNotificationShown = false

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
		if (!validateJulia(project.juliaSettings.settings) && PlatformUtils.isIntelliJ()) notify(
			JuliaBundle.message("julia.messages.notify.invalid-julia.title"),
			JuliaBundle.message("julia.messages.notify.invalid-julia.content"),
			NotificationType.WARNING)
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
