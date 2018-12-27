#=
IntelliJDebugger:
- Julia version: 1.0.2
- Author: zh
- Date: 2018-12-25
=#
if VERSION >= v"0.7.0"
    using Pkg
    pkgs = Pkg.installed()
    if "JSON" in keys(pkgs)
    else
        Pkg.add("JSON")
    end
    using Sockets
    if "DebuggerFramework" in pkgs && pkgs["DebuggerFramework"] >= v"0.1.2+"
        println("DebuggerFramework version $(pkgs["DebuggerFramework"]) correct.")
    else
        println("please add latest version DebuggerFramework by `add DebuggerFramework#master` within pkg REPL.")
    end

    if "ASTInterpreter2" in pkgs && pkgs["ASTInterpreter2"] >= v"0.1.1+"
        println("DebuggerFramework version $(pkgs["ASTInterpreter2"]) correct.")
    else
        println("please add latest version ASTInterpreter2 by `add ASTInterpreter2#master` within pkg REPL.")
    end
else # 0.6
    using Compat
    if !(joinpath(Pkg.dir(),"JSON") |> isdir)
        Pkg.add("JSON")
    end
end

using DebuggerFramework
using ASTInterpreter2
using Sockets
using REPL
using JSON
import DebuggerFramework:locdesc, locinfo, BufferLocInfo, print_sourcecode, print_next_state, print_locals, print_locdesc, Suppressed
import ASTInterpreter2:JuliaStackFrame, pc_expr, isexpr, lookup_var_if_var, maybe_quote

_intellij_debug_mode = true
_intellij_current_stack = []
_intellij_debug_port = 0

function _intellij_send_to(rows)
    if _intellij_debug_port != 0
        f = connect(getipaddr(), _intellij_debug_port)
        write(f,json(rows) * "\n")
    end
end

function DebuggerFramework.print_var(io::IO, name, val, undef_callback)
    if val === nothing
        @assert false
    else
        val = something(val)
        T = typeof(val)
        try
            val = repr(val)
            if length(val) > 150
                val = Suppressed("$(length(val)) bytes of output")
            end
        catch
            val = Suppressed("printing error")
        end
        Dict("name" => string(name), "type" => string(T), "value" => val)
#         println(io, name, "::", T, " = ", val)
    end
end

"""get current stack info with `[functionName,file,line]`"""
function DebuggerFramework.locdesc(frame::JuliaStackFrame, specslottypes = false)
    func = ""
    file = ""
    line = ""
    sprint() do io
        argnames = frame.code.slotnames[2:frame.meth.nargs]
        spectypes = Any[Any for i=1:length(argnames)]
#         print(io, frame.meth.name,'(')
        func *= "$(frame.meth.name)("
        first = true
        for (argname, argT) in zip(argnames, spectypes)
            first || #= print(io, ", ") =# (func *= ", ")
            first = false
#             print(io, argname)
            func *= "$argname"
            !(argT === Any) && #= print(io, "::", argT)=# (func *= "::$argT")
        end

        func *= ")"
        #=
        print(io, ") at ",
            frame.fullpath ? frame.meth.file :
            basename(String(frame.meth.file)),
            ":",frame.meth.line)
        =#
        file = frame.fullpath ? frame.meth.file : basename(String(frame.meth.file))
        line = frame.meth.line
        print(io,json(Dict("function" => func, "file" => file, "line" => line)))
    end
end

function DebuggerFramework.print_status(io, state, frame)
    # Buffer to avoid flickering
    outbuf = IOContext(IOBuffer(), io)
#     printstyled(outbuf, "In ", locdesc(frame), "\n"; color=:bold)
#     println("location: $fl")
    loc = locinfo(frame)

    currentLine = loc.line
    fileInfo = JSON.parse(locdesc(frame))
    !_intellij_debug_mode &&
    if loc !== nothing
        data = if isa(loc, BufferLocInfo)
                loc.data
            else
                VERSION < v"0.7" ? read(loc.filepath, String) :
                read(loc.filepath, String)
            end
        print_sourcecode(outbuf, data,
            loc.line, loc.defline)
    else
        buf = IOBuffer()
        active_line = print_status_synthtic(buf, state, frame, 2, 5)::Int
        code = split(String(take!(buf)),'\n')
        @assert active_line <= length(code)
        for (lineno, line) in enumerate(code)
            if lineno == active_line
                printstyled(outbuf, "=> ", bold = true, color = :yellow); println(outbuf, line)
            else
                printstyled(outbuf, "?  ", bold = true); println(outbuf, line)
            end
        end
    end
    print_next_state(outbuf, state, frame)
#     print(io, String(take!(outbuf.io)))
    about_to_run = String(take!(outbuf.io))

    arr = DebuggerFramework.print_backtrace(state)
    _intellij_send_to(Dict("next" => Dict("line" => currentLine, "expr" => about_to_run, "file" => fileInfo["file"]),"frames" => arr))
end

"""about to run"""
function DebuggerFramework.print_next_state(io::IO, state, frame::JuliaStackFrame)
#     print(io, "About to run: ")
    expr = pc_expr(frame, frame.pc)
    isa(expr, Expr) && (expr = copy(expr))
    if isexpr(expr, :(=))
        expr = expr.args[2]
    end
    if isexpr(expr, :call) || isexpr(expr, :return)
        expr.args = map(var->maybe_quote(lookup_var_if_var(frame, var)), expr.args)
    end
    if isa(expr, Expr)
        for (i, arg) in enumerate(expr.args)
            try
                nbytes = length(repr(arg))
                if nbytes > max(40, div(200, length(expr.args)))
                    expr.args[i] = Suppressed("$nbytes bytes of output")
                end
            catch
                expr.args[i] = Suppressed("printing error")
            end
        end
    end
    print(io, expr)
#     println(io)
end


"""print frame: stackNumber,stackInfo,varInfo"""
function print_frame(_, io::IO, num, frame)
#     print(io, "[$num] ")
#     print_locdesc(io, frame)
    stackInfo = JSON.parse(DebuggerFramework.locdesc(frame))
    global _intellij_current_stack
    varinfo = _intellij_current_stack = []
    print_locals(io, frame)
    Dict("stack" => stackInfo, "vars" => varinfo)
end

function DebuggerFramework.print_backtrace(state)
    arr = []
    for (num, frame) in enumerate(state.stack)
        print_frame(state, Base.pipe_writer(state.terminal), num, frame) |> it->push!(arr,it)
    end
    arr
end

function DebuggerFramework.print_locals(io::IO, frame::JuliaStackFrame)
    global _intellij_current_stack
    for i = 1:length(frame.locals)
        if !isa(frame.locals[i], Nothing)
            # #self# is only interesting if it has values inside of it. We already know
            # which function we're in otherwise.
            val = something(frame.locals[i])
            if frame.code.slotnames[i] == Symbol("#self#") && (isa(val, Type) || sizeof(val) == 0)
                continue
            end
            ret = DebuggerFramework.print_var(io, frame.code.slotnames[i], something(frame.locals[i]), nothing)
            push!(_intellij_current_stack,ret)
        end
    end
    for i = 1:length(frame.sparams)
        ret = DebuggerFramework.print_var(io, frame.meth.sparam_syms[i], frame.sparams[i], nothing)
        push!(_intellij_current_stack,ret)
    end
end