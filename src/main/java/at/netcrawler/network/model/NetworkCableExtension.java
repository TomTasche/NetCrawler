package at.netcrawler.network.model;

import java.util.Map;

import at.andiwand.library.util.TypeToken;


public abstract class NetworkCableExtension extends NetworkModelExtension {
	
	private static final long serialVersionUID = -5337435562364555L;
	
	public static final Class<NetworkCable> EXTENDED_MODEL_CLASS = NetworkCable.class;
	
	protected NetworkCableExtension() {
		super(EXTENDED_MODEL_CLASS);
	}
	
	protected NetworkCableExtension(Map<String, TypeToken<?>> extendedTypeMap) {
		super(EXTENDED_MODEL_CLASS, extendedTypeMap);
	}
	
}