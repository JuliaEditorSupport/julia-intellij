#=
# ParseJuliac
=#
function julia_compile(julia_program, c_program = nothing, build_dir = "builddir", verbose = false, quiet = false,
                       clean = false, sysimage = nothing, compile = nothing, cpu_target = nothing, optimize = nothing,
                       debug = nothing, inline = nothing, check_bounds = nothing, math_mode = nothing, depwarn = nothing,
                       auto = false, object = false, shared = false, executable = true, julialibs = true)
											 verbose && quiet && (verbose = false)
# ...............
    if julialibs
        verbose && println("Sync Julia libraries:")
        libfiles = String[]
        dlext = "." * Libdl.dlext
        for dir in (shlibdir, private_shlibdir)
            if is_windows() || is_apple()
                append!(libfiles, joinpath.(dir, filter(x -> endswith(x, dlext), readdir(dir))))
            else
                append!(libfiles, joinpath.(dir, filter(x -> ismatch(r"^lib.+\.so(?:\.\d+)*$", x), readdir(dir))))
            end
        end
        sync = false
        for src in libfiles
            ismatch(r"debug", src) && continue
            dst = basename(src)
            if filesize(src) != filesize(dst) || ctime(src) > ctime(dst) || mtime(src) > mtime(dst)
                verbose && println("  $dst")
                cp(src, dst, remove_destination=true, follow_symlinks=false)
                sync = true
            end
        end
        sync || verbose && println("  none")
    end
end