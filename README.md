# Julia plugin for the IntelliJ Platform

[![](https://tinyurl.com/y9e4n2zh)](https://github.com/ice1000/julia-intellij)
[![JetBrains plugins](https://img.shields.io/jetbrains/plugin/v/10413-julia.svg)](https://plugins.jetbrains.com/plugin/10413-julia)
[![JetBrains plugins](https://img.shields.io/jetbrains/plugin/d/10413-julia.svg)](https://plugins.jetbrains.com/plugin/10413-julia)

## Status

This is a **work in progress**, some features are implemented partially, there may be performance and stability problems.

## Installation \& Usage

Install IntelliJ IDEA, open `Settings | Plugins | Browse repositories`,
install Julia plugin, and create a Julia project.

### Configuration

If you're creating a new project, the plugin will automatically detect a julia executable.<br/>
But that one might not be correct, so you can select one yourself as well.

#### Configure an existing project

Open `File | Settings | Languages & Frameworks | Julia`,
choose a valid Julia executable and click `Apply`.

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
