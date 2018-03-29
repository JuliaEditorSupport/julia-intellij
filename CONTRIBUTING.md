# How to build

## Use gradle

```bash
$ chmod a+x gradlew
$ ./gradlew buildPlugin
```

It's now recommended to use gradle because it can be used under command line.

But anyway you're gonna edit this project code with IDEA.

## Use Idea build

### Prerequirements

First install required plugins in your IntelliJ IDEA:

+ Grammar-Kit [![JetBrains Plugins](https://img.shields.io/jetbrains/plugin/v/6606-grammar-kit.svg)](https://plugins.jetbrains.com/plugin/6606-grammar-kit)
+ UI Designer (built-in, just make sure you've enabled it)
+ Plugin DevKit (built-in, just make sure you've enabled it)
+ Kotlin [![JetBrains Plugins](https://img.shields.io/jetbrains/plugin/v/6954-kotlin.svg)](https://plugins.jetbrains.com/plugin/6954-kotlin)

For debugging purpose, it's recommended to install a plugin called PsiViewer in the plugin sandbox [![JetBrains Plugins](https://plugins.jetbrains.com/plugin/227-psiviewer)](https://plugins.jetbrains.com/plugin/227-psiviewer).

### Build

Clone this repo:

```shell
$ git clone https://github.com/ice1000/julia-intellij.git
```

Create a plugin project from your cloned source, and use `gradle buildPlugin` to do code generation.

To debug, run `gradle runIde` in the debugger. Break points works like a charm.

For more information, see [the official doc](http://www.jetbrains.org/intellij/sdk/docs/basics.html).

# Contributing guidelines

## You must

0. Put all natrual language strings into the [resource bundle](res/org/ice1000/julia/lang/julia-bundle.properties)
0. Use as much `@NotNull` and `@Nullable` as you can in Java codes except local variables

## You must not

0. Break the code style -- use tab indents with spaces aligns (see [.editorconfig](.editorconfig))
0. Open pull requests just to fix code style, or use some syntax sugar (julia-intellij is not SharpLang!)
0. Add any kind of generated file into the git repo (including the parser!)
0. Violate the open source license

## You should

0. Use Kotlin except UI, but if you only know Java, never mind, we can help you convert
0. Name your files like `julia-xxx.kt`
0. Put all highly related classes into a single file
0. Use English, but we also read Chinese so if you only know Chinese just use it
0. Write commit message starts with `[ issue id or refactor type ]`

## You'd better

0. Read http://www.jetbrains.org/display/IJOS/IntelliJ+Coding+Guidelines

## You don't have to

0. Write comments, except you're using magics. Tell us if you do
0. Write tests, because we'll review your codes carefully
