package com.yoshiko.internal.task;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

import com.yoshiko.internal.util.YoshikoUtil;
import com.yoshiko.internal.view.YoshikoMainPanel;

/**
 * Closes the main Yoshiko panel.
 */
public class YoshikoCloseTask implements Task {

	private final YoshikoCloseAllResultsTask closeAllResultsTask;
	private final CyServiceRegistrar registrar;
	private final YoshikoUtil yoshikoUtil;
	
	public YoshikoCloseTask(final YoshikoCloseAllResultsTask closeAllResultsTask,
						  final CyServiceRegistrar registrar,
			  			  final YoshikoUtil yoshikoUtil) {
		this.closeAllResultsTask = closeAllResultsTask;
		this.registrar = registrar;
		this.yoshikoUtil = yoshikoUtil;
	}

	@Override
	public void run(final TaskMonitor taskMonitor) throws Exception {
		if (closeAllResultsTask == null || closeAllResultsTask.close) {
			YoshikoMainPanel mainPanel = yoshikoUtil.getMainPanel();

			if (mainPanel != null) {
				registrar.unregisterService(mainPanel, CytoPanelComponent.class);
			}

			yoshikoUtil.reset();
		}
	}

	@Override
	public void cancel() {
		// Do nothing
	}
}
