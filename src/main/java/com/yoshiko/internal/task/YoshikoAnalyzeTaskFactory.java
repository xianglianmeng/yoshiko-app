package com.yoshiko.internal.task;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

import com.yoshiko.internal.event.AnalysisCompletedListener;
import com.yoshiko.internal.model.YoshikoAlgorithm;
import com.yoshiko.internal.util.YoshikoUtil;

public class YoshikoAnalyzeTaskFactory implements TaskFactory {

	private final CyNetwork network;
	private final int analyze;
	private final int resultId;
	private final YoshikoAlgorithm alg;
	private final YoshikoUtil yoshikoUtil;
	private final AnalysisCompletedListener listener;

	public YoshikoAnalyzeTaskFactory(final CyNetwork network,
								   final int analyze,
								   final int resultId,
								   final YoshikoAlgorithm alg,
								   final YoshikoUtil yoshikoUtil,
								   final AnalysisCompletedListener listener) {
		this.network = network;
		this.analyze = analyze;
		this.resultId = resultId;
		this.alg = alg;
		this.yoshikoUtil = yoshikoUtil;
		this.listener = listener;
	}

	@Override
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new YoshikoAnalyzeTask(network, analyze, resultId, alg, yoshikoUtil, listener));
	}

	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
		return true;
	}
}
