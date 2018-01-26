# julia-intellij

<!-- badges -->

This is a Julia IDE based on the IntelliJ Platform -- say, the Julia plugin for IntelliJ IDEA.

## Why IntelliJ

It's time to give up programming in a browser and turn to use a real integrated-developing-environment.<br/>
┗:smiley:┛ ┏:smiley:┓ ┗:smiley:┛ ┏:smiley:┓ ┗:smiley:┛ ┏:smiley:┓

## Build

First clone this repo:

```shell
$ git clone https://github.com/ice1000/julia-intellij.git
```

Create a plugin project from your cloned source, and use [Grammar-Kit](https://github.com/JetBrains/Grammar-Kit)
to generate the Parser and Lexer.

Then, click `Build | Prepare Plugin Module 'julia-intellij' for deployment`, and you'll see a jar
appears at the project root.
