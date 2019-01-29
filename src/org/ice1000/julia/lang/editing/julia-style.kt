package org.ice1000.julia.lang.editing

import com.intellij.application.options.*
import com.intellij.openapi.options.Configurable
import com.intellij.psi.codeStyle.*
import org.ice1000.julia.lang.*


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
		}
	}

	override fun getIndentOptionsEditor() = SmartIndentOptionsEditor()


	override fun getCodeSample(settingsType: LanguageCodeStyleSettingsProvider.SettingsType): String {
		return CodeStyleAbstractPanel.readFromFile(this.javaClass, "preview.jl.template")
	}
}
