#=
ParseIssue195:
- Julia version: 1.0.0
- Author: zxj5470
- Date: 2018-08-19
=#

"abstractarray.jl"
a[i...]
dest[i+=1] = a
B[pos:p1, :] = Ak

concatenate_setindex!(R, v, I...) = (R[I...] .= (v,); R)

concatenate_setindex!(R, X::AbstractArray, I...) = (R[I...] = X)

"abstractarraymath.jl"
*(x::AbstractArray{<:Number,2}) = x

"base/strings/io.jl"
==(a::AbstractString, b::AbstractString) = cmp(a, b) == 0
⊇(l, r) = r ⊆ l
# < = <= and unicode...

# """ these success
# '\a' <= c
# c <= '\r'
#
# but failed:
# """
#
# '\a' <= c <= '\r'
#
"abstractarraymath.jl"
y[1][isa(v, KeySet) ? 1 : 2]

# `=>`, `==` asOp
pairs(collection) = Generator(=>, keys(collection), values(collection))

isin = in(pair, r, ==)

function ==(l::AbstractDict, r::AbstractDict)
end

"abstractset.jl"
# union as function name in v1.0
function union end

# # some unicode assign like ∪ ∩ ⊆ ∉ ∈
const ∪ = union
const ∩ = intersect
const ⊆ = issubset

"accumulate.jl"
# cannot parse this as `compact function` but `where statement`.
# cumsum!(B::AbstractArray{T}, A; dims::Integer) where {T} = accumulate(add_sum, B, A, dims=dims)

# `isa`
if nt isa NamedTuple{(:init,)}
end

"array.jl"
# where T where N.
function reshape(a::Array{T,N}, dims::NTuple{N,Int}) where T where N
end
function sub2ind_gen_impl(dims::Type{T}, I...) where T <: NTuple{N,Any} where N
end

# endOfLine after `a`
Union{eltype(inds), Nothing}[
        get(bdict, i, nothing) for i in a
]

# idk what is
filter(f, As::AbstractArray) = As[map(f, As)::AbstractArray{Bool}]
map(f, As)::AbstractArray{Bool}

findmax(a) = _findmax(a, :)

:(a{*})
:(a{+})