import groovy.lang.Closure
import org.jetbrains.grammarkit.tasks.GenerateLexer
import org.jetbrains.grammarkit.tasks.GenerateParser
import java.io.*
import java.nio.file.*
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

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
}

@Suppress("FunctionName", "ConvertTryFinallyToUseCall")
@SinceKotlin("1.2")
inline fun <reified Closable, reified Unit>
	Closable.`fuck kotlin! it doesn't support "use" here`(block: Closable.() -> Unit): Unit
	where Closable : Closeable, Unit : Any = try {
	block()
} finally {
	close()
}

val commitHash by lazy {
	val output: String
	val process: Process = Runtime.getRuntime().exec("git rev-parse --short HEAD")
	process.waitFor(2000L, TimeUnit.MILLISECONDS)
	output = process.inputStream.`fuck kotlin! it doesn't support "use" here` {
		bufferedReader().`fuck kotlin! it doesn't support "use" here` {
			readText()
		}
	}
	process.destroy()
	output.trim()
}

val isCI = !System.getenv("CI").isNullOrBlank()

val pluginVersion = "0.1.6"
val packageName = "org.ice1000.julia"
val kotlinVersion: String by extra

group = packageName
version = if (isCI) "$pluginVersion-$commitHash" else pluginVersion

repositories {
	mavenCentral()
}

dependencies {
	compile(kotlin("stdlib", kotlinVersion))
	compile(files(Paths.get("lib", "org.eclipse.egit.github.core-2.1.5")))
	testCompile("junit", "junit", "4.12")
}

task("displayCommitHash") {
	group = "help"
	description = "Display the newest commit hash"
	doFirst {
		println("Commit hash: $commitHash")
	}
}

task("isCI") {
	group = "help"
	description = "Check if it's running in a continuous-integration"
	doFirst {
		println(if (isCI) "Yes, I'm on a CI." else "No, I'm not on CI.")
	}
}

task("genParser", GenerateParser::class) {
	group = "build setup"
	description = "Generate the Parser and PsiElement classes"
	source = "grammar/julia-grammar.bnf"
	targetRoot = "gen"
	pathToParser = "org/ice1000/julia/lang/JuliaParser.java"
	pathToPsiRoot = "org/ice1000/julia/lang/psi"
	purgeOldFiles = true
}

task("genLexer", GenerateLexer::class) {
	dependsOn("genParser")
	group = "build setup"
	description = "Generate the Lexer"
	source = "grammar/julia-lexer.flex"
	targetDir = "gen/org/ice1000/julia/lang"
	targetClass = "JuliaLexer"
	purgeOldFiles = true
}

task("genDocfmtParser", GenerateParser::class) {
	group = "build setup"
	description = "Generate the Parser for DocumentFormat.jl"
	source = "grammar/docfmt-grammar.bnf"
	targetRoot = "gen"
	pathToParser = "org/ice1000/julia/lang/docfmt/DocfmtParser.java"
	pathToPsiRoot = "org/ice1000/julia/lang/docfmt/psi"
	purgeOldFiles = true
}

task("genDocfmtLexer", GenerateLexer::class) {
	dependsOn("genDocfmtParser")
	group = "build setup"
	description = "Generate the Lexer for DocumentFormat.jl"
	source = "grammar/docfmt-lexer.flex"
	targetDir = "gen/org/ice1000/julia/lang/docfmt"
	targetClass = "DocfmtLexer"
	purgeOldFiles = true
}

task("cleanGenerated") {

}

getTasksByName("buildPlugin", false).first().dependsOn("genLexer")
getTasksByName("buildPlugin", false).first().dependsOn("genDocfmtLexer")
