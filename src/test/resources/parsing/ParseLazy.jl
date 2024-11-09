function extract_tvars(t :: AbstractArray)
    @match t begin
        [] => nil()
        [hd && if hd isa Symbol end, tl...] => cons(TypeVar(hd), extract_tvars(tl))
        [:($hd <: $r), tl...] =>  cons(Relation(hd, :<:, r), extract_tvars(tl))
        [:($hd >: $(r)), tl...] =>  cons(Relation(hd, Symbol(">:"), r), extract_tvars(tl))
        _ => @error "invalid tvars"
    end
end
# why?
