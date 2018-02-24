package org.ice1000.julia.lang

import com.intellij.ide.plugins.PluginManager
import com.intellij.notification.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.AbstractProjectComponent
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project
import icons.JuliaIcons

class JuliaProjectComponent(project: Project) : AbstractProjectComponent(project) {
	private lateinit var application: JuliaApplicationComponent
	override fun initComponent() {
		application = JuliaApplicationComponent.instance
	}

	override fun projectOpened() {
		super.projectOpened()
		if (!application.isRelease and !application.isNotReleaseNotificationShown) {
			application.isNotReleaseNotificationShown = true
			val group = NotificationGroup("", NotificationDisplayType.STICKY_BALLOON, false, null, JuliaIcons.JULIA_BIG_ICON)
			val notification = group.createNotification("", NotificationType.INFORMATION)
			Notifications.Bus.notify(notification)
		}

	}
}

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
		if (!isRelease and !isNotReleaseNotificationShown) {
			isNotReleaseNotificationShown = true
			val group = NotificationGroup("", NotificationDisplayType.STICKY_BALLOON, false, null, JuliaIcons.JULIA_BIG_ICON)
			val notification = group.createNotification("", NotificationType.INFORMATION)
			Notifications.Bus.notify(notification)
		}
	}
}
