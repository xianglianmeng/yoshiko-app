package com.yoshiko.internal.task;

import java.util.Properties;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

import com.yoshiko.internal.YoshikoAnalyzeAction;
import com.yoshiko.internal.util.YoshikoUtil;
import com.yoshiko.internal.view.YoshikoMainPanel;

/**
 * Open the Yoshiko panel in the Control panel.
 */
public class YoshikoOpenTask implements Task {

	private final CySwingApplication swingApplication;
	private final CyServiceRegistrar registrar;
	private final YoshikoUtil yoshikoUtil;
	private final YoshikoAnalyzeAction analyzeAction;
	
	public YoshikoOpenTask(final CySwingApplication swingApplication,
						 final CyServiceRegistrar registrar,
						 final YoshikoUtil yoshikoUtil,
						 final YoshikoAnalyzeAction analyzeAction) {
		this.swingApplication = swingApplication;
		this.registrar = registrar;
		this.yoshikoUtil = yoshikoUtil;
		this.analyzeAction = analyzeAction;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		// Display YoshikoMainPanel in left cytopanel
		synchronized (this) {
			YoshikoMainPanel mainPanel = null;

			// First we must make sure that the app is not already open
			if (!yoshikoUtil.isOpened()) {
				mainPanel = new YoshikoMainPanel(swingApplication, yoshikoUtil);
				mainPanel.addAction(analyzeAction);

				registrar.registerService(mainPanel, CytoPanelComponent.class, new Properties());
				analyzeAction.updateEnableState();
			} else {
				mainPanel = yoshikoUtil.getMainPanel();
			}

			if (mainPanel != null) {
				CytoPanel cytoPanel = yoshikoUtil.getControlCytoPanel();
				int index = cytoPanel.indexOfComponent(mainPanel);
				cytoPanel.setSelectedIndex(index);
			}
		}
	}

	@Override
	public void cancel() {
		// Do nothing
	}
}
