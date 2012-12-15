package zpisync.desktop;

import javax.swing.JComponent;
import javax.swing.UIDefaults;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.PanelUI;
import javax.swing.plaf.basic.BasicPanelUI;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

/**
 * Extension of WindowsLookAndFeel that attempts to look more native.
 */
public class BetterWindowsLookAndFeel extends WindowsLookAndFeel {
	private static final long serialVersionUID = 1L;

	@Override
	protected void initClassDefaults(UIDefaults table) {
		super.initClassDefaults(table);
		Object[] uiDefaults = { "PanelUI", BWPanelUI.class.getName() };
		table.putDefaults(uiDefaults);
	}

	public static class BWPanelUI extends BasicPanelUI {
		// Shared UI object
		private static PanelUI panelUI;

		public static ComponentUI createUI(JComponent c) {
			if (panelUI == null) {
				panelUI = new BWPanelUI();
			}
			return panelUI;
		}

		@Override
		public void installUI(JComponent c) {
			super.installUI(c);
			// panels should be transparent (fixes JTabbedPane on Win7)
			installProperty(c, "opaque", Boolean.FALSE);
		}
	}
}
