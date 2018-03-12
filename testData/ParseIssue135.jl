function DateTime(Y::AbstractArray{<:AbstractString}, df::DateFormat=ISODateTimeFormat)
    return reshape(DateTime[parse(DateTime, y, df) for y in Y], size(Y))
end

tokens = Type[CONVERSION_SPECIFIERS[letter] for letter in letters]
value_names = Symbol[genvar(t) for t in tokens]
value_defaults = Tuple(CONVERSION_DEFAULTS[t] for t in tokens)
R = typeof(value_defaults)

assign_defaults = Expr[
    quote
        $name = $default
    end
    for (name, default) in zip(value_names, value_defaults)
]


($op)(X::StridedArray{<:GeneralPeriod}, Y::StridedArray{<:GeneralPeriod}) =
    reshape(CompoundPeriod[($op)(x, y) for (x, y) in zip(X, Y)], promote_shape(size(X), size(Y)))

# ②
# (==)(x::CompoundPeriod, y::CompoundPeriod) = canonicalize(x).periods == canonicalize(y).periods

last = Union{}
readtask = @schedule readline(io)
Aasd.asd.dasf.workers()[(nextidx % nworkers()) + 1]
finalizer(w, (w)->if myid() == 1 manage(w.manager, w.id, w.config, :finalize) end)

Int[x.id for x in PGRP.workers if isa(x, LocalProcess) || (x.state == W_CONNECTED)]

function DateTime(Y::AbstractArray{<:AbstractString}, df::DateFormat=ISODateTimeFormat)
    return reshape(DateTime[parse(DateTime, y, df) for y in Y], size(Y))
end

# ②
# if (x.id::Int) == id && (isa(x, LocalProcess) || (x::Worker).state == W_CONNECTED)
#     return true
# end