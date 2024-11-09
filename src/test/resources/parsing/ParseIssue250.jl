
kspec_cmd, = installkernel("Julia")

function msg_comm(comm::Comm, m::IJulia.Msg, msg_type,
                  data=Dict{String,Any}(),
                  metadata=Dict{String, Any}(); kwargs...)
    content = Dict("comm_id"=>comm.id, "data"=>data)
end