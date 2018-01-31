package org.ice1000.julia.lang.editing

import com.intellij.ide.IconProvider
import com.intellij.openapi.util.IconLoader
import com.intellij.psi.PsiElement
import com.intellij.util.PlatformIcons
import org.ice1000.julia.lang.JuliaFile
import org.ice1000.julia.lang.psi.JuliaModuleDeclaration
import org.ice1000.julia.lang.psi.JuliaStatements
import javax.swing.Icon

@JvmField val JULIA_BIG_ICON = IconLoader.getIcon("/icons/julia.png")
@JvmField val JULIA_ICON = IconLoader.getIcon("/icons/julia_file.png")
@JvmField val JULIA_MODULE_ICON: Icon = PlatformIcons.METHOD_ICON

class JuliaIconProvider : IconProvider() {
	override fun getIcon(element: PsiElement, flags: Int): Icon? {
		val file = element as? JuliaFile ?: return null
		val statements = file.children
			.firstOrNull { it is JuliaStatements }
			?.let { it as? JuliaStatements } ?: return null
		if (statements.children.size != 1 && statements.firstChild !is JuliaModuleDeclaration) return null
		return JULIA_MODULE_ICON
	}
}
