bbb = Symbol("Somefuns")
aaa = quote
    export $(esc(bbb)), $esc
    $(esc(bbb))() = 1
end