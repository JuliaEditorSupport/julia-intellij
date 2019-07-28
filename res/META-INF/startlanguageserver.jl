using LanguageServer, Sockets, SymbolServer, Pkg

const depot = Pkg.depots1()
server = LanguageServer.LanguageServerInstance(stdin, stdout, false, string(depot, ""/environments/v1.1"), depot, Dict())
server.runlinter = true
server.debug_mode = true
run(server)