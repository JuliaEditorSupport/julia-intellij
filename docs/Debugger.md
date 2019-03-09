# Julia Debugger
This is an English document for our Julia Debugger of Julia-IntelliJ.
Our debugger is a wrapper based on [Keno Fischer](https://github.com/Keno)'s [DebuggerFramework](https://github.com/Keno/DebuggerFramework.jl) and [ASTInterpreter2](https://github.com/Keno/ASTInterpreter2.jl). 

**First of all, thanks to Keno!**

## Overview
![gif](https://user-images.githubusercontent.com/20026798/50418049-670a7080-0864-11e9-96cf-d0ebc5b26431.gif)

## Installation & Usage (from GitHub repository)

Install IntelliJ IDEA (or other JetBrains IntelliJ platform IDEs such as PyCharm / WebStorm / GoLand / and even Android Studio :joy:).

## Setup Julia in IntelliJ

+ [English video instruction on YouTube](https://www.youtube.com/watch?v=gjRhvPBiasU)
+ [Chinese video instruction on Bilibili](https://www.bilibili.com/video/av20155813)

## Dependencies

- ASTInterpreter2#73711a4
- DebuggerFramework#78d649e
- JSON
- Julia 0.7+

> (using the master branch is required by the debugger)

### add packages
```julia
(v1.0) pkg> add DebuggerFramework#78d649e
(v1.0) pkg> add ASTInterpreter2#73711a4
(v1.0) pkg> add JSON
```

Create a Julia project with the plugin,
and write simple example codes:
```julia
function func()
    i = 10
    while i>0
        println(sin(i))
        i-=2
    end
end

@enter func()
```

use `@enter` macro before a function call expression as `ASTInterpreter2` does. 
And **put a breakpoint** at any line (which is useless right now but necessary for the debugger).
The work is done by the `@enter` macro, not breakpoints.

then you'll notice that the `Debug` button at upper right corner is enabled, which is shown as a green bug. **Press it!**

**Note!!!** 
> If you use this debugger under **Windows**, the first time you run the debugger may froze your IDE about 2 minutes tested on my PC, while it'll become faster afterwards. **So we strongly recommend not to use this debugger under Windows.**

## Debug mode
**Relayout** your debug panel like this by dragging:

![relayout](https://user-images.githubusercontent.com/20026798/50675140-172b6680-1027-11e9-93cb-a25370a37667.jpg)
You can see call stackframes at the left of panel and local variables at right.
## Functions
![step over](https://user-images.githubusercontent.com/20026798/50675203-77baa380-1027-11e9-8e14-e712ae9556b6.jpg)
- **Step over :** Run next call in current file. The `nc` command for DebuggerFramework.
- **Step into :** Run the next call into a deeper stackframes. The `sg` command for DebuggerFramework.
- **Froce Step into :** (maybe useless). `s` command.
- **Step out :** Run out of current stackframes. `finish` command.
- <del>**Run to cursor**(unsupported currently). Do not try this button.</del>
- **Rerun**: Rerun the Julia program and debug.
- **Resume |▶**: Rerun the debug when debug session is not terminated. (**Note that** debug session will not exit until you press the **Red Stop Rectangle Button**)

## Thanks
- [DebuggerFramework.jl](https://github.com/Keno/DebuggerFramework.jl)
- [ASTInterpreter2.jl](https://github.com/Keno/ASTInterpreter2.jl)
