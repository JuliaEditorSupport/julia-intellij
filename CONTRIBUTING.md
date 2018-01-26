# julia-intellij contributing guide

## You must

0. Put all Nls into the [resource bundle](src/org/ice1000/julia/lang/julia-bundle.properties)
0. Use as much `@NotNull` and `@Nullable` as you can in Java codes

## You must not

0. Break the code style -- use tab indents with spaces aligns
0. Open pull requests just to fix code style, or use some syntax sugar (this is not SharpLang!)
0. Add generated files into the git repo (including the parser!)
0. Violate the open source license

## You should

0. Use Kotlin except UI, but if you only know Java we can help you convert
0. Name your files with `julia-xxx.kt`
0. Put all highly related classes into a single file
0. Use English, but we also read Chinese so if you only know Chinese just use it

## You don't have to

0. Write comments, except you're using magics. Tell us if you do
0. Write tests, because we'll review your codes carefully
