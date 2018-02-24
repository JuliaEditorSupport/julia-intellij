package org.ice1000.julia.lang.module

import com.intellij.ide.plugins.PluginManager
import com.intellij.notification.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.AbstractProjectComponent
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project
import icons.JuliaIcons
import org.ice1000.julia.lang.JULIA_PLUGIN_ID
import org.ice1000.julia.lang.JuliaBundle
import org.jetbrains.annotations.Nls

interface JuliaApplicationComponent : ProjectComponent {
	companion object InstanceHolder {
		val instance: JuliaApplicationComponent
			get() = ApplicationManager.getApplication().getComponent(JuliaApplicationComponent::class.java)
	}

	val isRelease: Boolean
	var isNotReleaseNotificationShown: Boolean
	override fun getComponentName(): String
	override fun initComponent()
	override fun projectOpened()
}

class JuliaApplicationComponentImpl(project: Project) : JuliaApplicationComponent, AbstractProjectComponent(project) {
	override var isNotReleaseNotificationShown = false
	override var isRelease = false

	override fun getComponentName() = "JuliaApplicationComponent"
	override fun initComponent() {
		isRelease = PluginManager.getPlugin(PluginId.getId(JULIA_PLUGIN_ID))?.run { '-' !in version } == true
	}

	override fun projectOpened() {
		super.projectOpened()
		if (!validateJulia(myProject.juliaSettings.settings)) {
			notify(
				JuliaBundle.message("julia.messages.notify.invalid-julia.title"),
				JuliaBundle.message("julia.messages.notify.invalid-julia.content"),
				NotificationType.WARNING)
		}
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
