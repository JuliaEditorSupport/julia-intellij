package org.ice1000.julia.lang.action

import com.intellij.openapi.actionSystem.*
import org.ice1000.julia.lang.JuliaFileType
import javax.swing.Icon

abstract class JuliaAction(text: String?, description: String?, icon: Icon?) : AnAction(text, description, icon) {
	override fun update(e: AnActionEvent) {
		e.presentation.isEnabledAndVisible = e.getData(CommonDataKeys.VIRTUAL_FILE)?.fileType == JuliaFileType
	}
}
