struct Point{T <: Real}
    x::T
    y::T
end

Point{Int}[Point(1,2)]

Point{Int}[]