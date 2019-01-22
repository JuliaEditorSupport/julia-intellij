# Julia plugin for the IntelliJ Platform

This is a **work in progress**, some features are implemented partially, there may be performance and stability problems.

[![](https://tinyurl.com/y9e4n2zh)](https://github.com/ice1000/julia-intellij)
[![JetBrains plugins](https://img.shields.io/jetbrains/plugin/v/10413-julia.svg)](https://plugins.jetbrains.com/plugin/10413-julia)
[![JetBrains plugins](https://img.shields.io/jetbrains/plugin/d/10413-julia.svg)](https://plugins.jetbrains.com/plugin/10413-julia)
[![Documentation Status](https://readthedocs.org/projects/julia-intellij/badge/?version=latest)](http://julia-intellij.readthedocs.io/en/latest/?badge=latest)
[![Join the chat at https://gitter.im/julia-intellij/Lobby](https://badges.gitter.im/julia-intellij/Lobby.svg)](https://gitter.im/julia-intellij/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

<table>
  <tr>
    <th>CI</th>
    <th>Status</th>
  </tr>
  <tr>
    <td>Travis CI (with IdeaC)</td>
    <td><a href="https://travis-ci.org/ice1000/julia-intellij"><img src="https://travis-ci.org/ice1000/julia-intellij.svg" alt="Travis CI Build status"></a></td>
  </tr>
  <tr>
    <td>AppVeyor (on Windows)</td>
    <td><a href="https://ci.appveyor.com/project/ice1000/julia-intellij"><img src="https://ci.appveyor.com/api/projects/status/jboqu7yt2vhqpmfr?svg=true" alt="AppVeyor Build status"></a></td>
  </tr>
  <tr>
    <td>CircleCI (with tests)</td>
    <td><a href="https://circleci.com/gh/ice1000/julia-intellij"><img src="https://circleci.com/gh/ice1000/julia-intellij.svg?style=svg" alt="CircleCI Build status"></a></td>
  </tr>
  <tr>
    <td>CodeShip (branch master)</td>
    <td><a href="https://app.codeship.com/projects/270342"><img src="https://app.codeship.com/projects/4c89a940-ec81-0135-9688-6eaa099eb415/status?branch=master" alt="CodeShip Build status"></a></td>
  </tr>
</table>

## Installation \& Usage

Install IntelliJ IDEA (or other JetBrains IDEs),
open `Settings | Plugins | Browse repositories`,
install Julia plugin, and create a Julia project.

For detailed use instruction, visit: https://julia-intellij.readthedocs.io/en/latest/<br/>
To download a nightly build (buggy!), visit https://ci.appveyor.com/project/ice1000/julia-intellij/build/artifacts/ .<br/>
To learn about the test summery, visit https://circleci.com/build-insights/gh/ice1000/julia-intellij/master .

### Video Instructions

+ English video instruction on YouTube: https://www.youtube.com/watch?v=gjRhvPBiasU
+ Chinese video instruction on Bilibili: https://www.bilibili.com/video/av20155813

## Screenshots

### Execution
![](https://plugins.jetbrains.com/files/10413/screenshot_17880.png)

### Doc-String
![](https://plugins.jetbrains.com/files/10413/screenshot_17881.png)

### Refactoring and Editing
![](https://plugins.jetbrains.com/files/10413/screenshot_17879.png)
![](https://plugins.jetbrains.com/files/10413/screenshot_17932.png)

### Package Manager
![](https://github.com/zxj5470/julia-intellij-docs-cn/blob/master/screenshots/pkg-manager.gif?raw=true)

### Plots
![](https://user-images.githubusercontent.com/20026798/49950430-c72f1780-ff32-11e8-8498-68ebcad8c4b5.gif)

### VarInfo (Workspace)
![](https://user-images.githubusercontent.com/20026798/50019689-91f7f780-000e-11e9-85ce-ab602cab6505.png)

### Debugger (nightly-build)
> based on DebuggerFramework and ASTInterpreter2

![](https://user-images.githubusercontent.com/20026798/50418049-670a7080-0864-11e9-96cf-d0ebc5b26431.gif)

## Compatible IDEs

The plugin is compatible with any IntelliJ based IDE starting from 2016.1.
If you don't have any yet, try [IntelliJ IDEA Community Edition](https://www.jetbrains.com/idea/),
it's free.

## Alternatives

If you don't like JetBrains IDE, turn right and search `JuliaPro` or `Juno`.

Otherwise:<br/>
If you search GitHub with "Julia IntelliJ" (data collected at 2018/1/28 (YYYY/M/DD)),
you'll find 4 related repositories:

+ snefru/juliafy (incomplete syntax highlight, SDK management, file recognizing, only support MacOS)
+ sysint64/intellij-julia (this only recognize your file as a `Julia file`, and do nothing else)
+ satamas/julia-plugin (ditto)
+ ice1000/julia-intellij (too many [features](https://julia-intellij.readthedocs.io/en/latest/Features.html), can't list here)

Now you know your choice 😉

## Contributing

You're encouraged to contribute to the plugin in any form if you've found any issues or missing functionality that you'd want to see.
Check out [CONTRIBUTING.md](./CONTRIBUTING.md) to learn how to setup the project and contributing guidelines.

## Contributors

+ [@ice1000](https://github.com/ice1000)
+ [@zxj5470](https://github.com/zxj5470)
+ [@HoshinoTented](https://github.com/HoshinoTented)
+ [@Hexadecimal](https://github.com/Hexadecimaaal)
