import java.io.ByteArrayOutputStream
import com.google.common.util.concurrent.SimpleTimeLimiter
import java.io.InputStream
import java.util.stream.Collectors

buildscript {
	var kotlin_version : String by extra
	var grammar_kit_version : String by extra

	grammar_kit_version = "2017.1.1"
	kotlin_version = "1.2.21"

	repositories {
		mavenCentral()
		maven("https://jitpack.io")
	}

	dependencies {
		classpath(kotlin("gradle-plugin", kotlin_version))
		classpath("com.github.hurricup:gradle-grammar-kit-plugin:$grammar_kit_version")
	}
}

plugins {
	id("org.jetbrains.intellij") version "0.2.18"
}

allprojects {
	apply {
		listOf(
			"kotlin",
			"java",
			"org.jetbrains.grammarkit",
			"org.jetbrains.intellij"
		).forEach {
			plugin(it)
		}
	}

	intellij {
		updateSinceUntilBuild = false
		instrumentCode = true
		localPath = when(System.getProperty("user.name")) {
			"ice1000" -> "/home/ice1000/.local/share/JetBrains/Toolbox/apps/IDEA-U/ch-0/173.4548.28"
			"hoshino" -> "/home/hoshino/文档/IntelliJ"

			else -> ""
		}
	}
}

fun collectLines(it: InputStream): List<String> {
	val reader = it.bufferedReader()
	val ret = reader.lines().collect(Collectors.toList())
	forceRun(reader::close)
	return ret
}

inline fun forceRun(lambda: () -> Unit) {
	try {
		lambda()
	} catch (e: Throwable) {

	}
}

fun executeCommand(
	command: String,
	input: String?,
	timeLimit: Long): Pair<List<String>, List<String>> {
	var processRef: Process? = null
	var output: List<String> = emptyList()
	var outputErr: List<String> = emptyList()
	try {
		SimpleTimeLimiter().callWithTimeout({
			val process: Process = Runtime.getRuntime().exec(command)
			processRef = process
			if (input != null) process.outputStream.use {
				it.write(input.toByteArray())
				it.flush()
			}
			process.waitFor(timeLimit, TimeUnit.MILLISECONDS)
			output = process.inputStream.use(::collectLines)
			outputErr = process.errorStream.use(::collectLines)
			forceRun(process::destroy)
		}, timeLimit + 100, TimeUnit.MILLISECONDS, true)
	} catch (e: Throwable) {
		processRef?.destroy()
	}
	return output to outputErr
}

val commitHash get() = ByteArrayOutputStream().apply {
	//QAQ
}.toString().trim()

val pluginVersion = "0.1.6"
val packageName = "org.ice1000.julia"
val kotlin_version : String by extra

group = packageName
version = if(System.getenv("CI")?.trim() != null) "$pluginVersion-$commitHash" else pluginVersion

repositories {
	mavenCentral()
}

dependencies {
	compile(kotlin("stdlib", kotlin_version))
	compile("org.eclipse.egit.github.core-2.1.5")
	testCompile("junit", "junit", "4.12")
}