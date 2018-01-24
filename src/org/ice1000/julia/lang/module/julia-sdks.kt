package org.ice1000.julia.lang.module

import com.intellij.openapi.projectRoots.*
import com.intellij.openapi.roots.OrderRootType
import org.ice1000.julia.lang.JULIA_BIG_ICON
import org.ice1000.julia.lang.JuliaBundle
import org.jdom.Element

class JuliaSdkType : SdkType(JuliaBundle.message("julia.name")) {
	override fun getPresentableName() = JuliaBundle.message("julia.modules.sdk.name")
	override fun getIcon() = JULIA_BIG_ICON
	override fun getIconForAddAction() = icon
	override fun isValidSdkHome(sdkHome: String?) = validateJuliaSDK(sdkHome.orEmpty())
	override fun suggestSdkName(s: String?, p1: String?) = JuliaBundle.message("julia.modules.sdk.name")
	override fun suggestHomePath() = TODO()
	override fun createAdditionalDataConfigurable(md: SdkModel, m: SdkModificator) = TODO()
	override fun getVersionString(sdkHome: String?) = versionOf(sdkHome.orEmpty())
	override fun saveAdditionalData(additionalData: SdkAdditionalData, element: Element) = Unit // leave blank
	override fun setupSdkPaths(sdk: Sdk, sdkModel: SdkModel): Boolean {
		val modificator = sdk.sdkModificator
		modificator.sdkAdditionalData = sdk.sdkAdditionalData ?: TODO()
		modificator.versionString = getVersionString(sdk) ?: JuliaBundle.message("julia.modules.sdk.unknown-version")
		sdk.homeDirectory
				?.findChild("imports")
				?.let { modificator.addRoot(it, OrderRootType.CLASSES) }
		modificator.commitChanges()
		return true
	}

	companion object InstanceHolder {
		val instance get() = SdkType.findInstance(JuliaSdkType::class.java)
	}
}

fun versionOf(sdkHome: String): String {
	TODO()
}

fun validateJuliaSDK(sdkHome: String): Boolean {
	TODO("not implemented")
}

