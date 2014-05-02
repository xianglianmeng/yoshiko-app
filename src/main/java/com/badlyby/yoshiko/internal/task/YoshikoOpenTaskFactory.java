package com.badlyby.yoshiko.internal.task;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

import com.badlyby.yoshiko.internal.YoshikoAnalyzeAction;
import com.badlyby.yoshiko.internal.util.YoshikoUtil;

public class YoshikoOpenTaskFactory implements TaskFactory {

	private final CySwingApplication swingApplication;
	private final CyServiceRegistrar registrar;
	private final YoshikoUtil mcodeUtil;
	private final YoshikoAnalyzeAction analyzeAction;
	
	public YoshikoOpenTaskFactory(final CySwingApplication swingApplication,
			 					final CyServiceRegistrar registrar,
			 					final YoshikoUtil mcodeUtil,
			 					final YoshikoAnalyzeAction analyzeAction) {
		this.swingApplication = swingApplication;
		this.registrar = registrar;
		this.mcodeUtil = mcodeUtil;
		this.analyzeAction = analyzeAction;
	}
	
	@Override
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new YoshikoOpenTask(swingApplication, registrar, mcodeUtil, analyzeAction));
	}

	@Override
	public boolean isReady() {
		return !mcodeUtil.isOpened();
	}
}
