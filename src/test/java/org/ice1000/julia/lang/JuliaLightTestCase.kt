/*
 *     Julia language support plugin for Intellij-based IDEs.
 *     Copyright (C) 2024 julia-intellij contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.ice1000.julia.lang

import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

private const val PER_TEST_CODE = "<per test code>"

@RunWith(JUnit4::class)
abstract class JuliaLightTestCase : BasePlatformTestCase() {
	val fileExtension: String = "jl"

	val perTestCode: String = "Implement getPerTestCode() method in test"

	fun file(): PsiFile = myFixture.file!!
	fun project() = myFixture.project

	fun initWithTextSmart(content: String) = initWithFileContent("test", fileExtension, content)

	fun initWithFileContent(filename: String, extension: String, content: String) =
		myFixture.configureByText(filename + (if (extension.isEmpty()) "" else ".$extension"), getPatchedContent(content))

	protected fun getPatchedContent(content: String): String = StringUtil.replace(content, PER_TEST_CODE, perTestCode)
}