package org.ice1000.julia.lang.action

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.util.ui.JBUI
import org.ice1000.julia.lang.module.JuliaProjectGenerator
import org.ice1000.julia.lang.module.JuliaProjectSettingsStep
import javax.swing.JComponent
import javax.swing.JPanel

/**
 * create project for CLion
 * @author: zxj5470
 * @date: 2018/1/30
 */
object NewJuliaProject : JuliaProjectSettingsStep(JuliaProjectGenerator()){
	override fun actionPerformed(e: AnActionEvent) {
		val panel = createPanel()
		panel.preferredSize = JBUI.size(600, 300)
		JuliaNewProjectDialog(panel).show()
	}
	private class JuliaNewProjectDialog(private val centerPanel: JPanel) : DialogWrapper(true) {
		init {
			title = "New Julia Project"
			init()
		}
		override fun createCenterPanel(): JComponent? = centerPanel
		override fun createSouthPanel(): JComponent? = null
	}
}

