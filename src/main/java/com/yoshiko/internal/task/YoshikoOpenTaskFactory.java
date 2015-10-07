package com.yoshiko.internal.task;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

import com.yoshiko.internal.YoshikoAnalyzeAction;
import com.yoshiko.internal.util.YoshikoUtil;

public class YoshikoOpenTaskFactory implements TaskFactory {

	private final CySwingApplication swingApplication;
	private final CyServiceRegistrar registrar;
	private final YoshikoUtil yoshikoUtil;
	private final YoshikoAnalyzeAction analyzeAction;
	
	public YoshikoOpenTaskFactory(final CySwingApplication swingApplication,
			 					final CyServiceRegistrar registrar,
			 					final YoshikoUtil yoshikoUtil,
			 					final YoshikoAnalyzeAction analyzeAction) {
		this.swingApplication = swingApplication;
		this.registrar = registrar;
		this.yoshikoUtil = yoshikoUtil;
		this.analyzeAction = analyzeAction;
	}
	
	@Override
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new YoshikoOpenTask(swingApplication, registrar, yoshikoUtil, analyzeAction));
	}

	@Override
	public boolean isReady() {
		return !yoshikoUtil.isOpened();
	}
}
