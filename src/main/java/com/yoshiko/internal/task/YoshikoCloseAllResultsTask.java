package com.yoshiko.internal.task;

import java.util.Collection;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

import com.yoshiko.internal.util.YoshikoUtil;
import com.yoshiko.internal.view.YoshikoResultsPanel;

/**
 * Closes the result panels.
 */
public class YoshikoCloseAllResultsTask implements Task {

	@Tunable(description = "<html>You are about to close the MCODE app.<br />Do you want to continue?</html>", params="ForceSetDirectly=true")
	public boolean close = true;
	
	private final CySwingApplication swingApplication;
	private final YoshikoUtil mcodeUtil;
	
	public YoshikoCloseAllResultsTask(final CySwingApplication swingApplication,
						  			final YoshikoUtil mcodeUtil) {
		this.swingApplication = swingApplication;
		this.mcodeUtil = mcodeUtil;
	}

	@ProvidesTitle
	public String getTitle() {
		return "Close Yoshiko";
	}

	@Override
	public void run(final TaskMonitor taskMonitor) throws Exception {
		if (close) {
			final Collection<YoshikoResultsPanel> resultPanels = mcodeUtil.getResultPanels();
			
			for (YoshikoResultsPanel panel : resultPanels) {
				panel.discard(false);
			}

			CytoPanel cytoPanel = swingApplication.getCytoPanel(CytoPanelName.WEST);

			if (cytoPanel.getCytoPanelComponentCount() == 0)
				cytoPanel.setState(CytoPanelState.HIDE);
		}
	}

	@Override
	public void cancel() {
		// Do nothing
	}
}
