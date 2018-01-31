package org.ice1000.julia.lang.action

import com.intellij.CommonBundle
import com.intellij.ide.actions.CreateFileAction
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.psi.*
import org.ice1000.julia.lang.*
import org.ice1000.julia.lang.editing.JULIA_ICON
import org.ice1000.julia.lang.module.JuliaSdkType
import org.ice1000.julia.lang.module.projectSdk
import java.time.LocalDate

class NewJuliaFile : CreateFileAction(
	JuliaBundle.message("julia.actions.new-file.title"),
	JuliaBundle.message("julia.actions.new-file.description"),
	JULIA_ICON) {
	override fun getActionName(directory: PsiDirectory?, s: String?) =
		JuliaBundle.message("julia.actions.new-file.title")

	override fun isAvailable(context: DataContext) =
		context.getData(CommonDataKeys.PROJECT)?.projectSdk?.sdkType is JuliaSdkType

	override fun getErrorTitle(): String = CommonBundle.getErrorTitle()
	override fun getDefaultExtension() = JULIA_EXTENSION
	override fun create(name: String, directory: PsiDirectory): Array<PsiElement> {
		val fixedExtension = when (FileUtilRt.getExtension(name)) {
			JULIA_EXTENSION -> name
			else -> "$name.$JULIA_EXTENSION"
		}
		return arrayOf(directory.add(PsiFileFactory
			.getInstance(directory.project)
			.createFileFromText(fixedExtension, JuliaFileType, """$JULIA_BLOCK_COMMENT_BEGIN
# ${FileUtilRt.getNameWithoutExtension(name)}
${JuliaBundle.message("julia.actions.new-file.content", System.getProperty("user.name"), LocalDate.now())}
$JULIA_BLOCK_COMMENT_END

""")))
	}
}

