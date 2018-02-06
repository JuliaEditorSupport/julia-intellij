package org.ice1000.julia.lang.action

import com.intellij.ide.actions.CreateFileAction
import com.intellij.ide.util.projectWizard.AbstractNewProjectStep
import com.intellij.ide.util.projectWizard.ProjectSettingsStepBase
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.psi.*
import icons.JuliaIcons
import org.ice1000.julia.lang.*
import org.ice1000.julia.lang.module.JuliaProjectGenerator
import org.ice1000.julia.lang.module.JuliaSettings
import java.time.LocalDate
import java.util.regex.Pattern
import kotlin.reflect.KProperty

object ActionProperties {
	operator fun getValue(_this : Any?, properties : KProperty<*>) : String{
		val matcher = Pattern.compile("new([A-Z][a-z]+)(Title|Description)").matcher(properties.name)

		return if( matcher.matches() ) {
			JuliaBundle.message("julia.actions.new-${matcher.group(1).toLowerCase()}.${matcher.group(2).toLowerCase()}")
		} else ""
	}
}

val newFileTitle : String by ActionProperties
val newFileDescription : String by ActionProperties

val newModuleTitle : String by ActionProperties
val newModuleDescription : String by ActionProperties

val newTypeTitle : String by ActionProperties
val newTypeDescription : String by ActionProperties

inline fun createFile( name : String , directory : PsiDirectory , template : () -> String = {""} ) : Array<PsiElement> {
	val fixedExtension = when (FileUtilRt.getExtension(name)) {
		JULIA_EXTENSION -> name
		else -> "$name.$JULIA_EXTENSION"
	}
	return arrayOf(
		directory.add(
			PsiFileFactory
				.getInstance(directory.project)
				.createFileFromText(
					fixedExtension, JuliaFileType,
					"""
						|$JULIA_BLOCK_COMMENT_BEGIN
						|# ${FileUtilRt.getNameWithoutExtension(name)}
						|${JuliaBundle.message("julia.actions.new-file.content", System.getProperty("user.name"), LocalDate.now())}
						|$JULIA_BLOCK_COMMENT_END
						|
						|${template()}
					""".trimMargin()
				)
		)
	)
}

class NewJuliaFile : CreateFileAction(
	newFileTitle,
	newFileDescription,
	JuliaIcons.JULIA_ICON) {

	override fun getActionName(directory : PsiDirectory?, s : String?) = newFileTitle
	override fun getDefaultExtension() = JULIA_EXTENSION
	override fun create(name : String, directory : PsiDirectory) : Array<PsiElement> =
		createFile(name, directory)
}

class NewJuliaModule : CreateFileAction (
	newModuleTitle,
	newModuleDescription,
	JuliaIcons.JULIA_MODULE_ICON
) {
	override fun getActionName(directory : PsiDirectory?, newName : String?) : String = newModuleTitle
	override fun getDefaultExtension() : String? = JULIA_EXTENSION
	override fun create(newName : String, directory : PsiDirectory) : Array<PsiElement> =
		createFile(newName, directory) {
			"""
				|module ${FileUtilRt.getNameWithoutExtension(newName)}
				| # TODO
				|end
			""".trimMargin()
		}
}

class NewJuliaType : CreateFileAction (
	newTypeTitle,
	newTypeDescription,
	JuliaIcons.JULIA_TYPE_ICON
) {
	override fun getActionName(directory : PsiDirectory?, newName : String?) : String = newTypeTitle
	override fun getDefaultExtension() : String? = JULIA_EXTENSION
	override fun create(newName : String, directory : PsiDirectory) : Array<PsiElement> =
		createFile(newName, directory) {
			"""
				|type ${FileUtilRt.getNameWithoutExtension(newName)}
				| # TODO
				|end
			""".trimMargin()
		}
}

/**
 * create project for CLion
 * @author zxj5470
 * @date 2018/1/30
 */
class NewJuliaProject : ProjectSettingsStepBase<JuliaSettings>(
	JuliaProjectGenerator(),
	AbstractNewProjectStep.AbstractCallback<JuliaSettings>())
