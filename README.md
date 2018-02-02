# Julia plugin for the IntelliJ Platform

[![](https://tinyurl.com/y9e4n2zh)](https://github.com/ice1000/julia-intellij)
[![JetBrains plugins](https://img.shields.io/jetbrains/plugin/v/10413-julia.svg)](https://plugins.jetbrains.com/plugin/10413-julia)
[![JetBrains plugins](https://img.shields.io/jetbrains/plugin/d/10413-julia.svg)](https://plugins.jetbrains.com/plugin/10413-julia)

## Status

This is a **work in progress**, some features are implemented partially, there may be performance and stability problems.

## Installation \& Usage

Install IntelliJ IDEA, open `Settings | Plugins | Browse repositories`,
install Julia plugin, and create a Julia project (you'll be asked to create a Julia SDK, don't worry it's easy).

### SDK Home Configuration 

+ Auto Detect:
	+ Windows:	`C:\Users\yourAccount\AppData\Local\Julia-0.6.0\bin` in System PATH
	+ Linux: env `PATH:$JULIA_HOME/bin:$PATH` <del>Maybe you need reboot and I don't know whether `ln -s` is useful to find it</del>
	+ Mac: Auto detect `/Application/Julia-0.6.app/Contents/Resources/julia/`
+ Set Manually:
	`Settings(Perference) -> Languages & Frameworks -> Julia -> Julia SDK Home Location`
	after choosing the location and click `apply`,if you see `Julia SDK version` become a number instead <Unknown>.
	It's OK~

## Compatible IDEs

The plugin is compatible with any IntelliJ based IDE starting from 2016.1.
If you don't have any yet, try [IntelliJ IDEA Community Edition](https://www.jetbrains.com/idea/),
it's free.

## Which Julia Plugin should I choose

If you search GitHub with "Julia IntelliJ" (data collected at 2018/1/28 (YYYY/M/DD)),
you'll find 4 related repos:

+ snefru/juliafy (incomplete syntax highlight, SDK management, file recognizing, only support MacOS)
+ sysint64/intellij-julia (this only recognize your file as a `Julia file`, and do nothing else)
+ satamas/julia-plugin (ditto)
+ ice1000/julia-intellij
 (better syntax highlight (may not be 100% correct, though),
  inspections and quick fixes,
  basic completions,
  code execution,
  JetBrains style icons,
  SDK management,
  support all three platforms,
  try-evaluate)

Now you know your choice :wink:

## Unfinished features

+ Formatter
+ Reference resolving
+ Completion based on context
+ Displaying numeral output as images
+ More handy run configuration
+ Minor features like folding, bread crumbs, etc.

Please don't downvote our plugin for not having one of the features above, because we're just working on it.

## Contributing

You're encouraged to contribute to the plugin in any form if you've found any issues or missing functionality that you'd want to see.
Check out [CONTRIBUTING.md](./CONTRIBUTING.md) to learn how to setup the project and contributing guidelines.

## Contributors

+ [@ice1000](https://github.com/ice1000)
+ [@zxj5470](https://github.com/zxj5470)
+ [@Hexadecimal](https://github.com/Hexadecimaaal)
