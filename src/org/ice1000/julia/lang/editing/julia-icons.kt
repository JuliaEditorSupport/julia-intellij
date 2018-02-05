package org.ice1000.julia.lang.editing

import com.intellij.ide.IconProvider
import com.intellij.openapi.util.IconLoader
import com.intellij.psi.PsiElement
import org.ice1000.julia.lang.JuliaFile
import org.ice1000.julia.lang.psi.*
import javax.swing.Icon

@JvmField val JULIA_BIG_ICON = IconLoader.getIcon("/icons/julia.png")
@JvmField val JULIA_ICON = IconLoader.getIcon("/icons/julia_file.png")
@JvmField val JOJO_ICON = IconLoader.getIcon("/icons/jojo.png")
@JvmField val JULIA_MODULE_ICON: Icon = IconLoader.getIcon("/icons/module.png")
@JvmField val JULIA_TYPE_ICON: Icon = IconLoader.getIcon("/icons/type.png")

class JuliaIconProvider : IconProvider() {
	override fun getIcon(element: PsiElement, flags: Int): Icon? {
		val file = element as? JuliaFile ?: return null
		val statements = file.children
			.firstOrNull { it is JuliaStatements }
			?.let { it as? JuliaStatements } ?: return JULIA_ICON
		if (statements.children.size != 1) return JULIA_ICON
		return when (statements.firstChild) {
			is JuliaModuleDeclaration -> JULIA_MODULE_ICON
			is JuliaType -> JULIA_TYPE_ICON
			else -> JULIA_ICON
		}
	}
}
