# Julia plugin for the IntelliJ Platform

## Status

This is a **work in progress**, some features are implemented partially, there may be performance and stability problems.

[![](https://tinyurl.com/y9e4n2zh)](https://github.com/ice1000/julia-intellij)
[![JetBrains plugins](https://img.shields.io/jetbrains/plugin/v/10413-julia.svg)](https://plugins.jetbrains.com/plugin/10413-julia)
[![JetBrains plugins](https://img.shields.io/jetbrains/plugin/d/10413-julia.svg)](https://plugins.jetbrains.com/plugin/10413-julia)

CI | Status
:---:|:---:
Travis CI (with IdeaC)|[![Travis CI Build status](https://travis-ci.org/ice1000/julia-intellij.svg)](https://travis-ci.org/ice1000/julia-intellij)
AppVeyor (on Windows)|[![AppVeyor Build status](https://ci.appveyor.com/api/projects/status/jboqu7yt2vhqpmfr?svg=true)](https://ci.appveyor.com/project/ice1000/julia-intellij)
CircleCI (with IdeaC)|[![CircleCI Build status](https://circleci.com/gh/ice1000/julia-intellij.svg?style=svg)](https://circleci.com/gh/ice1000/julia-intellij)
CodeShip (branch master)|[![CodeShip Build status](https://app.codeship.com/projects/4c89a940-ec81-0135-9688-6eaa099eb415/status?branch=master)](https://app.codeship.com/projects/270342)

## Installation \& Usage

Install IntelliJ IDEA, open `Settings | Plugins | Browse repositories`,
install Julia plugin, and create a Julia project.

### Configuration

If you're creating a new project, the plugin will automatically detect a julia executable.<br/>
But that one might not be correct, so you can select one yourself as well.

### Configure an existing project

Open `File | Settings | Languages & Frameworks | Julia`,
choose a valid Julia executable and click `Apply`.

## Screenshots

![](https://plugins.jetbrains.com/files/10413/screenshot_17870.png)
![](https://plugins.jetbrains.com/files/10413/screenshot_17865.png)

## Compatible IDEs

The plugin is compatible with any IntelliJ based IDE starting from 2016.1.
If you don't have any yet, try [IntelliJ IDEA Community Edition](https://www.jetbrains.com/idea/),
it's free.

## Feedback

You can report the error to us when you see this plugin throwing exceptions, this will create a GitHub issue labeled
with `pending` and we'll deal with it.<br/>
This function comes from the Mathematica plugin and I just did very little refactor without changing any classes' or
functions' names.

## Alternatives

If you don't like JetBrains IDE, turn right and search `JuliaPro` or `Juno`.

Otherwise:<br/>
If you search GitHub with "Julia IntelliJ" (data collected at 2018/1/28 (YYYY/M/DD)),
you'll find 4 related repositories:

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
  External Julia tools integration,
  support all three platforms,
  try-evaluate)

Now you know your choice :wink:

## Unfinished features

+ Formatter
+ Integrating existing Julia tools
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
+ [@HoshinoTented](https://github.com/HoshinoTented)
+ [@Hexadecimal](https://github.com/Hexadecimaaal)
