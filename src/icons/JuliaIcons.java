package icons;

import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Icon holder, don't change package name because it can be used in plugin.xml
 *
 * @author ice1000, HoshinoTented
 */
public interface JuliaIcons {
	@NotNull Icon JULIA_BIG_ICON = IconLoader.getIcon("/icons/julia.png");
	@NotNull Icon JULIA_RUN_ICON = IconLoader.getIcon("/icons/julia_run.png");
	@NotNull Icon JULIA_ICON = IconLoader.getIcon("/icons/julia_file.png");
	@NotNull Icon JOJO_ICON = IconLoader.getIcon("/icons/jojo.png");
	@NotNull Icon JULIA_MODULE_ICON = IconLoader.getIcon("/icons/julia_module.png");
	@NotNull Icon JULIA_TYPE_ICON = IconLoader.getIcon("/icons/julia_type.png");
	@NotNull Icon JULIA_ABSTRACT_TYPE_ICON = IconLoader.getIcon("/icons/julia_abstract_type.png");
	@NotNull Icon JULIA_FUNCTION_ICON = IconLoader.getIcon("/icons/field.png");
	@NotNull Icon JULIA_VARIABLE_ICON = IconLoader.getIcon("/icons/field_variable.png");
	@NotNull Icon JULIA_CONST_ICON = IconLoader.getIcon("/icons/const.png");
	@NotNull Icon JULIA_MACRO_ICON = IconLoader.getIcon("/icons/julia_macro.png");

	@NotNull Icon DOCFMT_RED_ICON = IconLoader.getIcon("/icons/docfmt/docfmt_red.png");
	@NotNull Icon DOCFMT_BLUE_ICON = IconLoader.getIcon("/icons/docfmt/docfmt_blue.png");

	@NotNull Icon JULIA_IF_ICON = IconLoader.getIcon("/icons/structure-view/if.png");
	@NotNull Icon JULIA_WHILE_ICON = IconLoader.getIcon("/icons/structure-view/while.png");

	@NotNull Icon ADD_SDK_ICON = IconLoader.getIcon("/icons/julia_sdk.png");

	@NotNull Icon REFRESH_ICON = IconLoader.getIcon("/actions/refresh.png");
	@NotNull Icon ADD_ICON = IconLoader.getIcon("/general/add.png");
	@NotNull Icon REMOVE_ICON = IconLoader.getIcon("/general/remove.png");
}
