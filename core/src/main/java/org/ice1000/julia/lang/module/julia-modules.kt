/*
 *     Julia language support plugin for Intellij-based IDEs.
 *     Copyright (C) 2024 julia-intellij contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.ice1000.julia.lang.module

import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.ide.util.projectWizard.ModuleBuilderListener
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.module.ModuleTypeManager
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.project.rootManager
import com.intellij.openapi.projectRoots.SdkTypeId
import com.intellij.openapi.roots.ModifiableModelsProvider
import com.intellij.openapi.roots.ModifiableRootModel
import icons.JuliaIcons
import org.ice1000.julia.lang.JULIA_DEFAULT_MODULE_NAME
import org.ice1000.julia.lang.JULIA_MODULE_ID
import org.ice1000.julia.lang.JuliaBundle
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
			val modifiableModel: ModifiableRootModel = ModifiableModelsProvider.getInstance().getModuleModifiableModel(module)
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
			ModifiableModelsProvider.getInstance().commitModuleModifiableModel(modifiableModel)
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

