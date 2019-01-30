package org.ice1000.julia.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.*
import com.intellij.psi.stubs.StubIndex.getElements
import com.intellij.psi.tree.IStubFileElementType
import com.intellij.util.io.StringRef
import org.ice1000.julia.lang.JuliaElementType.Companion.ABSTRACT_TYPE_DECLARATION
import org.ice1000.julia.lang.JuliaElementType.Companion.MODULE_DECLARATION
import org.ice1000.julia.lang.JuliaElementType.Companion.TYPE_DECLARATION
import org.ice1000.julia.lang.JuliaLanguage
import org.ice1000.julia.lang.psi.impl.*

object JuliaTypeDeclarationIndex : StringStubIndexExtension<JuliaTypeDeclaration>() {
	override fun getKey(): StubIndexKey<String, JuliaTypeDeclaration> = KEY
	val KEY = StubIndexKey.createIndexKey<String, JuliaTypeDeclaration>("julia.index.type-declaration")
	override fun getAllKeys(project: Project): MutableCollection<String> {
		return StubIndex.getInstance().getAllKeys<String>(KEY, project)
	}

	fun findElementsByName(
		project: Project,
		target: String,
		scope: GlobalSearchScope = GlobalSearchScope.allScope(project)
	) = getElements(KEY, target, project, scope, JuliaTypeDeclaration::class.java)
}

object JuliaAbstractTypeDeclarationIndex : StringStubIndexExtension<JuliaAbstractTypeDeclaration>() {
	override fun getKey(): StubIndexKey<String, JuliaAbstractTypeDeclaration> = KEY
	val KEY = StubIndexKey.createIndexKey<String, JuliaAbstractTypeDeclaration>("julia.index.abstract-type-declaration")
	override fun getAllKeys(project: Project): MutableCollection<String> {
		return StubIndex.getInstance().getAllKeys<String>(KEY, project)
	}

	fun findElementsByName(
		project: Project,
		target: String,
		scope: GlobalSearchScope = GlobalSearchScope.allScope(project)
	) = getElements(KEY, target, project, scope, JuliaAbstractTypeDeclaration::class.java)
}

object JuliaModuleDeclarationIndex : StringStubIndexExtension<JuliaModuleDeclaration>() {
	override fun getKey(): StubIndexKey<String, JuliaModuleDeclaration> = KEY
	val KEY = StubIndexKey.createIndexKey<String, JuliaModuleDeclaration>("julia.index.module-declaration")
	override fun getAllKeys(project: Project): MutableCollection<String> {
		return StubIndex.getInstance().getAllKeys<String>(KEY, project)
	}

	fun findElementsByName(
		project: Project,
		target: String,
		scope: GlobalSearchScope = GlobalSearchScope.allScope(project)
	) = getElements(KEY, target, project, scope, JuliaModuleDeclaration::class.java)
}

abstract class JuliaStubBase<T : PsiElement>(parent: StubElement<*>?, type: JuliaStubElementType<*, *>)
	: StubBase<T>(parent, type)

interface JuliaTypeDeclarationClassStub : StubElement<JuliaTypeDeclaration> {
	val name: String?
	val superTypeName: String?
}

interface JuliaAbstractTypeDeclarationClassStub : StubElement<JuliaAbstractTypeDeclaration> {
	val name: String?
	val superTypeName: String?
}

interface JuliaModuleDeclarationClassStub : StubElement<JuliaModuleDeclaration> {
	val name: String?
}

class JuliaTypeDeclarationClassStubImpl(parent: StubElement<*>, override val name: String?, override val superTypeName: String? = null)
	: JuliaStubBase<JuliaTypeDeclaration>(parent, TYPE_DECLARATION), JuliaTypeDeclarationClassStub

class JuliaAbstractTypeDeclarationClassStubImpl(parent: StubElement<*>, override val name: String?, override val superTypeName: String? = null)
	: JuliaStubBase<JuliaAbstractTypeDeclaration>(parent, ABSTRACT_TYPE_DECLARATION), JuliaAbstractTypeDeclarationClassStub

class JuliaModuleDeclarationClassStubImpl(override val name: String?, parent: StubElement<*>)
	: JuliaStubBase<JuliaModuleDeclaration>(parent, MODULE_DECLARATION), JuliaModuleDeclarationClassStub

class JuliaAbstractTypeDeclarationType(debugName: String) : JuliaStubElementType<JuliaAbstractTypeDeclarationClassStub, JuliaAbstractTypeDeclaration>(debugName) {
	override fun createPsi(stub: JuliaAbstractTypeDeclarationClassStub): JuliaAbstractTypeDeclaration {
		return JuliaAbstractTypeDeclarationImpl(stub, this)
	}

	override fun createStub(typeDeclaration: JuliaAbstractTypeDeclaration, stubElement: StubElement<*>): JuliaAbstractTypeDeclarationClassStub {
		val name = typeDeclaration.nameIdentifier?.text
		return JuliaAbstractTypeDeclarationClassStubImpl(stubElement, name)
	}

	override fun serialize(classStub: JuliaAbstractTypeDeclarationClassStub, stubOutputStream: StubOutputStream) {
		stubOutputStream.writeName(classStub.name)
	}

	override fun deserialize(stubInputStream: StubInputStream, stubElement: StubElement<*>): JuliaAbstractTypeDeclarationClassStub {
		val nameRef = stubInputStream.readName()
		return JuliaAbstractTypeDeclarationClassStubImpl(
			stubElement,
			StringRef.toString(nameRef)
		)
	}

	override fun indexStub(stub: JuliaAbstractTypeDeclarationClassStub, indexSink: IndexSink) {
		val name = stub.name ?: return
		indexSink.occurrence(JuliaAbstractTypeDeclarationIndex.KEY, name)
	}
}

class JuliaTypeDeclarationType(debugName: String) : JuliaStubElementType<JuliaTypeDeclarationClassStub, JuliaTypeDeclaration>(debugName) {
	override fun createPsi(stub: JuliaTypeDeclarationClassStub): JuliaTypeDeclaration {
		return JuliaTypeDeclarationImpl(stub, this)
	}

	override fun createStub(typeDeclaration: JuliaTypeDeclaration, stubElement: StubElement<*>): JuliaTypeDeclarationClassStub {
		val name = typeDeclaration.nameIdentifier?.text
		return JuliaTypeDeclarationClassStubImpl(stubElement, name)
	}

	override fun serialize(classStub: JuliaTypeDeclarationClassStub, stubOutputStream: StubOutputStream) {
		stubOutputStream.writeName(classStub.name)
	}

	override fun deserialize(stubInputStream: StubInputStream, stubElement: StubElement<*>): JuliaTypeDeclarationClassStub {
		val nameRef = stubInputStream.readName()
		return JuliaTypeDeclarationClassStubImpl(
			stubElement,
			StringRef.toString(nameRef)
		)
	}

	override fun indexStub(stub: JuliaTypeDeclarationClassStub, indexSink: IndexSink) {
		val name = stub.name ?: return
		indexSink.occurrence(JuliaTypeDeclarationIndex.KEY, name)
	}
}

class JuliaModuleDeclarationType(debugName: String) : JuliaStubElementType<JuliaModuleDeclarationClassStub, JuliaModuleDeclaration>(debugName) {
	override fun createPsi(stub: JuliaModuleDeclarationClassStub): JuliaModuleDeclaration {
		return JuliaModuleDeclarationImpl(stub, this)
	}

	override fun createStub(typeDeclaration: JuliaModuleDeclaration, stubElement: StubElement<*>): JuliaModuleDeclarationClassStub {
		val name = typeDeclaration.nameIdentifier?.text
		return JuliaModuleDeclarationClassStubImpl(name, stubElement)
	}

	override fun serialize(classStub: JuliaModuleDeclarationClassStub, stubOutputStream: StubOutputStream) {
		stubOutputStream.writeName(classStub.name)
	}

	override fun deserialize(stubInputStream: StubInputStream, stubElement: StubElement<*>): JuliaModuleDeclarationClassStub {
		val nameRef = stubInputStream.readName()
		return JuliaModuleDeclarationClassStubImpl(
			StringRef.toString(nameRef),
			stubElement)
	}

	override fun indexStub(stub: JuliaModuleDeclarationClassStub, indexSink: IndexSink) {
		val name = stub.name ?: return
		indexSink.occurrence(JuliaModuleDeclarationIndex.KEY, name)
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