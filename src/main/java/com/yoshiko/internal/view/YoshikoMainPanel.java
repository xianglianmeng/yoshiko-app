package com.yoshiko.internal.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
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

import com.yoshiko.internal.model.YoshikoParameterSet;
import com.yoshiko.internal.util.YoshikoResources;
import com.yoshiko.internal.util.YoshikoUtil;
import com.yoshiko.internal.util.YoshikoResources.ImageName;

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
	private final YoshikoUtil yoshikoUtil;
	private final List<CyAction> actions;

	private JPanel bottomPanel;
	private JPanel connectOptionsPanel;
	private YoshikoCollapsiblePanel yoshikoOptionsPanel;

	private YoshikoParameterSet currentParamsCopy; // stores current parameters - populates panel fields

	DecimalFormat decFormat; // used in the formatted text fields

	JPanel clusterFindingContent;
	JPanel customizeClusterFindingContent;

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
	public YoshikoMainPanel(final CySwingApplication swingApplication, final YoshikoUtil yoshikoUtil) {
		this.swingApplication = swingApplication;
		this.yoshikoUtil = yoshikoUtil;
		actions = new ArrayList<CyAction>();

		setLayout(new BorderLayout());

		currentParamsCopy = this.yoshikoUtil.getCurrentParameters().getParamsCopy(null);
		currentParamsCopy.setDefaultParams();

		decFormat = new DecimalFormat();
		decFormat.setParseIntegerOnly(true);

		add(getConnectOptionsPanel(), BorderLayout.NORTH);
		//add(getYoshikoOptionsPanel(), BorderLayout.CENTER);
		add(getBottomPanel(), BorderLayout.SOUTH);
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

	private JPanel getConnectOptionsPanel() {
		if (connectOptionsPanel == null) {
			connectOptionsPanel = new JPanel();

			connectOptionsPanel.setLayout(new GridLayout(0, 1));

			//command line path
			/*JLabel pathLabel = new JLabel("PATH");

			pathTextField = new JFormattedTextField();

			pathTextField.setColumns(25);
			pathTextField.addPropertyChangeListener("value",
																	 new YoshikoMainPanel.FormattedTextFieldAction());
			String pathTip = "Sets the tmp path.";
			pathTextField.setToolTipText(pathTip);
			pathTextField.setText(currentParamsCopy.getCMDLinePath());

			JPanel pathPanel = new JPanel() {
				private static final long serialVersionUID = 1L;

				public JToolTip createToolTip() {
					return new JMultiLineToolTip();
				}
			};

			pathPanel.setLayout(new BorderLayout());
			pathPanel.setToolTipText(pathTip);

			pathPanel.add(pathLabel, BorderLayout.WEST);
			pathPanel.add(pathTextField, BorderLayout.EAST);
			*/
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
			//connectOptionsPanel.add(pathPanel);
			connectOptionsPanel.add(addrPanel);
			connectOptionsPanel.add(portPanel);
		}

		return connectOptionsPanel;
	}
	
	/*private YoshikoCollapsiblePanel getYoshikoOptionsPanel() {
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
			yoshikoOptionsPanel.getContentPane().add(panel, BorderLayout.NORTH);
		}

		return yoshikoOptionsPanel;
	}*/
	
	/**
	 * Utility method that creates a panel for buttons at the bottom of the <code>YoshikoMainPanel</code>
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

	private class FormattedTextFieldAction implements PropertyChangeListener {

		public void propertyChange(PropertyChangeEvent e) {
			JFormattedTextField source = (JFormattedTextField) e.getSource();

			String message = "The value you have entered is invalid.\n";
			boolean invalid = false;

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
}
