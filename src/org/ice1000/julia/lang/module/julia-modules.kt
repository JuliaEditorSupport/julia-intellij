package org.ice1000.julia.lang.module

import com.intellij.ide.util.projectWizard.*
import com.intellij.openapi.Disposable
import com.intellij.openapi.module.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.roots.ProjectRootManager
import org.ice1000.julia.lang.*

class JuliaModuleBuilder : ModuleBuilder(), ModuleBuilderListener {
	init {
		addListener(this)
	}

	lateinit var sdk: Sdk
	override fun getWeight() = 98
	override fun getModuleType() = JuliaModuleType.instance
	override fun getCustomOptionsStep(context: WizardContext?, parentDisposable: Disposable?): ModuleWizardStep? {
		TODO("not implemented")
	}

	override fun setupRootModel(modifiableRootModel: ModifiableRootModel) {
		TODO("not implemented")
	}

	override fun moduleCreated(module: Module) {
		TODO("not implemented")
	}
}

class JuliaModuleType : ModuleType<JuliaModuleBuilder>(JULIA_MODULE_ID) {
	override fun createModuleBuilder() = JuliaModuleBuilder()
	override fun getName() = JuliaBundle.message("julia.name")
	override fun getDescription() = JuliaBundle.message("julia.modules.type")
	override fun getNodeIcon(isOpened: Boolean) = JULIA_BIG_ICON

	companion object InstanceHolder {
		@JvmStatic val instance get() = ModuleTypeManager.getInstance().findByID(JULIA_MODULE_ID) as JuliaModuleType
	}
}

val Project.projectSdk get() = ProjectRootManager.getInstance(this).projectSdk
