function add(x, y)
    println("x is $x and y is $y")
    # Functions return the value of their last statement
    x + y
end

add(5, 6) # => 11 after printing out "x is 5 and y is 6"
# Compact assignment of functions
f_add(x, y) = x + y # => "f (generic function with 1 method)"

f(x, y) = x + y, x - y
f(3, 4) # => (7, -1)

function varargs(args...)
    return args
    # use the keyword return to return anywhere in the function
end

# => varargs (generic function with 1 method)

# The ... is called a splat.
# We just used it in a function definition.
# It can also be used in a function call,
# where it will splat an Array or Tuple's contents into the argument list.
add([5,6]...) # this is equivalent to add(5,6)

x = (5,6)     # => (5,6)
add(x...)     # this is equivalent to add(5,6)

# You can define functions that take keyword arguments
function keyword_args(;k1=4,name2="hello") # note the ;
    return Dict("k1"=>k1,"name2"=>name2)
end

keyword_args(name2="ness") # => ["name2"=>"ness","k1"=>4]
keyword_args(k1="mine") # => ["k1"=>"mine","name2"=>"hello"]
keyword_args() # => ["name2"=>"hello","k1"=>4]

# You can combine all kinds of arguments in the same function
function all_the_args(normal_arg, optional_positional_arg=2; keyword_arg="foo")
    println("normal arg: $normal_arg")
    println("optional arg: $optional_positional_arg")
    println("keyword arg: $keyword_arg")
end

all_the_args(1, 3, keyword_arg=4)
# prints:
#   normal arg: 1
#   optional arg: 3
#   keyword arg: 4

# You can also name the internal function, if you want
function create_adder(x)
    function adder(y)
        x + y
    end
    adder
end

add_10 = create_adder(10)
add_10(3) # => 13


# You can define functions with optional positional arguments
function defaults(a,b,x=5,y=6)
    return "$a $b and $x $y"
end

defaults('h','g') # => "h g and 5 6"
defaults('h','g','j') # => "h g and j 6"
defaults('h','g','j','k') # => "h g and j k"
try
    defaults('h') # => ERROR: no method defaults(Char,)
    defaults() # => ERROR: no methods defaults()
catch e
    println(e)
end

# There are built-in higher order functions
map(add_10, [1,2,3]) # => [11, 12, 13]
filter(x -> x > 5, [3, 4, 5, 6, 7]) # => [6, 7]

# We can use list comprehensions for nicer maps
[add_10(i) for i=[1, 2, 3]] # => [11, 12, 13]
[add_10(i) for i in [1, 2, 3]] # => [11, 12, 13]

Pointy{:x}(1)
X{(1, 2)}(1)
X{Int}(1)

WeakKeyDict1(ps::Pair{K}...)             where {K}   = WeakKeyDict{K,Any}(ps)
# WeakKeyDict2(ps::Pair{K} where K)             where {K}   = WeakKeyDict{K,Any}(ps)
# WeakKeyDict3(ps::Pair{K} where K ...)             where {K}   = WeakKeyDict{K,Any}(ps)
#
# WeakKeyDict4(ps::(Pair{K,V} where K)...) where {V}   = WeakKeyDict{Any,V}(ps)
@eval function $(Symbol("atomic_", opname, "!"))(var::Atomic{T}, val::T) where T<:FloatTypes
        IT = inttype(T)
        old = var[]
        while true
            new = $op(old, val)
            cmp = old
            old = atomic_cas!(var, cmp, new)
            reinterpret(IT, old) == reinterpret(IT, cmp) && return new
            # Temporary solution before we have gc transition support in codegen.
            ccall(:jl_gc_safepoint, Cvoid, ())
        end
    end