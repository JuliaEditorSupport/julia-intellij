package org.ice1000.julia.lang.module

import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.ide.util.projectWizard.ModuleBuilderListener
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.roots.ProjectRootManager
import org.ice1000.julia.lang.JULIA_MODULE_ID
import javax.swing.Icon

class JuliaModuleBuilder : ModuleBuilder(), ModuleBuilderListener {
	override fun getModuleType(): ModuleType<*> {
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
	override fun getName(): String {
		TODO("not implemented")
	}

	override fun getDescription(): String {
		TODO("not implemented")
	}

	override fun getNodeIcon(isOpened: Boolean): Icon {
		TODO("not implemented")
	}

}

val Project.projectSdk get() = ProjectRootManager.getInstance(this).projectSdk
