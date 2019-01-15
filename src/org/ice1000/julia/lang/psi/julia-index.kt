package org.ice1000.julia.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.*
import com.intellij.psi.stubs.StubIndex.getElements
import com.intellij.psi.tree.IStubFileElementType
import com.intellij.util.io.StringRef
import org.ice1000.julia.lang.JuliaElementType.Companion.TYPE_DECLARATION
import org.ice1000.julia.lang.JuliaLanguage
import org.ice1000.julia.lang.psi.impl.JuliaTypeDeclarationImpl

object JuliaTypeDeclarationIndex : StringStubIndexExtension<JuliaTypeDeclaration>() {
	override fun getKey(): StubIndexKey<String, JuliaTypeDeclaration> = KEY
	override fun getVersion(): Int = 11
	val KEY = StubIndexKey.createIndexKey<String, JuliaTypeDeclaration>("julia.index.type-declaration")
	override fun getAllKeys(project: Project): MutableCollection<String> {
		return StubIndex.getInstance().getAllKeys<String>(JuliaTypeDeclarationIndex.KEY, project)
	}

	fun findElementsByName(
		project: Project,
		target: String,
		scope: GlobalSearchScope = GlobalSearchScope.allScope(project)
	) = getElements(JuliaTypeDeclarationIndex.KEY, target, project, scope, JuliaTypeDeclaration::class.java)
}

interface JuliaTypeDeclarationClassStub : StubElement<JuliaTypeDeclaration> {
	val name: String?
}

class JuliaTypeDeclarationClassStubImpl(override val name: String?, parent: StubElement<*>)
	: JuliaStubBase<JuliaTypeDeclaration>(parent, TYPE_DECLARATION), JuliaTypeDeclarationClassStub

abstract class JuliaStubBase<T : PsiElement>(parent: StubElement<*>?, type: JuliaStubElementType<*, *>)
	: StubBase<T>(parent, type)

class JuliaTypeDeclarationType(debugName: String) : JuliaStubElementType<JuliaTypeDeclarationClassStub, JuliaTypeDeclaration>(debugName) {
	override fun createPsi(stub: JuliaTypeDeclarationClassStub): JuliaTypeDeclaration {
		return JuliaTypeDeclarationImpl(stub, this)
	}

	override fun createStub(typeDeclaration: JuliaTypeDeclaration, stubElement: StubElement<*>): JuliaTypeDeclarationClassStub {
		val name = typeDeclaration.nameIdentifier?.text
		return JuliaTypeDeclarationClassStubImpl(name, stubElement)
	}

	override fun serialize(classStub: JuliaTypeDeclarationClassStub, stubOutputStream: StubOutputStream) {
		stubOutputStream.writeName(classStub.name)
	}

	override fun deserialize(stubInputStream: StubInputStream, stubElement: StubElement<*>): JuliaTypeDeclarationClassStub {
		val nameRef = stubInputStream.readName()
		return JuliaTypeDeclarationClassStubImpl(
			StringRef.toString(nameRef),
			stubElement)
	}

	override fun indexStub(stub: JuliaTypeDeclarationClassStub, indexSink: IndexSink) {
		val name = stub.name ?: return
		indexSink.occurrence(JuliaTypeDeclarationIndex.KEY, name)
	}
}

abstract class JuliaStubElementType<StubT : StubElement<*>, PsiT : PsiElement>(debugName: String)
	: IStubElementType<StubT, PsiT>(debugName, JuliaLanguage.INSTANCE) {

	protected fun createStubIfParentIsStub(node: ASTNode): Boolean {
		val parent = node.treeParent
		val parentType = parent.elementType
		return (parentType is IStubElementType<*, *> && parentType.shouldCreateStub(parent)) ||
			parentType is IStubFileElementType<*>
	}

	override fun getExternalId() = "julia.${super.toString()}"
}