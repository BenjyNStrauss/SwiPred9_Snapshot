package tools.writer.csv;

import java.util.ArrayList;

import biology.amino.Aminoid;
import biology.amino.ChainObject;
import biology.amino.SecondarySimple;
import biology.descriptor.Descriptor;
import biology.protein.AminoChain;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public interface CSVTools {
	public static final SecondarySimple helix = SecondarySimple.Helix;
	public static final SecondarySimple other = SecondarySimple.Other;
	public static final SecondarySimple sheet = SecondarySimple.Sheet;
	public static final SecondarySimple unassigned = SecondarySimple.Disordered;
	
	/**
	 * @param chain: the protein chain
	 * @param index: index into the chain
	 * @param type: descriptor type
	 * @return: window average value of residue and neighbors
	 */
	public static double getWindowAverage(AminoChain<?> chain, int index, Descriptor type) {
		ArrayList<Aminoid> list = new ArrayList<Aminoid>();
		
		//checking window average
		int windowSize = type.getWindowSize()*2+1;
		if(windowSize < 1) {
			throw new WindowAverageIndexOutOfBoundsException();
		} else if (windowSize == 1) {
			return getValue(chain.get(index), type);
		} else if (Double.isNaN(getValue(chain.get(index), type))) {
			return Double.NaN;
		}
		
		int windowIndex = index-type.getWindowSize();
		//LocalToolBase.qp("Start: " + windowIndex);
		//LocalToolBase.qp("End: " + (index+type.getWindowSize()));
		
		for(; windowIndex <= index+type.getWindowSize(); ++windowIndex) {
			
			try {
				if(chain.get(windowIndex) != null) {
					if(chain.get(windowIndex) instanceof Aminoid) {
						list.add((Aminoid) chain.get(windowIndex));
					}
				}
			} catch (IndexOutOfBoundsException AIOOBE) {
				/* nothing to do here
				 * IndexOutOfBoundsExceptions will be thrown at the start and end
				 *  of the chain 
				 */
			}
		}
		
		double sum = 0;
		int NANs = 0;
		
		for(Aminoid amino: list) {
			double val = getValue((ChainObject) amino, type);
			if(!Double.isNaN(val)) {
				sum += val;
			} else {
				++NANs;
			}
		}
		//divide by the size of the list to get the average
		sum /= list.size()-NANs;
		
		return sum;
	}
	
	/**
	 * 
	 * @param bioMolecule
	 * @param desc
	 * @return
	 */
	public static double getValue(ChainObject chainObject, Descriptor desc) {
		if(chainObject instanceof Aminoid) {
			Double value = ((Aminoid) chainObject).getDescriptor(desc.type.toString());
			return (value == null) ? Double.NaN : value;
		} else {
			
		}
		return Double.NaN;
	}
}
