import java.io.*
import java.util.stream.Collectors

buildscript {
	var kotlinVersion: String by extra
	var grammarKitVersion: String by extra

	grammarKitVersion = "2017.1.1"
	kotlinVersion = "1.2.21"

	repositories {
		mavenCentral()
		maven("https://jitpack.io")
	}

	dependencies {
		classpath(kotlin("gradle-plugin", kotlinVersion))
		classpath("com.github.hurricup:gradle-grammar-kit-plugin:$grammarKitVersion")
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
		when (System.getProperty("user.name")) {
			"ice1000" -> localPath = "/home/ice1000/.local/share/JetBrains/Toolbox/apps/IDEA-U/ch-0/173.4548.28"
			"hoshino" -> localPath = "/home/hoshino/文档/IntelliJ"
		}
	}
}

fun collectLines(it: InputStream) =
	it.bufferedReader().`fuck kotlin! it doesn't support "use" here` {
		lines().collect(Collectors.toList())
	}

@Suppress("FunctionName", "ConvertTryFinallyToUseCall")
@SinceKotlin("1.2")
fun <Closable, Unit> Closable.`fuck kotlin! it doesn't support "use" here`(block: Closable.() -> Unit): Unit
	where Closable : Closeable, Unit : Any = try {
	block()
} finally {
	close()
}

fun executeCommand(
	command: String,
	timeLimit: Long = 2000L): List<String> {
	var processRef: Process? = null
	var output = emptyList<String>()
	try {
		val process: Process = Runtime.getRuntime().exec(command)
		processRef = process
		process.waitFor(timeLimit, TimeUnit.MILLISECONDS)
		output = process.inputStream.`fuck kotlin! it doesn't support "use" here`(::collectLines)
		process.destroy()
	} catch (e: Throwable) {
		processRef?.destroy()
	}
	return output
}

val commitHash by lazy {
	executeCommand("git rev-parse --short HEAD").joinToString("").trim()
}

val pluginVersion = "0.1.6"
val packageName = "org.ice1000.julia"
val kotlinVersion: String by extra

group = packageName
version = if (System.getenv("CI").isNullOrBlank()) pluginVersion else "$pluginVersion-$commitHash"

repositories {
	mavenCentral()
}

dependencies {
	compile(kotlin("stdlib", kotlinVersion))
	compile(files("lib/org.eclipse.egit.github.core-2.1.5"))
	testCompile("junit", "junit", "4.12")
}