/*
 * Copyright (c) 2017 Patrick Scheibe
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.ice1000.julia.lang.error

import com.intellij.CommonBundle
import com.intellij.diagnostic.*
import com.intellij.ide.DataManager
import com.intellij.ide.plugins.PluginManager
import com.intellij.idea.IdeaLogger
import com.intellij.notification.NotificationListener
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationNamesInfo
import com.intellij.openapi.application.ex.ApplicationInfoEx
import com.intellij.openapi.diagnostic.*
import com.intellij.openapi.diagnostic.SubmittedReportInfo.SubmissionStatus
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.progress.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.SystemInfo
import com.intellij.util.Consumer
import org.apache.commons.codec.binary.Base64
import org.eclipse.egit.github.core.*
import org.eclipse.egit.github.core.client.GitHubClient
import org.eclipse.egit.github.core.service.IssueService
import org.ice1000.julia.lang.module.juliaSettings
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey
import java.awt.Component
import java.io.*
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

private object AnonymousFeedback {
	private const val tokenFile = "org/ice1000/julia/lang/error-report/token.bin"
	private const val gitRepoUser = "ice1000"
	private const val gitRepo = "julia-intellij"
	private const val issueLabel = "pending"

	/**
	 * Makes a connection to GitHub. Checks if there is an issue that is a duplicate and based on this, creates either a
	 * new issue or comments on the duplicate (if the user provided additional information).
	 *
	 * @param environmentDetails Information collected by [getKeyValuePairs]
	 * @return The report info that is then used in [GitHubErrorReporter] to show the user a balloon with the link
	 * of the created issue.
	 */
	internal fun sendFeedback(environmentDetails: MutableMap<String, String>): SubmittedReportInfo {
		val logger = Logger.getInstance(javaClass.name)
		try {
			val resource: URL? = javaClass.classLoader.getResource(tokenFile)
			if (resource == null) {
				logger.info("Could not find token file")
				throw IOException("Could not decrypt access token")
			}
			val gitAccessToken = decrypt(resource)
			val client = GitHubClient()
			client.setOAuth2Token(gitAccessToken)
			val repoID = RepositoryId(gitRepoUser, gitRepo)
			val issueService = IssueService(client)
			var newGibHubIssue = createNewGibHubIssue(environmentDetails)
			val duplicate = findFirstDuplicate(newGibHubIssue.title, issueService, repoID)
			var isNewIssue = true
			if (duplicate != null) {
				issueService.createComment(repoID, duplicate.number, generateGitHubIssueBody(environmentDetails, false))
				newGibHubIssue = duplicate
				isNewIssue = false
			} else newGibHubIssue = issueService.createIssue(repoID, newGibHubIssue)
			return SubmittedReportInfo(newGibHubIssue.htmlUrl, ErrorReportBundle.message(
				if (isNewIssue) "git.issue.text" else "git.issue.duplicate.text", newGibHubIssue.htmlUrl, newGibHubIssue.number.toLong()),
				if (isNewIssue) SubmissionStatus.NEW_ISSUE else SubmissionStatus.DUPLICATE)
		} catch (e: Exception) {
			return SubmittedReportInfo(null,
				ErrorReportBundle.message("report.error.connection.failure"),
				SubmissionStatus.FAILED)
		}
	}

	private fun findFirstDuplicate(uniqueTitle: String, service: IssueService, repo: RepositoryId): Issue? {
		val searchParameters = HashMap<String, String>(2)
		searchParameters[IssueService.FILTER_STATE] = IssueService.STATE_OPEN
		return service.pageIssues(repo, searchParameters).flatten().firstOrNull { it.title == uniqueTitle }
	}

	private fun createNewGibHubIssue(details: MutableMap<String, String>) = Issue().apply {
		val errorMessage = details.remove("error.message")?.takeIf(String::isNotBlank) ?: "Unspecified error"
		title = ErrorReportBundle.message("git.issue.title", details.remove("error.hash").orEmpty(), errorMessage)
		details["title"] = title
		body = generateGitHubIssueBody(details, true)
		labels = listOf(Label().apply { name = issueLabel })
	}

	private fun generateGitHubIssueBody(details: MutableMap<String, String>, includeStacktrace: Boolean) =
		buildString {
			val errorDescription = details.remove("error.description").orEmpty()
			val stackTrace = details.remove("error.stacktrace")?.takeIf(String::isNotBlank) ?: "invalid stacktrace"
			if (errorDescription.isNotEmpty()) append(errorDescription).appendln("\n\n----------------------\n")
			for ((key, value) in details) append("- ").append(key).append(": ").appendln(value)
			if (includeStacktrace)
				appendln("<details><summary>Full StackTrace</summary>")
					.appendln("<pre><code>")
					.appendln(stackTrace)
					.appendln("</code></pre>\n</details>")
		}
}

private const val initVector = "RandomInitVector"
private const val key = "GitHubErrorToken"

private fun decrypt(file: URL): String {
	val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
	cipher.init(Cipher.DECRYPT_MODE,
		SecretKeySpec(key.toByteArray(charset("UTF-8")), "AES"),
		IvParameterSpec(initVector.toByteArray(charset("UTF-8"))))
	return String(cipher.doFinal(Base64.decodeBase64(file.openStream().use {
		ObjectInputStream(it).use(ObjectInputStream::readObject) as String
	})))
}

fun main(args: Array<String>) {
	if (args.size != 2) return
	Files.write(Paths.get(args[1]), encrypt(args[0]).toByteArray())
}

private fun encrypt(value: String): String {
	val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
	cipher.init(Cipher.ENCRYPT_MODE,
		SecretKeySpec(key.toByteArray(charset("UTF-8")), "AES"),
		IvParameterSpec(initVector.toByteArray(charset("UTF-8"))))
	return Base64.encodeBase64String(cipher.doFinal(value.toByteArray()))
}

class GitHubErrorReporter : ErrorReportSubmitter() {
	override fun getReportActionText() = ErrorReportBundle.message("report.error.to.plugin.vendor")
	override fun submit(
		events: Array<IdeaLoggingEvent>,
		info: String?,
		parent: Component,
		consumer: Consumer<SubmittedReportInfo>): Boolean {
		// TODO improve
		val event = events.firstOrNull { it.throwable != null } ?: return false
		return doSubmit(event, parent, consumer, info)
	}

	private fun doSubmit(
		event: IdeaLoggingEvent,
		parent: Component,
		callback: Consumer<SubmittedReportInfo>,
		description: String?): Boolean {
		val dataContext = DataManager.getInstance().getDataContext(parent)
		val bean = GitHubErrorBean(
			event.throwable,
			IdeaLogger.ourLastActionId,
			description ?: "<No description>",
			event.message ?: event.throwable.message.toString())
		IdeErrorsDialog.findPluginId(event.throwable)?.let { pluginId ->
			PluginManager.getPlugin(pluginId)?.let { ideaPluginDescriptor ->
				if (!ideaPluginDescriptor.isBundled) {
					bean.pluginName = ideaPluginDescriptor.name
					bean.pluginVersion = ideaPluginDescriptor.version
				}
			}
		}

		(event.data as? AbstractMessage)?.let { bean.attachments = it.includedAttachments }
		val project = CommonDataKeys.PROJECT.getData(dataContext)
		val reportValues = getKeyValuePairs(
			project,
			bean,
			ApplicationInfoEx.getInstanceEx(),
			ApplicationNamesInfo.getInstance())
		val notifyingCallback = CallbackWithNotification(callback, project)
		val task = AnonymousFeedbackTask(project, ErrorReportBundle.message(
			"report.error.progress.dialog.text"), true, reportValues, notifyingCallback)
		if (project == null) task.run(EmptyProgressIndicator()) else ProgressManager.getInstance().run(task)
		return true
	}

	internal class CallbackWithNotification(
		private val consumer: Consumer<SubmittedReportInfo>,
		private val project: Project?) : Consumer<SubmittedReportInfo> {
		override fun consume(reportInfo: SubmittedReportInfo) {
			consumer.consume(reportInfo)
			if (reportInfo.status == SubmissionStatus.FAILED) ReportMessages.GROUP.createNotification(ReportMessages.ERROR_REPORT,
				reportInfo.linkText, NotificationType.ERROR, null).setImportant(false).notify(project)
			else ReportMessages.GROUP.createNotification(ReportMessages.ERROR_REPORT, reportInfo.linkText,
				NotificationType.INFORMATION, NotificationListener.URL_OPENING_LISTENER).setImportant(false).notify(project)
		}
	}
}

/**
 * Extends the standard class to provide the hash of the thrown exception stack trace.
 *
 * @author patrick (17.06.17).
 */
class GitHubErrorBean(
	throwable: Throwable,
	val lastAction: String?,
	val description: String,
	val message: String) {
	val exceptionHash: String
	val stackTrace: String
	init {
		val trace = throwable.stackTrace
		exceptionHash = Arrays.hashCode(trace).toString()
		val sw = StringWriter()
		throwable.printStackTrace(PrintWriter(sw))
		stackTrace = sw.toString()
	}

	var pluginName = ""
	var pluginVersion = ""
	var attachments: List<Attachment> = emptyList()
}

/**
 * Messages and strings used by the error reporter
 */
private object ErrorReportBundle {
	@NonNls private const val BUNDLE = "org.ice1000.julia.lang.error-report.report-bundle"
	private val bundle: ResourceBundle by lazy { ResourceBundle.getBundle(BUNDLE) }

	@JvmStatic
	internal fun message(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any) =
		CommonBundle.message(bundle, key, *params)
}

private class AnonymousFeedbackTask(
	project: Project?, title: String, canBeCancelled: Boolean,
	private val params: MutableMap<String, String>,
	private val callback: Consumer<SubmittedReportInfo>) : Task.Backgroundable(project, title, canBeCancelled) {
	override fun run(indicator: ProgressIndicator) {
		indicator.isIndeterminate = true
		callback.consume(AnonymousFeedback.sendFeedback(params))
	}
}

/**
 * Collects information about the running IDEA and the error
 */
private fun getKeyValuePairs(
	project: Project?,
	error: GitHubErrorBean,
	appInfo: ApplicationInfoEx,
	namesInfo: ApplicationNamesInfo): MutableMap<String, String> {
	PluginManager.getPlugin(PluginId.findId("org.ice1000.julia"))?.run {
		if (error.pluginName.isBlank()) error.pluginName = name
		if (error.pluginVersion.isBlank()) error.pluginVersion = version
	}
	val params = mutableMapOf(
		"error.description" to error.description,
		"Julia Version" to (project?.run { juliaSettings.settings.version } ?: "Unknown"),
		"Plugin Name" to error.pluginName,
		"Plugin Version" to error.pluginVersion,
		"OS Name" to SystemInfo.OS_NAME,
		"Java Version" to SystemInfo.JAVA_VERSION,
		"App Name" to namesInfo.productName,
		"App Full Name" to namesInfo.fullProductName,
		"App Version name" to appInfo.versionName,
		"Is EAP" to java.lang.Boolean.toString(appInfo.isEAP),
		"App Build" to appInfo.build.asString(),
		"App Version" to appInfo.fullVersion,
		"Last Action" to (error.lastAction ?: "Unknown"),
		"error.message" to error.message,
		"error.stacktrace" to error.stackTrace,
		"error.hash" to error.exceptionHash)
	for (attachment in error.attachments) params["Attachment ${attachment.name}"] = attachment.encodedBytes
	return params
}
