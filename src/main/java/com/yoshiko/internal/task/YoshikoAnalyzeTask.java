package com.yoshiko.internal.task;

import java.awt.Image;
import java.util.List;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yoshiko.internal.YoshikoAnalyzeAction;
import com.yoshiko.internal.event.AnalysisCompletedEvent;
import com.yoshiko.internal.event.AnalysisCompletedListener;
import com.yoshiko.internal.model.YoshikoAlgorithm;
import com.yoshiko.internal.model.YoshikoCluster;
import com.yoshiko.internal.model.YoshikoGetSIFData;
import com.yoshiko.internal.model.YoshikoParameterSet;
import com.yoshiko.internal.model.YoshikoSocket;
import com.yoshiko.internal.util.YoshikoUtil;

/**
 * Yoshiko Score network and find cluster task.
 */
public class YoshikoAnalyzeTask implements Task {

	private final YoshikoAlgorithm alg;
	private final YoshikoUtil yoshikoUtil;
	private final int analyze;
	private final int resultId;
	private final AnalysisCompletedListener listener;

	private boolean interrupted = false;
	private CyNetwork network;
	private YoshikoSocket ysocket = null;
	
	private static final Logger logger = LoggerFactory.getLogger(YoshikoAnalyzeTask.class);

	/**
	 * Scores and finds clusters in a given network
	 *
	 * @param network The network to cluster
	 * @param analyze Tells the task if we need to rescore and/or refind
	 * @param resultId Identifier of the current result set
	 * @param alg reference to the algorithm for this network
	 */
	public YoshikoAnalyzeTask(final CyNetwork network,
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

	/**
	 * Run Yoshiko (Both score and find steps).
	 */
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		if (taskMonitor == null) {
			throw new IllegalStateException("Task Monitor is not set.");
		}
		String host;
		int port;

		boolean success = false;
		List<YoshikoCluster> clusters = null;
		yoshikoUtil.resetLoading();
		YoshikoParameterSet params = yoshikoUtil.getCurrentParameters().getParamsCopy(network.getSUID());
		host = params.getSocketAddress();
		port = params.getSocketPort();
		try {
			// Run Yoshiko scoring algorithm - node scores are saved in the alg object
			alg.setTaskMonitor(taskMonitor, network.getSUID());

			// Only (re)score the graph if the scoring parameters have been changed
			if (analyze == YoshikoAnalyzeAction.RESCORE) {
				taskMonitor.setProgress(0.001);
				taskMonitor.setTitle("Yoshiko Analysis");
				ysocket = new YoshikoSocket(host, port);
				if (interrupted) {
					return;
				}
			}
			taskMonitor.setProgress(0.001);
			taskMonitor.setStatusMessage("Connect Yoshiko (Step 1 of 4)");
			interrupted = true;
			if(ysocket.SendCommand(0, null, null)) {
				taskMonitor.setProgress(0.02);
				ysocket.SendCommand(10, "-F", "2");
				taskMonitor.setProgress(0.03);
				YoshikoGetSIFData ysif = new YoshikoGetSIFData(network);
				taskMonitor.setStatusMessage("Send NetWork (Step 2 of 4)");
				taskMonitor.setProgress(0.5);
				ysocket.SendCommand(20, "-f", ysif.GetSIFData());
				taskMonitor.setProgress(0.8);
				ysocket.SendCommand(10, "-O", "5");
				taskMonitor.setProgress(0.9);
				ysocket.SendCommand(30, "-o", null);
				taskMonitor.setProgress(0.001);
				taskMonitor.setStatusMessage("Run Yoshiko (Step 3 of 4)");
				ysocket.SendCommand(40, "", "");
				taskMonitor.setProgress(0.001);
				taskMonitor.setStatusMessage("Import Results (Step 4 of 4)");
				ysocket.SendCommand(50, "0", null);
				taskMonitor.setProgress(0.5);
				String outdata = ysocket.getOutData();
				interrupted = false;
				clusters = alg.findClusters(network, resultId, outdata);
			}
			if (interrupted) {
				return;
			}

			// Also create all the images here for the clusters, since it can be a time consuming operation
			yoshikoUtil.sortClusters(clusters);
			int imageSize = yoshikoUtil.getCurrentParameters().getResultParams(resultId).getDefaultRowHeight();
			int count = 0;

			for (final YoshikoCluster c : clusters) {
				if (interrupted) return;
				
				final Image img = yoshikoUtil.createClusterImage(c, imageSize, imageSize, null, true, null);
				c.setImage(img);
				taskMonitor.setProgress((++count) / (double) clusters.size());
			}

			success = true;
		} catch (Exception e) {
			throw new Exception("Error while executing the Yoshiko analysis", e);
		} finally {
			yoshikoUtil.destroyUnusedNetworks(network, clusters);
			
			if (listener != null) {
				listener.handleEvent(new AnalysisCompletedEvent(success, clusters));
			}
		}
	}

	@Override
	public void cancel() {
		this.interrupted = true;
		alg.setCancelled(true);
		yoshikoUtil.removeNetworkResult(resultId);
		yoshikoUtil.removeNetworkAlgorithm(network.getSUID());
	}

	/**
	 * Gets the Task Title.
	 *
	 * @return human readable task title.
	 */
	public String getTitle() {
		return "Yoshiko Network Cluster Detection";
	}
}
