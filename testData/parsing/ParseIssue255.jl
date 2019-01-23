unzip(xy::StaticArrays.SVector{2,T}) where {T}        = T[xy[1]], T[xy[2]]

# 2
default(; newdefs...)
plot!(; xlabel = s, kw...)
# 2.5 `dims...` cause it.
function GridLayout(dims...;
                parent = RootLayout(),
                kw...)
end
# 2.6 redundant comma
scaled_ticks = optimize_ticks(sf(amin),sf(amax);
        k_min = 4, # minimum number of ticks
        k_max = 8, # maximum number of ticks
    )[1]

# 3
const TicksArgs = Union{AVec{T}, Tuple{AVec{T}, AVec{S}}, Symbol} where {T<:Real, S<:AbstractString}

# 9
minorticks[amin .<= minorticks .<= amax]
