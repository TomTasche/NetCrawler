package at.netcrawler.ui.crawler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import at.andiwand.library.math.graph.Edge;
import at.andiwand.library.math.graph.GraphListener;
import at.andiwand.library.util.comparator.ObjectToStringLengthComparator;
import at.netcrawler.network.model.NetworkDevice;
import at.netcrawler.network.topology.Topology;
import at.netcrawler.network.topology.TopologyDevice;
import at.netcrawler.util.NetworkDeviceHelper;


@SuppressWarnings("serial")
public class DeviceTableModel extends AbstractTableModel implements
		GraphListener {
	
	private static final Map<String, NetworkDeviceDataAccessor> ACCESSOR_FOR_NAME;
	
	static {
		ACCESSOR_FOR_NAME = new HashMap<String, DeviceTableModel.NetworkDeviceDataAccessor>();
		
		ACCESSOR_FOR_NAME.put("Hostname", new NetworkDeviceDataAccessor() {
			public String get(NetworkDevice device) {
				return NetworkDeviceHelper.getHostname(device);
			}
		});
		ACCESSOR_FOR_NAME.put("Major Capability",
				new NetworkDeviceDataAccessor() {
					public String get(NetworkDevice device) {
						return NetworkDeviceHelper.getMajorCapability(device);
					}
				});
		ACCESSOR_FOR_NAME.put("Capabilities", new NetworkDeviceDataAccessor() {
			public String get(NetworkDevice device) {
				return NetworkDeviceHelper.concatCapabilities(device);
			}
		});
		ACCESSOR_FOR_NAME.put("System", new NetworkDeviceDataAccessor() {
			public String get(NetworkDevice device) {
				return NetworkDeviceHelper.getSystem(device);
			}
		});
		ACCESSOR_FOR_NAME.put("Connected via", new NetworkDeviceDataAccessor() {
			public String get(NetworkDevice device) {
				return NetworkDeviceHelper.getConnectedVia(device);
			}
		});
		ACCESSOR_FOR_NAME.put("Management Addresses",
				new NetworkDeviceDataAccessor() {
					public String get(NetworkDevice device) {
						return NetworkDeviceHelper.getSomeAddress(device)
								.toString();
					}
				});
		ACCESSOR_FOR_NAME.put("Uptime", new NetworkDeviceDataAccessor() {
			public String get(NetworkDevice device) {
				return NetworkDeviceHelper.getUptime(device);
			}
		});
	}
	
	public static Collection<String> getColumnNames() {
		return Collections.unmodifiableCollection(ACCESSOR_FOR_NAME.keySet());
	}
	
	// private final JTable table;
	private final TableColumnModel columnModel;
	private List<TopologyDevice> devices;
	private Topology topology;
	
	public DeviceTableModel(JTable table) {
		// this.table = table;
		this.columnModel = table.getColumnModel();
		this.devices = new ArrayList<TopologyDevice>();
	}
	
	public synchronized void setTopology(Topology topology) {
		this.topology = topology;
		
		updateTopology();
		
		fireTableDataChanged();
		
		topology.addListener(this);
	}
	
	private void updateTopology() {
		List<TopologyDevice> temp = new ArrayList<TopologyDevice>(topology
				.getVertices());
		Collections.sort(temp, new ObjectToStringLengthComparator());
		devices = Collections.unmodifiableList(temp);
		
		fireTableDataChanged();
	}
	
	public int getColumnCount() {
		synchronized (columnModel) {
			return columnModel.getColumnCount();
		}
	}
	
	public synchronized int getRowCount() {
		return devices.size();
	}
	
	public void edgeAdded(Edge edge) {
		updateTopology();
	}
	
	public void edgeRemoved(Edge edge) {
		updateTopology();
	}
	
	public void vertexAdded(Object vertex) {
		updateTopology();
	}
	
	public void vertexRemoved(Object vertex) {
		updateTopology();
	}
	
	public synchronized Object getValueAt(int arg0, int arg1) {
		// NetworkDevice device =
		// devices.get(table.convertRowIndexToModel(arg0)).getNetworkDevice();
		NetworkDevice device = devices.get(arg0).getNetworkDevice();
		
		// String column = (String)
		// columnModel.getColumn(table.convertColumnIndexToModel(arg1)).getHeaderValue();
		String column;
		synchronized (columnModel) {
			column = (String) columnModel.getColumn(arg1).getHeaderValue();
		}
		
		if (ACCESSOR_FOR_NAME.containsKey(column)) {
			return ACCESSOR_FOR_NAME.get(column).get(device);
		} else {
			return "Not crawled.";
		}
	}
	
	public synchronized List<TopologyDevice> getDevices() {
		return Collections.unmodifiableList(devices);
	}
	
	private static interface NetworkDeviceDataAccessor {
		
		public String get(NetworkDevice device);
	}
	
}