package org.ice1000.julia.lang.psi

import com.intellij.navigation.GotoClassContributor
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.*
import org.ice1000.julia.lang.psi.impl.JuliaModuleDeclarationMixin

class JuliaModuleNavigationContributor : GotoClassContributor {
	private companion object JuliaModuleIndex : StringStubIndexExtension<JuliaModuleDeclarationMixin>() {
		private val KEY = StubIndexKey.createIndexKey<String, JuliaModuleDeclarationMixin>(JuliaModuleIndex::class.java.name)
		override fun getKey() = KEY
	}

	override fun getNames(project: Project, includeNonProjectItems: Boolean) =
		emptyArray<String>()
		// StubIndex.getInstance().getAllKeys(JuliaModuleIndex.key, project)?.toTypedArray()

	override fun getItemsByName(
		name: String,
		pattern: String?,
		project: Project,
		includeNonProjectItems: Boolean): Array<NavigationItem> {
		val scope = if (includeNonProjectItems)
			GlobalSearchScope.allScope(project)
		else
			GlobalSearchScope.projectScope(project)
		return StubIndex.getElements(JuliaModuleIndex.key, name, project, scope, JuliaModuleDeclarationMixin::class.java).toTypedArray()
	}

	override fun getQualifiedName(item: NavigationItem?) = (item as? JuliaModuleDeclaration)?.name
	override fun getQualifiedNameSeparator() = "::"
}
