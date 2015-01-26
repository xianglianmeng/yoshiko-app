package com.yoshiko.internal.model;

import java.util.ArrayList;
import java.util.List;
import org.cytoscape.model.CyNetwork;

public class YoshikoClustersReader {

	public YoshikoClustersReader() {

	}

	public List<List<Long>> getClusters(final CyNetwork inputNetwork,
			String text) {
		List<List<Long>> clusters = readTable(inputNetwork, text);
		return clusters;
	}

	private List<Long> getSUIDList(List<String> names) {
		List<Long> SUIDList = new ArrayList<Long>();
		for (String str : names) {
			SUIDList.add(Long.valueOf(str));
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
				clusters.add(getSUIDList(cluster));
				cluster_index = cidx;
				cluster = new ArrayList<String>();
				cluster.add(item1);
			} else {
				cluster.add(item1);
			}
		}
		clusters.add(getSUIDList(cluster));
		return clusters;
	}
}