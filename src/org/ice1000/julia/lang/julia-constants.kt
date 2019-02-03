package org.ice1000.julia.lang

import com.intellij.execution.process.OSProcessHandler
import com.intellij.openapi.util.Key
import org.ice1000.julia.lang.action.JuliaReplRunner
import org.ice1000.julia.lang.module.JuliaDebugValue
import org.ice1000.julia.lang.module.JuliaVariablesView
import org.intellij.lang.annotations.Language
import org.jetbrains.annotations.NonNls

@NonNls const val JULIA_CONTEXT_ID = "JULIA_CONTEXT"
@NonNls const val JULIA_LANGUAGE_NAME = "Julia"
@NonNls const val JULIA_EXTENSION = "jl"
@NonNls const val JULIA_DOC_SURROUNDING = "\"\"\""
@NonNls const val JULIA_BLOCK_COMMENT_BEGIN = "#="
@NonNls const val JULIA_BLOCK_COMMENT_END = "=#"
@NonNls const val JULIA_STRING_DOLLAR = '\$'
@NonNls const val JULIA_MODULE_ID = "JULIA_MODULE_TYPE"
@NonNls const val JULIA_RUN_CONFIG_ID = "JULIA_RUN_CONFIG_ID"
/// See plugin.xml
@NonNls const val JULIA_REPL_EMPTY_ACTION_ID = "Julia.Tools.EmptyAction"
@NonNls const val JULIA_SCI_VIEW_ID = "JuliaSciView"
@NonNls const val JULIA_INTELLIJ_PLOT_PORT = "JULIA_INTELLIJ_PLOT_PORT"
@NonNls const val JULIA_INTELLIJ_DATA_PORT = "JULIA_INTELLIJ_DATA_PORT"
@NonNls const val JULIA_PLUGIN_ID = "org.ice1000.julia"
@NonNls @Language("RegExp") const val JULIA_CHAR_SINGLE_UNICODE_X_REGEX = "\\\\x([A-Fa-f0-9]){2}"
@NonNls @Language("RegExp") const val JULIA_CHAR_NOT_UX_REGEX = "\\\\([^uxUX])"
@NonNls @Language("RegExp") const val JULIA_CHAR_SINGLE_UNICODE_U_REGEX = "\\\\u([A-Fa-f0-9]){4}"
@NonNls @Language("RegExp") const val JULIA_CHAR_TRIPLE_UNICODE_X_REGEX = "(\\\\x([A-Fa-f0-9]){2}){3}"
@NonNls @Language("RegExp") const val JULIA_STACK_FRAME_LOCATION_REGEX = "at ([^:<>*])+:\\d+"
@NonNls @Language("RegExp") const val JULIA_IN_EXPR_STARTING_AT = "in expression starting at "
const val JULIA_IN_EXPR_STARTING_AT_LEN = JULIA_IN_EXPR_STARTING_AT.length
//@NonNls @Language("RegExp") const val JULIA_ERROR_FILE_LOCATION_REGEX = "[^ ,]+,"

// with IGNORE_CASE and COMMENTS options
@NonNls @Language("RegExp") const val JULIA_VERSION_NUMBER_REGEX_IX = """^
    v?                                      # prefix        (optional)
    (\d+)                                   # major         (required)
    (?:\.(\d+))?                            # minor         (optional)
    (?:\.(\d+))?                            # patch         (optional)
    (?:(-)|                                 # pre-release   (optional)
    ([a-z][0-9a-z-]*(?:\.[0-9a-z-]+)*|-(?:[0-9a-z-]+\.)*[0-9a-z-]+)?
    (?:(\+)|
    (?:\+((?:[0-9a-z-]+\.)*[0-9a-z-]+))?    # build         (optional)
    ))$"""
@NonNls
@Language("RegExp")
const val JULIA_VERSION_NUMBER_REGEX_PRE_I = """^(?:|[0-9a-z-]*[a-z-][0-9a-z-]*)$"""
@Language("CSS")
const val JULIA_MARKDOWN_DARCULA_CSS = """code{
    color: #FF79C6
}"""
@Language("CSS")
const val JULIA_MARKDOWN_INTELLIJ_CSS = """code{
    color: #B03776
}"""
@NonNls const val JULIA_DEFAULT_MODULE_NAME = "MyBizarreJuliaModule"
@NonNls const val JULIA_WEBSITE = "https://julialang.org/downloads/"
@NonNls const val MAC_APPLICATIONS = "/Applications"

@NonNls val JULIA_TABLE_HEADER_COLUMN = arrayOf("Package", "Version", "Latest")
@NonNls @Language("HTML") const val UNKNOWN_VALUE_PLACEHOLDER = "<unknown>"

// ========= DocumentFormat.jl ==========

@NonNls @Language("Julia") const val DOCFMT_INSTALL =
	"""Pkg.add("CSTParser")
Pkg.clone("git://github.com/ZacLN/DocumentFormat.jl.git")
Pkg.build("DocumentFormat")"""

@NonNls const val DOCFMT_EXTENSION = "julia-config"
@NonNls const val DOCFMT_LANGUAGE_NAME = "DocumentFormat"
val JULIA_SCI_PORT_KEY = Key<String>("JuliaSciPortKey")
val JULIA_DATA_PORT_KEY = Key<String>("JuliaSciDataPortKey")
val JULIA_SCI_DATA_KEY = Key<JuliaVariablesView>("JuliaVariablesViewKey")
val JULIA_VAR_LIST_KEY = Key<List<JuliaDebugValue>>("JuliaVarListKey")
val JULIA_REPL_RUNNER_KEY = Key<JuliaReplRunner>("JuliaReplRunnerKey")
val JULIA_DEBUG_PROCESS_HANDLER_KEY = Key<OSProcessHandler>("JuliaDebugProcessHandlerKey")
val JULIA_DEBUG_FILE_KEY = Key<String>("JuliaDebugFileKey")

const val REPL_ERROR_TAG = "Julia REPL ERROR"
