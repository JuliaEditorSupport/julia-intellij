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

package org.ice1000.julia.lang.psi

import com.intellij.navigation.GotoClassContributor
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StringStubIndexExtension
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.stubs.StubIndexKey
import org.ice1000.julia.lang.psi.impl.JuliaModuleDeclarationMixin

class JuliaModuleNavigationContributor : GotoClassContributor {
	private companion object JuliaModuleIndex : StringStubIndexExtension<JuliaModuleDeclarationMixin>() {
		private val KEY = StubIndexKey.createIndexKey<String, JuliaModuleDeclarationMixin>(JuliaModuleIndex::class.java.name)
		override fun getKey() = KEY
	}

	override fun getNames(project: Project, includeNonProjectItems: Boolean) =
//		emptyArray<String>()
		StubIndex.getInstance().getAllKeys(key, project).toTypedArray()

	override fun getItemsByName(
		name: String,
		pattern: String?,
		project: Project,
		includeNonProjectItems: Boolean): Array<NavigationItem> {
		val scope = if (includeNonProjectItems)
			GlobalSearchScope.allScope(project)
		else
			GlobalSearchScope.projectScope(project)
		return StubIndex.getElements(key, name, project, scope, JuliaModuleDeclarationMixin::class.java).toTypedArray()
	}

	override fun getQualifiedName(item: NavigationItem): String? = (item as? JuliaModuleDeclaration)?.name
	override fun getQualifiedNameSeparator() = "::"
}
