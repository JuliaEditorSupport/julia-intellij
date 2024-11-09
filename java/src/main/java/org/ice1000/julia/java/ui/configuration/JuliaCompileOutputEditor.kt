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

package org.ice1000.julia.java.ui.configuration

import com.intellij.openapi.roots.ui.configuration.BuildElementsEditor
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState
import com.intellij.openapi.roots.ui.configuration.ModuleElementsEditor

class JuliaCompileOutputEditor(state: ModuleConfigurationState) : ModuleElementsEditor(state) {
    var editor: BuildElementsEditor = object : BuildElementsEditor(state) {
    }

    override fun createComponentImpl() = editor.createComponentImpl()
    override fun saveData() = editor.saveData()
    override fun getDisplayName() = "Paths"
    override fun getHelpTopic() = editor.helpTopic
}
