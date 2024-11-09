function emm(a::Type{Array{T} where T})
end

(::Type{Array{T,N} where T})(x::AbstractArray{S,N}) where {S,N} = Array{S,N}(x)

convert(::Type{Any}, @nospecialize(x)) = x

test(a,b;emm=1,kw...)

test(kw...)
