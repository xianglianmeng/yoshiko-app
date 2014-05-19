package org.cytoscape.yoshiko.internal;

import java.util.ArrayList;
import java.util.Collection;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.view.model.CyNetworkViewManager;

import org.cytoscape.yoshiko.internal.view.YoshikoMainPanel;
import org.cytoscape.yoshiko.internal.view.YoshikoResultsPanel;

public abstract class AbstractYoshikoAction extends AbstractCyAction {

	private static final long serialVersionUID = 844755168181859513L;

	protected final CySwingApplication swingApplication;
	protected final CyApplicationManager applicationManager;
	protected final CyNetworkViewManager netViewManager;

	public AbstractYoshikoAction(final String name,
							   final CyApplicationManager applicationManager,
							   final CySwingApplication swingApplication,
							   final CyNetworkViewManager netViewManager,
							   final String enableFor) {
		super(name, applicationManager, enableFor, netViewManager);
		this.applicationManager = applicationManager;
		this.swingApplication = swingApplication;
		this.netViewManager = netViewManager;
	}

	/**
	 * @return Cytoscape's control panel
	 */
	protected CytoPanel getControlCytoPanel() {
		return swingApplication.getCytoPanel(CytoPanelName.WEST);
	}

	/**
	 * @return Cytoscape's results panel
	 */
	protected CytoPanel getResultsCytoPanel() {
		return swingApplication.getCytoPanel(CytoPanelName.EAST);
	}

	/**
	 * @return The main panel of the app if it is opened, and null otherwise
	 */
	protected YoshikoMainPanel getMainPanel() {
		CytoPanel cytoPanel = getControlCytoPanel();
		int count = cytoPanel.getCytoPanelComponentCount();

		for (int i = 0; i < count; i++) {
			if (cytoPanel.getComponentAt(i) instanceof YoshikoMainPanel)
				return (YoshikoMainPanel) cytoPanel.getComponentAt(i);
		}

		return null;
	}

	/**
	 * @return The result panels of the app if it is opened, or an empty collection otherwise
	 */
	protected Collection<YoshikoResultsPanel> getResultPanels() {
		Collection<YoshikoResultsPanel> panels = new ArrayList<YoshikoResultsPanel>();
		CytoPanel cytoPanel = getResultsCytoPanel();
		int count = cytoPanel.getCytoPanelComponentCount();

		for (int i = 0; i < count; i++) {
			if (cytoPanel.getComponentAt(i) instanceof YoshikoResultsPanel)
				panels.add((YoshikoResultsPanel) cytoPanel.getComponentAt(i));
		}

		return panels;
	}

	protected YoshikoResultsPanel getResultPanel(final int resultId) {
		for (YoshikoResultsPanel panel : getResultPanels()) {
			if (panel.getResultId() == resultId) return panel;
		}

		return null;
	}

	/**
	 * @return true if the app is opened and false otherwise
	 */
	protected boolean isOpened() {
		return getMainPanel() != null;
	}
}
