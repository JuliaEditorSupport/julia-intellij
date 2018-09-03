macro doc(x...)
    docex = atdoc(__source__, __module__, x...)
    isa(docex, Expr) && docex.head === :escape && return docex
    return Expr(:escape, Expr(unescape, docex, typeof(atdoc).name.module))
end
macro __doc__(x)
    return Expr(:escape, Expr(:block, Expr(:meta, :doc), x))
end
atdoc     = (source, mod, str, expr) -> Expr(:escape, expr)
# atdoc!(λ) =
global atdoc = λ


# simple stand-alone print definitions for debugging
io_pointer(::CoreSTDERR) = Intrinsics.pointerref(Intrinsics.cglobal(:jl_uv_stderr, Ptr{Cvoid}), 1, 1)

unsafe_write(io::IO, x::Ptr{UInt8}, nb::UInt) =
    (ccall(:jl_uv_puts, Cvoid, (Ptr{Cvoid}, Ptr{UInt8}, UInt), io_pointer(io), x, nb); nb)
function write(io::IO, x::String)
    nb = sizeof(x)
    unsafe_write(io, ccall(:jl_string_ptr, Ptr{UInt8}, (Any,), x), nb)
    return nb
end

show(io::IO, @nospecialize x) = ccall(:jl_static_show, Cvoid, (Ptr{Cvoid}, Any), io_pointer(io), x)
print(io::IO, x::AbstractChar) = ccall(:jl_uv_putc, Cvoid, (Ptr{Cvoid}, Char), io_pointer(io), x)
print(io::IO, x::String) = (write(io, x); nothing)
print(io::IO, @nospecialize x) = show(io, x)
print(io::IO, @nospecialize(x), @nospecialize a...) = (print(io, x); print(io, a...))
println(io::IO) = (write(io, 0x0a); nothing) # 0x0a = '\n'
println(io::IO, @nospecialize x...) = (print(io, x...); println(io))

show(@nospecialize a) = show(stdout, a)
println(@nospecialize a...) = println(stdout, a...)

struct GeneratedFunctionStub
    gen
    argnames::Array{Any,1}
    spnames::Union{Nothing, Array{Any,1}}
    line::Int
    file::Symbol
    expand_early::Bool
end
"""
# invoke and wrap the results of @generated
function (g::GeneratedFunctionStub)(@nospecialize args...)
    body = g.gen(args...)
    if body isa CodeInfo
        return body
    end
    lam = Expr(:lambda, g.argnames,
               Expr(Symbol("scope-block"),
                    Expr(:block,
                         LineNumberNode(g.line, g.file),
                         Expr(:meta, :push_loc, g.file, Symbol("@generated body")),
                         Expr(:return, body),
                         Expr(:meta, :pop_loc))))
    if g.spnames === nothing
        return lam
    else
        return Expr(Symbol("with-static-parameters"), lam, g.spnames...)
    end
end
"""
NamedTuple() = NamedTuple{(),Tuple{}}(())

"""
    NamedTuple{names}(args::Tuple)

Construct a named tuple with the given `names` (a tuple of Symbols) from a tuple of values.
"""
NamedTuple{names}(args::Tuple) where {names} = NamedTuple{names,typeof(args)}(args)

using .Intrinsics: sle_int, add_int

macro generated()
    return Expr(:generated)
end

function NamedTuple{names,T}(args::T) where {names, T <: Tuple}
    if @generated
        N = nfields(names)
        flds = Array{Any,1}(undef, N)
        i = 1
        while sle_int(i, N)
            arrayset(false, flds, :(getfield(args, $i)), i)
            i = add_int(i, 1)
        end
        Expr(:new, :(NamedTuple{names,T}), flds...)
    else
        N = nfields(names)
        NT = NamedTuple{names,T}
        flds = Array{Any,1}(undef, N)
        i = 1
        while sle_int(i, N)
            arrayset(false, flds, getfield(args, i), i)
            i = add_int(i, 1)
        end
        ccall(:jl_new_structv, Any, (Any, Ptr{Cvoid}, UInt32), NT,
              ccall(:jl_array_ptr, Ptr{Cvoid}, (Any,), flds), toUInt32(N))::NT
    end
end

# constructors for built-in types

import .Intrinsics: eq_int, trunc_int, lshr_int, sub_int, shl_int, bitcast, sext_int, zext_int, and_int

throw_inexacterror(f::Symbol, @nospecialize(T), val) = (@_noinline_meta; throw(InexactError(f, T, val)))

function is_top_bit_set(x)
    @_inline_meta
    eq_int(trunc_int(UInt8, lshr_int(x, sub_int(shl_int(sizeof(x), 3), 1))), trunc_int(UInt8, 1))
end
