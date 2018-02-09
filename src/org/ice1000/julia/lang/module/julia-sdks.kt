package org.ice1000.julia.lang.module

import com.intellij.openapi.projectRoots.*
import icons.JuliaIcons.JULIA_BIG_ICON
import org.ice1000.julia.lang.*
import org.jdom.Element
import java.nio.file.Files
import java.nio.file.Paths

class JuliaSdkType : SdkType(JuliaBundle.message("julia.name")) {
	override fun getPresentableName() = JuliaBundle.message("julia.modules.sdk.name")
	override fun getIcon() = JULIA_BIG_ICON
	override fun getIconForAddAction() = icon
	override fun isValidSdkHome(sdkHome: String?) = validateJuliaSDK(sdkHome.orEmpty())
	override fun suggestSdkName(s: String?, p1: String?) = JuliaBundle.message("julia.modules.sdk.name")
	override fun suggestHomePath() = Paths.get(defaultExePath).parent?.parent?.toString()
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

fun validateJuliaSDK(sdkHome: String) = Files.isExecutable(Paths.get(sdkHome, "bin", "julia")) or
	Files.isExecutable(Paths.get(sdkHome, "bin", "julia.exe"))
