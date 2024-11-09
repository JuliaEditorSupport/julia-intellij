import org.jetbrains.grammarkit.tasks.GenerateLexerTask
import org.jetbrains.grammarkit.tasks.GenerateParserTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.nio.file.Paths

fun properties(key: String) = providers.gradleProperty(key)
fun environment(key: String) = providers.environmentVariable(key)
val genRoot = project.file("src/main/gen")
val resourceRoot = project.file("src/main/resources")

dependencies{
	implementation(group = "org.eclipse.mylyn.github", name = "org.eclipse.egit.github.core", version = "2.1.5") {
		exclude(module = "gson")
	}
	intellijPlatform{
		val platformVersionProvider: Provider<String> by rootProject.extra
		create("IC", platformVersionProvider.get(), useInstaller = properties("useInstaller").get().toBoolean())
	}
}

sourceSets {
	main {
		java.srcDirs(genRoot)
	}
}

// Don't specify type explicitly. Will be incorrectly recognized
fun bnf(name: String) = Paths.get("grammar", "$name-grammar.bnf").toFile()
fun flex(name: String) = Paths.get("grammar", "$name-lexer.flex").toFile()

val genParser = tasks.register<GenerateParserTask>("genParser") {
	group = "code generation"
	description = "Generate the Parser and PsiElement classes"
	sourceFile.set(bnf("julia"))
	pathToParser.set("/org/ice1000/julia/lang/JuliaParser.java")
	pathToPsiRoot.set("/org/ice1000/julia/lang/psi")
	targetRootOutputDir.set(genRoot)
}

val genLexer = tasks.register<GenerateLexerTask>("genLexer") {
	group = "code generation"
	description = "Generate the Lexer"
	sourceFile.set(flex("julia"))
	targetOutputDir.set(genRoot.resolve("org/ice1000/julia/lang"))

	dependsOn(genParser)
}

val genDocfmtParser = tasks.register<GenerateParserTask>("genDocfmtParser") {
	group = "code generation"
	description = "Generate the Parser for DocumentFormat.jl"
	sourceFile.set(bnf("docfmt"))
	pathToParser.set("/org/ice1000/julia/lang/docfmt/DocfmtParser.java")
	pathToPsiRoot.set("/org/ice1000/julia/lang/docfmt/psi")
	targetRootOutputDir.set(genRoot)
}

val genDocfmtLexer = tasks.register<GenerateLexerTask>("genDocfmtLexer") {
	group = "code generation"
	description = "Generate the Lexer for DocumentFormat.jl"
	sourceFile.set(flex("docfmt"))
	targetOutputDir.set(genRoot.resolve("org/ice1000/julia/lang/docfmt"))
	dependsOn(genDocfmtParser)
}

val sortSpelling = tasks.register("sortSpellingFile") {
	val fileName = "spelling.txt"
	val isWindows = "windows" in System.getProperty("os.name").toLowerCase()
	project.exec {
		workingDir = resourceRoot.resolve("org/ice1000/julia/lang/editing")
		commandLine = when {
			isWindows -> listOf("sort.exe", fileName, "/O", fileName)
			else -> listOf("sort", fileName, "-f", "-o", fileName)
		}
	}
}

tasks.withType<KotlinCompile> {
	dependsOn(
		genLexer,
		genDocfmtLexer,
		sortSpelling
	)
}
