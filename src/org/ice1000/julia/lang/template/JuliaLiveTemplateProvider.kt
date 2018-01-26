package org.ice1000.julia.lang.template

import com.intellij.codeInsight.template.impl.DefaultLiveTemplatesProvider

class JuliaLiveTemplateProvider : DefaultLiveTemplatesProvider {
	override fun getDefaultLiveTemplateFiles() = arrayOf("liveTemplates/Julia")
	override fun getHiddenLiveTemplateFiles(): Array<String>? = emptyArray()
}