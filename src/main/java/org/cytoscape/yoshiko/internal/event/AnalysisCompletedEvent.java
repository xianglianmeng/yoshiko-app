package org.cytoscape.yoshiko.internal.event;

import java.util.List;

import org.cytoscape.yoshiko.internal.model.YoshikoCluster;

public class AnalysisCompletedEvent {

	private final boolean successful;
	private final List<YoshikoCluster> clusters;

	public AnalysisCompletedEvent(final boolean successful, final List<YoshikoCluster> clusters) {
		this.successful = successful;
		this.clusters = clusters;
	}

	/**
	 * @return true if the task has completed successfully.
	 */
	public boolean isSuccessful() {
		return successful;
	}

	/**
	 * Get computed clusters once MCODE has been run.  Will be null if not computed.
	 * @return
	 */
	public List<YoshikoCluster> getClusters() {
		return clusters;
	}
}
