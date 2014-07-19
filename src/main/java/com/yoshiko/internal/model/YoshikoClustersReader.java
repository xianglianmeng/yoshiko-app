package com.yoshiko.internal.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;

public class YoshikoClustersReader {
	
	private static final int BUFFER_SIZE = 16384;

	public YoshikoClustersReader() {

	}

	public List<List<Long>> getClusters(final CyNetwork inputNetwork,
			String text) {
		List<List<Long>> clusters = readTable(inputNetwork, text);
		return clusters;
	}

	private List<Long> getSUIDList(final CyNetwork inputNetwork,
			List<String> names) {
		final List<CyNode> nodes = inputNetwork.getNodeList();
		final CyTable ct = inputNetwork.getDefaultNodeTable();
		List<Long> SUIDList = new ArrayList<Long>();
		for (CyNode n : nodes) {
			CyRow cr = ct.getRow(n.getSUID());
			String name = cr.get("name", String.class);
			for (String str : names) {
				if (name.equals(str)) {
					SUIDList.add(n.getSUID());
				}
			}
		}
		return SUIDList;
	}

	private List<List<Long>> readTable(final CyNetwork inputNetwork,final String text) {
		List<List<Long>> clusters = new ArrayList<List<Long>>();

		String[] lines = text.split("\n");

		if (lines.length == 0) {
			return null;
		}
		int total_name = Integer.valueOf(lines[0]);
		int total_cluster = Integer.valueOf(lines[1]);
		String[] items_name = lines[2].split("\t");
		String name1 = items_name[0].trim();
		String name2 = items_name[1].trim();
		System.out.println(total_name);
		System.out.println(total_cluster);
		System.out.println(name1);
		System.out.println(name2);
		int cluster_index = 0;
		List<String> cluster = new ArrayList<String>();
		for (int i = 3; i < lines.length; i++) {
			String line = lines[i];
			String[] items = line.split("\t");
			String item1 = items[0].trim();
			String item2 = items[1].trim();
			int cidx = Integer.valueOf(item2);
			if (cidx != cluster_index) {
				clusters.add(getSUIDList(inputNetwork, cluster));
				cluster_index = cidx;
				cluster = new ArrayList<String>();
				cluster.add(item1);
			} else {
				cluster.add(item1);
			}
		}
		clusters.add(getSUIDList(inputNetwork, cluster));
		return clusters;
	}
	
	public String readFile(String path) throws IOException {
		InputStream stream = null;
		File fp = new File(path);
		stream = new FileInputStream(fp);
		String result = readString(stream);
		stream.close();
		return result;
	}
	
	private static String readString(InputStream source) throws IOException {
		StringWriter writer = new StringWriter();
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(source));
		try {
			char[] buffer = new char[BUFFER_SIZE];
			int charactersRead = reader.read(buffer, 0, buffer.length);
			while (charactersRead != -1) {
				writer.write(buffer, 0, charactersRead);
				charactersRead = reader.read(buffer, 0, buffer.length);
			}
		} finally {
			reader.close();
		}
		return writer.toString();
	}
}