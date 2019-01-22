package org.ice1000.julia.lang.module

import com.intellij.ide.util.projectWizard.*
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.*
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.project.rootManager
import com.intellij.openapi.projectRoots.SdkTypeId
import com.intellij.openapi.roots.ModifiableModelsProvider
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.roots.ui.configuration.*
import icons.JuliaIcons
import org.ice1000.julia.lang.*
import org.ice1000.julia.lang.action.errorNotification
import org.ice1000.julia.lang.module.ui.JuliaSetupSdkWizardStepImpl

class JuliaModuleBuilder : ModuleBuilder(), ModuleBuilderListener {
	init {
		addListener(this)
	}

	lateinit var settings: JuliaSettings
	override fun isSuitableSdkType(sdkType: SdkTypeId?) = true
	override fun getWeight() = 98
	override fun getNodeIcon() = JuliaIcons.JULIA_BIG_ICON
	override fun getModuleType() = JuliaModuleType.instance
	override fun getCustomOptionsStep(context: WizardContext, parentDisposable: Disposable): ModuleWizardStep? {
		parentDisposable.dispose()
		context.projectName = JULIA_DEFAULT_MODULE_NAME
		// doesn't work under 2017.2
		// context.defaultModuleName = JULIA_DEFAULT_MODULE_NAME
		return JuliaSetupSdkWizardStepImpl(this)
	}

	override fun setupRootModel(model: ModifiableRootModel) {
		doAddContentEntry(model)
		if (::settings.isInitialized)
			(model.project.juliaSettings as JuliaProjectSettingsServiceImpl).loadState(settings)
	}

	override fun moduleCreated(module: Module) {
		ApplicationManager.getApplication().runWriteAction {
			val modifiableModel: ModifiableRootModel = ModifiableModelsProvider.SERVICE.getInstance().getModuleModifiableModel(module)
			module.rootManager.modifiableModel.apply {
				inheritSdk()
				contentEntries.firstOrNull()?.apply setupRoot@{
					val project = module.project
					val baseDir = file ?: project.guessProjectDir()
						?: run {
							errorNotification(project, "Created project does not have a root directory.")
							return@setupRoot
						}
					addExcludeFolder(findOrCreate(baseDir, "out", module))
					addSourceFolder(findOrCreate(baseDir, "src", module), false)
				}
				commit()
			}
			ModifiableModelsProvider.SERVICE.getInstance().commitModuleModifiableModel(modifiableModel)
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

