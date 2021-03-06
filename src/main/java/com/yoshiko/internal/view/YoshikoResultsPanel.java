package com.yoshiko.internal.view;

import static org.cytoscape.view.presentation.property.BasicVisualLexicon.NETWORK_CENTER_X_LOCATION;
import static org.cytoscape.view.presentation.property.BasicVisualLexicon.NETWORK_CENTER_Y_LOCATION;
import static org.cytoscape.view.presentation.property.BasicVisualLexicon.NETWORK_HEIGHT;
import static org.cytoscape.view.presentation.property.BasicVisualLexicon.NETWORK_WIDTH;
import static org.cytoscape.view.presentation.property.BasicVisualLexicon.NODE_X_LOCATION;
import static org.cytoscape.view.presentation.property.BasicVisualLexicon.NODE_Y_LOCATION;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.SavePolicy;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.vizmap.VisualStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yoshiko.internal.YoshikoDiscardResultAction;
import com.yoshiko.internal.model.YoshikoAlgorithm;
import com.yoshiko.internal.model.YoshikoCluster;
import com.yoshiko.internal.util.YoshikoResources;
import com.yoshiko.internal.util.YoshikoUtil;
import com.yoshiko.internal.util.YoshikoResources.ImageName;
import com.yoshiko.internal.util.layout.SpringEmbeddedLayouter;

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
 * * User: Vuk Pavlovic
 * * Description: The Results Panel displaying found clusters
 */

/**
 * Reports the results of Yoshiko cluster finding. This class sets up the UI.
 */
public class YoshikoResultsPanel extends JPanel implements CytoPanelComponent {

	private static final long serialVersionUID = 868213052692609076L;
	
	//private static final String SCORE_ATTR = "Yoshiko_Score";
	//private static final String NODE_STATUS_ATTR = "Yoshiko_Node_Status";
	//private static final String CLUSTER_ATTR = "Yoshiko_Cluster";
	
	// table size parameters
	private static final int graphPicSize = 80;
	private static final int defaultRowHeight = graphPicSize + 8;
	
	private final int resultId;
	private final YoshikoAlgorithm alg;
	private final List<YoshikoCluster> clusters;
	
	// Actual cluster data
	private final CyNetwork network; // Keep a record of the original input record for use in
	// the table row selection listener
	private CyNetworkView networkView; // Keep a record of this too, if it exists
	private YoshikoCollapsiblePanel explorePanel;
	private JPanel[] exploreContent;
	private JButton closeButton;

	//private YoshikoParameterSet currentParamsCopy;
	// Keep track of selected attribute for enumeration so it stays selected for all cluster explorations
	private int enumerationSelection = 0;

	// Graphical classes
	private YoshikoClusterBrowserPanel clusterBrowserPanel;

	private final YoshikoUtil yoshikoUtil;
	private final YoshikoDiscardResultAction discardResultAction;
	
	private static final Logger logger = LoggerFactory.getLogger(YoshikoResultsPanel.class);

	/**
	 * Constructor for the Results Panel which displays the clusters in a
	 * browswer table and explore panels for each cluster.
	 * 
	 * @param clusters Found clusters from the YoshikoAnalyzeTask
	 * @param alg A reference to the alg for this particular network
	 * @param network Network were these clusters were found
	 * @param clusterImages A list of images of the found clusters
	 * @param resultId Title of this result as determined by YoshikoSCoreAndFindAction
	 */
	public YoshikoResultsPanel(final List<YoshikoCluster> clusters,
							 final YoshikoAlgorithm alg,
							 final YoshikoUtil yoshikoUtil,
							 final CyNetwork network,
							 final CyNetworkView networkView,
							 final int resultId,
							 final YoshikoDiscardResultAction discardResultAction) {
		setLayout(new BorderLayout());

		this.alg = alg;
		this.yoshikoUtil = yoshikoUtil;
		this.resultId = resultId;
		this.clusters = Collections.synchronizedList(clusters);
		this.network = network;
		// The view may not exist, but we only test for that when we need to (in the TableRowSelectionHandler below)
		this.networkView = networkView;
		this.discardResultAction = discardResultAction;
		//this.currentParamsCopy = yoshikoUtil.getCurrentParameters().getResultParams(resultId);
		
		this.clusterBrowserPanel = new YoshikoClusterBrowserPanel();
		add(clusterBrowserPanel, BorderLayout.CENTER);
		add(createBottomPanel(), BorderLayout.SOUTH);

		this.setSize(this.getMinimumSize());
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.EAST;
	}

	@Override
	public Icon getIcon() {
		URL iconURL = YoshikoResources.getUrl(ImageName.LOGO_SMALL);
		return new ImageIcon(iconURL);
	}

	@Override
	public String getTitle() {
		return "Result " + getResultId();
	}

	public int getResultId() {
		return this.resultId;
	}

	public CyNetworkView getNetworkView() {
		return networkView;
	}
	
	public List<YoshikoCluster> getClusters() {
		return clusters;
	}

	public CyNetwork getNetwork() {
		return network;
	}

	public int getSelectedClusterRow() {
		return clusterBrowserPanel.getSelectedRow();
	}

	public void discard(final boolean requestUserConfirmation) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				boolean oldRequestUserConfirmation = Boolean.valueOf(discardResultAction
						.getValue(YoshikoDiscardResultAction.REQUEST_USER_CONFIRMATION_COMMAND).toString());

				discardResultAction.putValue(YoshikoDiscardResultAction.REQUEST_USER_CONFIRMATION_COMMAND,
											 requestUserConfirmation);
				closeButton.doClick();
				discardResultAction.putValue(YoshikoDiscardResultAction.REQUEST_USER_CONFIRMATION_COMMAND,
											 oldRequestUserConfirmation);
			}
		});
	}

	/**
	 * Creates a panel containing the explore collapsable panel and result set
	 * specific buttons
	 * 
	 * @return Panel containing the explore cluster collapsable panel and button
	 *         panel
	 */
	private JPanel createBottomPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		explorePanel = new YoshikoCollapsiblePanel("Explore");
		explorePanel.setCollapsed(false);
		explorePanel.setVisible(false);

		JPanel buttonPanel = new JPanel();

		// The Export button
		//JButton exportButton = new JButton("Export");
		//exportButton.addActionListener(new YoshikoResultsPanel.ExportAction());
		//exportButton.setToolTipText("Export result set to a text file");

		// The close button
		closeButton = new JButton(discardResultAction);
		discardResultAction.putValue(YoshikoDiscardResultAction.REQUEST_USER_CONFIRMATION_COMMAND, true);

		//buttonPanel.add(exportButton);
		buttonPanel.add(closeButton);

		panel.add(explorePanel, BorderLayout.NORTH);
		panel.add(buttonPanel, BorderLayout.SOUTH);

		return panel;
	}

	/**
	 * This method creates a JPanel containing a node score cutoff slider and a
	 * node attribute enumeration viewer
	 * 
	 * @param selectedRow
	 *            The cluster that is selected in the cluster browser
	 * @return panel A JPanel with the contents of the explore panel, get's
	 *         added to the explore collapsable panel's content pane
	 */
	//@SuppressWarnings("rawtypes")
	private JPanel createExploreContent(int selectedRow) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		//JPanel sizePanel = new JPanel(new BorderLayout());
		//sizePanel.setBorder(BorderFactory.createTitledBorder("Size Threshold"));

		// Create a slider to manipulate node score cutoff (goes to 1000 so that
		// we get a more precise double variable out of it)
		/*JSlider sizeSlider = new JSlider(JSlider.HORIZONTAL, 0, 1000,
										 (int) (currentParamsCopy.getNodeScoreCutoff() * 1000)) {

			public JToolTip createToolTip() {
				return new JMultiLineToolTip();
			}
		};

		// Turn on ticks and labels at major and minor intervals.
		sizeSlider.setMajorTickSpacing(200);
		sizeSlider.setMinorTickSpacing(50);
		sizeSlider.setPaintTicks(true);
		sizeSlider.setPaintLabels(true);*/

		// Set labels ranging from 0 to 100
		/*Dictionary<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(0, new JLabel("Min"));
		labelTable.put(1000, new JLabel("Max"));
		// Make a special label for the initial position
		labelTable.put((int) (currentParamsCopy.getNodeScoreCutoff() * 1000), new JLabel("^"));

		sizeSlider.setLabelTable(labelTable);
		sizeSlider.setFont(new Font("Arial", Font.PLAIN, 8));

		String sizeTip = "Move the slider to include or\nexclude nodes from the cluster";
		sizeSlider.setToolTipText(sizeTip);

		sizePanel.add(sizeSlider, BorderLayout.NORTH);
		*/
		// Node attributes enumerator
		/*JPanel nodeAttributesPanel = new JPanel(new BorderLayout());
		nodeAttributesPanel.setBorder(BorderFactory.createTitledBorder("Node Attribute Enumerator"));

		Collection<CyColumn> nodeColumns = network.getDefaultNodeTable().getColumns();
		String[] availableAttributes = new String[nodeColumns.size()];

		int i = 0;
		for (CyColumn column : nodeColumns)
			availableAttributes[i++] = column.getName();

		Arrays.sort(availableAttributes, String.CASE_INSENSITIVE_ORDER);

		String[] attributesList = new String[availableAttributes.length + 1];
		System.arraycopy(availableAttributes, 0, attributesList, 1, availableAttributes.length);
		attributesList[0] = "Please Select";

		JComboBox nodeAttributesComboBox = new JComboBox(attributesList);

		sizeSlider.addChangeListener(new YoshikoResultsPanel.SizeAction(selectedRow, nodeAttributesComboBox));

		// Create a table listing the node attributes and their enumerations
		final YoshikoResultsPanel.YoshikoResultsEnumeratorTableModel modelEnumerator;
		modelEnumerator = new YoshikoResultsPanel.YoshikoResultsEnumeratorTableModel(new HashMap());

		JTable enumerationsTable = new JTable(modelEnumerator);

		JScrollPane tableScrollPane = new JScrollPane(enumerationsTable);
		tableScrollPane.getViewport().setBackground(Color.WHITE);
		enumerationsTable.setPreferredScrollableViewportSize(new Dimension(100, graphPicSize));
		enumerationsTable.setGridColor(Color.LIGHT_GRAY);
		enumerationsTable.setFont(new Font(enumerationsTable.getFont().getFontName(), Font.PLAIN, 11));
		enumerationsTable.setDefaultRenderer(StringBuffer.class, new YoshikoResultsPanel.JTextAreaRenderer(0));
		enumerationsTable.setFocusable(false);

		// Create a combo box that lists all the available node attributes for enumeration
		nodeAttributesComboBox.addActionListener(new YoshikoResultsPanel.enumerateAction(modelEnumerator, selectedRow));

		nodeAttributesPanel.add(nodeAttributesComboBox, BorderLayout.NORTH);
		nodeAttributesPanel.add(tableScrollPane, BorderLayout.SOUTH);
		*/

		JPanel bottomExplorePanel = createBottomExplorePanel(selectedRow);

		//panel.add(sizePanel);
		//panel.add(nodeAttributesPanel);
		panel.add(bottomExplorePanel);

		return panel;
	}

	/**
	 * Creates a panel containing buttons for the cluster explore collapsable panel
	 * @param selectedRow Currently selected row in the cluster browser table
	 * @return panel
	 */
	private JPanel createBottomExplorePanel(int selectedRow) {
		JPanel panel = new JPanel();
		JButton createChildButton = new JButton("Create Sub-Network");
		createChildButton.addActionListener(new YoshikoResultsPanel.CreateSubNetworkAction(this, selectedRow));
		panel.add(createChildButton);

		return panel;
	}

	/**
	 * Sets the network node attributes to the current result set's scores and clusters.
	 * This method is accessed from YoshikoVisualStyleAction only when a results panel is selected in the east cytopanel.
	 * 
	 * @return the maximal score in the network given the parameters that were
	 *         used for scoring at the time
	 */
	public double setNodeAttributesAndGetMaxScore() {
		/*for (CyNode n : network.getNodeList()) {
			Long rgi = n.getSUID();
			CyTable netNodeTbl = network.getDefaultNodeTable();
			
			if (netNodeTbl.getColumn(CLUSTER_ATTR) == null)
				netNodeTbl.createListColumn(CLUSTER_ATTR, String.class, false);
			if (netNodeTbl.getColumn(NODE_STATUS_ATTR) == null)
				netNodeTbl.createColumn(NODE_STATUS_ATTR, String.class, false);
			if (netNodeTbl.getColumn(SCORE_ATTR) == null)
				netNodeTbl.createColumn(SCORE_ATTR, Double.class, false);

			CyRow nodeRow = network.getRow(n);
			nodeRow.set(NODE_STATUS_ATTR, "Unclustered");
			nodeRow.set(SCORE_ATTR, alg.getNodeScore(n.getSUID(), resultId));

			for (final YoshikoCluster cluster : clusters) {
				if (cluster.getALCluster().contains(rgi)) {
					Set<String> clusterNameSet = new LinkedHashSet<String>();

					if (nodeRow.isSet(CLUSTER_ATTR))
						clusterNameSet.addAll(nodeRow.getList(CLUSTER_ATTR, String.class));

					clusterNameSet.add(cluster.getName());
					nodeRow.set(CLUSTER_ATTR, new ArrayList<String>(clusterNameSet));

					if (cluster.getSeedNode() == rgi)
						nodeRow.set(NODE_STATUS_ATTR, "Seed");
					else
						nodeRow.set(NODE_STATUS_ATTR, "Clustered");
				}
			}
		}*/

		return alg.getMaxScore(resultId);
	}
	
	private static StringBuffer getClusterDetails(final YoshikoCluster cluster) {
		StringBuffer details = new StringBuffer();

		details.append("Module: ");
		details.append(String.valueOf(cluster.getModule() + 1));

		/*details.append("\n");
		details.append("Score: ");
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(3);
		details.append(nf.format(cluster.getScore()));*/

		details.append("\n");
		details.append("Nodes: ");
		details.append(cluster.getNetwork().getNodeCount());

		details.append("\n");
		details.append("Edges: ");
		details.append(cluster.getNetwork().getEdgeCount());

		return details;
	}

	/**
	 * Handles the create child network press in the cluster exploration panel
	 */
	private class CreateSubNetworkAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		int selectedRow;
		YoshikoResultsPanel trigger;

		CreateSubNetworkAction(YoshikoResultsPanel trigger, int selectedRow) {
			this.selectedRow = selectedRow;
			this.trigger = trigger;
		}

		@Override
		public void actionPerformed(final ActionEvent evt) {
			final NumberFormat nf = NumberFormat.getInstance();
			nf.setMaximumFractionDigits(3);
			final YoshikoCluster cluster = clusters.get(selectedRow);
			final CyNetwork clusterNetwork = cluster.getNetwork();
			final String title = trigger.getResultId() + ": " + cluster.getName();
			// Create the child network and view
			final SwingWorker<CyNetworkView, ?> worker = new SwingWorker<CyNetworkView, Object>() {

				@Override
				protected CyNetworkView doInBackground() throws Exception {
					CySubNetwork newNetwork = yoshikoUtil.createSubNetwork(clusterNetwork, clusterNetwork.getNodeList(),
							SavePolicy.SESSION_FILE);
					newNetwork.getRow(newNetwork).set(CyNetwork.NAME, title);
					
					VisualStyle vs = yoshikoUtil.getNetworkViewStyle(networkView);
					CyNetworkView newNetworkView = yoshikoUtil.createNetworkView(newNetwork, vs);

					newNetworkView.setVisualProperty(NETWORK_CENTER_X_LOCATION, 0.0);
					newNetworkView.setVisualProperty(NETWORK_CENTER_Y_LOCATION, 0.0);

					yoshikoUtil.displayNetworkView(newNetworkView);

					// Layout new cluster and fit it to window.
					// Randomize node positions before layout so that they don't all layout in a line
					// (so they don't fall into a local minimum for the SpringEmbedder)
					// If the SpringEmbedder implementation changes, this code may need to be removed
					boolean layoutNecessary = false;
					CyNetworkView clusterView = cluster.getView();
					
					for (View<CyNode> nv : newNetworkView.getNodeViews()) {
						CyNode node = nv.getModel();
						View<CyNode> cnv = clusterView != null ? clusterView.getNodeView(node) : null;
						
						if (cnv != null) {
							// If it does, then we take the layout position that was already generated for it
							double x = cnv.getVisualProperty(NODE_X_LOCATION);
							double y = cnv.getVisualProperty(NODE_Y_LOCATION);
							nv.setVisualProperty(NODE_X_LOCATION, x);
							nv.setVisualProperty(NODE_Y_LOCATION, y);
						} else {
							// This will likely never occur.
							// Otherwise, randomize node positions before layout so that they don't all layout in a line
							// (so they don't fall into a local minimum for the SpringEmbedder).
							// If the SpringEmbedder implementation changes, this code may need to be removed.
							double w = newNetworkView.getVisualProperty(NETWORK_WIDTH);
							double h = newNetworkView.getVisualProperty(NETWORK_HEIGHT);

							nv.setVisualProperty(NODE_X_LOCATION, w * Math.random());
							// height is small for many default drawn graphs, thus +100
							nv.setVisualProperty(NODE_Y_LOCATION, (h + 100) * Math.random());

							layoutNecessary = true;
						}
					}

					if (layoutNecessary) {
						SpringEmbeddedLayouter lay = new SpringEmbeddedLayouter(newNetworkView);
						lay.doLayout(0, 0, 0, null);
					}

					newNetworkView.fitContent();
					newNetworkView.updateView();

					return newNetworkView;
				}
			};

			worker.execute();
		}
	}

	/**
	 * Panel that contains the browser table with a scroll bar.
	 */
	private class YoshikoClusterBrowserPanel extends JPanel {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final YoshikoResultsPanel.YoshikoClusterBrowserTableModel browserModel;
		private final JTable table;
		
		public YoshikoClusterBrowserPanel() {
			super();
			setLayout(new BorderLayout());
			setBorder(BorderFactory.createTitledBorder("Module Browser"));

			// main data table
			browserModel = new YoshikoResultsPanel.YoshikoClusterBrowserTableModel();

			table = new JTable(browserModel);
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table.setDefaultRenderer(StringBuffer.class, new YoshikoResultsPanel.JTextAreaRenderer(defaultRowHeight));
			table.setIntercellSpacing(new Dimension(0, 4)); // gives a little vertical room between clusters
			table.setFocusable(false); // removes an outline that appears when the user clicks on the images

			// Ask to be notified of selection changes.
			ListSelectionModel rowSM = table.getSelectionModel();
			rowSM.addListSelectionListener(new YoshikoResultsPanel.TableRowSelectionHandler());

			JScrollPane tableScrollPane = new JScrollPane(table);
			tableScrollPane.getViewport().setBackground(Color.WHITE);

			add(tableScrollPane, BorderLayout.CENTER);
		}

		public int getSelectedRow() {
			return table.getSelectedRow();
		}
		
		public void update(final ImageIcon image, final int row) {
			table.setValueAt(image, row, 0);
		}
		
		public void update(final YoshikoCluster cluster, final int row) {
			final StringBuffer details = getClusterDetails(cluster);
			table.setValueAt(details, row, 1);
		}

		JTable getTable() { // TODO: delete it: do not expose the JTable
			return table;
		}
	}
	
	/**
	 * Handles the data to be displayed in the cluster browser table
	 */
	private class YoshikoClusterBrowserTableModel extends AbstractTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final String[] columnNames = { "Network", "Details" };
		private final Object[][] data; // the actual table data

		public YoshikoClusterBrowserTableModel() {
			exploreContent = new JPanel[clusters.size()];
			data = new Object[clusters.size()][columnNames.length];

			for (int i = 0; i < clusters.size(); i++) {
				final YoshikoCluster c = clusters.get(i);
				c.setModule(i);
				StringBuffer details = getClusterDetails(c);
				data[i][1] = new StringBuffer(details);

				// get an image for each cluster - make it a nice layout of the cluster
				final Image image = c.getImage();
				data[i][0] = image != null ? new ImageIcon(image) : new ImageIcon();
			}
		}

		@Override
		public String getColumnName(int col) {
			return columnNames[col];
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public int getRowCount() {
			return data.length;
		}

		@Override
		public Object getValueAt(int row, int col) {
			return data[row][col];
		}

		@Override
		public void setValueAt(Object object, int row, int col) {
			data[row][col] = object;
			fireTableCellUpdated(row, col);
		}

		@Override
		public Class<?> getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}
	}

	/**
	 * Handles the data to be displayed in the node attribute enumeration table
	 */
	private class YoshikoResultsEnumeratorTableModel extends AbstractTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		// Create column headings
		String[] columnNames = { "Value", "Occurrence" };
		Object[][] data = new Object[0][columnNames.length]; // the actual table

		// data

		public YoshikoResultsEnumeratorTableModel(HashMap<?, ?> enumerations) {
			listIt(enumerations);
		}

		@SuppressWarnings("rawtypes")
		public void listIt(HashMap<?, ?> enumerations) {
			// First we sort the hash map of attributes values and their occurrences
			ArrayList<?> enumerationsSorted = sortMap(enumerations);
			// Then we put it into the data array in reverse order so that the
			// most frequent attribute value is on top
			Object[][] newData = new Object[enumerationsSorted.size()][columnNames.length];
			int c = enumerationsSorted.size() - 1;

			for (Iterator i = enumerationsSorted.iterator(); i.hasNext();) {
				Map.Entry mp = (Map.Entry) i.next();
				newData[c][0] = new StringBuffer(mp.getKey().toString());
				newData[c][1] = mp.getValue().toString();
				c--;
			}

			// Finally we redraw the table, however, in order to prevent constant flickering
			// we only fire the data change if the number or rows is altered.
			// That way, when the number of rows stays the same, which is most of the
			// time, there is no flicker.
			if (getRowCount() == newData.length) {
				data = new Object[newData.length][columnNames.length];
				System.arraycopy(newData, 0, data, 0, data.length);
				fireTableRowsUpdated(0, getRowCount());
			} else {
				data = new Object[newData.length][columnNames.length];
				System.arraycopy(newData, 0, data, 0, data.length);
				fireTableDataChanged();
			}
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public int getRowCount() {
			return data.length;
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public Object getValueAt(int row, int col) {
			return data[row][col];
		}

		public void setValueAt(Object object, int row, int col) {
			data[row][col] = object;
			fireTableCellUpdated(row, col);
		}

		public Class<?> getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		public boolean isCellEditable(int row, int col) {
			return false;
		}
	}

	/**
	 * This method uses Arrays.sort for sorting a Map by the entries' values
	 * 
	 * @param map
	 *            Has values mapped to keys
	 * @return outputList of Map.Entries
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ArrayList sortMap(Map map) {
		ArrayList outputList = null;
		int count = 0;
		Set set = null;
		Map.Entry[] entries = null;

		set = (Set) map.entrySet();
		Iterator iterator = set.iterator();
		entries = new Map.Entry[set.size()];

		while (iterator.hasNext()) {
			entries[count++] = (Map.Entry) iterator.next();
		}

		// Sort the entries with own comparator for the values:
		Arrays.sort(entries, new Comparator<Map.Entry>() {

			@Override
			public int compare(Map.Entry o1, Map.Entry o2) {
				return ((Comparable) o1.getValue()).compareTo((Comparable) o2.getValue());
			}
		});

		outputList = new ArrayList();

		for (int i = 0; i < entries.length; i++) {
			outputList.add(entries[i]);
		}

		return outputList;
	}

	/**
	 * Handles the selection of all available node attributes for the enumeration within the cluster
	 */
	private class enumerateAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		int selectedRow;
		YoshikoResultsPanel.YoshikoResultsEnumeratorTableModel modelEnumerator;

		enumerateAction(YoshikoResultsPanel.YoshikoResultsEnumeratorTableModel modelEnumerator, int selectedRow) {
			this.selectedRow = selectedRow;
			this.modelEnumerator = modelEnumerator;
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void actionPerformed(ActionEvent e) {
			// The key is the attribute value and the value is the number of times that value appears in the cluster
			HashMap<String, Integer> attributeEnumerations = new HashMap<String, Integer>();

			// First we want to see which attribute was selected in the combo box
			String attributeName = (String) ((JComboBox) e.getSource()).getSelectedItem();
			int selectionIndex = (int) ((JComboBox) e.getSource()).getSelectedIndex();

			// If its the generic 'please select' option then we don't do any enumeration
			if (!attributeName.equals("Please Select")) {
				final CyNetwork net = clusters.get(selectedRow).getNetwork();
				
				// Otherwise, we want to get the selected attribute's value for each node in the selected cluster
				for (CyNode node : net.getNodeList()) {
					// The attribute value will be stored as a string no matter
					// what it is but we need an array list because some attributes are maps or lists of any size
					ArrayList attributeValues = new ArrayList();
					CyRow row = net.getRow(node);
					Class<?> type = row.getTable().getColumn(attributeName).getType();

					if (Collection.class.isAssignableFrom(type)) {
						Collection valueList = (Collection) row.get(attributeName, type);

						if (valueList != null) {
							for (Object value : valueList) {
								attributeValues.add(value);
							}
						}
					} else {
						attributeValues.add(row.get(attributeName, type));
					}

					// Next we must make a non-repeating list with the attribute values and enumerate the repetitions
					for (Object aviElement : attributeValues) {
						if (aviElement != null) {
							String value = aviElement.toString();

							if (!attributeEnumerations.containsKey(value)) {
								// If the attribute value appears for the first
								// time, we give it an enumeration of 1 and add it to the enumerations
								attributeEnumerations.put(value, 1);
							} else {
								// If it already appeared before, we want to add to the enumeration of the value
								Integer enumeration = (Integer) attributeEnumerations.get(value);
								enumeration = enumeration.intValue() + 1;
								attributeEnumerations.put(value, enumeration);
							}
						}
					}
				}
			}

			modelEnumerator.listIt(attributeEnumerations);
			// Finally we make sure that the selection is stored so that all the
			// cluster explorations are looking at the already selected attribute
			enumerationSelection = selectionIndex;
		}
	}

	/**
	 * Handles the Export press for this panel (export results to a text file)
	 */
	private class ExportAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			yoshikoUtil.exportYoshikoResults(alg, clusters, network);
		}
	}

	/**
	 * Handler to select nodes in graph when a row is selected
	 */
	private class TableRowSelectionHandler implements ListSelectionListener {

		public void valueChanged(ListSelectionEvent e) {
			// Ignore extra messages.
			if (e.getValueIsAdjusting()) return;

			ListSelectionModel lsm = (ListSelectionModel) e.getSource();
			final CyNetwork gpCluster;

			if (!lsm.isSelectionEmpty()) {
				final int selectedRow = lsm.getMinSelectionIndex();
				final YoshikoCluster c = clusters.get(selectedRow);
				gpCluster = c.getNetwork();
				selectCluster(gpCluster);

				// Upon selection of a cluster, we must show the corresponding explore panel content
				// First we test if this cluster has been selected yet and if its content exists
				// If it does not, we create it
				if (exploreContent[selectedRow] == null) {
					exploreContent[selectedRow] = createExploreContent(selectedRow);
				}

				// Next, if this is the first time explore panel content is being displayed, then the
				// explore panel is not visible yet, and there is no content in
				// it yet, so we do not have to remove it, otherwise,
				// if the panel is visible, then it must have content which needs to be removed
				if (explorePanel.isVisible()) {
					explorePanel.getContentPane().remove(0);
				}

				// Now we add the currently selected cluster's explore panel
				// content
				explorePanel.getContentPane().add(exploreContent[selectedRow], BorderLayout.CENTER);

				// and set the explore panel to visible so that it can be seen
				// (this only happens once after the first time the user selects a cluster
				if (!explorePanel.isVisible()) {
					explorePanel.setVisible(true);
				}

				// Finally the explore panel must be redrawn upon the selection
				// event to display the new content with the name of the cluster, if it exists
				String title = "Explore: ";

				if (c.getName() != null) {
					title = title + c.getName();
				} else {
					title = title + "Module " + (selectedRow + 1);
				}

				explorePanel.setTitleComponentText(title);
				explorePanel.updateUI();

				// In order for the enumeration to be conducted for this cluster
				// on the same attribute that might already have been selected
				// we get a reference to the combo box within the explore content
				JComboBox nodeAttributesComboBox = (JComboBox) ((JPanel) exploreContent[selectedRow].getComponent(1))
						.getComponent(0);
				// and fire the enumeration action
				nodeAttributesComboBox.setSelectedIndex(enumerationSelection);

				// TODO: it doesn't work from here
				// This needs to be called when the explore panel shows up 
				// so that any selection in the cluster browser is centered in the visible portion of the table
				// table.scrollRectToVisible(table.getCellRect(selectedRow, 0, true));
			}
		}
	}

	/**
	 * Selects a cluster in the view that is selected by the user in the browser table
	 * 
	 * @param custerNetwork Cluster to be selected
	 */
	public void selectCluster(final CyNetwork custerNetwork) {
		if (custerNetwork != null) {
			// Only do this if a view has been created on this network
			// start with no selected nodes
			//				yoshikoUtil.setSelected(network.getNodeList(), false, networkView);
			yoshikoUtil.setSelected(custerNetwork.getNodeList(), network);

			// TODO: is it still necessary?
			// We want the focus to switch to the appropriate network view but only if the cytopanel is docked
			// If it is not docked then it is best if the focus stays on the panel
			//				if (swingApplication.getCytoPanel(CytoPanelName.EAST).getState() == CytoPanelState.DOCK) {
			//					
			//					 Cytoscape.getDesktop().setFocus(networkView.getSUID());
			//				}
		} else {
			yoshikoUtil.setSelected(new ArrayList<CyIdentifiable>(), network); // deselect all
		}
	}

	/**
	 * A text area renderer that creates a line wrapped, non-editable text area
	 */
	private static class JTextAreaRenderer extends JTextArea implements TableCellRenderer {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		int minHeight;

		/**
		 * Constructor
		 * 
		 * @param minHeight
		 *            The minimum height of the row, either the size of the
		 *            graph picture or zero
		 */
		public JTextAreaRenderer(int minHeight) {
			this.setLineWrap(true);
			this.setWrapStyleWord(true);
			this.setEditable(false);
			this.setFont(new Font(this.getFont().getFontName(), Font.PLAIN, 11));
			this.minHeight = minHeight;
		}

		/**
		 * Used to render a table cell. Handles selection color and cell heigh
		 * and width. Note: Be careful changing this code as there could easily
		 * be infinite loops created when calculating preferred cell size as the
		 * user changes the dialog box size.
		 * 
		 * @param table Parent table of cell
		 * @param value Value of cell
		 * @param isSelected True if cell is selected
		 * @param hasFocus True if cell has focus
		 * @param row The row of this cell
		 * @param column The column of this cell
		 * @return The cell to render by the calling code
		 */
		public Component getTableCellRendererComponent(JTable table,
													   Object value,
													   boolean isSelected,
													   boolean hasFocus,
													   int row,
													   int column) {
			StringBuffer sb = (StringBuffer) value;
			this.setText(sb.toString());

			if (isSelected) {
				this.setBackground(table.getSelectionBackground());
				this.setForeground(table.getSelectionForeground());
			} else {
				this.setBackground(table.getBackground());
				this.setForeground(table.getForeground());
			}

			// Row height calculations
			int currentRowHeight = table.getRowHeight(row);
			int rowMargin = table.getRowMargin();
			this.setSize(table.getColumnModel().getColumn(column).getWidth(), currentRowHeight - (2 * rowMargin));
			int textAreaPreferredHeight = (int) this.getPreferredSize().getHeight();

			// JTextArea can grow and shrink here
			if (currentRowHeight != Math.max(textAreaPreferredHeight + (2 * rowMargin), minHeight + (2 * rowMargin))) {
				table.setRowHeight(row, Math
						.max(textAreaPreferredHeight + (2 * rowMargin), minHeight + (2 * rowMargin)));
			}

			return this;
		}
	}

	/**
	 * Handles the dynamic cluster size manipulation via the JSlider
	 */
	private class SizeAction implements ChangeListener {

		private final ScheduledExecutorService scheduler =  Executors.newScheduledThreadPool(3);
		private ScheduledFuture<?> futureLoader;
		
		private int selectedRow;
		private JComboBox nodeAttributesComboBox;
		private boolean drawPlaceHolder;
		private final GraphDrawer drawer;
		private final YoshikoLoader loader;
		
		/**
		 * @param selectedRow The selected cluster
		 * @param nodeAttributesComboBox Reference to the attribute enumeration picker
		 */
		SizeAction(final int selectedRow, final JComboBox nodeAttributesComboBox) {
			this.selectedRow = selectedRow;
			this.nodeAttributesComboBox = nodeAttributesComboBox;
			loader = new YoshikoLoader(selectedRow, clusterBrowserPanel.getTable(), graphPicSize, graphPicSize);
			drawer = new GraphDrawer(loader);
		}

		public void stateChanged(final ChangeEvent e) {
			// This method as been written so that the slider is responsive to the user's input at all times, despite
			// the computationally challenging drawing, layout out, and selection of large clusters. As such, we only
			// perform real work if the slider produces a different cluster, and furthermore we only perform the quick
			// processes here, while the heavy weights are handled by the drawer's thread.
			final JSlider source = (JSlider) e.getSource();
			final double nodeScoreCutoff = (((double) source.getValue()) / 1000);
			final int clusterRow = selectedRow;
			// Store current cluster content for comparison
        	final YoshikoCluster oldCluster = clusters.get(clusterRow);
			
			if (futureLoader != null && !futureLoader.isDone()) {
				drawer.stop();
				futureLoader.cancel(false);
				
				if (!oldCluster.equals(clusters.get(clusterRow)))
					oldCluster.dispose();
			}
			
			final Runnable command = new Runnable() {
	            @Override
	        	public void run() {
	            	final List<Long> oldALCluster = oldCluster.getALCluster();
					// Find the new cluster given the node score cutoff
					final YoshikoCluster newCluster = alg.exploreCluster(oldCluster, nodeScoreCutoff, network, resultId);
					
					// We only want to do the following work if the newly found cluster is actually different
					// So we get the new cluster content
					List<Long> newALCluster = newCluster.getALCluster();
					
					// If the new cluster is too large to draw within a reasonable time
					// and won't look understandable in the table cell, then we draw a place holder
					drawPlaceHolder = newALCluster.size() > 300;
					
					// And compare the old and new
					if (!newALCluster.equals(oldALCluster)) { // TODO
						// If the cluster has changed, then we conduct all non-rate-limiting steps:
						// Update the cluster array
						clusters.set(clusterRow, newCluster);
						// Update the cluster details
						clusterBrowserPanel.update(newCluster, clusterRow);
						// Fire the enumeration action
						nodeAttributesComboBox.setSelectedIndex(nodeAttributesComboBox.getSelectedIndex());
		
						// There is a small difference between expanding and retracting the cluster size.
						// When expanding, new nodes need random position and thus must go through the layout.
						// When retracting, we simply use the layout that was generated and stored.
						// This speeds up the drawing process greatly.
						boolean layoutNecessary = newALCluster.size() > oldALCluster.size();
						// Draw Graph and select the cluster in the view in a separate thread so that it can be
						// interrupted by the slider movement
						if (!newCluster.isDisposed()) {
							drawer.drawGraph(newCluster, layoutNecessary, drawPlaceHolder);
							oldCluster.dispose();
						}
					}
					yoshikoUtil.destroyUnusedNetworks(network, clusters);// TODO
	            }
	        };
	        
	        futureLoader = scheduler.schedule(command, 100, TimeUnit.MILLISECONDS);
		}
	}

	/**
	 * Threaded method for drawing exploration graphs which allows the slider to
	 * move uninterruptedly despite Yoshiko's drawing efforts
	 */
	private class GraphDrawer implements Runnable {

		private boolean drawGraph; // run switch
		private boolean placeHolderDrawn;
		private boolean drawPlaceHolder;
		YoshikoCluster cluster;
		SpringEmbeddedLayouter layouter;
		boolean layoutNecessary;
		boolean clusterSelected;
		private Thread t;
		private final  YoshikoLoader loader;

		GraphDrawer(final YoshikoLoader loader) {
			this.loader = loader;
			layouter = new SpringEmbeddedLayouter();
		}

		/**
		 * Constructor for drawing graphs during exploration
		 * 
		 * @param cluster Cluster to be drawn
		 * @param layout Necessary True only if the cluster is expanding in size or lacks a DGView
		 * @param trigger Reference to the slider size action
		 * @param drawPlaceHolder Determines if the cluster should be drawn or a place
		 *                        holder in the case of big clusters
		 */
		public void drawGraph(YoshikoCluster cluster,
							  boolean layoutNecessary,
							  boolean drawPlaceHolder) {
			this.cluster = cluster;
			this.layoutNecessary = layoutNecessary;

			// Graph drawing will only occur if the cluster is not too large,
			// otherwise a place holder will be drawn
			drawGraph = !drawPlaceHolder;
			this.drawPlaceHolder = drawPlaceHolder;
			clusterSelected = false;
			t = new Thread(this);
			t.start();
		}

		public void run() {
			try {
				// We want to set the loader only once during continuous exploration
				// It is only set again when a graph is fully loaded and placed in the table
				if (!drawPlaceHolder) {
					// internally, the loader is only drawn into the appropriate cell after a short sleep period
					// to ensure that quick loads are not displayed unnecessarily
					loader.start();
				}
				
				final Thread currentThread = Thread.currentThread(); 
				
				while (t == currentThread) {
					// This ensures that the drawing of this cluster is only attempted once
					// if it is unsuccessful it is because the setup or layout
					// process was interrupted by the slider movement
					// In that case the drawing must occur for a new cluster using the drawGraph method
					if (drawGraph && !drawPlaceHolder) {
						Image image = yoshikoUtil.createClusterImage(cluster,
																   graphPicSize,
																   graphPicSize,
																   layouter,
																   layoutNecessary,
																   loader);
						// If the drawing process was interrupted, a new cluster must have been found and
						// this will have returned null, the drawing will be recalled (with the new cluster)
						// However, if the graphing was successful, we update
						// the table, stop the loader from animating and select the new cluster
						if (image != null && drawGraph) {
							// Select the new cluster (surprisingly a time consuming step)
							loader.setProgress(100, "Selecting Nodes");
							selectCluster(cluster.getNetwork());
							clusterSelected = true;
							cluster.setImage(image);
							// Update the table
							clusterBrowserPanel.update(new ImageIcon(image), cluster.getModule());
							drawGraph = false;
						}

						// This is here just in case to reset the variable
						placeHolderDrawn = false;
					} else if (drawPlaceHolder && !placeHolderDrawn) {
						// draw place holder, only once though (as per the if statement)
						Image image = yoshikoUtil.getPlaceHolderImage(graphPicSize, graphPicSize);
						cluster.setImage(image);
						// Update the table
						clusterBrowserPanel.update(new ImageIcon(image), cluster.getModule());
						// select the cluster
						selectCluster(cluster.getNetwork());
						drawGraph = false;
						// Make sure this block is not run again unless if we need to reload the image
						placeHolderDrawn = true;
					} else if (!drawGraph && drawPlaceHolder && !clusterSelected) {
						selectCluster(cluster.getNetwork());
						clusterSelected = true;
					}

					if ((!drawGraph && !drawPlaceHolder) || placeHolderDrawn)
						stop();
					
					// This sleep time produces the drawing response time of 1 20th of a second
					Thread.sleep(100);
				}
			} catch (Exception e) {
				logger.error("Error while drawing cluster image", e);
			}
		}

		void stop() {
			// stop loader from animating and taking up computer processing power
			loader.stop();
			layouter.interruptDoLayout();
			yoshikoUtil.interruptLoading();
			drawGraph = false;
			t = null;
		}
	}
}
