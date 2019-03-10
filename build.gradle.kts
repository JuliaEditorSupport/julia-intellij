import org.jetbrains.grammarkit.tasks.GenerateLexer
import org.jetbrains.grammarkit.tasks.GenerateParser
import org.jetbrains.intellij.tasks.PatchPluginXmlTask
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.*
import java.nio.file.Paths

val isCI = !System.getenv("CI").isNullOrBlank()
val commitHash = kotlin.run {
	val process: Process = Runtime.getRuntime().exec("git rev-parse --short HEAD")
	process.waitFor()
	@Suppress("RemoveExplicitTypeArguments")
	val output = process.inputStream.use {
		process.inputStream.use {
			it.readBytes().let<ByteArray, String>(::String)
		}
	}
	process.destroy()
	output.trim()
}

val pluginComingVersion = "0.3.11"
val pluginVersion = if (isCI) "$pluginComingVersion-$commitHash" else pluginComingVersion
val packageName = "org.ice1000.julia"
val kotlinVersion = "1.2.70"

group = packageName
version = pluginVersion

plugins {
	java
	id("org.jetbrains.intellij") version "0.4.4"
	id("org.jetbrains.grammarkit") version "2018.3.1"
	kotlin("jvm") version "1.2.70"
}

allprojects {
	apply { plugin("org.jetbrains.grammarkit") }

	intellij {
		updateSinceUntilBuild = false
		instrumentCode = true
		val username = System.getProperty("user.name")
		val root = "/home/$username/.local/share/JetBrains/Toolbox/apps"
		when (username) {
			"ice1000" -> {
				localPath = "$root/IDEA-C/ch-0/191.5849.21"
				alternativeIdePath = "$root/PyCharm-C/ch-0/191.5849.23"
			}
			"hoshino" -> version = "2018.2.1"
			"zxj5470" -> {
				version = "2018.3"
//				alternativeIdePath = "$root/PyCharm-P/ch-0/183.4284.139"
			}
		/* for CI */ else -> version = "2018.3"
		}
		setMarkdownDependency()
	}
}

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<PatchPluginXmlTask> {
	changeNotes(file("change-notes.html").readText())
	pluginDescription(file("description.html").readText())
	version(pluginVersion)
	pluginId(packageName)
}

sourceSets {
	getByName("main") {
		withConvention(KotlinSourceSet::class) {
			listOf(java, kotlin).forEach { it.srcDirs("src", "gen") }
		}
		resources.srcDirs("res")
	}

	getByName("test") {
		withConvention(KotlinSourceSet::class) {
			listOf(java, kotlin).forEach { it.srcDirs("test") }
		}
		resources.srcDirs("testData")
	}
}

repositories { mavenCentral() }

dependencies {
	compileOnly(kotlin(module = "stdlib"))
	compile(kotlin(module = "stdlib-jdk8").toString()) {
		exclude(module = "kotlin-runtime")
		exclude(module = "kotlin-reflect")
		exclude(module = "kotlin-stdlib")
	}
	compile(group = "org.eclipse.mylyn.github", name = "org.eclipse.egit.github.core", version = "2.1.5") {
		exclude(module = "gson")
	}
	testCompile(kotlin(module = "test-junit"))
	testCompile(group = "junit", name = "junit", version = "4.12")
}

task("displayCommitHash") {
	group = "help"
	description = "Display the newest commit hash"
	doFirst { println("Commit hash: $commitHash") }
}

task("isCI") {
	group = "help"
	description = "Check if it's running in a continuous-integration"
	doFirst { println(if (isCI) "Yes, I'm on a CI." else "No, I'm not on CI.") }
}

// Don't specify type explicitly. Will be incorrectly recognized
val parserRoot = Paths.get("org", "ice1000", "julia", "lang")!!
val lexerRoot = Paths.get("gen", "org", "ice1000", "julia", "lang")!!
fun path(more: Iterable<*>) = more.joinToString(File.separator)
fun bnf(name: String) = Paths.get("grammar", "$name-grammar.bnf").toString()
fun flex(name: String) = Paths.get("grammar", "$name-lexer.flex").toString()

val genParser = task<GenerateParser>("genParser") {
	group = tasks["init"].group!!
	description = "Generate the Parser and PsiElement classes"
	source = bnf("julia")
	targetRoot = "gen/"
	pathToParser = path(parserRoot + "JuliaParser.java")
	pathToPsiRoot = path(parserRoot + "psi")
	purgeOldFiles = true
}

val genLexer = task<GenerateLexer>("genLexer") {
	group = genParser.group
	description = "Generate the Lexer"
	source = flex("julia")
	targetDir = path(lexerRoot)
	targetClass = "JuliaLexer"
	purgeOldFiles = true
}

val genDocfmtParser = task<GenerateParser>("genDocfmtParser") {
	group = genParser.group
	description = "Generate the Parser for DocumentFormat.jl"
	source = bnf("docfmt")
	targetRoot = "gen/"
	val root = parserRoot + "docfmt"
	pathToParser = path(root + "DocfmtParser.java")
	pathToPsiRoot = path(root + "psi")
	purgeOldFiles = true
}

val genDocfmtLexer = task<GenerateLexer>("genDocfmtLexer") {
	group = genParser.group
	description = "Generate the Lexer for DocumentFormat.jl"
	source = flex("docfmt")
	targetDir = path(lexerRoot + "docfmt")
	targetClass = "DocfmtLexer"
	purgeOldFiles = true
}

val cleanGenerated = task("cleanGenerated") {
	group = tasks["clean"].group
	description = "Remove all generated codes"
	doFirst { delete("gen") }
}

val sortSpelling = task("sortSpellingFile") {
	val fileName = "spelling.txt"
	val isWindows = "windows" in System.getProperty("os.name").toLowerCase()
	project.exec {
		workingDir = file("$projectDir/res/org/ice1000/julia/lang/editing")
		commandLine = when {
			isWindows -> listOf("sort.exe", fileName, "/O", fileName)
			else -> listOf("sort", fileName, "-f", "-o", fileName)
		}
	}
}

tasks.withType<KotlinCompile> {
	dependsOn(
		genParser,
		genLexer,
		genDocfmtParser,
		genDocfmtLexer,
		sortSpelling
	)
	kotlinOptions {
		jvmTarget = "1.8"
		languageVersion = "1.2"
		apiVersion = "1.2"
		freeCompilerArgs = listOf("-Xjvm-default=enable")
	}
}

tasks.withType<Delete> { dependsOn(cleanGenerated) }

fun setMarkdownDependency() {
	repositories {
		maven("https://dl.bintray.com/jetbrains/markdown/")
	}
	dependencies {
		compile("org.jetbrains", "markdown", "0.1.31")
	}
}
