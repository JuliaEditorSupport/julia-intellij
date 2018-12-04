package org.ice1000.julia.lang.module

import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.*
import com.intellij.openapi.roots.AdditionalLibraryRootsProvider
import com.intellij.openapi.roots.SyntheticLibrary
import com.intellij.openapi.roots.libraries.*
import com.intellij.openapi.roots.libraries.ui.LibraryEditorComponent
import com.intellij.openapi.roots.libraries.ui.LibraryPropertiesEditor
import com.intellij.openapi.util.Condition
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.xmlb.XmlSerializerUtil
import icons.JuliaIcons
import org.ice1000.julia.lang.*
import org.jdom.Element
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import javax.swing.Icon
import javax.swing.JComponent

///** @deprecated */
//@Deprecated("No longer used")
class JuliaSdkType : SdkType(JuliaBundle.message("julia.name")) {
	override fun getPresentableName() = JuliaBundle.message("julia.modules.sdk.name")
	override fun getIcon() = JuliaIcons.JULIA_BIG_ICON
	override fun getIconForAddAction() = JuliaIcons.ADD_SDK_ICON
	override fun isValidSdkHome(sdkHome: String?) = validateJuliaSDK(sdkHome.orEmpty())
	override fun suggestSdkName(s: String?, p1: String?) = JuliaBundle.message("julia.modules.sdk.name")
	override fun suggestHomePath() = Paths.get(juliaPath).parent?.parent?.toString()
	override fun getDownloadSdkUrl() = JULIA_WEBSITE
	override fun createAdditionalDataConfigurable(md: SdkModel, m: SdkModificator): AdditionalDataConfigurable? = null
	override fun getVersionString(sdkHome: String?) = versionOf(sdkHome.orEmpty())
	override fun saveAdditionalData(additionalData: SdkAdditionalData, element: Element) = Unit // leave blank
	override fun setupSdkPaths(sdk: Sdk, sdkModel: SdkModel): Boolean {
		val modificator = sdk.sdkModificator
		modificator.versionString = getVersionString(sdk) ?: JuliaBundle.message("julia.modules.sdk.unknown-version")
		modificator.commitChanges()
		return true
	}

	companion object InstanceHolder {
		val instance get() = SdkType.findInstance(JuliaSdkType::class.java)
	}
}

fun validateJuliaSDK(sdkHome: String) = Files.isExecutable(Paths.get(sdkHome, "bin", "julia")) ||
	Files.isExecutable(Paths.get(sdkHome, "bin", "julia.exe"))

class JuliaLibraryProperties : LibraryProperties<JuliaLibraryProperties>() {
	var map: Map<String, List<String>> = TreeMap()
	override fun getState(): JuliaLibraryProperties = this
	override fun loadState(state: JuliaLibraryProperties) {
		XmlSerializerUtil.copyBean(state, this)
	}

	override fun equals(other: Any?): Boolean = other is JuliaLibraryProperties && map == other.map
	override fun hashCode(): Int = map.hashCode()
}

class JuliaLibraryType : LibraryType<JuliaLibraryProperties>(LIBRARY_KIND) {
	override fun createPropertiesEditor(editorComponent: LibraryEditorComponent<JuliaLibraryProperties>): LibraryPropertiesEditor? = null
	override fun createNewLibrary(parentComponent: JComponent, contextDirectory: VirtualFile?, project: Project): NewLibraryConfiguration? = null
	override fun getCreateActionName(): String? = null

	companion object {
		@JvmField
		val LIBRARY_KIND: PersistentLibraryKind<JuliaLibraryProperties> = object : PersistentLibraryKind<JuliaLibraryProperties>("JuliaLibraryType") {
			override fun createDefaultProperties(): JuliaLibraryProperties {
				return JuliaLibraryProperties()
			}
		}
	}
}

open class JuliaSdkLibraryPresentationProvider protected constructor() : LibraryPresentationProvider<DummyLibraryProperties>(KIND) {
	override fun getIcon(properties: DummyLibraryProperties?): Icon? = JuliaIcons.JULIA_BIG_ICON

	override fun detect(classesRoots: List<VirtualFile>): DummyLibraryProperties? =
		if (findJuliaRoot(classesRoots) == null) null else DummyLibraryProperties.INSTANCE

	companion object {
		private val KIND = LibraryKind.create("Julia")

		fun findJuliaRoot(classesRoots: List<VirtualFile>): VirtualFile? =
			classesRoots.find { root ->
				root.isInLocalFileSystem && root.isDirectory && root.findChild("share") != null
			}
	}
}

class JuliaStdLibraryProvider : AdditionalLibraryRootsProvider() {
	override fun getAdditionalProjectLibraries(project: Project): Collection<StdLibrary> {
		if (!project.withJulia) return emptyList()

		val base = project.juliaSettings.settings.basePath
		val version = project.juliaSettings.settings.version
		val list = linkedSetOf<StdLibrary>()

		val sharePath = Paths.get(base, "..").toFile()
		val dir = VfsUtil.findFileByIoFile(sharePath, true)
		if (dir != null) list.add(StdLibrary("Julia $version", dir))

		try {
//			packages
		} finally {
			return list
		}
	}

	class StdLibrary(private val name: String,
									 private val root: VirtualFile) : SyntheticLibrary(), ItemPresentation {
		private val roots = root.children.asList()
		override fun hashCode() = root.hashCode()
		override fun equals(other: Any?): Boolean = other is StdLibrary && other.root == root
		override fun getSourceRoots() = roots
		override fun getLocationString() = ""
		override fun getIcon(p0: Boolean): Icon = JuliaIcons.JULIA_BIG_ICON
		override fun getPresentableText() = name
		override fun getExcludedRoots(): MutableSet<VirtualFile> {
			return roots.asSequence().filter { !it.isDirectory && !it.name.endsWith(".jl") }.toMutableSet()
		}

		override fun getExcludeFileCondition(): Condition<VirtualFile>? {
			return Condition {
				// if true, excluded.
				// ignore the directory name with some dirName
				(it.isDirectory && it.name in arrayOf("test", "docs", "deps"))
					|| !it.name.endsWith(".jl")
			}
		}
	}
}