package com.yoshiko.internal.task;

import java.util.Collection;
import java.util.Set;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

import com.yoshiko.internal.util.YoshikoUtil;
import com.yoshiko.internal.view.YoshikoResultsPanel;

public class YoshikoCloseTaskFactory implements TaskFactory, NetworkAboutToBeDestroyedListener {
	
	private final CySwingApplication swingApplication;
	private final CyServiceRegistrar registrar;
	private final YoshikoUtil mcodeUtil;
	
	public YoshikoCloseTaskFactory(final CySwingApplication swingApplication,
								 final CyServiceRegistrar registrar,
								 final YoshikoUtil mcodeUtil) {
		this.swingApplication = swingApplication;
		this.registrar = registrar;
		this.mcodeUtil = mcodeUtil;
	}

	@Override
	public TaskIterator createTaskIterator() {
		final TaskIterator taskIterator = new TaskIterator();
		final Collection<YoshikoResultsPanel> resultPanels = mcodeUtil.getResultPanels();
		final YoshikoCloseAllResultsTask closeResultsTask = new YoshikoCloseAllResultsTask(swingApplication, mcodeUtil);

		if (resultPanels.size() > 0)
			taskIterator.append(closeResultsTask);
		
		taskIterator.append(new YoshikoCloseTask(closeResultsTask, registrar, mcodeUtil));
		
		return taskIterator;
	}

	@Override
	public boolean isReady() {
		return mcodeUtil.isOpened();
	}
	
	@Override
	public void handleEvent(final NetworkAboutToBeDestroyedEvent e) {
		if (mcodeUtil.isOpened()) {
			CyNetwork network = e.getNetwork();
			Set<Integer> resultIds = mcodeUtil.getNetworkResults(network.getSUID());

			for (int id : resultIds) {
				YoshikoResultsPanel panel = mcodeUtil.getResultPanel(id);
				if (panel != null) panel.discard(false);
			}
		}
	}
}
