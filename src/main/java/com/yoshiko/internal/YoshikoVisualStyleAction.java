package com.yoshiko.internal;

import java.awt.Component;
import java.awt.event.ActionEvent;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.ActionEnableSupport;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.events.CytoPanelComponentSelectedEvent;
import org.cytoscape.application.swing.events.CytoPanelComponentSelectedListener;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;

import com.yoshiko.internal.util.YoshikoUtil;
import com.yoshiko.internal.view.YoshikoResultsPanel;

/**
 * * Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center
 * *
 * * Code written by: Gary Bader
 * * Authors: Gary Bader, Ethan Cerami, Chris Sander
 * *
 * * This library is free software; you can redistribute it and/or modify it
 * * under the terms of the GNU Lesser General Public License as published
 * * by the Free Software Foundation; either version 2.1 of the License, or
 * * any later version.
 * *
 * * This library is distributed in the hope that it will be useful, but
 * * WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 * * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 * * documentation provided hereunder is on an "as is" basis, and
 * * Memorial Sloan-Kettering Cancer Center
 * * has no obligations to provide maintenance, support,
 * * updates, enhancements or modifications.  In no event shall the
 * * Memorial Sloan-Kettering Cancer Center
 * * be liable to any party for direct, indirect, special,
 * * incidental or consequential damages, including lost profits, arising
 * * out of the use of this software and its documentation, even if
 * * Memorial Sloan-Kettering Cancer Center
 * * has been advised of the possibility of such damage.  See
 * * the GNU Lesser General Public License for more details.
 * *
 * * You should have received a copy of the GNU Lesser General Public License
 * * along with this library; if not, write to the Free Software Foundation,
 * * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 * *
 * * User: Vuk Pavlovic
 * * Date: Jan 4, 2007
 * * Time: 11:30:35 AM
 * * Description: A controller for the Yoshiko attributes used for visualization
 */

/**
 * A controller for the Yoshiko attributes used for visualization. Only the onComponentSelected method is
 * used in this listener to determine when a result has been selected.
 */
public class YoshikoVisualStyleAction extends AbstractYoshikoAction implements CytoPanelComponentSelectedListener {

	private static final long serialVersionUID = -6884537645922099638L;

	private final VisualMappingManager visualMappingMgr;
	private final YoshikoUtil yoshikoUtil;

	public YoshikoVisualStyleAction(final String title,
								  final CyApplicationManager applicationManager,
								  final CySwingApplication swingApplication,
								  final CyNetworkViewManager netViewManager,
								  final VisualMappingManager visualMappingMgr,
								  final YoshikoUtil yoshikoUtil) {
		super(title, applicationManager, swingApplication, netViewManager, ActionEnableSupport.ENABLE_FOR_NETWORK);
		this.visualMappingMgr = visualMappingMgr;
		this.yoshikoUtil = yoshikoUtil;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		// TODO Auto-generated method stub
	}

	/**
	 * Whenever an Yoshiko result tab is selected in the east CytoPanel, the Yoshiko attributes
	 * have to be rewritten to correspond to that particular result. At the same time the
	 * Visual Style has to redraw the network given the new attributes.
	 */
	@Override
	public void handleEvent(CytoPanelComponentSelectedEvent event) {
		// When the user selects a tab in the east cytopanel we want to see if it is a results panel
		// and if it is we want to re-draw the network with the Yoshiko visual style and reselect the
		// cluster that may be selected in the results panel
		Component component = event.getCytoPanel().getSelectedComponent();

		if (component instanceof YoshikoResultsPanel) {
			YoshikoResultsPanel resultsPanel = (YoshikoResultsPanel) component;

			// To re-initialize the calculators we need the highest score of this particular result set
			double maxScore = resultsPanel.setNodeAttributesAndGetMaxScore();
			// We also need the selected row if one is selected at all
			resultsPanel.selectCluster(null);
// TODO: delete/fix
//			int selectedRow = resultsPanel.getSelectedRow();
//			resultsPanel.getClusterBrowserTable().clearSelection();
//
//			if (selectedRow >= 0) {
//				resultsPanel.getClusterBrowserTable().setRowSelectionInterval(selectedRow, selectedRow);
//			}
			
			// Get the updated app's style
			VisualStyle appStyle = yoshikoUtil.getAppStyle(maxScore);
			// Register the app's style but don't make it active by default
			yoshikoUtil.registerVisualStyle(appStyle);

			// Update the network view if there is one and it is using the app's style
			CyNetworkView netView = resultsPanel.getNetworkView();
			
			if (netView != null) {
				if (visualMappingMgr.getVisualStyle(netView) == appStyle) {
					appStyle.apply(netView);
					netView.updateView();
				}
			}
		}
	}
}
