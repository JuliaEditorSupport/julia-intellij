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

package org.ice1000.julia.lang

import com.intellij.CommonBundle
import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.codeInsight.template.impl.DefaultLiveTemplatesProvider
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.ExactFileNameMatcher
import com.intellij.openapi.fileTypes.FileTypeConsumer
import com.intellij.openapi.fileTypes.FileTypeFactory
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiFile
import icons.JuliaIcons
import org.ice1000.julia.lang.docfmt.DocfmtFileType
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey
import java.util.*

object JuliaFileType : LanguageFileType(JuliaLanguage.INSTANCE) {
	override fun getDefaultExtension() = JULIA_EXTENSION
	override fun getName() = JuliaBundle.message("julia.name")
	override fun getIcon() = JuliaIcons.JULIA_ICON
	override fun getDescription() = JuliaBundle.message("julia.name.description")
}

class JuliaFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, JuliaLanguage.INSTANCE) {
	override fun getFileType() = JuliaFileType
}

class JuliaFileTypeFactory : FileTypeFactory() {
	override fun createFileTypes(consumer: FileTypeConsumer) {
		consumer.consume(JuliaFileType, JULIA_EXTENSION)
		consumer.consume(DocfmtFileType, ExactFileNameMatcher(".$DOCFMT_EXTENSION"))
	}
}

class JuliaContext : TemplateContextType(JULIA_LANGUAGE_NAME) {
	override fun isInContext(file: PsiFile, offset: Int) = file.fileType == JuliaFileType
}

class JuliaLiveTemplateProvider : DefaultLiveTemplatesProvider {
	private companion object DefaultHolder {
		private val DEFAULT = arrayOf("liveTemplates/Julia")
	}

	override fun getDefaultLiveTemplateFiles() = DEFAULT
	override fun getHiddenLiveTemplateFiles(): Array<String>? = null
}

object JuliaBundle {
	@NonNls private const val BUNDLE = "org.ice1000.julia.lang.julia-bundle"
	private val bundle: ResourceBundle by lazy { ResourceBundle.getBundle(BUNDLE) }

	@JvmStatic
	fun message(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any) =
		CommonBundle.message(bundle, key, *params)
}
