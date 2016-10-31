package at.netcrawler.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Set;

import at.andiwand.library.network.ip.IPv4Address;
import at.andiwand.library.util.collections.CollectionUtil;
import at.netcrawler.network.connection.ConnectionSettings;
import at.netcrawler.network.connection.ConnectionType;
import at.netcrawler.network.connection.ssh.SSHSettings;
import at.netcrawler.network.connection.ssh.SSHVersion;
import at.netcrawler.network.connection.telnet.TelnetSettings;
import at.netcrawler.network.model.Capability;
import at.netcrawler.network.model.NetworkDevice;
import at.netcrawler.ui.assistant.Configuration;
import at.netcrawler.ui.assistant.ConnectionContainer;


public class NetworkDeviceHelper {
	
	private static final String NEW_LINE = System.getProperty("line.separator");
	
	public static String getConnectedVia(NetworkDevice device) {
		// ConnectionType connection = (ConnectionType)
		// device.getValue(NetworkDevice.CONNECTED_VIA);
		// if (connection == ConnectionType.SSH) {
		// SSHSettings ssh = (SSHSettings) connection.getSettings();
		// if (ssh.getVersion() == SSHVersion.VERSION1) {
		// return ConnectionContainer.SSH1.getName();
		// } else {
		// return ConnectionContainer.SSH2.getName();
		// }
		// } else if (connection == ConnectionType.TELNET) {
		// return ConnectionContainer.TELNET.getName();
		// } else if (connection == ConnectionType.SNMP) {
		// return "SNMP";
		// } else {
		// return "Unknown";
		// }
		
		return ConnectionContainer.SSH2.getName();
	}
	
	@SuppressWarnings("unchecked")
	public static IPv4Address getSomeAddress(NetworkDevice device) {
		Collection<IPv4Address> addresses = (Collection<IPv4Address>) device
				.getValue(NetworkDevice.MANAGEMENT_ADDRESSES);
		if (addresses != null) {
			return addresses.iterator().next();
		} else {
			return null;
		}
	}
	
	public static String getHostname(NetworkDevice device) {
		return device.getValue(NetworkDevice.HOSTNAME).toString();
	}
	
	public static String getMajorCapability(NetworkDevice device) {
		return device.getValue(NetworkDevice.MAJOR_CAPABILITY).toString();
	}
	
	public static String getUptime(NetworkDevice device) {
		Long uptime = (Long) device.getValue(NetworkDevice.UPTIME);
		if (uptime == null) return "";
		
		return (uptime / 1000) + "s";
	}
	
	public static String getSystem(NetworkDevice device) {
		return device.getValue(NetworkDevice.SYSTEM).toString();
	}
	
	@SuppressWarnings("unchecked")
	public static String getManagementAddresses(NetworkDevice device) {
		Set<Object> addresses = (Set<Object>) device
				.getValue(NetworkDevice.MANAGEMENT_ADDRESSES);
		if (addresses == null) return "";
		
		String s = "";
		for (Object address : addresses) {
			s += address.toString() + NEW_LINE;
		}
		s = s.trim();
		
		return s;
	}
	
	@SuppressWarnings("unchecked")
	public static String concatCapabilities(NetworkDevice device) {
		String caps = "";
		
		Set<Capability> capabilities = (Set<Capability>) device
				.getValue(NetworkDevice.CAPABILITIES);
		if (capabilities == null) return "";
		
		for (Capability capability : capabilities) {
			caps += capability.name().substring(0, 1);
		}
		
		return caps;
	}
	
	public static Configuration getConfiguration(NetworkDevice device) {
		// ConnectionType connection = (ConnectionType)
		// device.getValue(NetworkDevice.CONNECTED_VIA);
		// ConnectionSettings settings = connection.getSettings();
		
		ConnectionType connection = ConnectionType.SSH;
		ConnectionSettings settings = ConnectionContainer.SSH2
				.getDefaultSettings();
		
		int port = 0;
		String username = null;
		String password = null;
		ConnectionContainer container = null;
		if (connection == ConnectionType.SSH) {
			SSHSettings ssh = (SSHSettings) settings;
			username = ssh.getUsername();
			password = ssh.getPassword();
			port = ssh.getPort();
			container = ssh.getVersion() == SSHVersion.VERSION1 ? ConnectionContainer.SSH1
					: ConnectionContainer.SSH2;
		} else if (connection == ConnectionType.TELNET) {
			TelnetSettings telnet = (TelnetSettings) settings;
			port = telnet.getPort();
			container = ConnectionContainer.TELNET;
		}
		
		InetAddress address = null;
		try {
			address = InetAddress.getByName(getSomeAddress(device).toString());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		Configuration configuration = new Configuration();
		configuration.setConnection(container);
		configuration.setAddresses(CollectionUtil.arrayToHashSet(address));
		configuration.setPort(port);
		configuration.setUsername(username);
		configuration.setPassword(password);
		
		return configuration;
	}
}
