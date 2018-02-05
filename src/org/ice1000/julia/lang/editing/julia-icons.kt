@file:JvmName("Icons")

package org.ice1000.julia.lang.editing

import com.intellij.ide.IconProvider
import com.intellij.psi.PsiElement
import icons.JuliaIcons
import org.ice1000.julia.lang.JuliaFile
import org.ice1000.julia.lang.psi.*
import javax.swing.Icon

class JuliaIconProvider : IconProvider() {
	override fun getIcon(element: PsiElement, flags: Int): Icon? {
		val file = element as? JuliaFile ?: return null
		val statements = file.children
			.firstOrNull { it is JuliaStatements }
			?.let { it as? JuliaStatements } ?: return JuliaIcons.JULIA_ICON
		if (statements.children.size != 1) return JuliaIcons.JULIA_ICON
		return when (statements.firstChild) {
			is JuliaModuleDeclaration -> JuliaIcons.JULIA_MODULE_ICON
			is JuliaTypeDeclaration -> JuliaIcons.JULIA_TYPE_ICON
			else -> JuliaIcons.JULIA_ICON
		}
	}
}
