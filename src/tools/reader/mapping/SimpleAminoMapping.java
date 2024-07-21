package tools.reader.mapping;

import java.util.Objects;

import assist.data.Quantizable;
import biology.BioObject;
import biology.amino.InsertCode;
import biology.protein.ChainID;

/**
 * Used to map amino acids from a set of false indicies in PDB files to the real indicies
 * @author Benjy Strauss
 *
 */

public class SimpleAminoMapping extends BioObject implements AminoMapping {
	private static final long serialVersionUID = 1L;
	
	private final ChainID id;
	private InsertCode mapStart;
	private InsertCode mapFinish;
	private InsertCode trueStart;
	private InsertCode trueFinish;
	
	public SimpleAminoMapping(ChainID id, String mapStart, String mapFinish, String trueStart, String trueFinish) {
		Objects.requireNonNull(id, "null id");
		Objects.requireNonNull(mapStart, "null mapStart");
		Objects.requireNonNull(mapFinish, "null mapFinish");
		Objects.requireNonNull(trueStart, "null trueStart");
		Objects.requireNonNull(trueFinish, "null trueFinish");
		
		this.id = id;
		this.mapStart	= new InsertCode(mapStart);
		this.mapFinish  = new InsertCode(mapFinish);
		this.trueStart 	= new InsertCode(trueStart);
		this.trueFinish = new InsertCode(trueFinish);
	}
	
	public SimpleAminoMapping(ChainID id, int mapStart, int mapFinish, int trueStart, int trueFinish) {
		Objects.requireNonNull(id, "null id");
		Objects.requireNonNull(mapStart, "null mapStart");
		Objects.requireNonNull(mapFinish, "null mapFinish");
		Objects.requireNonNull(trueStart, "null trueStart");
		Objects.requireNonNull(trueFinish, "null trueFinish");
		
		this.id = id;
		this.mapStart	= new InsertCode(mapStart);
		this.mapFinish  = new InsertCode(mapFinish);
		this.trueStart 	= new InsertCode(trueStart);
		this.trueFinish = new InsertCode(trueFinish);
	}
	
	private SimpleAminoMapping(SimpleAminoMapping cloneFrom) {
		this.id = cloneFrom.id.clone();
		this.mapStart	= cloneFrom.mapStart.clone();
		this.mapFinish  = cloneFrom.mapFinish.clone();
		this.trueStart 	= cloneFrom.trueStart.clone();
		this.trueFinish = cloneFrom.trueFinish.clone();
	}

	//DBREF  2H5C A   15A  245  UNP    P00778   PRLA_LYSEN     200    397
	public static SimpleAminoMapping parse(ChainID id, String dbrefLine) {
		Objects.requireNonNull(dbrefLine, "Error!  Nothing to parse from!");
		String[] fields = dbrefLine.split("\\s+");
		
		if(id == null) { id = new ChainID();}
		if(id.protein() == null) { id.setProtein(fields[1]); }
		if(id.chain() == null)   { id.setChain(fields[2]); }
		if(id.uniprot() == null) { id.setUniprot(fields[6]); }
		
		return new SimpleAminoMapping(id, fields[3], fields[4], fields[8], fields[9]);
	}
	
	public int map(int position) {
		if(isValid(position)) {
			return position - (mapStart() - trueStart());
		} else {
			throw new IndexOutOfBoundsException(position);
		}
	}
	
	@Override
	public int map(InsertCode key) {
		if(isValid(key.index)) {
			return key.index - (mapStart() - trueStart());
		} else {
			throw new IndexOutOfBoundsException(key.index);
		}
	}

	@Override
	public boolean isValid(Object index) {
		int val;
		if(index instanceof InsertCode) {
			val = ((InsertCode) index).index;
		} else if(index instanceof Number) {
			val = ((Number) index).intValue();
		} else if(index instanceof Quantizable) {
			val = (int) ((Quantizable) index).quantize();
		} else if(index instanceof String) { 
			val = new InsertCode((String) index).index;
		} else {
			return false;
		}
		
		return (mapStart() <= val) && (mapFinish() >= val);
	}
	
	@Override
	public String saveString() { return "map-for-" + id.uniqueSaveID(); }
	
	public int mapStart() { return mapStart.index; }
	public int mapFinish() { return mapFinish.index; }
	public int trueStart() { return trueStart.index; }
	public int trueFinish() { return trueFinish.index; }
	
	public boolean isTrivial() { return (mapStart() == trueStart()) && (mapFinish() == trueFinish()); }
	
	public SimpleAminoMapping clone() { return new SimpleAminoMapping(this); }
	
	private static String fillField8Char(String str) {
		for(int i = str.length(); i < 8; ++i) {
			str = " " + str;
		}
		return str;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder("|");
		builder.append(fillField8Char(id.protein())+"|");
		builder.append(fillField8Char(id.chain())+"|");
		builder.append(fillField8Char(id.uniprot())+"|");
		
		builder.append(fillField8Char(mapStart.toString())+"|");
		builder.append(fillField8Char(mapFinish.toString())+"|");
		builder.append(fillField8Char(trueStart.toString())+"|");
		builder.append(fillField8Char(trueFinish.toString())+"|");
		builder.append(fillField8Char(""+(mapFinish() - mapStart() == trueFinish() - trueStart()))+"|");
		return builder.toString();
	}

	@Override
	public void adjust(int amount) {
		trueStart = new InsertCode(trueStart.index + amount, trueStart.code);
		trueFinish = new InsertCode(trueFinish.index + amount, trueFinish.code);
	}
}
