cons(R(hd, :<:,r),asd)

:(<:)

cons(R(hd, :>:,r),asd)

function bnd(out::Set{Any})
    @match t begin
        if t isa zxj end => foreach(a,b)
        if t isa zxj end => push!(emmm)
        a => @error "emmmm"
    end
end

#307
[1,2,3] * transpose([1,2,4])
[1,2,3] .* transpose([1,2,4])

function retry(f::Function;  delays=ExponentialBackOff(), check=nothing)
    (args...; kwargs...) -> begin
        y = iterate(delays)
        while y !== nothing
            (delay, state) = y
            try
                return f(args...; kwargs...)
            catch e
                y === nothing && rethrow(e)
                if check !== nothing
                    result = check(state, e)
                    state, retry_or_not = length(result) == 2 ? result : (state, result)
                    retry_or_not || rethrow(e)
                end
            end
            sleep(delay)
            y = iterate(delays, state)
        end
        # When delays is out, just run the function without try/catch
        return f(args...; kwargs...)
    end
end

ident_cmp(a::Integer, b::String ) = isempty(b) ? +1 : -1

:(const assn = _)
:(const $assn = _)
:(const assn = $_)
:(const $assn = $_)

@forward Foo.bar f, g, h
using MLStyle.toolz: ($)

patternOr  = (p1, p2) -> body ->
    let p1 = p1(body), p2 = p2(body)
        tmp = mangle(Infras)
        @format [tmp, p1, p2] quote
            tmp = p1
            tmp === failed ? p2 : tmp
        end
    end

$(a)

:(typename1{a}) => typename
:(typename2{$}) => typename
:(typename3{$a}) => typename
:($typename4{$a}) => typename
:($typename5{$(a)}) => typename
:($typename6{$(a...)}) => typename

:($typename) => typename
