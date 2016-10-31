package at.netcrawler.network.topology;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import at.andiwand.library.math.graph.AbstractHypergraph;
import at.andiwand.library.math.graph.GraphListener;
import at.andiwand.library.math.graph.ListenableGraph;
import at.netcrawler.network.model.information.identifier.DeviceIdentifier;


public abstract class Topology extends
		AbstractHypergraph<TopologyDevice, TopologyCable> implements
		ListenableGraph<TopologyDevice, TopologyCable> {
	
	private final List<GraphListener> listeners = new ArrayList<GraphListener>();
	
	public abstract Map<TopologyInterface, TopologyCable> getConnectionMap();
	
	@Override
	public Set<TopologyCable> getConnectedEdges(TopologyDevice vertex) {
		Set<TopologyCable> result = new HashSet<TopologyCable>();
		Map<TopologyInterface, TopologyCable> connectionMap = getConnectionMap();
		
		for (TopologyInterface interfaze : vertex.getInterfaces()) {
			TopologyCable cable = connectionMap.get(interfaze);
			result.add(cable);
		}
		
		return result;
	}
	
	// TODO: improve
	public TopologyDevice getByIdentifier(DeviceIdentifier identifier) {
		if (!containsIdentifier(identifier)) return null;
		TopologyDevice device = new TopologyDevice(identifier, null);
		
		for (TopologyDevice topologyDevice : getVertices()) {
			if (device.equals(topologyDevice)) return topologyDevice;
		}
		
		throw new IllegalStateException("Unreachable section");
	}
	
	public boolean containsIdentifier(DeviceIdentifier identifier) {
		TopologyDevice device = new TopologyDevice(identifier, null);
		return containsVertex(device);
	}
	
	public void addListener(GraphListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}
	
	protected abstract boolean addVertexImpl(TopologyDevice vertex);
	
	public final boolean addVertex(TopologyDevice vertex) {
		if (!addVertexImpl(vertex)) return false;
		fireVertexAdded(vertex);
		return true;
	}
	
	protected abstract boolean addEdgeImpl(TopologyCable edge);
	
	public final boolean addEdge(TopologyCable edge) {
		if (!addEdgeImpl(edge)) return false;
		fireEdgeAdded(edge);
		return true;
	}
	
	public void removeListener(GraphListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}
	
	protected abstract boolean removeVertexImpl(TopologyDevice vertex);
	
	public final boolean removeVertex(TopologyDevice vertex) {
		if (!removeVertexImpl(vertex)) return false;
		fireVertexRemoved(vertex);
		return true;
	}
	
	protected abstract boolean removeEdgeImpl(TopologyCable edge);
	
	public final boolean removeEdge(TopologyCable edge) {
		if (!removeEdgeImpl(edge)) return false;
		fireEdgeRemoved(edge);
		return true;
	}
	
	private void fireVertexAdded(TopologyDevice vertex) {
		synchronized (listeners) {
			for (GraphListener listener : listeners) {
				listener.vertexAdded(vertex);
			}
		}
	}
	
	private void fireVertexRemoved(TopologyDevice vertex) {
		synchronized (listeners) {
			for (GraphListener listener : listeners) {
				listener.vertexRemoved(vertex);
			}
		}
	}
	
	private void fireEdgeAdded(TopologyCable edge) {
		synchronized (listeners) {
			for (GraphListener listener : listeners) {
				listener.edgeAdded(edge);
			}
		}
	}
	
	private void fireEdgeRemoved(TopologyCable edge) {
		synchronized (listeners) {
			for (GraphListener listener : listeners) {
				listener.edgeRemoved(edge);
			}
		}
	}
	
}