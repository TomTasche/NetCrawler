package at.netcrawler.ui.device;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import at.andiwand.library.component.JFrameUtil;
import at.andiwand.library.util.ObjectIdentifier;
import at.andiwand.library.util.StringUtil;
import at.netcrawler.network.connection.ConnectionBuilder;
import at.netcrawler.network.connection.ConnectionType;
import at.netcrawler.network.connection.snmp.SNMPConnection;
import at.netcrawler.network.connection.snmp.SNMPEntry;
import at.netcrawler.network.connection.snmp.SNMPSecurityLevel;
import at.netcrawler.network.connection.snmp.SNMPSettings;
import at.netcrawler.network.connection.snmp.SNMPVersion;
import at.netcrawler.network.model.NetworkDevice;
import at.netcrawler.util.DialogUtil;
import at.netcrawler.util.GridBagLayoutUtil;
import at.netcrawler.util.NetworkDeviceHelper;


@SuppressWarnings("serial")
public class SnmpConfigurator extends JFrame {
	
	private NetworkDevice device;
	private JTextField oidField;
	private JTextArea resultArea;
	private JButton saveButton;
	private JLabel executeStatusLabel;
	private JLabel saveStatusLabel;
	private JPasswordField cryptoField;
	private JPasswordField passwordField;
	private JTextField userField;
	private JTextField communityField;
	private JComboBox versionBox;
	private JTextField portField;
	private JTextField addressField;
	private JComboBox methodBox;
	
	public SnmpConfigurator() {
		super("SNMP");
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		JLabel addressLabel = new JLabel("Address:");
		JLabel portLabel = new JLabel("Port:");
		JLabel versionLabel = new JLabel("Version:");
		JLabel communityLabel = new JLabel("Community:");
		JLabel userLabel = new JLabel("Username:");
		JLabel passwordLabel = new JLabel("Password:");
		JLabel cryptoLabel = new JLabel("Crypto Key:");
		JLabel oidLabel = new JLabel("Object Identifier:");
		JLabel methodLabel = new JLabel("Method:");
		JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
		executeStatusLabel = new JLabel();
		saveStatusLabel = new JLabel();
		
		addressField = new JTextField();
		portField = new JTextField();
		versionBox = new JComboBox(SNMPVersion.values());
		communityField = new JTextField();
		userField = new JTextField();
		passwordField = new JPasswordField();
		cryptoField = new JPasswordField();
		oidField = new JTextField();
		methodBox = new JComboBox(SnmpMethod.values());
		resultArea = new JTextArea();
		resultArea.setEnabled(false);
		JButton executeButton = new JButton("Execute");
		saveButton = new JButton("Save");
		saveButton.setEnabled(false);
		
		GridBagLayoutUtil util = new GridBagLayoutUtil(2);
		util.add(addressLabel);
		util.add(addressField);
		util.add(portLabel);
		util.add(portField);
		util.add(versionLabel);
		util.add(versionBox);
		util.add(communityLabel);
		util.add(communityField);
		util.add(userLabel);
		util.add(userField);
		util.add(passwordLabel);
		util.add(passwordField);
		util.add(cryptoLabel);
		util.add(cryptoField);
		util.add(oidLabel);
		util.add(oidField);
		util.add(methodLabel);
		util.add(methodBox);
		util.add(executeStatusLabel);
		util.add(executeButton);
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.weightx = util.getWeight();
		constraints.gridx = 0;
		constraints.gridy = util.getRow();
		constraints.gridwidth = util.getColumns();
		constraints.insets = util.getInsets();
		util.add(separator, constraints);
		
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.weighty = 1;
		constraints.gridx = 0;
		constraints.gridy = util.getRow();
		constraints.gridwidth = util.getColumns();
		constraints.insets = util.getInsets();
		util.add(new JScrollPane(resultArea), constraints);
		
		util.add(saveStatusLabel);
		util.add(saveButton);
		
		JPanel panel = util.getPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
		add(panel);
		
		versionBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (ItemEvent.DESELECTED == e.getStateChange()) return;
				
				boolean isV3 = SNMPVersion.VERSION_3 == e.getItem();
				
				cryptoField.setEnabled(isV3);
				passwordField.setEnabled(isV3);
				userField.setEditable(isV3);
			}
		});
		executeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String oid = oidField.getText();
				
				if (oid.isEmpty()) {
					DialogUtil.showErrorDialog(SnmpConfigurator.this,
							"Empty OID.");
					return;
				}
				
				try {
					List<SNMPEntry> entries = getValue(oid);
					
					executeStatusLabel.setText("Success.");
					
					StringBuilder builder = new StringBuilder();
					for (SNMPEntry entry : entries) {
						builder.append(entry);
						builder.append(StringUtil.NEW_LINE);
					}
					builder.setLength(builder.length()
							- StringUtil.NEW_LINE.length());
					
					resultArea.setText(builder.toString());
					
					resultArea.setEditable(true);
					resultArea.setEnabled(true);
					saveButton.setEnabled(true);
				} catch (IOException ex) {
					ex.printStackTrace();
					
					executeStatusLabel.setText("Failed.");
				}
			}
		});
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String oid = oidField.getText();
				
				if (oid.isEmpty()) {
					DialogUtil.showErrorDialog(SnmpConfigurator.this,
							"Empty OID.");
					return;
				}
				
				try {
					setValue(oid);
					
					saveStatusLabel.setText("Success.");
				} catch (IOException ex) {
					ex.printStackTrace();
					
					saveStatusLabel.setText("Failed.");
				}
				
			}
		});
		
		passwordField.setPreferredSize(new Dimension(150, passwordField
				.getPreferredSize().height));
		
		pack();
		
		JFrameUtil.centerFrame(this);
		setVisible(true);
	}
	
	public SnmpConfigurator(NetworkDevice device) {
		this();
		
		this.device = device;
		
		setTitle("SNMP - " + NetworkDeviceHelper.getHostname(device));
		
		portField.setText("161");
		addressField.setText(NetworkDeviceHelper.getSomeAddress(device)
				.toString());
	}
	
	private SNMPConnection getConnection() throws IOException {
		SNMPSettings settings = new SNMPSettings();
		settings.setPort(Integer.parseInt(portField.getText()));
		settings.setCommunity(communityField.getText());
		
		String username = userField.getText();
		String password = new String(passwordField.getPassword());
		String crypto = new String(cryptoField.getPassword());
		
		SNMPVersion version = (SNMPVersion) versionBox.getSelectedItem();
		SNMPSecurityLevel level;
		if (SNMPVersion.VERSION_1 == version) {
			level = SNMPSecurityLevel.NOAUTH_NOPRIV;
		} else if (!crypto.isEmpty()) {
			level = SNMPSecurityLevel.AUTH_PRIV;
		} else if (!username.isEmpty() && !password.isEmpty()) {
			level = SNMPSecurityLevel.AUTH_NOPRIV;
		} else {
			level = SNMPSecurityLevel.NOAUTH_NOPRIV;
		}
		
		settings.setVersion(version);
		settings.setSecurityLevel(level);
		settings.setUsername(username);
		settings.setCryptoKey(crypto);
		settings.setPassword(password);
		
		return ConnectionBuilder.getLocalConnectionBuilder().openConnection(
				ConnectionType.SNMP, device, settings);
	}
	
	private List<SNMPEntry> getValue(String oid) throws IOException {
		SNMPConnection connection = null;
		try {
			connection = getConnection();
			
			// TODO: allow multiple OIDs seperated by ;
			ObjectIdentifier identifier = new ObjectIdentifier(oid);
			SnmpMethod method = (SnmpMethod) methodBox.getSelectedItem();
			
			if (SnmpMethod.GET == method) {
				return connection.get(new ObjectIdentifier[] {identifier});
			} else if (SnmpMethod.GET_NEXT == method) {
				return connection.getNext(new ObjectIdentifier[] {identifier});
			} else if (SnmpMethod.WALK == method) {
				return connection.walk(identifier);
			}
		} finally {
			try {
				if (connection != null) connection.close();
			} catch (IOException e) {}
		}
		
		return null;
	}
	
	private void setValue(String oid) throws IOException {
		SNMPConnection connection = null;
		try {
			connection = getConnection();
			
			connection.set(new ObjectIdentifier(oid), resultArea.getText());
		} finally {
			try {
				if (connection != null) connection.close();
			} catch (IOException e) {}
		}
	}
	
	private enum SnmpMethod {
		GET,
		GET_NEXT,
		WALK;
	}
}
