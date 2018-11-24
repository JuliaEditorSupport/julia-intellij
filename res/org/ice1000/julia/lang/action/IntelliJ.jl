#=
backend_inter.jl:
- Julia version: 1.0.2
- Author: zxj5470
- Date: 2018-11-23
=#

if VERSION >= v"0.7.0"
	using Pkg
end

println("Initialize julia-intellij REPL environment...")

if "PyCall" in keys(Pkg.installed())
	using PyCall
	@eval @pyimport matplotlib
	function use_intellij_backend()
			pushfirst!(PyVector(pyimport("sys")["path"]),@__DIR__)
			matplotlib.use("module://backend_interagg")
			println("set plot outputs redirect to intellij.")
	end

	println("Initialize done.\n")
  println("""execute `use_intellij_backend()` to enable JuliaSciView Plots,
  supported backends if you installed:
      `matplotlib.pyplot`""")

else
	 println("""PyCall package not found, to use JuliaSciView Plots,
	 please add PyCall and install matplotlib.pyplot package.""")
	 println("Initialize done.")
end