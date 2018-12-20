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

	override fun getCodeSample(settingsType: SettingsType) =
		when (settingsType) {
			SettingsType.INDENT_SETTINGS -> """struct A
    v::Int
    function A(a, b)
        new(a + b)
    end
end"""
			SettingsType.BLANK_LINES_SETTINGS -> """function a()

end


Blank=1


Blank=2


"""
			else ->
				"""#=
		      BLOCK COMMENT
		   =#
		   module <moduleName>ice1000</moduleName>
		   NaN32 # NaN32 (Float32)
		   (1 + 3.2)::Float64
		   IntOrString = Union{Int, AbstractString}
		   div(5, 2) # => 2 # for a truncated result, use div
		   <macroRef>@printf</macroRef> "%d is less than %f" 4.5 5.3 # 5 is less than 5.300000
		   assertTrue("1 + 2 = 3" == "1 + 2 = $(1 + 2)")
		   [1, 2, 3][2] # => 2, index start from 1
		   try
		       println("Hello<stringEscape>\n</stringEscape>World <stringEscapeInvalid>'\xjb'</stringEscapeInvalid>" +
		         '<charEscapeInvalid>\x</charEscapeInvalid>' + '<charEscape>\a<charEscape>')
		       some_other_var # => Unresolved reference: some_other_var
		   catch exception
		       println(exception)
		   end
		   abstract type <abstractTypeName>Cat</abstractTypeName> <: Animals end
		   primitive type <primitiveTypeName>Bool</primitiveTypeName> <: Integer 8 end
		   type <typeName>Dog</typeName> <: Animals
		       age::Int64
		   end
		   macro <macroName>assert</macroName>(condition)
		       return :( ${JULIA_STRING_DOLLAR}ex ? nothing : throw(AssertionError(${JULIA_STRING_DOLLAR}{'$'}(string(ex)))) )
		   end
		   function <functionName>fib</functionName>(n)
		       return n ≤ 2 ? 1 : fib(n - 1) + fib(n - 2)
		   end
		   for (k, v) in Dict("dog"=>"mammal", "cat"=>"mammal", "mouse"=>"mammal")
		       println("${JULIA_STRING_DOLLAR}k is a ${JULIA_STRING_DOLLAR}v")
		   end
		   x = 0
		   while x ≤ 4
		       println(x)
		       x += 1
		   end
		   end""".trimIndent()
		}
}
