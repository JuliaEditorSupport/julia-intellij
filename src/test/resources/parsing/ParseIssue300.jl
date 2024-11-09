foo = [1, 2, 3]
Tuple{(x for x in foo)...}

f(x) = x^2 + 1
Tuple{f(2)}

com=(x for x in foo)

com=((x for x in foo))