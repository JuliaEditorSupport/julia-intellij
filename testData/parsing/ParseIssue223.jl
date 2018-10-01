fack(a::T) where T <: Int = 1

fack(a::T) where {T <: Int} = 1

fack(a::T, b::S) where {T <: Int, S <: String} = 1

fack(a::T, b::S) where T <: Int where S <: Int = 2
