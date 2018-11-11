package org.ice1000.julia.lang.psi

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler
import com.intellij.lang.*
import com.intellij.navigation.GotoClassContributor
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.ex.VirtualFileManagerEx
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.source.tree.LightTreeUtil
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.*
import com.intellij.util.CharTable
import org.ice1000.julia.lang.JuliaLanguage
import org.ice1000.julia.lang.psi.impl.JuliaFunctionImpl
import org.ice1000.julia.lang.psi.impl.JuliaModuleDeclarationMixin
import java.io.File

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

class JuliaFunctionStubElementType(debugName: String) : ILightStubElementType<JuliaFunctionStub, JuliaFunction>(debugName, JuliaLanguage.INSTANCE) {
	override fun createPsi(stub: JuliaFunctionStub): JuliaFunction {
		return JuliaFunctionImpl(stub, this)
	}

	override fun serialize(stub: JuliaFunctionStub, dataStream: StubOutputStream) {
		dataStream.writeName(stub.name)
	}

	override fun createStub(psi: JuliaFunction, parentStub: StubElement<*>?): JuliaFunctionStub {
		return JuliaFunctionStub(parentStub, psi.nameIdentifier?.text.toString())
	}

	override fun createStub(tree: LighterAST, node: LighterASTNode?, parentStub: StubElement<*>?): JuliaFunctionStub {
		val keyNode = LightTreeUtil.firstChildOfType(tree, node, JuliaTypes.SYMBOL)
			?: throw IllegalStateException("Attribute without name!")
		val key = intern(tree.charTable, keyNode)
		return JuliaFunctionStub(parentStub, key)
	}

	override fun indexStub(stub: JuliaFunctionStub, sink: IndexSink) {
		sink.occurrence(JuliaFunctionIndex.key, stub.name)
	}

	override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): JuliaFunctionStub {
		val ref = dataStream.readName()
		return JuliaFunctionStub(parentStub, ref!!.string)
	}

	override fun getExternalId(): String {
		return "JuliaFunctionStub"
	}

	fun intern(table: CharTable, node: LighterASTNode): String {
		assert(node is LighterASTTokenNode) { node }
		return table.intern((node as LighterASTTokenNode).text).toString()
	}
}

object JuliaFunctionIndex : StringStubIndexExtension<JuliaFunction>() {
	var KEY: StubIndexKey<String, JuliaFunction>? = null

	init {
		if (KEY == null) {
			KEY = StubIndexKey.createIndexKey("julia.function.index")
		}
	}

	override fun getKey(): StubIndexKey<String, JuliaFunction> {
		return KEY!!
	}

	override fun get(key: String, project: Project, scope: GlobalSearchScope): Collection<JuliaFunction> {
		return StubIndex.getElements(getKey(), key, project, scope, JuliaFunction::class.java)
	}
}

class JuliaFunctionStub(parent: StubElement<*>?, val name: String) : StubBase<JuliaFunction>(parent, JuliaTypes.FUNCTION as IStubElementType<out StubElement<*>, *>?)

/**
 * Goto JuliaFile in a string by Ctrl/Meta + Click
 */
class JuliaGotoDeclarationHandler : GotoDeclarationHandler {
	override fun getGotoDeclarationTargets(sourceElement: PsiElement?, offset: Int, editor: Editor?): Array<PsiElement>? {

		sourceElement ?: return emptyArray()

		if (sourceElement.node?.elementType == JuliaTypes.REGULAR_STRING_PART_LITERAL) {
			val dir = sourceElement.containingFile.containingDirectory
			val url = dir.virtualFile.url + File.separator + sourceElement.text
			val vf = VirtualFileManagerEx.getInstance().findFileByUrl(url) ?: return emptyArray()
			val f = PsiManager.getInstance(sourceElement.project).findFile(vf) ?: return emptyArray()
			return arrayOf(f)
		}
		return emptyArray()
	}

	override fun getActionText(context: DataContext?): String? = null
}