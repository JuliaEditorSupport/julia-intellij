package org.ice1000.julia.lang

import com.intellij.openapi.util.IconLoader
import org.jetbrains.annotations.NonNls

@NonNls const val JULIA_EXTENSION = "jl"
@NonNls const val JULIA_DOC_SURROUNDING = "\"\"\""
@NonNls const val JULIA_MODULE_ID = "JULIA_MODULE_TYPE"

@NonNls const val JULIA_DEFAULT_MODULE_NAME = "MyBizarreJuliaModule"
@NonNls const val JULIA_WEBSITE = "https://julialang.org/downloads/"

@JvmField val JULIA_BIG_ICON = IconLoader.getIcon("/icons/julia.png")
@JvmField val JULIA_ICON = IconLoader.getIcon("/icons/julia_file.png")

@NonNls const val POSSIBLE_SDK_HOME_LINUX = "/usr/share/julia"
