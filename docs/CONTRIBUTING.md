# How to build

## Use gradle

```bash
$ chmod a+x gradlew
$ ./gradlew buildPlugin
```

It's not recommended to use gradle because it will
include unnecessary dependencies and it's slow.

## Use Idea build

### Prerequisites

First install required plugins in your IntelliJ IDEA:

+ Grammar-Kit [![JetBrains Plugins](https://img.shields.io/jetbrains/plugin/v/6606-grammar-kit.svg)](https://plugins.jetbrains.com/plugin/6606-grammar-kit)
+ UI Designer (built-in, just make sure you've enabled it)
+ Plugin DevKit (built-in, just make sure you've enabled it)
+ Kotlin [![JetBrains Plugins](https://img.shields.io/jetbrains/plugin/v/6954-kotlin.svg)](https://plugins.jetbrains.com/plugin/6954-kotlin)

I've compiled a Grammar-Kit which supports completing Java codes in the jflex code fragment which can be found
[here](https://github.com/ice1000/julia-intellij/releases/download/0.0.2/grammar-kit-2017.1.1.zip).

For debugging purpose, it's recommended to install a plugin called PsiViewer [![JetBrains Plugins](https://plugins.jetbrains.com/plugin/227-psiviewer)](https://plugins.jetbrains.com/plugin/227-psiviewer).

### Build

Clone this repo:

```shell
$ git clone https://github.com/ice1000/julia-intellij.git
```

Create a plugin project from your cloned source, and use Grammar-Kit
to generate the Parser and Lexer.

Then, click `File | Settings | Build, Execution, Deployment | Compiler | Kotlin Compiler`,
make sure "Target Kotlin version" is "1.8".

Then, click `Build | Prepare Plugin Module 'julia-intellij' for deployment`, and you'll see a jar
appears at the project root.

For more information, see [the official doc](http://www.jetbrains.org/intellij/sdk/docs/basics.html).

# Contributing guidelines

## You must

0. Put all Nls into the [resource bundle](src/org/ice1000/julia/lang/julia-bundle.properties)
0. Use as much `@NotNull` and `@Nullable` as you can in Java codes except local variables

## You must not

0. Break the code style -- use tab indents with spaces aligns (see [.editorconfig](.editorconfig))
0. Open pull requests just to fix code style, or use some syntax sugar (this is not SharpLang!)
0. Add any kind of generated file into the git repo (including the parser!)
0. Use `!!`
0. Violate the open source license

## You should

0. Use Kotlin except UI, but if you only know Java, never mind, we can help you convert
0. Name your files like `julia-xxx.kt`, or `utils.kt`, or `ui-impl.kt`
0. Put all highly related classes into a single file
0. Use English, but we also read Chinese so if you only know Chinese just use it
0. Write commit message starts with `[ issue id or refactor type ]`

## You don't have to

0. Write comments, except you're using magics. Tell us if you do
0. Write tests, because we'll review your codes carefully
