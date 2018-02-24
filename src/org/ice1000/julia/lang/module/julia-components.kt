package org.ice1000.julia.lang.module

import com.intellij.ide.plugins.PluginManager
import com.intellij.notification.*
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project
import icons.JuliaIcons
import org.ice1000.julia.lang.JULIA_PLUGIN_ID
import org.ice1000.julia.lang.JuliaBundle
import org.jetbrains.annotations.Nls

class JuliaApplicationComponent(private val project: Project) : ProjectComponent {
	var isNotReleaseNotificationShown = false

	override fun getComponentName() = "JuliaApplicationComponent"
	override fun projectOpened() {
		val isRelease = PluginManager.getPlugin(PluginId.getId(JULIA_PLUGIN_ID))?.run { '-' !in version } == true
		if (!validateJulia(project.juliaSettings.settings)) notify(
			JuliaBundle.message("julia.messages.notify.invalid-julia.title"),
			JuliaBundle.message("julia.messages.notify.invalid-julia.content"),
			NotificationType.WARNING)
		if (!isRelease and !isNotReleaseNotificationShown) {
			isNotReleaseNotificationShown = true
			notify(
				JuliaBundle.message("julia.messages.notify.nightly.title"),
				JuliaBundle.message("julia.messages.notify.nightly.content"))
		}
	}

	/** 好想把函数名写成 hugify 。。。 */
	private fun notify(@Nls title: String, @Nls content: String, type: NotificationType = NotificationType.INFORMATION) {
		val group = NotificationGroup(
			JuliaBundle.message("julia.messages.notify.group"),
			NotificationDisplayType.STICKY_BALLOON,
			false, null, JuliaIcons.JULIA_BIG_ICON)
		val notification = group.createNotification(title, content, type, null)
		Notifications.Bus.notify(notification)
	}
}
