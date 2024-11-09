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

package org.ice1000.julia.lang.editing

import com.intellij.application.options.CodeStyleAbstractConfigurable
import com.intellij.application.options.CodeStyleAbstractPanel
import com.intellij.application.options.SmartIndentOptionsEditor
import com.intellij.application.options.TabbedLanguageCodeStylePanel
import com.intellij.openapi.options.Configurable
import com.intellij.psi.codeStyle.*
import org.ice1000.julia.lang.JuliaBundle
import org.ice1000.julia.lang.JuliaLanguage


class JuliaCodeStyleSettings(settings: CodeStyleSettings)
	: CustomCodeStyleSettings(JuliaBundle.message("julia.style.settings.tag-name"), settings)

class JuliaCodeStyleSettingsProvider : CodeStyleSettingsProvider() {
	override fun getConfigurableDisplayName() = JuliaBundle.message("julia.name")
	override fun createCustomSettings(settings: CodeStyleSettings) = JuliaCodeStyleSettings(settings)
	override fun createSettingsPage(settings: CodeStyleSettings, originalSettings: CodeStyleSettings): Configurable {
		return object : CodeStyleAbstractConfigurable(settings, originalSettings, JuliaBundle.message("julia.name")) {
			override fun createPanel(settings: CodeStyleSettings) = SimpleCodeStyleMainPanel(currentSettings, settings)
		}
	}

	private class SimpleCodeStyleMainPanel(currentSettings: CodeStyleSettings, settings: CodeStyleSettings) :
		TabbedLanguageCodeStylePanel(JuliaLanguage.INSTANCE, currentSettings, settings) {
		override fun initTabs(settings: CodeStyleSettings?) {
			addIndentOptionsTab(settings)
			addSpacesTab(settings)
			addWrappingAndBracesTab(settings)
			addBlankLinesTab(settings)
		}
	}
}

class JuliaStyleSettingsProvider : LanguageCodeStyleSettingsProvider() {
	override fun getLanguage() = JuliaLanguage.INSTANCE

	override fun getDefaultCommonSettings(): CommonCodeStyleSettings =
		CommonCodeStyleSettings(language).apply {
			RIGHT_MARGIN = 100
			ALIGN_MULTILINE_PARAMETERS_IN_CALLS = true
			initIndentOptions().apply {
				INDENT_SIZE = 4
				USE_TAB_CHARACTER = false
				CONTINUATION_INDENT_SIZE = INDENT_SIZE
			}
		}

	override fun customizeSettings(consumer: CodeStyleSettingsCustomizable, settingsType: SettingsType) {
		@Suppress("NON_EXHAUSTIVE_WHEN")
		when (settingsType) {
			SettingsType.SPACING_SETTINGS -> {
				consumer.showStandardOptions("SPACE_AROUND_ASSIGNMENT_OPERATORS")
				consumer.renameStandardOption("SPACE_AROUND_ASSIGNMENT_OPERATORS", "Separator")
			}
			SettingsType.WRAPPING_AND_BRACES_SETTINGS -> {
				consumer.showStandardOptions("WRAPPING_KEEP", "WRAPPING_BRACES")
			}
			SettingsType.BLANK_LINES_SETTINGS -> consumer.showStandardOptions("KEEP_BLANK_LINES_IN_CODE")
//			SettingsType.LANGUAGE_SPECIFIC -> TODO()
			else -> {}
		}
	}

	override fun getIndentOptionsEditor() = SmartIndentOptionsEditor()


	override fun getCodeSample(settingsType: SettingsType): String {
		return CodeStyleAbstractPanel.readFromFile(this.javaClass, "preview.jl.template")
	}
}
