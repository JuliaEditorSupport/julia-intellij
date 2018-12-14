#=
IntelliJ.jl:
- Julia version: 1.0.2
- Author: zxj5470
- Date: 2018-11-23
=#

if VERSION >= v"0.7.0"
	using Pkg
  using DelimitedFiles
  using Sockets
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

  println("""execute `use_intellij_backend()` to enable JuliaSciView Plots,
  supported backends if you installed:
      `matplotlib.pyplot`""")

else
	 println("""PyCall package not found, to use JuliaSciView Plots,
	 please add PyCall and install matplotlib.pyplot package.""")
end

const _intellij_full_angle_space_key = "　"

_intellij_filter_names(v) = string(v) ∉ ["Base", "Core", "Main", "InteractiveUtils", "matplotlib"]

function _intellij_x_to_bytes(x)
   sz = sizeof(x)
   if VERSION >= v"0.7.0"
     ret = Vector{UInt8}(undef,sz)
     src_ptr = convert(Ptr{UInt8}, pointer_from_objref(x))
     unsafe_copyto!(pointer(ba), src_ptr, sz)
   else
     ret = Vector{UInt8}(sz)
     src_ptr = convert(Ptr{UInt8}, pointer_from_objref(x))
     unsafe_copy!(pointer(ba), src_ptr, sz)
   end
   return ret
end

function _intellij_send_to(rows)
    open("$(tempdir())/tempJuliaVarInfo.tsv","w") do f
      writedlm(f,rows,'\t')
    end
end

function _intellij_stringfy(value)
    if isa(value,Array)
        "$(join(value,_intellij_full_angle_space_key))"
    else
        repr(value)
    end
end

function _intellij_varinfo(m=Main)
	@eval using Base:summarysize, format_bytes
	pattern="_intellij_"
	rows = Array[ let value = getfield(m, v)
  name = string(v)
  size = value === Main || value === Base || value === Core ? "" : format_bytes(summarysize(value))
  summary_info = summary(value)
  info = repr(_intellij_stringfy(value))
  if size == "0 bytes" && summary_info == "typeof($(name))"
		summary_info = "function"
	end
  String[name, size, info, summary_info]
	end
	for v in sort!(names(m)) if isdefined(m, v) &&
	!occursin(pattern, string(v)) && _intellij_filter_names(v)]
  _intellij_send_to(rows)
  return
end

println("\nInitialize done.\n")