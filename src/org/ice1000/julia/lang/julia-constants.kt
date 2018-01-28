package org.ice1000.julia.lang

import com.intellij.openapi.util.IconLoader
import org.jetbrains.annotations.NonNls

@NonNls const val JULIA_CONTEXT_ID = "JULIA_CONTEXT"
@NonNls const val JULIA_LANGUAGE_NAME = "Julia"
@NonNls const val JULIA_EXTENSION = "jl"
@NonNls const val JULIA_DOC_SURROUNDING = "\"\"\""
@NonNls const val JULIA_STRING_DOLLAR = '\$'
@NonNls const val JULIA_MODULE_ID = "JULIA_MODULE_TYPE"
@NonNls const val JULIA_RUN_CONFIG_ID = "JULIA_RUN_CONFIG_ID"
@NonNls const val JULIA_CHAR_SINGLE_UNICODE_X_REGEX = "\\\\x([A-Fa-f0-9]){2}"
@NonNls const val JULIA_CHAR_NOT_UX_REGEX = "\\\\([^uxUX]){1}"
@NonNls const val JULIA_CHAR_SINGLE_UNICODE_U_REGEX = "\\\\u([A-Fa-f0-9]){4}"
@NonNls const val JULIA_CHAR_TRIPLE_UNICODE_X_REGEX = "(\\\\x([A-Fa-f0-9]){2}){3}"

@NonNls const val JULIA_DEFAULT_MODULE_NAME = "MyBizarreJuliaModule"
@NonNls const val JULIA_WEBSITE = "https://julialang.org/downloads/"

@JvmField val JULIA_BIG_ICON = IconLoader.getIcon("/icons/julia.png")
@JvmField val JULIA_ICON = IconLoader.getIcon("/icons/julia_file.png")

@NonNls const val MAC_APPLICATIONS = "/Applications"
