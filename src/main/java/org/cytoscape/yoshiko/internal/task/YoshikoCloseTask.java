package org.cytoscape.yoshiko.internal.task;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

import org.cytoscape.yoshiko.internal.util.YoshikoUtil;
import org.cytoscape.yoshiko.internal.view.YoshikoMainPanel;

/**
 * Closes the main MCODE panel.
 */
public class YoshikoCloseTask implements Task {

	private final YoshikoCloseAllResultsTask closeAllResultsTask;
	private final CyServiceRegistrar registrar;
	private final YoshikoUtil mcodeUtil;
	
	public YoshikoCloseTask(final YoshikoCloseAllResultsTask closeAllResultsTask,
						  final CyServiceRegistrar registrar,
			  			  final YoshikoUtil mcodeUtil) {
		this.closeAllResultsTask = closeAllResultsTask;
		this.registrar = registrar;
		this.mcodeUtil = mcodeUtil;
	}

	@Override
	public void run(final TaskMonitor taskMonitor) throws Exception {
		if (closeAllResultsTask == null || closeAllResultsTask.close) {
			YoshikoMainPanel mainPanel = mcodeUtil.getMainPanel();

			if (mainPanel != null) {
				registrar.unregisterService(mainPanel, CytoPanelComponent.class);
			}

			mcodeUtil.reset();
		}
	}

	@Override
	public void cancel() {
		// Do nothing
	}
}
