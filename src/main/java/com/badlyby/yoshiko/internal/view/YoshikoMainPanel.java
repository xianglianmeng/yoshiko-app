package com.badlyby.yoshiko.internal.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToolTip;

import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

import com.badlyby.yoshiko.internal.model.YoshikoParameterSet;
import com.badlyby.yoshiko.internal.util.YoshikoResources;
import com.badlyby.yoshiko.internal.util.YoshikoResources.ImageName;
import com.badlyby.yoshiko.internal.util.YoshikoUtil;

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
 * * Description: The Main Panel allowing user to choose scope and other parameters
 */

/**
 * The parameter change cytpanel which the user can use to select scope and change the scoring and finding parameters
 */
public class YoshikoMainPanel extends JPanel implements CytoPanelComponent {

	private static final long serialVersionUID = -4442491309881609088L;
	
	private final CySwingApplication swingApplication;
	private final YoshikoUtil mcodeUtil;
	private final List<CyAction> actions;

	// Panels

	private JPanel bottomPanel;
	private JPanel scopePanel;
	private JPanel advancedOptionsPanel;
	private YoshikoCollapsiblePanel collapsibleOptionsPanel;
	//private MCODECollapsiblePanel networkScoringPanel;
	private YoshikoCollapsiblePanel connectOptionsPanel;
	private YoshikoCollapsiblePanel yoshikoOptionsPanel;

	// These are used to dynamically toggle the way cluster finding content is organized based
	// on the scope that is selected.  For network scope, the user has the option of using a
	// benchmark or customizing the parameters while for the other scopes, benchmarks are not appropriate.
	//private MCODECollapsiblePanel clusterFindingPanel;
	//private MCODECollapsiblePanel customizeClusterFindingPanel;

	// Parameters for MCODE
	private YoshikoParameterSet currentParamsCopy; // stores current parameters - populates panel fields

	DecimalFormat decFormat; // used in the formatted text fields

	JPanel clusterFindingContent;
	JPanel customizeClusterFindingContent;

	// resetable UI elements

	// Scoring
	//JCheckBox includeLoopsCheckBox;
	//JTextField degreeCutOffFormattedTextField;
	JFormattedTextField pathTextField;
	JFormattedTextField addrTextField;
	JFormattedTextField portTextField;
	JFormattedTextField utilizeTextField;
	JFormattedTextField cpulimitTextField;
	JFormattedTextField exportTextField;
	JFormattedTextField graphTextField;
	JFormattedTextField multiplicativeTextField;
	JFormattedTextField numberTextField;
	JFormattedTextField rulesTextField;
	JFormattedTextField cutsTextField;
	JFormattedTextField trianglesTextField;
	JFormattedTextField threadsTextField;
	JFormattedTextField verbosityTextField;
	//JTextField kCoreFormattedTextField;
	//JTextField nodeScoreCutoffFormattedTextField;
	// cluster finding
	JRadioButton optimizeOption; // only for network scope
	JRadioButton customizeOption;
	JCheckBox preprocessCheckBox; // only for node and node set scopes
	JCheckBox haircutCheckBox;
	JCheckBox fluffCheckBox;
	JTextField fluffNodeDensityCutOffFormattedTextField;
	JTextField maxDepthFormattedTextField;

	/**
	 * The actual parameter change panel that builds the UI
	 */
	public YoshikoMainPanel(final CySwingApplication swingApplication, final YoshikoUtil mcodeUtil) {
		this.swingApplication = swingApplication;
		this.mcodeUtil = mcodeUtil;
		actions = new ArrayList<CyAction>();

		setLayout(new BorderLayout());

		// get the current parameters
		currentParamsCopy = this.mcodeUtil.getCurrentParameters().getParamsCopy(null);
		currentParamsCopy.setDefaultParams();

		decFormat = new DecimalFormat();
		decFormat.setParseIntegerOnly(true);

		// Create the three main panels: scope, advanced options, and bottom
		// Add all the vertically alligned components to the main panel
		add(getScopePanel(), BorderLayout.NORTH);
		add(getAdvancedOptionsPanel(), BorderLayout.CENTER);
		add(getBottomPanel(), BorderLayout.SOUTH);

		//TODO: Remove this ({...}) when benchmarking is implemented
		// {
		//remove content with 2 options
		//getClusterFindingPanel().getContentPane().remove(clusterFindingContent);
		//add customize content
		//getClusterFindingPanel().getContentPane().add(customizeClusterFindingContent, BorderLayout.NORTH);
		// }
	}

	public void addAction(CyAction action) {
		JButton bt = new JButton(action);
		getBottomPanel().add(bt);

		this.actions.add(action);
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.WEST;
	}

	@Override
	public Icon getIcon() {
		URL iconURL = YoshikoResources.getUrl(ImageName.LOGO_SMALL);
		return new ImageIcon(iconURL);
	}

	@Override
	public String getTitle() {
		return "";
	}

	public YoshikoParameterSet getCurrentParamsCopy() {
		return currentParamsCopy;
	}

	/**
	 * Creates a JPanel containing scope radio buttons
	 *
	 * @return panel containing the scope option buttons
	 */
	private JPanel getScopePanel() {
		if (scopePanel == null) {
			scopePanel = new JPanel();
			scopePanel.setLayout(new BoxLayout(scopePanel, BoxLayout.Y_AXIS));
			scopePanel.setBorder(BorderFactory.createTitledBorder("Connect options"));

			JRadioButton scopeCmdLine = new JRadioButton("In command line", currentParamsCopy.getScope()
					.equals(YoshikoParameterSet.COMMANDLINE));
			JRadioButton scopeSocket = new JRadioButton("Connect with socket", currentParamsCopy.getScope()
					.equals(YoshikoParameterSet.SOCKET));

			scopeCmdLine.setActionCommand(YoshikoParameterSet.COMMANDLINE);
			scopeSocket.setActionCommand(YoshikoParameterSet.SOCKET);

			scopeCmdLine.addActionListener(new ScopeAction());
			scopeSocket.addActionListener(new ScopeAction());

			ButtonGroup scopeOptions = new ButtonGroup();
			scopeOptions.add(scopeCmdLine);
			scopeOptions.add(scopeSocket);

			scopePanel.add(scopeCmdLine);
			scopePanel.add(scopeSocket);
		}

		return scopePanel;
	}

	private JPanel getAdvancedOptionsPanel() {
		if (advancedOptionsPanel == null) {
			//Since the advanced options panel is being added to the center of this border layout
			//it will stretch it's height to fit the main panel.  To prevent this we create an
			//additional border layout panel and add advanced options to it's north compartment
			advancedOptionsPanel = new JPanel(new BorderLayout());
			advancedOptionsPanel.add(getCollapsibleOptionsPanel(), BorderLayout.NORTH);
		}

		return advancedOptionsPanel;
	}

	/**
	 * Creates a collapsible panel that holds 2 other collapsable panels for
	 * network scoring and cluster finding parameter inputs
	 * @return collapsablePanel
	 */
	private YoshikoCollapsiblePanel getCollapsibleOptionsPanel() {
		if (collapsibleOptionsPanel == null) {
			collapsibleOptionsPanel = new YoshikoCollapsiblePanel("Advanced Options");

			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

			panel.add(getConnectOptionsPanel());
			panel.add(getYoshikoOptionsPanel());
			//panel.add(getNetworkScoringPanel());
			//panel.add(getClusterFindingPanel());

			collapsibleOptionsPanel.getContentPane().add(panel, BorderLayout.NORTH);
		}

		return collapsibleOptionsPanel;
	}


	private YoshikoCollapsiblePanel getConnectOptionsPanel() {
		if (connectOptionsPanel == null) {
			connectOptionsPanel = new YoshikoCollapsiblePanel("Connect options");

			JPanel panel = new JPanel();
			panel.setLayout(new GridLayout(0, 1));

			//command line path
			JLabel pathLabel = new JLabel("PATH");

			pathTextField = new JFormattedTextField();

			pathTextField.setColumns(25);
			pathTextField.addPropertyChangeListener("value",
																	 new YoshikoMainPanel.FormattedTextFieldAction());
			String pathTip = "Sets the path for the command line of the yoshiko.";
			pathTextField.setToolTipText(pathTip);
			pathTextField.setText(currentParamsCopy.getCMDLinePath());

			JPanel pathPanel = new JPanel() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public JToolTip createToolTip() {
					return new JMultiLineToolTip();
				}
			};

			pathPanel.setLayout(new BorderLayout());
			pathPanel.setToolTipText(pathTip);

			pathPanel.add(pathLabel, BorderLayout.WEST);
			pathPanel.add(pathTextField, BorderLayout.EAST);

			//add the components to the panel
			panel.add(pathPanel);

			//IP address
			JLabel addressLabel = new JLabel("Socket address");

			addrTextField = new JFormattedTextField();

			addrTextField.setColumns(18);
			addrTextField.addPropertyChangeListener("value",
																	 new YoshikoMainPanel.FormattedTextFieldAction());
			String ipaddressTip = "Sets the network address of the socket.";
			addrTextField.setToolTipText(ipaddressTip);
			addrTextField.setText(currentParamsCopy.getSocketAddress());

			JPanel addrPanel = new JPanel() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public JToolTip createToolTip() {
					return new JMultiLineToolTip();
				}
			};

			addrPanel.setLayout(new BorderLayout());
			addrPanel.setToolTipText(ipaddressTip);

			addrPanel.add(addressLabel, BorderLayout.WEST);
			addrPanel.add(addrTextField, BorderLayout.EAST);
			
			JLabel portLabel = new JLabel("Sockets port");
			
			portTextField = new JFormattedTextField();

			portTextField.setColumns(6);
			portTextField.addPropertyChangeListener("value",
																	 new YoshikoMainPanel.FormattedTextFieldAction());
			String portTip = "Sets the network address of the socket.";
			portTextField.setToolTipText(portTip);
			portTextField.setText(String.valueOf(currentParamsCopy.getSocketPort()));

			JPanel portPanel = new JPanel() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public JToolTip createToolTip() {
					return new JMultiLineToolTip();
				}
			};

			portPanel.setLayout(new BorderLayout());
			portPanel.setToolTipText(portTip);

			portPanel.add(portLabel, BorderLayout.WEST);
			portPanel.add(portTextField, BorderLayout.EAST);

			//add the components to the panel
			panel.add(addrPanel);
			panel.add(portPanel);
			connectOptionsPanel.getContentPane().add(panel, BorderLayout.NORTH);
		}

		return connectOptionsPanel;
	}
	
	private String YoshikoGetCMDStr() {
		String str = "";
		if(currentParamsCopy.getCMDLinePath().length() > 0) {
			str += currentParamsCopy.getCMDLinePath();
			str += "/yoshiko";
		} else {
			str += "./yoshiko";
		}
		str += " -F 2 -f in.sif";
		if(currentParamsCopy.getutilize().length() > 0) {
			str += " -H ";
			str += currentParamsCopy.getutilize();
		}
		if(currentParamsCopy.getcpulimit().length() > 0) {
			str += " -T ";
			str += currentParamsCopy.getcpulimit();
		}
		if(currentParamsCopy.getexport().length() > 0) {
			str += " -e ";
			str += currentParamsCopy.getexport();
		}
		if(currentParamsCopy.getgraph().length() > 0) {
			str += " -g ";
			str += currentParamsCopy.getgraph();
		}
		if(currentParamsCopy.getmultiplicative().length() > 0) {
			str += " -m ";
			str += currentParamsCopy.getmultiplicative();
		}
		if(currentParamsCopy.getnumber().length() > 0) {
			str += " -n ";
			str += currentParamsCopy.getnumber();
		}
		if(currentParamsCopy.getrules().length() > 0) {
			str += " -r ";
			str += currentParamsCopy.getrules();
		}
		if(currentParamsCopy.getcuts().length() > 0) {
			str += " -sp ";
			str += currentParamsCopy.getcuts();
		}
		if(currentParamsCopy.gettriangles().length() > 0) {
			str += " -st ";
			str += currentParamsCopy.gettriangles();
		}
		if(currentParamsCopy.getthreads().length() > 0) {
			str += " -threads ";
			str += currentParamsCopy.getthreads();
		}
		if(currentParamsCopy.getverbosity().length() > 0) {
			str += " -v ";
			str += currentParamsCopy.getverbosity();
		}
		str += " -o out -O 2";
		return str;
	}
	
	private YoshikoCollapsiblePanel getYoshikoOptionsPanel() {
		if (yoshikoOptionsPanel == null) {
			yoshikoOptionsPanel = new YoshikoCollapsiblePanel("Yoshiko options");

			JPanel panel = new JPanel();
			panel.setLayout(new GridLayout(0, 1));

			//command line path
			JLabel utilizeLabel = new JLabel("utilize heuristic instead of ILP");
			JLabel cpulimitLabel = new JLabel("CPU time limit");
			JLabel exportLabel = new JLabel("export LP");
			JLabel graphLabel = new JLabel("graph label");
			JLabel multiplicativeLabel = new JLabel("multiplicative factor");
			JLabel numberLabel = new JLabel("number of optimal solutions");
			JLabel rulesLabel = new JLabel("explicitly turn on/off reduction rules");
			JLabel cutsLabel = new JLabel("separate partition cuts");
			JLabel trianglesLabel = new JLabel("separate triangles");
			JLabel threadsLabel = new JLabel("number of threads");
			JLabel verbosityLabel = new JLabel("verbosity");
			String utilizeTip = "utilize heuristic instead of ILP, [false]";
			String cpulimitTip = "CPU time limit (s), -1 = no limit [-1]";
			String exportTip = "export LP [false]";
			String graphTip = "graph label []";
			String multiplicativeTip = "multiplicative factor for real valued edge weights in SimilarNeighborhoodRule (the higher the better the reduction results and the slower the performance) [1]";
			String numberTip = " number of optimal solutions [1]";
			String rulesTip = "explicitly turn on/off reduction rules, bit string (right to left): bit 0 = CliqueRule, bit 1 = CriticalCliqueRule, bit 2 = AlmostCliqueRule, bit 3 = HeavyEdgeRule3in1, bit 4 = ParameterDependentReductionRule, bit 5 = SimilarNeighborhoodRule [111111]";
			String cutsTip = "separate partition cuts [false]";
			String trianglesTip = "separate triangles [false]";
			String threadsTip = " number of threads [max]";
			String verbosityTip = "verbosity, 0 = silent, 5 = full [0]";
			utilizeTextField = new JFormattedTextField();
			utilizeTextField.setColumns(6);
			utilizeTextField.addPropertyChangeListener("value",new YoshikoMainPanel.FormattedTextFieldAction());
			utilizeTextField.setToolTipText(utilizeTip);
			utilizeTextField.setText(currentParamsCopy.getutilize());

			JPanel utilizePanel = new JPanel() {
				private static final long serialVersionUID = 1L;

				public JToolTip createToolTip() {
					return new JMultiLineToolTip();
				}
			};

			utilizePanel.setLayout(new BorderLayout());
			utilizePanel.setToolTipText(utilizeTip);

			utilizePanel.add(utilizeLabel, BorderLayout.WEST);
			utilizePanel.add(utilizeTextField, BorderLayout.EAST);
			cpulimitTextField = new JFormattedTextField();
			cpulimitTextField.setColumns(6);
			cpulimitTextField.addPropertyChangeListener("value",new YoshikoMainPanel.FormattedTextFieldAction());
			cpulimitTextField.setToolTipText(cpulimitTip);
			cpulimitTextField.setText(currentParamsCopy.getcpulimit());

			JPanel cpulimitPanel = new JPanel() {
				private static final long serialVersionUID = 1L;

				public JToolTip createToolTip() {
					return new JMultiLineToolTip();
				}
			};

			cpulimitPanel.setLayout(new BorderLayout());
			cpulimitPanel.setToolTipText(cpulimitTip);

			cpulimitPanel.add(cpulimitLabel, BorderLayout.WEST);
			cpulimitPanel.add(cpulimitTextField, BorderLayout.EAST);
			exportTextField = new JFormattedTextField();
			exportTextField.setColumns(6);
			exportTextField.addPropertyChangeListener("value",new YoshikoMainPanel.FormattedTextFieldAction());
			exportTextField.setToolTipText(exportTip);
			exportTextField.setText(currentParamsCopy.getexport());

			JPanel exportPanel = new JPanel() {
				private static final long serialVersionUID = 1L;

				public JToolTip createToolTip() {
					return new JMultiLineToolTip();
				}
			};

			exportPanel.setLayout(new BorderLayout());
			exportPanel.setToolTipText(exportTip);

			exportPanel.add(exportLabel, BorderLayout.WEST);
			exportPanel.add(exportTextField, BorderLayout.EAST);
			graphTextField = new JFormattedTextField();
			graphTextField.setColumns(6);
			graphTextField.addPropertyChangeListener("value",new YoshikoMainPanel.FormattedTextFieldAction());
			graphTextField.setToolTipText(graphTip);
			graphTextField.setText(currentParamsCopy.getgraph());

			JPanel graphPanel = new JPanel() {
				private static final long serialVersionUID = 1L;

				public JToolTip createToolTip() {
					return new JMultiLineToolTip();
				}
			};

			graphPanel.setLayout(new BorderLayout());
			graphPanel.setToolTipText(graphTip);

			graphPanel.add(graphLabel, BorderLayout.WEST);
			graphPanel.add(graphTextField, BorderLayout.EAST);
			multiplicativeTextField = new JFormattedTextField();
			multiplicativeTextField.setColumns(6);
			multiplicativeTextField.addPropertyChangeListener("value",new YoshikoMainPanel.FormattedTextFieldAction());
			multiplicativeTextField.setToolTipText(multiplicativeTip);
			multiplicativeTextField.setText(currentParamsCopy.getmultiplicative());

			JPanel multiplicativePanel = new JPanel() {
				private static final long serialVersionUID = 1L;

				public JToolTip createToolTip() {
					return new JMultiLineToolTip();
				}
			};

			multiplicativePanel.setLayout(new BorderLayout());
			multiplicativePanel.setToolTipText(multiplicativeTip);

			multiplicativePanel.add(multiplicativeLabel, BorderLayout.WEST);
			multiplicativePanel.add(multiplicativeTextField, BorderLayout.EAST);
			numberTextField = new JFormattedTextField();
			numberTextField.setColumns(6);
			numberTextField.addPropertyChangeListener("value",new YoshikoMainPanel.FormattedTextFieldAction());
			numberTextField.setToolTipText(numberTip);
			numberTextField.setText(currentParamsCopy.getnumber());

			JPanel numberPanel = new JPanel() {
				private static final long serialVersionUID = 1L;

				public JToolTip createToolTip() {
					return new JMultiLineToolTip();
				}
			};

			numberPanel.setLayout(new BorderLayout());
			numberPanel.setToolTipText(numberTip);

			numberPanel.add(numberLabel, BorderLayout.WEST);
			numberPanel.add(numberTextField, BorderLayout.EAST);
			rulesTextField = new JFormattedTextField();
			rulesTextField.setColumns(6);
			rulesTextField.addPropertyChangeListener("value",new YoshikoMainPanel.FormattedTextFieldAction());
			rulesTextField.setToolTipText(rulesTip);
			rulesTextField.setText(currentParamsCopy.getrules());

			JPanel rulesPanel = new JPanel() {
				private static final long serialVersionUID = 1L;

				public JToolTip createToolTip() {
					return new JMultiLineToolTip();
				}
			};

			rulesPanel.setLayout(new BorderLayout());
			rulesPanel.setToolTipText(rulesTip);

			rulesPanel.add(rulesLabel, BorderLayout.WEST);
			rulesPanel.add(rulesTextField, BorderLayout.EAST);
			cutsTextField = new JFormattedTextField();
			cutsTextField.setColumns(6);
			cutsTextField.addPropertyChangeListener("value",new YoshikoMainPanel.FormattedTextFieldAction());
			cutsTextField.setToolTipText(cutsTip);
			cutsTextField.setText(currentParamsCopy.getcuts());

			JPanel cutsPanel = new JPanel() {
				private static final long serialVersionUID = 1L;

				public JToolTip createToolTip() {
					return new JMultiLineToolTip();
				}
			};

			cutsPanel.setLayout(new BorderLayout());
			cutsPanel.setToolTipText(cutsTip);

			cutsPanel.add(cutsLabel, BorderLayout.WEST);
			cutsPanel.add(cutsTextField, BorderLayout.EAST);
			trianglesTextField = new JFormattedTextField();
			trianglesTextField.setColumns(6);
			trianglesTextField.addPropertyChangeListener("value",new YoshikoMainPanel.FormattedTextFieldAction());
			trianglesTextField.setToolTipText(trianglesTip);
			trianglesTextField.setText(currentParamsCopy.gettriangles());

			JPanel trianglesPanel = new JPanel() {
				private static final long serialVersionUID = 1L;

				public JToolTip createToolTip() {
					return new JMultiLineToolTip();
				}
			};

			trianglesPanel.setLayout(new BorderLayout());
			trianglesPanel.setToolTipText(trianglesTip);

			trianglesPanel.add(trianglesLabel, BorderLayout.WEST);
			trianglesPanel.add(trianglesTextField, BorderLayout.EAST);
			threadsTextField = new JFormattedTextField();
			threadsTextField.setColumns(6);
			threadsTextField.addPropertyChangeListener("value",new YoshikoMainPanel.FormattedTextFieldAction());
			threadsTextField.setToolTipText(threadsTip);
			threadsTextField.setText(currentParamsCopy.getthreads());

			JPanel threadsPanel = new JPanel() {
				private static final long serialVersionUID = 1L;

				public JToolTip createToolTip() {
					return new JMultiLineToolTip();
				}
			};

			threadsPanel.setLayout(new BorderLayout());
			threadsPanel.setToolTipText(threadsTip);

			threadsPanel.add(threadsLabel, BorderLayout.WEST);
			threadsPanel.add(threadsTextField, BorderLayout.EAST);
			verbosityTextField = new JFormattedTextField();
			verbosityTextField.setColumns(6);
			verbosityTextField.addPropertyChangeListener("value",new YoshikoMainPanel.FormattedTextFieldAction());
			verbosityTextField.setToolTipText(verbosityTip);
			verbosityTextField.setText(currentParamsCopy.getverbosity());

			JPanel verbosityPanel = new JPanel() {
				private static final long serialVersionUID = 1L;

				public JToolTip createToolTip() {
					return new JMultiLineToolTip();
				}
			};

			verbosityPanel.setLayout(new BorderLayout());
			verbosityPanel.setToolTipText(verbosityTip);

			verbosityPanel.add(verbosityLabel, BorderLayout.WEST);
			verbosityPanel.add(verbosityTextField, BorderLayout.EAST);
			
			JButton btst = new JButton("Start");
			btst.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(swingApplication.getJFrame(),
							YoshikoGetCMDStr(),
							"command line:",
							JOptionPane.WARNING_MESSAGE);
				/*try {
						Process localProcess = Runtime.getRuntime().exec(YoshikoGetCMDStr());
						OutputStream localOutputStream = (OutputStream) localProcess.getOutputStream();
						//DataOutputStream localDataOutputStream = new DataOutputStream(localOutputStream);
						InputStream localInputStream = localProcess.getInputStream();
						DataInputStream localDataInputStream = new DataInputStream(localInputStream);
						//String str1 = String.valueOf(paramString);
						//String str2 = str1 + "\n";
						//localDataOutputStream.writeBytes(str2);
						//localDataOutputStream.flush();
						String str3 = localDataInputStream.readLine();
						//localVector.add(str3);
						//localDataOutputStream.writeBytes("exit\n");
						//localDataOutputStream.flush();
						try {
							localProcess.waitFor();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				}
			});

			panel.add(utilizePanel);
			panel.add(cpulimitPanel);
			panel.add(exportPanel);
			panel.add(graphPanel);
			panel.add(multiplicativePanel);
			panel.add(numberPanel);
			panel.add(rulesPanel);
			panel.add(cutsPanel);
			panel.add(trianglesPanel);
			panel.add(threadsPanel);
			panel.add(verbosityPanel);
			panel.add(btst);

			yoshikoOptionsPanel.getContentPane().add(panel, BorderLayout.NORTH);
		}

		return yoshikoOptionsPanel;
	}
	

/*	private MCODECollapsiblePanel getNetworkScoringPanel() {
		if (networkScoringPanel == null) {
			networkScoringPanel = new MCODECollapsiblePanel("Network Scoring");

			JPanel panel = new JPanel();
			panel.setLayout(new GridLayout(0, 1));

			//Include loops input
			JLabel includeLoopsLabel = new JLabel("Include Loops");
			includeLoopsCheckBox = new JCheckBox() {

				public JToolTip createToolTip() {
					return new JMultiLineToolTip();
				}
			};
			includeLoopsCheckBox.addItemListener(new MCODEMainPanel.IncludeLoopsCheckBoxAction());
			String includeLoopsTip = "Self-edges may increase a\n" + "node's score slightly";
			includeLoopsCheckBox.setToolTipText(includeLoopsTip);
			includeLoopsCheckBox.setSelected(currentParamsCopy.isIncludeLoops());

			JPanel includeLoopsPanel = new JPanel() {

				public JToolTip createToolTip() {
					return new JMultiLineToolTip();
				}
			};
			includeLoopsPanel.setLayout(new BorderLayout());
			includeLoopsPanel.setToolTipText(includeLoopsTip);

			includeLoopsPanel.add(includeLoopsLabel, BorderLayout.WEST);
			includeLoopsPanel.add(includeLoopsCheckBox, BorderLayout.EAST);

			//Degree cutoff input
			JLabel degreeCutOffLabel = new JLabel("Degree Cutoff");

			degreeCutOffFormattedTextField = new JTextField(decFormat) {

				public JToolTip createToolTip() {
					return new JMultiLineToolTip();
				}
			};

			degreeCutOffFormattedTextField.setColumns(3);
			degreeCutOffFormattedTextField.addPropertyChangeListener("value",
																	 new MCODEMainPanel.FormattedTextFieldAction());
			String degreeCutOffTip = "Sets the minimum number of\n" + "edges for a node to be scored.";
			degreeCutOffFormattedTextField.setToolTipText(degreeCutOffTip);
			degreeCutOffFormattedTextField.setText(String.valueOf(currentParamsCopy.getDegreeCutoff()));

			JPanel degreeCutOffPanel = new JPanel() {

				public JToolTip createToolTip() {
					return new JMultiLineToolTip();
				}
			};

			degreeCutOffPanel.setLayout(new BorderLayout());
			degreeCutOffPanel.setToolTipText(degreeCutOffTip);

			degreeCutOffPanel.add(degreeCutOffLabel, BorderLayout.WEST);
			degreeCutOffPanel.add(degreeCutOffFormattedTextField, BorderLayout.EAST);

			//add the components to the panel
			panel.add(includeLoopsPanel);
			panel.add(degreeCutOffPanel);

			networkScoringPanel.getContentPane().add(panel, BorderLayout.NORTH);
		}

		return networkScoringPanel;
	}
*/
/*	private MCODECollapsiblePanel getClusterFindingPanel() {
		if (clusterFindingPanel == null) {
			clusterFindingPanel = new MCODECollapsiblePanel("Cluster Finding");

			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

			customizeOption = new JRadioButton("Customize", !currentParamsCopy.isOptimize());
			optimizeOption = new JRadioButton("Optimize", currentParamsCopy.isOptimize());
			ButtonGroup clusterFindingOptions = new ButtonGroup();
			clusterFindingOptions.add(customizeOption);
			clusterFindingOptions.add(optimizeOption);

			customizeOption.addActionListener(new ClusterFindingAction());
			optimizeOption.addActionListener(new ClusterFindingAction());

			//optimize parameters panel
			MCODECollapsiblePanel optimizeClusterFindingPanel = createOptimizeClusterFindingPanel(optimizeOption);

			panel.add(getCustomizeClusterFindingPanel(customizeOption));
			panel.add(optimizeClusterFindingPanel);

			this.clusterFindingContent = panel;

			clusterFindingPanel.getContentPane().add(panel, BorderLayout.NORTH);
		}

		return clusterFindingPanel;
	}
*/
/*
	private MCODECollapsiblePanel getCustomizeClusterFindingPanel(JRadioButton component) {
		if (customizeClusterFindingPanel == null) {
			customizeClusterFindingPanel = new MCODECollapsiblePanel(component);
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

			//Node Score Cutoff
			JLabel nodeScoreCutoffLabel = new JLabel("Node Score Cutoff");
			nodeScoreCutoffFormattedTextField = new JTextField(new DecimalFormat("0.000")) {

				public JToolTip createToolTip() {
					return new JMultiLineToolTip();
				}
			};

			nodeScoreCutoffFormattedTextField.setColumns(3);
			nodeScoreCutoffFormattedTextField.addPropertyChangeListener("value",
																		new MCODEMainPanel.FormattedTextFieldAction());
			String nodeScoreCutoffTip = "Sets the acceptable score deviance from\n"
										+ "the seed node's score for expanding a cluster\n"
										+ "(most influental parameter for cluster size).";
			nodeScoreCutoffFormattedTextField.setToolTipText(nodeScoreCutoffTip);
			nodeScoreCutoffFormattedTextField.setText((new Double(currentParamsCopy.getNodeScoreCutoff()).toString()));

			JPanel nodeScoreCutoffPanel = new JPanel(new BorderLayout()) {

				@Override
				public JToolTip createToolTip() {
					return new JMultiLineToolTip();
				}
			};

			nodeScoreCutoffPanel.setToolTipText(nodeScoreCutoffTip);
			nodeScoreCutoffPanel.add(nodeScoreCutoffLabel, BorderLayout.WEST);
			nodeScoreCutoffPanel.add(nodeScoreCutoffFormattedTextField, BorderLayout.EAST);

			//K-Core input
			JLabel kCoreLabel = new JLabel("K-Core");
			kCoreFormattedTextField = new JTextField(decFormat) {

				@Override
				public JToolTip createToolTip() {
					return new JMultiLineToolTip();
				}
			};

			kCoreFormattedTextField.setColumns(3);
			kCoreFormattedTextField.addPropertyChangeListener("value", new MCODEMainPanel.FormattedTextFieldAction());
			String kCoreTip = "Filters out clusters lacking a\n" + "maximally inter-connected core\n"
							  + "of at least k edges per node.";
			kCoreFormattedTextField.setToolTipText(kCoreTip);
			kCoreFormattedTextField.setText(String.valueOf(currentParamsCopy.getKCore()));

			JPanel kCorePanel = new JPanel(new BorderLayout()) {

				@Override
				public JToolTip createToolTip() {
					return new JMultiLineToolTip();
				}
			};

			kCorePanel.setToolTipText(kCoreTip);
			kCorePanel.add(kCoreLabel, BorderLayout.WEST);
			kCorePanel.add(kCoreFormattedTextField, BorderLayout.EAST);

			//Haircut Input
			JLabel haircutLabel = new JLabel("Haircut");
			haircutCheckBox = new JCheckBox() {

				@Override
				public JToolTip createToolTip() {
					return new JMultiLineToolTip();
				}
			};

			haircutCheckBox.addItemListener(new MCODEMainPanel.HaircutCheckBoxAction());
			String haircutTip = "Remove singly connected\n" + "nodes from clusters.";
			haircutCheckBox.setToolTipText(haircutTip);
			haircutCheckBox.setSelected(currentParamsCopy.isHaircut());

			JPanel haircutPanel = new JPanel(new BorderLayout()) {

				@Override
				public JToolTip createToolTip() {
					return new JMultiLineToolTip();
				}
			};

			haircutPanel.setToolTipText(haircutTip);
			haircutPanel.add(haircutLabel, BorderLayout.WEST);
			haircutPanel.add(haircutCheckBox, BorderLayout.EAST);

			// Fluff Input
			JLabel fluffLabel = new JLabel("Fluff");
			fluffCheckBox = new JCheckBox() {

				@Override
				public JToolTip createToolTip() {
					return new JMultiLineToolTip();
				}
			};

			fluffCheckBox.addItemListener(new MCODEMainPanel.FluffCheckBoxAction());
			String fluffTip = "Expand core cluster by one\n" + "neighbour shell (applied\n"
							  + "after the optional haircut).";
			fluffCheckBox.setToolTipText(fluffTip);
			fluffCheckBox.setSelected(currentParamsCopy.isFluff());

			JPanel fluffPanel = new JPanel(new BorderLayout()) {

				@Override
				public JToolTip createToolTip() {
					return new JMultiLineToolTip();
				}
			};

			fluffPanel.setToolTipText(fluffTip);
			fluffPanel.add(fluffLabel, BorderLayout.WEST);
			fluffPanel.add(fluffCheckBox, BorderLayout.EAST);

			// Fluff node density cutoff input
			JLabel fluffNodeDensityCutOffLabel = new JLabel("   Node Density Cutoff");

			fluffNodeDensityCutOffFormattedTextField = new JTextField(new DecimalFormat("0.000")) {

				@Override
				public JToolTip createToolTip() {
					return new JMultiLineToolTip();
				}
			};

			fluffNodeDensityCutOffFormattedTextField.setColumns(3);
			fluffNodeDensityCutOffFormattedTextField
					.addPropertyChangeListener("value", new MCODEMainPanel.FormattedTextFieldAction());
			String fluffNodeDensityCutoffTip = "Limits fluffing by setting the acceptable\n"
											   + "node density deviance from the core cluster\n"
											   + "density (allows clusters' edges to overlap).";
			fluffNodeDensityCutOffFormattedTextField.setToolTipText(fluffNodeDensityCutoffTip);
			fluffNodeDensityCutOffFormattedTextField.setText((new Double(currentParamsCopy.getFluffNodeDensityCutoff())
					.toString()));

			JPanel fluffNodeDensityCutOffPanel = new JPanel(new BorderLayout()) {

				@Override
				public JToolTip createToolTip() {
					return new JMultiLineToolTip();
				}
			};

			fluffNodeDensityCutOffPanel.setToolTipText(fluffNodeDensityCutoffTip);
			fluffNodeDensityCutOffPanel.add(fluffNodeDensityCutOffLabel, BorderLayout.WEST);
			fluffNodeDensityCutOffPanel.add(fluffNodeDensityCutOffFormattedTextField, BorderLayout.EAST);
			fluffNodeDensityCutOffPanel.setVisible(currentParamsCopy.isFluff());

			//Max depth input
			JLabel maxDepthLabel = new JLabel("Max. Depth");

			maxDepthFormattedTextField = new JTextField(decFormat) {

				@Override
				public JToolTip createToolTip() {
					return new JMultiLineToolTip();
				}
			};

			maxDepthFormattedTextField.setColumns(3);
			maxDepthFormattedTextField
					.addPropertyChangeListener("value", new MCODEMainPanel.FormattedTextFieldAction());
			String maxDepthTip = "Limits the cluster size by setting the\n" + "maximum search distance from a seed\n"
								 + "node (100 virtually means no limit).";
			maxDepthFormattedTextField.setToolTipText(maxDepthTip);
			maxDepthFormattedTextField.setText(String.valueOf(currentParamsCopy.getMaxDepthFromStart()));

			JPanel maxDepthPanel = new JPanel(new BorderLayout()) {

				@Override
				public JToolTip createToolTip() {
					return new JMultiLineToolTip();
				}
			};

			maxDepthPanel.setToolTipText(maxDepthTip);
			maxDepthPanel.add(maxDepthLabel, BorderLayout.WEST);
			maxDepthPanel.add(maxDepthFormattedTextField, BorderLayout.EAST);

			//Add all inputs to the panel
			panel.add(haircutPanel);
			panel.add(fluffPanel);
			panel.add(fluffNodeDensityCutOffPanel);
			panel.add(nodeScoreCutoffPanel);
			panel.add(kCorePanel);
			panel.add(maxDepthPanel);

			this.customizeClusterFindingContent = panel;

			customizeClusterFindingPanel.getContentPane().add(panel, BorderLayout.NORTH);
		}

		return customizeClusterFindingPanel;
	}
*/
/*
	private MCODECollapsiblePanel createOptimizeClusterFindingPanel(JRadioButton component) {
		MCODECollapsiblePanel collapsiblePanel = new MCODECollapsiblePanel(component);

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JLabel benchmarkStarter = new JLabel("Benchmark file location");

		JPanel benchmarkStarterPanel = new JPanel(new BorderLayout());
		benchmarkStarterPanel.add(benchmarkStarter, BorderLayout.WEST);

		JTextField benchmarkFileLocation = new JTextField();
		JButton browseButton = new JButton("Browse...");

		JPanel fileChooserPanel = new JPanel(new BorderLayout());
		fileChooserPanel.add(benchmarkFileLocation, BorderLayout.CENTER);
		fileChooserPanel.add(browseButton, BorderLayout.EAST);

		panel.add(benchmarkStarterPanel);
		panel.add(fileChooserPanel);

		collapsiblePanel.getContentPane().add(panel, BorderLayout.NORTH);
		return collapsiblePanel;
	}
*/
	/**
	 * Utility method that creates a panel for buttons at the bottom of the <code>MCODEMainPanel</code>
	 *
	 * @return a flow layout panel containing the analyze and quite buttons
	 */
	private JPanel getBottomPanel() {
		if (bottomPanel == null) {
			bottomPanel = new JPanel();
			bottomPanel.setLayout(new FlowLayout());
			bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
		}

		return bottomPanel;
	}

	/**
	 * Handles the press of a scope option. Makes sure that appropriate advanced options
	 * inputs are added and removed depending on which scope is selected
	 */
	private class ScopeAction extends AbstractAction {

		/*
		//TODO: Uncomment this action event handler when benchmarking is implemented, and delete the one below
		public void actionPerformed(ActionEvent e) {
		    String scope = e.getActionCommand();
		    if (scope.equals(MCODEParameterSet.NETWORK)) {
		        //We want to have a layered structure such that when network scope is selected, the cluster finding
		        //content allows the user to choose between optimize and customize.  When the other scopes are selected
		        //the user should only see the customize cluster parameters content.
		        //Here we ensured that these two contents are toggled depending on the scope selection.
		        clusterFindingPanel.getContentPane().remove(customizeClusterFindingContent);
		        //add content with 2 options
		        clusterFindingPanel.getContentPane().add(clusterFindingContent, BorderLayout.NORTH);
		        //need to re-add the customize content to its original container
		        customizeClusterFindingPanel.getContentPane().add(customizeClusterFindingContent, BorderLayout.NORTH);
		    } else {
		        //since only one option will be left, it must be selected so that its content is visible
		        customizeOption.setSelected(true);
		        //remove content with 2 options
		        clusterFindingPanel.getContentPane().remove(clusterFindingContent);
		        //add customize content; this automatically removes it from its original container
		        clusterFindingPanel.getContentPane().add(customizeClusterFindingContent, BorderLayout.NORTH);


		    }
		    currentParamsCopy.setScope(scope);
		}
		*/

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		//TODO: Delete this ({...}) when benchmarking is implemented
		// TEMPORARY ACTION EVENT HANDLER {
		public void actionPerformed(ActionEvent e) {
			String scope = e.getActionCommand();
			currentParamsCopy.setScope(scope);
		}
		// }
	}

	/**
	 * Sets the optimization parameter depending on which radio button is selected (cusomize/optimize)
	 */
	/*private class ClusterFindingAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			if (optimizeOption.isSelected()) {
				currentParamsCopy.setOptimize(true);
			} else {
				currentParamsCopy.setOptimize(false);
			}
		}
	}*/

	/**
	 * Handles setting of the include loops parameter
	 */
	/*private class IncludeLoopsCheckBoxAction implements ItemListener {

		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.DESELECTED) {
				currentParamsCopy.setIncludeLoops(false);
			} else {
				currentParamsCopy.setIncludeLoops(true);
			}
		}
	}*/

	private class FormattedTextFieldAction implements PropertyChangeListener {

		public void propertyChange(PropertyChangeEvent e) {
			JFormattedTextField source = (JFormattedTextField) e.getSource();

			String message = "The value you have entered is invalid.\n";
			boolean invalid = false;

			/*if (source == degreeCutOffFormattedTextField) {
				Number value = (Number) degreeCutOffFormattedTextField.getValue();
				
				if ((value != null) && (value.intValue() > 1)) {
					currentParamsCopy.setDegreeCutoff(value.intValue());
				} else {
					source.setValue(2);
					message += "The degree cutoff must be greater than 1.";
					invalid = true;
				}
			} else if (source == nodeScoreCutoffFormattedTextField) {
				Number value = (Number) nodeScoreCutoffFormattedTextField.getValue();
				
				if ((value != null) && (value.doubleValue() >= 0.0) && (value.doubleValue() <= 1.0)) {
					currentParamsCopy.setNodeScoreCutoff(value.doubleValue());
				} else {
					source.setValue(new Double(currentParamsCopy.getNodeScoreCutoff()));
					message += "The node score cutoff must be between 0 and 1.";
					invalid = true;
				}
			} else if (source == kCoreFormattedTextField) {
				Number value = (Number) kCoreFormattedTextField.getValue();
				
				if ((value != null) && (value.intValue() > 1)) {
					currentParamsCopy.setKCore(value.intValue());
				} else {
					source.setValue(2);
					message += "The K-Core must be greater than 1.";
					invalid = true;
				}*/
			if (source == pathTextField) {
				String value = pathTextField.getText();
				currentParamsCopy.setCMDLinePath(value);
			} else if (source == addrTextField) {
				String value = addrTextField.getText();
				
				if ((value != null) && (value.length() > 0)) {
					currentParamsCopy.setSocketAddress(value);
				} else {
					source.setText("localhost");
					//message += "";
					invalid = true;
				}
			} else if (source == portTextField) {
				Integer value = Integer.valueOf(portTextField.getText());
				
				if ((value != null) && (value.intValue() > 0) && (value.intValue() < 65536)) {
					currentParamsCopy.setSocketPort(value);
				} else if(value.intValue() < 0) {
					//source.setValue(1);
					source.setText("1");
					message += "The port must be greater than 0.";
					invalid = true;
				} else {
					source.setText("65535");
					message += "The port must be less than 65536.";
					invalid = true;
				}
			} else if (source == utilizeTextField) {
				String value = utilizeTextField.getText();
				currentParamsCopy.setutilize(value);
			} else if (source == cpulimitTextField) {
				String value = cpulimitTextField.getText();
				currentParamsCopy.setcpulimit(value);
			} else if (source == exportTextField) {
				String value = exportTextField.getText();
				currentParamsCopy.setexport(value);
			} else if (source == graphTextField) {
				String value = graphTextField.getText();
				currentParamsCopy.setgraph(value);
			} else if (source == multiplicativeTextField) {
				String value = multiplicativeTextField.getText();
				currentParamsCopy.setmultiplicative(value);
			} else if (source == numberTextField) {
				String value = numberTextField.getText();
				currentParamsCopy.setnumber(value);
			} else if (source == rulesTextField) {
				String value = rulesTextField.getText();
				currentParamsCopy.setrules(value);
			} else if (source == cutsTextField) {
				String value = cutsTextField.getText();
				currentParamsCopy.setcuts(value);
			} else if (source == trianglesTextField) {
				String value = trianglesTextField.getText();
				currentParamsCopy.settriangles(value);
			} else if (source == threadsTextField) {
				String value = threadsTextField.getText();
				currentParamsCopy.setthreads(value);
			} else if (source == verbosityTextField) {
				String value = verbosityTextField.getText();
				currentParamsCopy.setverbosity(value);
			}
			if (invalid) {
				JOptionPane.showMessageDialog(swingApplication.getJFrame(),
											  message,
											  "Parameter out of bounds",
											  JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	/**
	 * Handles setting of the haircut parameter
	 */
	/*private class HaircutCheckBoxAction implements ItemListener {

		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.DESELECTED) {
				currentParamsCopy.setHaircut(false);
			} else {
				currentParamsCopy.setHaircut(true);
			}
		}
	}*/

	/**
	 * Handles setting of the fluff parameter and showing or hiding of the fluff node density cutoff input
	 */
	/*private class FluffCheckBoxAction implements ItemListener {

		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.DESELECTED) {
				currentParamsCopy.setFluff(false);
			} else {
				currentParamsCopy.setFluff(true);
			}

			fluffNodeDensityCutOffFormattedTextField.getParent().setVisible(currentParamsCopy.isFluff());
		}
	}*/

}
