package org.ice1000.julia.lang.psi

import com.intellij.navigation.GotoClassContributor
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StringStubIndexExtension
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.stubs.StubIndexKey
import org.ice1000.julia.lang.psi.impl.JuliaSymbolMixin

//又抄了点代码过来x

class JuliaNamedElementIndex : StringStubIndexExtension<JuliaSymbolMixin>() {
	companion object {
		val KEY : StubIndexKey<String, JuliaSymbolMixin> = StubIndexKey.createIndexKey("org.ice1000.julia.lang.psi.JuliaNamedElementIndex")
	}

	override fun getKey() = KEY
}

// TODO this doesn't work at all
abstract class JuliaNavigationContributorBase<T>
protected constructor(
	private val indexKey: StubIndexKey<String, T>,
	private val clazz: Class<T>) : GotoClassContributor where T : NavigationItem, T : JuliaSymbolMixin {
	override fun getNames(project: Project, includeNonProjectItems: Boolean) =
		StubIndex.getInstance().getAllKeys(indexKey, project).toTypedArray()

	override fun getItemsByName(
		name: String,
		pattern: String?,
		project: Project,
		includeNonProjectItems: Boolean): Array<NavigationItem> {
		val scope = if (includeNonProjectItems)
			GlobalSearchScope.allScope(project)
		else
			GlobalSearchScope.projectScope(project)
		return StubIndex.getElements(indexKey, name, project, scope, clazz).toTypedArray()
	}

	override fun getQualifiedName(item: NavigationItem?) = (item as? JuliaSymbol)?.name
	override fun getQualifiedNameSeparator() = "::"
}

class JuliaSymbolNavigationContributor : JuliaNavigationContributorBase<JuliaSymbolMixin>(
	JuliaNamedElementIndex.KEY,
	JuliaSymbolMixin::class.java
)