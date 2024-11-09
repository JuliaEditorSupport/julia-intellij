@eval export $each
@eval const $each = T($n)
@eval ($f)(A::Number, B::AbstractArray) = broadcast($f, A, B)
quote
    if $I isa Generator && ($I).f isa Type
        ($I).f
    else
        Core.Compiler.return_type(first, Tuple{typeof($I)})
    end
end