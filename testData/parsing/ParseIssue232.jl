function foo(a, b; c=3)
    return a + b + c
end

function foo2(args...; kwargs...)
    foo(args...; kwargs...)
end
