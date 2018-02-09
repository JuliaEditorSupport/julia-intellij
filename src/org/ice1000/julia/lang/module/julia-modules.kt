package org.ice1000.julia.lang.module

import com.intellij.ide.util.projectWizard.*
import com.intellij.openapi.Disposable
import com.intellij.openapi.module.*
import com.intellij.openapi.projectRoots.SdkTypeId
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.roots.ui.configuration.*
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.util.PlatformUtils
import icons.JuliaIcons
import org.ice1000.julia.lang.*
import java.nio.file.Files
import java.nio.file.Paths

class JuliaModuleBuilder : ModuleBuilder() {
	lateinit var settings: JuliaSettings
	override fun isSuitableSdkType(sdkType: SdkTypeId?) = true
	override fun getWeight() = 98
	override fun getNodeIcon() = JuliaIcons.JULIA_BIG_ICON
	override fun getModuleType() = JuliaModuleType.instance
	override fun getCustomOptionsStep(context: WizardContext, parentDisposable: Disposable): ModuleWizardStep? {
		parentDisposable.dispose()
		context.projectName = JULIA_DEFAULT_MODULE_NAME
		context.defaultModuleName = JULIA_DEFAULT_MODULE_NAME
		return JuliaSetupSdkWizardStepImpl(this)
	}

	override fun setupRootModel(model: ModifiableRootModel) {
		if (::settings.isInitialized) model.project.juliaSettings.settings = settings
		model.inheritSdk()
		val srcPath = Paths.get(contentEntryPath, "src").toAbsolutePath()
		Files.createDirectories(srcPath)
		//Idea Only
		if (PlatformUtils.isIntelliJ()) {
			val sourceRoot = LocalFileSystem
				.getInstance()
				.refreshAndFindFileByPath(FileUtil.toSystemIndependentName(srcPath.toString()))
				?: return
			doAddContentEntry(model)?.addSourceFolder(sourceRoot, false)
		} else {
			//other Platform just doAddContentEntry
			doAddContentEntry(model)
		}
	}
}

class JuliaModuleType : ModuleType<JuliaModuleBuilder>(JULIA_MODULE_ID) {
	override fun createModuleBuilder() = JuliaModuleBuilder()
	override fun getName() = JuliaBundle.message("julia.name")
	override fun getDescription() = JuliaBundle.message("julia.modules.type")
	override fun getNodeIcon(isOpened: Boolean) = JuliaIcons.JULIA_BIG_ICON

	companion object InstanceHolder {
		@JvmStatic val instance get() = ModuleTypeManager.getInstance().findByID(JULIA_MODULE_ID) as JuliaModuleType
	}
}

/**
 * Module Configure
 * Inspired by Haskell plugin
 * @author: zxj5470
 * @date: 2018/1/29
 */
class JuliaModuleConfigEditor : ModuleConfigurationEditorProvider {

	override fun createEditors(state: ModuleConfigurationState): Array<ModuleConfigurationEditor> {
		val module = state.rootModel?.module ?: return emptyArray()
		return arrayOf(ContentEntriesEditor(module.name, state),
			JuliaCompileOutputEditor(state))
	}
}

class JuliaCompileOutputEditor(state: ModuleConfigurationState) : ModuleElementsEditor(state) {
	var editor: BuildElementsEditor = object : BuildElementsEditor(state) {
	}

	override fun createComponentImpl() = editor.createComponentImpl()
	override fun saveData() = editor.saveData()
	override fun getDisplayName() = "Paths"
	override fun getHelpTopic() = editor.helpTopic
}

