package dev.inProgress.sable;

import java.lang.reflect.Constructor;

import assist.base.ToolBeltLimited;
import assist.exceptions.ClassNotFoundRuntimeException;
import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class NetworkLoader extends ClassLoader implements ToolBeltLimited {
	private static NetworkLoader instance = null;
	
	private NetworkLoader() { }
	
	public static NetworkLoader getInstance() {
		if(instance == null) {
			instance = new NetworkLoader();
		}
		return instance;
	}
	
	/**
	 * Returns an instance of a map given by the specified name
	 * @param mapName: the name of the map to load
	 * @return: an instance of the map specified
	 * @throws MapNotFoundException: if the name given does not correspond to an existing map.
	 */
	@SuppressWarnings("unchecked")
	public NetworkBase getNetwork(String _package, String network) {
		NetworkBase retVal = null;
		Constructor<NetworkBase> maker = null;
		Class<NetworkBase> loadThis = null;
		
		int endIndex = getClass().toString().length() - "NetworkLoader".length();
		
		String netName = getClass().toString().substring(6, endIndex);
		
		
		netName += _package + "." + network;
		LocalToolBase.qp(netName);
		
		//files/predict/sable/complexSA/Approx_el_1
		
		try {
			loadThis = (Class<NetworkBase>) loadClass(netName);
		} catch (Exception e) {
			throw new ClassNotFoundRuntimeException("Network \"" + netName + "\" could not be found.");
		}
		
		retVal = loadThis.cast(retVal);
		
		try {
			maker = (Constructor<NetworkBase>) loadThis.getConstructor();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		try {
			retVal = maker.newInstance();
		} catch (Exception e) {
			LocalToolBase.qerr("Could not find map class named: " + netName);
			e.printStackTrace();
		}
		
		return retVal;
	}
}
