package com.badlyby.yoshiko.internal.model;

/**
 * * Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center
 * *
 * * Code written by: Gary Bader
 * * Authors: Gary Bader, Ethan Cerami, Chris Sander
 * *
 * * This library is free software; you can redistribute it and/or modify it
 * * under the terms of the GNU Lesser General Public License as published
 * * by the Free Software Foundation; either version 2.1 of the License, or
 * * any later version.
 * *
 * * This library is distributed in the hope that it will be useful, but
 * * WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 * * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 * * documentation provided hereunder is on an "as is" basis, and
 * * Memorial Sloan-Kettering Cancer Center
 * * has no obligations to provide maintenance, support,
 * * updates, enhancements or modifications.  In no event shall the
 * * Memorial Sloan-Kettering Cancer Center
 * * be liable to any party for direct, indirect, special,
 * * incidental or consequential damages, including lost profits, arising
 * * out of the use of this software and its documentation, even if
 * * Memorial Sloan-Kettering Cancer Center
 * * has been advised of the possibility of such damage.  See
 * * the GNU Lesser General Public License for more details.
 * *
 * * You should have received a copy of the GNU Lesser General Public License
 * * along with this library; if not, write to the Free Software Foundation,
 * * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 * *
 ** User: Gary Bader
 ** Date: Jan 26, 2004
 ** Time: 2:44:30 PM
 ** Description: Stores an MCODE parameter set
 **/

/**
 * Stores an MCODE parameter set
 */
public class YoshikoParameterSet {

	//parameters

	//scope
	public static String COMMANDLINE = "commandline";
	public static String SOCKET = "sockets";
	public static String NETWORK = "network";
	public static String SELECTION = "selection";
	private String connectType = "";
	private String CMDLinePath = ".";
	private String SocketAddress = "";
	private int SocketPort;
	private String utilize = "";
	private String cpulimit = "";
	private String export = "";
	private String graph = "";
	private String multiplicative = "";
	private String number = "";
	private String rules = "";
	private String cuts = "";
	private String triangles = "";
	private String threads = "";
	private String verbosity = "";
	private String scope;
	private Long[] selectedNodes;

	//used in scoring stage
	private boolean includeLoops;
	private int degreeCutoff;
	private int kCore;

	//used in cluster finding stage
	private boolean optimize;
	private int maxDepthFromStart;
	private double nodeScoreCutoff;
	private boolean fluff;
	private boolean haircut;
	private double fluffNodeDensityCutoff;

	//result viewing parameters (only used for dialog box of results)
	private int defaultRowHeight;

	/**
	 * Constructor for the parameter set object. Default parameters are:
	 * scope = NETWORK,
	 * selectedNodes = new Integer[0],
	 * loops = false,
	 * degree cutoff = 2,
	 * max depth = 100,
	 * k-core = 2,
	 * node score cutoff = 0.2,
	 * fluff = false,
	 * haircut = true,
	 * fluff node density cutoff = 0.1,
	 * row height for results table = 80 pixels.
	 */
	public YoshikoParameterSet() {
		//default parameters
		setDefaultParams();

		//results dialog box
		defaultRowHeight = 150;
	}

	/**
	 * Constructor for non-default algorithm parameters.
	 * Once an alalysis is conducted, new parameters must be saved so that they can be retrieved in the result panel
	 * for exploration and export purposes.
	 */
	public YoshikoParameterSet(String connectType,
			String CMDLinePath,
			String SocketAddress,
			int SocketPort,
			String utilize,
			String cpulimit,
			String export,
			String graph,
			String multiplicative,
			String number,
			String rules,
			String cuts,
			String triangles,
			String threads,
			String verbosity) {

		setAllAlgorithmParams(connectType,
				CMDLinePath,
				SocketAddress,
				SocketPort,
				utilize,
				cpulimit,
				export,
				graph,
				multiplicative,
				number,
				rules,
				cuts,
				triangles,
				threads,
				verbosity);


		//results dialog box
		defaultRowHeight = 150;
	}

	/**
	 * Method for setting all parameters to their default values
	 */
	public void setDefaultParams() {
		setAllAlgorithmParams(COMMANDLINE,".", "localhost", 8080, "false", "-1", "false", "", "1", "1", "111111", "false", "false", "max", "0");
		setAllAlgorithmParams1(NETWORK, new Long[0], false, 2, 2, false, 100, 0.2, false, true, 0.1);
	}

	/**
	 * Convenience method to set all the main algorithm parameters
	 */
	public void setAllAlgorithmParams(String connectType,
			String CMDLinePath,
			String SocketAddress,
			int SocketPort,
			String utilize,
			String cpulimit,
			String export,
			String graph,
			String multiplicative,
			String number,
			String rules,
			String cuts,
			String triangles,
			String threads,
			String verbosity) {
		this.connectType = connectType;
		this.CMDLinePath = CMDLinePath;
		this.SocketAddress = SocketAddress;
		this.SocketPort = SocketPort;
		this.utilize = utilize;
		this.cpulimit = cpulimit;
		this.export = export;
		this.graph = graph;
		this.multiplicative = multiplicative;
		this.number = number;
		this.rules = rules;
		this.cuts = cuts;
		this.triangles = triangles;
		this.threads = threads;
		this.verbosity = verbosity;
	}
	
	public void setAllAlgorithmParams1(String scope,
			  Long[] selectedNodes,
			  boolean includeLoops,
			  int degreeCutoff,
			  int kCore,
			  boolean optimize,
			  int maxDepthFromStart,
			  double nodeScoreCutoff,
			  boolean fluff,
			  boolean haircut,
			  double fluffNodeDensityCutoff) {

	this.scope = scope;
	this.selectedNodes = selectedNodes;
	this.includeLoops = includeLoops;
	this.degreeCutoff = degreeCutoff;
	this.kCore = kCore;
	this.optimize = optimize;
	this.maxDepthFromStart = maxDepthFromStart;
	this.nodeScoreCutoff = nodeScoreCutoff;
	this.fluff = fluff;
	this.haircut = haircut;
	this.fluffNodeDensityCutoff = fluffNodeDensityCutoff;
	}

	/**
	 * Copies a parameter set object
	 *
	 * @return A copy of the parameter set
	 */
	public YoshikoParameterSet copy() {
		YoshikoParameterSet newParam = new YoshikoParameterSet();
		newParam.setconnectType(this.connectType);
		newParam.setCMDLinePath(this.CMDLinePath);
		newParam.setSocketAddress(this.SocketAddress);
		newParam.setSocketPort(this.SocketPort);
		newParam.setutilize(this.utilize);
		newParam.setcpulimit(this.cpulimit);
		newParam.setexport(this.export);
		newParam.setgraph(this.graph);
		newParam.setmultiplicative(this.multiplicative);
		newParam.setnumber(this.number);
		newParam.setrules(this.rules);
		newParam.setcuts(this.cuts);
		newParam.settriangles(this.triangles);
		newParam.setthreads(this.threads);
		newParam.setverbosity(this.verbosity);
		
		
		newParam.setScope(this.scope);
		newParam.setSelectedNodes(this.selectedNodes);
		newParam.setIncludeLoops(this.includeLoops);
		newParam.setDegreeCutoff(this.degreeCutoff);
		newParam.setKCore(this.kCore);
		newParam.setOptimize(this.optimize);
		newParam.setMaxDepthFromStart(this.maxDepthFromStart);
		newParam.setNodeScoreCutoff(this.nodeScoreCutoff);
		newParam.setFluff(this.fluff);
		newParam.setHaircut(this.haircut);
		newParam.setFluffNodeDensityCutoff(this.fluffNodeDensityCutoff);
		//results dialog box
		newParam.setDefaultRowHeight(this.defaultRowHeight);
		
		return newParam;
	}

	//parameter getting and setting
	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public Long[] getSelectedNodes() {
		return selectedNodes;
	}

	public void setSelectedNodes(Long[] selectedNodes) {
		this.selectedNodes = selectedNodes;
	}

	public boolean isIncludeLoops() {
		return includeLoops;
	}

	public void setIncludeLoops(boolean includeLoops) {
		this.includeLoops = includeLoops;
	}

	public int getDegreeCutoff() {
		return degreeCutoff;
	}
	
	public String getconnectType() {
		return connectType;
	}
	
	public void setconnectType(String value) {
		connectType = value;
	}
	
	public String getCMDLinePath() {
		return CMDLinePath;
	}
	
	public void setCMDLinePath(String value) {
		CMDLinePath = value;
	}
	
	public String getSocketAddress() {
		return SocketAddress;
	}
	
	public void setSocketAddress(String value) {
		SocketAddress = value;
	}
	
	public int getSocketPort() {
		return SocketPort;
	}
	
	public void setSocketPort(int value) {
		SocketPort = value;
	}

	public String getutilize() {
		return utilize;
	}

	public void setutilize(String value) {
		utilize = value;
	}

	public String getcpulimit() {
		return cpulimit;
	}

	public void setcpulimit(String value) {
		cpulimit = value;
	}
	
	public String getexport() {
		return export;
	}

	public void setexport(String value) {
		export = value;
	}

	public String getgraph() {
		return graph;
	}

	public void setgraph(String value) {
		graph = value;
	}

	public String getmultiplicative() {
		return multiplicative;
	}

	public void setmultiplicative(String value) {
		multiplicative = value;
	}

	public String getnumber() {
		return number;
	}

	public void setnumber(String value) {
		number = value;
	}

	public String getrules() {
		return rules;
	}

	public void setrules(String value) {
		rules = value;
	}

	public String getcuts() {
		return cuts;
	}

	public void setcuts(String value) {
		cuts = value;
	}

	public String gettriangles() {
		return triangles;
	}

	public void settriangles(String value) {
		triangles = value;
	}

	public String getthreads() {
		return threads;
	}

	public void setthreads(String value) {
		threads = value;
	}

	public String getverbosity() {
		return verbosity;
	}

	public void setverbosity(String value) {
		verbosity = value;
	}
	
	public void setDegreeCutoff(int degreeCutoff) {
		this.degreeCutoff = degreeCutoff;
	}

	public int getKCore() {
		return kCore;
	}

	public void setKCore(int kCore) {
		this.kCore = kCore;
	}

	public void setOptimize(boolean optimize) {
		this.optimize = optimize;
	}

	public boolean isOptimize() {
		return optimize;
	}

	public int getMaxDepthFromStart() {
		return maxDepthFromStart;
	}

	public void setMaxDepthFromStart(int maxDepthFromStart) {
		this.maxDepthFromStart = maxDepthFromStart;
	}

	public double getNodeScoreCutoff() {
		return nodeScoreCutoff;
	}

	public void setNodeScoreCutoff(double nodeScoreCutoff) {
		this.nodeScoreCutoff = nodeScoreCutoff;
	}

	public boolean isFluff() {
		return fluff;
	}

	public void setFluff(boolean fluff) {
		this.fluff = fluff;
	}

	public boolean isHaircut() {
		return haircut;
	}

	public void setHaircut(boolean haircut) {
		this.haircut = haircut;
	}

	public double getFluffNodeDensityCutoff() {
		return fluffNodeDensityCutoff;
	}

	public void setFluffNodeDensityCutoff(double fluffNodeDensityCutoff) {
		this.fluffNodeDensityCutoff = fluffNodeDensityCutoff;
	}

	public int getDefaultRowHeight() {
		return defaultRowHeight;
	}

	public void setDefaultRowHeight(int defaultRowHeight) {
		this.defaultRowHeight = defaultRowHeight;
	}

	/**
	 * Generates a summary of the parameters. Only parameters that are necessary are included.
	 * For example, if fluff is not turned on, the fluff density cutoff will not be included.
	 * 
	 * @return Buffered string summarizing the parameters
	 */
	public String toString() {
		String lineSep = System.getProperty("line.separator");
		StringBuffer sb = new StringBuffer();
		sb.append("   Network Scoring:" + lineSep + "      Include Loops: " + includeLoops + "  Degree Cutoff: " +
				  degreeCutoff + lineSep);
		sb.append("   Cluster Finding:" + lineSep + "      Node Score Cutoff: " + nodeScoreCutoff + "  Haircut: " +
				  haircut + "  Fluff: " + fluff +
				  ((fluff) ? ("  Fluff Density Cutoff " + fluffNodeDensityCutoff) : "") + "  K-Core: " + kCore +
				  "  Max. Depth from Seed: " + maxDepthFromStart + lineSep);
		return sb.toString();
	}
}
