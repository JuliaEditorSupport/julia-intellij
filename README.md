# Julia plugin for the IntelliJ Platform

<!-- badges -->

## Status

This is a **work in progress**, some features are implemented partially, there may be performance and stability problems.

## Installation \& Usage

Install IntelliJ IDEA, open `Settings | Plugins | Browse repositories`,
install Julia plugin, and create a Julia project (you'll be asked to create a Julia SDK, don't worry it's easy).

## Compatible IDEs

The plugin is compatible with any IntelliJ based IDE starting from 2016.1.
If you don't have any yet, try [IntelliJ IDEA Community Edition](https://www.jetbrains.com/idea/),
it's free.

## Alternatives

If you search GitHub with "Julia IntelliJ" (data collected at 2018/1/28 (YYYY/M/DD)),
you'll find 4 related repos:

+ snefru/juliafy (poor syntax highlight, SDK management, file recognizing, only support MacOS)
+ sysint64/intellij-julia (this only recognize your file as a `Julia file`, and do nothing else)
+ satamas/julia-plugin (ditto)
+ ice10000/julia-intellij
 (better syntax highlight,
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
+ More handy run configuration
+ Minor features like folding, bread crumbs, etc.

## Contributing

You're encouraged to contribute to the plugin in any form if you've found any issues or missing functionality that you'd want to see.
Check out [CONTRIBUTING.md](./CONTRIBUTING.md) to learn how to setup the project and contributing guidelines.

## Contributors

+ [@ice1000](https://github.com/ice1000)
+ [@zxj5470](https://github.com/zxj5470)
+ [@Hexadecimal](https://github.com/Hexadecimaaal)
