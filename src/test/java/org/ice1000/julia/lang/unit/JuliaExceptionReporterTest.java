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

package org.ice1000.julia.lang.unit;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Attachment;
import com.intellij.openapi.diagnostic.IdeaLoggingEvent;
import com.intellij.openapi.diagnostic.RuntimeExceptionWithAttachments;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilCore;
import org.ice1000.julia.lang.JuliaLightTestCase;
import org.ice1000.julia.lang.error.YoutrackErrorHandler;
import org.jetbrains.annotations.NotNull;
import org.junit.Assume;
import org.junit.Test;

import java.io.IOException;

import static org.ice1000.julia.lang.error.YoutrackErrorHandler.YOUTRACK_PROPERTY_KEY;
import static org.ice1000.julia.lang.error.YoutrackErrorHandler.YOUTRACK_PROPERTY_VALUE;

public class JuliaExceptionReporterTest extends JuliaLightTestCase {
	@Test
	public void testReporting() {
		assumeNotSkipped();
		Throwable first = new Throwable();
		Throwable second = new Throwable(first);
		Throwable third = new Throwable(second);

		var mainException = new RuntimeExceptionWithAttachments("Test body и немного русского языка", new Attachment("fist", first),
			new Attachment("fist", second), new Attachment("second", third),
			new Attachment("последний", "русский аттачмент"));

		var loggingEvents = new IdeaLoggingEvent[]{new IdeaLoggingEvent("Test description и по-русски", mainException)};

		doTest(loggingEvents, "nothing", 4);
	}

	private static void assumeNotSkipped() {
		Assume.assumeFalse("Skipped for pull requests", StringUtil.equals("skip", YOUTRACK_PROPERTY_VALUE));
	}

	@Test
	public void testReportingInvalidPsiElement() {
		assumeNotSkipped();
		initWithTextSmart("say 'hi'");
		PsiFile file = file();
		assertTrue(file.isValid());
		PsiElement statement = file.getFirstChild();
		WriteCommandAction.runWriteCommandAction(getProject(), statement::delete);
		assertFalse(statement.isValid());
		try {
			PsiUtilCore.ensureValid(statement);
		} catch (Exception e) {
			var loggingEvents = new IdeaLoggingEvent[]{new IdeaLoggingEvent("Test message", e)};
			var additionalInfo = "No info";
			doTest(loggingEvents, additionalInfo, 1);
		}
	}

	private void doTest(@NotNull IdeaLoggingEvent[] loggingEvents, @NotNull String additionalInfo, int expectedAttachments) {
		var errorHandler = new YoutrackErrorHandler();
		var submittingData = errorHandler.doSubmit(loggingEvents, additionalInfo, getProject());

		var youtrackResponse = submittingData.second;
		assertNotNull(youtrackResponse);
		LOG.warn("Posted issue: " + youtrackResponse.idReadable);

		assertEquals("Wrong attachments number: ", expectedAttachments, youtrackResponse.attachmentsAdded);

		if (!YoutrackErrorHandler.hasAdminToken()) {
			LOG.warn("Pass token via property to remove test issues automatically: " + YOUTRACK_PROPERTY_KEY);
			return;
		}

		try {
			var deleteResponse = errorHandler.deleteIssue(youtrackResponse);
			assertEquals("Error removing issue: " + youtrackResponse + "; " + deleteResponse.getStatusLine(), 200,
				deleteResponse.getStatusLine().getStatusCode());
		} catch (IOException ex) {
			fail("Error removing issue: " + ex.getMessage());
		}
	}
}
