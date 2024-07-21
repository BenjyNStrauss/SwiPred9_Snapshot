package tools.reader.mapping;

import java.util.Objects;

import assist.data.Quantizable;
import assist.util.LabeledHash;
import biology.amino.InsertCode;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class AminoHashMapping extends LabeledHash<InsertCode, Integer> implements AminoMapping {
	private static final long serialVersionUID = 1L;
	
	private static final InsertCode DEFAULT_START = new InsertCode(Integer.MAX_VALUE, 'Z');
	private static final InsertCode DEFAULT_END = new InsertCode(Integer.MIN_VALUE, InsertCode.NO_CODE);
	
	private InsertCode start = null;
	private InsertCode end = null;
	
	public AminoHashMapping() {
		start = DEFAULT_START;
		end = DEFAULT_END;
	}
	
	private AminoHashMapping(AminoHashMapping cloneFrom) {
		start = cloneFrom.start;
		end = cloneFrom.end;
		for(InsertCode code: cloneFrom.keySet()) {
			put(code.clone(), cloneFrom.get(code));
		}
	}
	
	@Override
	public int map(InsertCode key) { return get(key); }

	@Override
	public int map(int key) { return get(key); }
	
	public Integer put(Integer key, Integer val) {
		InsertCode code = new InsertCode(key, InsertCode.NO_CODE);
		if(code.lesserThan(start)) { start = code; }
		if(code.greaterThan(end)) { end = code; }
		return super.put(code, val);
	}
	
	public boolean containsKey(Object key) {
		if(key instanceof Number) { 
			InsertCode code = new InsertCode(((Number) key).intValue(), InsertCode.NO_CODE);
			return super.containsKey(code); 
		} else if(key instanceof Quantizable) { 
			InsertCode code = new InsertCode((int) ((Quantizable) key).quantize(), InsertCode.NO_CODE);
			return super.containsKey(code); 
		} else {
			return super.containsKey(key); 
		}
	}
	
	public Integer put(InsertCode code, Integer val) {
		if(code.lesserThan(start)) { start = code; }
		if(code.greaterThan(end)) { end = code; }
		return super.put(code, val);
	}
	
	public int get(int key) {
		InsertCode tempCode = new InsertCode(key, InsertCode.NO_CODE);
		Integer retval = super.get(tempCode);
		tempCode.deconstruct();
		return retval;
	}
	
	public Integer remove(Object key) {
		Objects.requireNonNull(key);
		InsertCode code;
		if(key instanceof InsertCode) {
			code = (InsertCode) key;
		} else if(key instanceof Number) {
			code = new InsertCode(((Number) key).intValue(), InsertCode.NO_CODE);
		} else if(key instanceof Quantizable) {
			code = new InsertCode((int) ((Quantizable) key).quantize(), InsertCode.NO_CODE);
		} else {
			return null;
		}
		
		Integer temp = super.remove(code);
		
		if(code.equals(start)) { 
			start = DEFAULT_START;
			for(InsertCode ic: keySet()) {
				if(ic.lesserThan(start)) { start = code; }
			}
		}
		
		if(code.equals(end)) { 
			end = DEFAULT_START;
			for(InsertCode ic: keySet()) {
				if(ic.greaterThan(end)) { end = code; }
			}
		}
		
		return temp;
	}
	
	public boolean isValid(Object key) {
		InsertCode metaKey;
		if(key instanceof InsertCode) {
			metaKey = (InsertCode) key;
		} else if(key instanceof Number) {
			metaKey = new InsertCode(((Number) key).intValue(), InsertCode.NO_CODE);
		} else if(key instanceof Quantizable) {
			metaKey = new InsertCode((int) ((Quantizable) key).quantize(), InsertCode.NO_CODE);
		} else if(key instanceof String) { 
			metaKey = new InsertCode((String) key);
		} else {
			return false;
		}
		
		return get(metaKey) != null;
	}
	
	public boolean inRange(InsertCode code) {
		return (start.lesserThan(code) && end.greaterThan(code));
	}
	
	public boolean inRange(Integer key) {
		return inRange(new InsertCode(key, InsertCode.NO_CODE));
	}
	
	public AminoHashMapping clone() {
		return new AminoHashMapping(this);
	}

	@Override
	public void adjust(int amount) {
		start = new InsertCode(start.index + amount, start.code);
		end = new InsertCode(end.index + amount, end.code);
		
		LabeledHash<InsertCode, Integer> temp = new LabeledHash<InsertCode, Integer>();
		//empty the hashtable into a temp
		for(InsertCode code: keySet()) {
			temp.put(code, remove(code));
		}
		//fill up the hashtable with the correct indices
		for(InsertCode code: temp.keySet()) {
			temp.put(code, remove(code)+amount);
		}
	}
}
