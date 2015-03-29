package org.pmedv.core.util;

import javax.swing.JPanel;

import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;

import org.pmedv.core.beans.ApplicationPerspective;
import org.pmedv.core.beans.BeanDirectory;
import org.pmedv.core.components.IMemento;
import org.pmedv.core.context.AppContext;
import org.pmedv.core.gui.ApplicationWindow;
import org.pmedv.core.perspectives.AbstractPerspective;
import org.pmedv.core.provider.ApplicationPerspectiveProvider;
import org.springframework.context.ApplicationContext;

/**
 * @author Matthias Pueski (13.04.2010)
 * 
 */

public class DockingWindowUtils {

	private static final ApplicationContext ctx = AppContext.getApplicationContext();

	public static void postProcessApplicationWindowClose(Class<?> viewComponentClass) {

		final ApplicationPerspectiveProvider provider = (ApplicationPerspectiveProvider) ctx.getBean("perspectiveProvider");
		final ApplicationWindow win = (ApplicationWindow) ctx.getBean(BeanDirectory.WINDOW_APPLICATION);

		for (ApplicationPerspective ap : provider.getPerspectives()) {

			AbstractPerspective a = (AbstractPerspective) ctx.getBean(ap.getId());

			if (a instanceof IMemento) {
				IMemento memento = (IMemento) a;
				memento.saveState();
			}

			for (int i = 0; i < a.getRootWindow().getChildWindowCount(); i++) {

				if (a.getRootWindow().getChildWindow(i) instanceof TabWindow) {

					TabWindow tabWin = (TabWindow) a.getRootWindow().getChildWindow(i);

					for (int j = 0; j < tabWin.getChildWindowCount(); j++) {

						if (tabWin.getChildWindow(j) instanceof View) {

							View view = (View) tabWin.getChildWindow(j);

							if (view.getComponent().getClass().equals(viewComponentClass)) {

							}

						}

					}

				}

				/**
				 * That's not very beautiful, but it works. Please try this not
				 * at home!
				 */

				else if (a.getRootWindow().getChildWindow(i) instanceof SplitWindow) {

					SplitWindow splitWin = (SplitWindow) a.getRootWindow().getChildWindow(i);

					for (int j = 0; j < splitWin.getChildWindowCount(); j++) {

						if (splitWin.getChildWindow(j) instanceof TabWindow) {

							TabWindow tabWin = (TabWindow) splitWin.getChildWindow(j);

							for (int k = 0; k < tabWin.getChildWindowCount(); k++) {

								if (tabWin.getChildWindow(k) instanceof View) {

									View view = (View) tabWin.getChildWindow(k);

									if (view.getComponent() instanceof JPanel) {

										JPanel panel = (JPanel) view.getComponent();

										for (int l = 0; l < panel.getComponentCount(); l++) {

											if (view.getComponent().getClass().equals(viewComponentClass)) {

											}

										}

									}

								}

							}

						}

					}

				}

			}

		}

	}

}
