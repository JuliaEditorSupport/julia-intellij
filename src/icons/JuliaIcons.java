package icons;

import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Icon holder, don't change package name because it can be used in plugin.xml
 *
 * @author ice1000
 */
public interface JuliaIcons {
	@NotNull Icon JULIA_BIG_ICON = IconLoader.getIcon("/icons/julia.png");
	@NotNull Icon JULIA_ICON = IconLoader.getIcon("/icons/julia_file.png");
	@NotNull Icon JOJO_ICON = IconLoader.getIcon("/icons/jojo.png");
	@NotNull Icon JULIA_MODULE_ICON = IconLoader.getIcon("/icons/module.png");
	@NotNull Icon JULIA_TYPE_ICON = IconLoader.getIcon("/icons/type.png");
	@NotNull Icon DOCFMT_ICON = IconLoader.getIcon("/icons/docfmt.png");
	@NotNull Icon DOCFMT_RED_ICON = IconLoader.getIcon("/icons/temp/docfmt.png");
	@NotNull Icon DOCFMT_BLUE_ICON = IconLoader.getIcon("/icons/temp/Bdocfmt.png");
}
