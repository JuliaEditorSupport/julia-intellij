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
function differentiate(x::Vector, f::Vector)::Vector
    length(x) == length(f) ? n = length(x) : throw(DimensionMismatch("The two arguments must have the same length!"))

    derivative = zeros(n)
    derivative[1] = (f[2] - f[1]) / (x[2] - x[1])
    for i in 2:(n - 1)
        derivative[i] = (f[i + 1] - f[i - 1]) / (x[i + 1] - x[i - 1])
    end
    derivative[n] = (f[n] - f[n - 1]) / (x[n] - x[n - 1])
    derivative
end
# 4 for expr
 (xf, xinv), (yf, yinv) = ((scalefunc(s),invscalefunc(s)) for s in (xscale,yscale))

plt[:size] = (s * scale for s in sz)

# 5
typeof(g)((x_u, (groupedvec2mat(x_ind, x, arg, groupby, NaN) for arg in last_args)...))

# 6. comparable operator in arrayIndex
st == :pre ? i : i - 1
# todo
# newx[idx - 1] = x[st == :pre ? i:(i - 1)]

# 9
minorticks[amin .<= minorticks .<= amax]
