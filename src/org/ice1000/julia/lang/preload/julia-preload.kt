package org.ice1000.julia.lang.preload

import com.intellij.openapi.application.PreloadingActivity
import com.intellij.openapi.progress.ProgressIndicator
import org.wso2.lsp4intellij.IntellijLanguageClient
import org.wso2.lsp4intellij.client.languageserver.serverdefinition.RawCommandServerDefinition

class JuliaPreloadingActivity: PreloadingActivity() {
	override fun preload(indicator: ProgressIndicator) {
		indicator.isIndeterminate = true
		IntellijLanguageClient.addServerDefinition(
			RawCommandServerDefinition("jl", arrayOf("julia", "/home/quangio/IdeaProjects/julia-plugin-test/lsp.jl")))
	}
}