package org.cytoscape.yoshiko.internal;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.cytoscape.ding.NetworkViewTestSupport;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

import com.yoshiko.internal.model.YoshikoAlgorithm;
import com.yoshiko.internal.model.YoshikoCluster;
import com.yoshiko.internal.model.YoshikoParameterSet;
import com.yoshiko.internal.util.YoshikoUtil;

public abstract class AbstractYoshikoTest {

	protected YoshikoAlgorithm alg;
	protected YoshikoUtil mcodeUtil;
	protected final NetworkViewTestSupport netViewTestSupport;

	public AbstractYoshikoTest() {
		netViewTestSupport = new NetworkViewTestSupport();
	}

	protected List<YoshikoCluster> findClusters(final CyNetwork net, final int resultId) {
		return findClusters(net, resultId, new YoshikoParameterSet());
	}

	protected List<YoshikoCluster> findClusters(CyNetwork net, int resultId, YoshikoParameterSet params) {
		mcodeUtil.getCurrentParameters().setParams(params, resultId, net.getSUID());
		alg = new YoshikoAlgorithm(net.getSUID(), mcodeUtil);
		alg.scoreGraph(net, resultId);
		
		return alg.findClusters(net, resultId,"");
	}

	protected CyNetwork createCompleteGraph(int totalNodes) {
		CyNetwork net = netViewTestSupport.getNetwork();
		
		for (int i = 0; i < totalNodes; i++) {
			net.addNode();
		}
		
		List<CyNode> nodes = net.getNodeList();
		
		for (int i = 0; i < totalNodes; i++) {
			CyNode src = nodes.get(i);
			
			for (int j = 0; j < totalNodes; j++) {
				CyNode tgt = nodes.get(j);
				
				if (src != tgt && !net.containsEdge(src, tgt))
					net.addEdge(src, tgt, false);
			}
		}
		
		assertEquals((totalNodes*(totalNodes-1))/2, net.getEdgeCount());
		
		return net;
	}

}