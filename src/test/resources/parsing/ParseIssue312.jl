(+, -, *, /)
(-, +, /, *)
(*, -, /, +)
(^, -, +, /)

if isexpr(x, :.)
    emmm
end
if isexpr(ex, :->) && length(ex.args) > 1
    return docm(source, mod, ex.args...)
else
    # TODO: this is a shim to continue to allow `@doc` for looking up docstrings
    REPL = Base.REPL_MODULE_REF[]
    return REPL.lookup_doc(ex)
end