package org.ice1000.julia.lang.module

import com.intellij.openapi.module.ModuleConfigurationEditor
import com.intellij.openapi.roots.ui.configuration.*

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
	var myCompilerOutputEditor: BuildElementsEditor = object : BuildElementsEditor(state) {
	}

	override fun createComponentImpl() = myCompilerOutputEditor.createComponentImpl()!!
	override fun saveData() = myCompilerOutputEditor.saveData()
	override fun getDisplayName() = "Paths"
	override fun getHelpTopic() = myCompilerOutputEditor.helpTopic!!
}
