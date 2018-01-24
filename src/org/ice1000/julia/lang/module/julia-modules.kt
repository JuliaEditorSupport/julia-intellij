package org.ice1000.julia.lang.module

import com.intellij.ide.util.projectWizard.*
import com.intellij.openapi.Disposable
import com.intellij.openapi.module.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SdkTypeId
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.roots.ProjectRootManager
import org.ice1000.julia.lang.*
import java.nio.file.Files
import java.nio.file.Paths

class JuliaModuleBuilder : ModuleBuilder(), ModuleBuilderListener {
	init {
		addListener(this)
	}

	lateinit var sdk: Sdk
	override fun isSuitableSdkType(sdkType: SdkTypeId?) = sdkType is JuliaSdkType
	override fun getWeight() = 98
	override fun getModuleType() = JuliaModuleType.instance
	override fun getCustomOptionsStep(context: WizardContext, parentDisposable: Disposable): ModuleWizardStep? {
		parentDisposable.dispose()
		context.projectName = JULIA_DEFAULT_MODULE_NAME
		TODO("not implemented")
	}

	override fun setupRootModel(model: ModifiableRootModel) {
		model.sdk = sdk
		Files.createDirectories(Paths.get(contentEntryPath, "src"))
		doAddContentEntry(model)
	}

	override fun moduleCreated(module: Module) {
		module.project.projectSdk = sdk
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

var Project.projectSdk
	get() = ProjectRootManager.getInstance(this).projectSdk
	set(value) {
		ProjectRootManager.getInstance(this).projectSdk = value
	}
