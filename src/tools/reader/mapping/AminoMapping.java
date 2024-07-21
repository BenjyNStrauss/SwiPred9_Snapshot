package tools.reader.mapping;

import assist.ActuallyCloneable;
import biology.amino.InsertCode;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public interface AminoMapping extends ActuallyCloneable {
	public int map(InsertCode key);
	public void adjust(int amount);
	public boolean isValid(Object index);
	public AminoMapping clone();
	
	public default int map(int key) {
		return map(new InsertCode(key));
	}
	
	public default int map(String key) {
		return map(new InsertCode(key));
	}
}
