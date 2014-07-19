package com.yoshiko.internal.model;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;
import java.util.List;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YoshikoGetSIFData {
	
	private CharsetEncoder encoder;
	private static final Logger logger = LoggerFactory.getLogger(YoshikoGetSIFData.class);
	private final CyNetwork network;
	public YoshikoGetSIFData(final CyNetwork network) {
		this.network = network;
	}
	public String GetSIFData() throws IOException {
		if (Charset.isSupported("UTF-8")) {
			this.encoder = Charset.forName("UTF-8").newEncoder();
		} else {
			logger.warn("UTF-8 is not supported by this system.  This can be a problem for non-English annotations.");
			this.encoder = Charset.defaultCharset().newEncoder();
		}
		System.out.println("Encoding = " + this.encoder.charset());
		StringWriter writer = new StringWriter();
		String lineSep = System.getProperty("line.separator");
		String sourceName;
		CyNode node;
		List<CyNode> nodeList = network.getNodeList();
		for (Iterator<CyNode> i$ = nodeList.iterator(); i$.hasNext();) {
			node = (CyNode) i$.next();
			sourceName = (String) network.getRow(node)
					.get("name", String.class);
			List<CyEdge> edges = network.getAdjacentEdgeList(node,
					CyEdge.Type.ANY);
			if ((sourceName == null) || (sourceName.length() == 0)) {
				throw new IllegalStateException(
						"This network contains null or empty node name.");
			}

			if (edges.size() == 0)
				writer.write(sourceName + lineSep);
			else
				for (CyEdge edge : edges) {
					if (node == edge.getSource()) {
						CyNode target = edge.getTarget();
						String targetName = (String) network.getRow(target)
								.get("name", String.class);
						if ((targetName == null) || (targetName.length() == 0)) {
							throw new IllegalStateException(
									"This network contains null or empty node name.");
						}
						String interactionName = (String) network.getRow(edge)
								.get("interaction", String.class);
						if (interactionName == null) {
							interactionName = "-";
						}
						writer.write(sourceName);
						writer.write("\t");
						writer.write(interactionName);
						writer.write("\t");
						writer.write(targetName);
						writer.write(lineSep);
					}
				}
		}
		return writer.toString();
	}
}