package biology.molecule.types;

import assist.base.ToolBelt;

/**
 * TODO in progress (currently unused)
 * https://www.rcsb.org/ligand/[code here]
 * @author Benjamin Strauss
 *
 */

public enum AtomType implements MoleculeType, ToolBelt {
	UNKNOWN("??"),
	
	Lithium("LI", 1),
	Oxygen("O"),
	Fluorine("F", -1),
	
	Sodium("NA", 1),
	Magnesium("MG", 2), 
	Chlorine("CL", -1),
	Argon("AR"),
	
	Potassium("K", 1),
	Calcium("CA", 2),
	Titanium("4TI", "TI", 4),
	Cromium("CR", 3),
	Manganese2("MN", 2),		Manganese3("MN3", "MN", 3),
	Iron2("FE2", "FE", 2), 		Iron3("FE", 3), 
	Cobalt2("CO", 2),
	Nickel2("NI", 2),
	Copper1("CU1", "CU", 1),	Copper2("CU", 2),
	Zinc("ZN", 2),
	Arsenic("ARS"),
	Selenium("SE"),
	Bromide("BR", -1),
	Krypton("KR"),
	
	Rubidium("RB", 1),
	Strontium("SR", 2),
	Yttrium("Y1", "Y", 2),		Yttrium3("YT3", "Y", 3),
	Zirconium("ZR", 4),
	Molybdenum("MO", 0),		Molybdenum4("4MO", "MO", 4),
	Ruthenium("RU", 3),
	Rhodium3("RH3", "RH", 3),
	Palladium("PD", 2),
	Silver("AG", 1),
	Cadmium("CD", 2),
	Antimony("SB", 3),
	Tellurium("TE"),
	Iodide("IOD", "I", -1),
	Xenon("XE"),
	
	Cesium("CS", 1),
	Barium("BA", 2),
	Lanthanum("LA", 3),
	Praseodymium("PR, 3"),
	Samarium3("SM", 3),
	Europium("EU", 2),
	Gadolinium("GD", 0),		Gadolinium3("GD3", "GD", 3),
	Terbium("TB", 3),
	Holmium("HO"),				Holmium3("HO3", "HO", 3),
	Ytterbium("YB", 3),
	Lutetium("LU", 3),
	Tungsten("W", 6),
	Rhenium("RE"),
	Osmium3("OS", 3),			Osmium4("OS4", "OS", 4),
	Iridium3("IR3", 3),			Iridium("IR", 4),
	Platinum2("PT", 2),			Platinum4("PT4", "PT", 2),
	Mercury2("HG", 2),
	Thallium("TL", 1),
	Gold("AU", 1),				Gold3("AU3", "AU", 3),
	Thallium1("TL", 1),
	Lead2("PB", 2),
	
	Thorium("TH", 4),
	Uranium("U1", "U"),
	Plutonium("4PU", "PU", 4),
	Americium("AM", 3), 
	Curium("ZCM", "CM", 3),
	Californium("CF", 3);
	
	public final String code;
	public final String elementID;
	public final int charge;
	
	private AtomType(String code) { this(code, code, 0); }
	private AtomType(String code, String elementID) { this(code, elementID, 0); }
	private AtomType(String code, int charge) { this(code, code, charge); }
	
	private AtomType(String code, String elementID, int charge) {
		this.code = code;
		this.elementID = toProperCase(elementID);
		this.charge = charge;
	}

	@Override
	public char toChar() { return 'X'; }
	
	@Override
	public String toCode() { return code; }

	public String toString() { 
		String value = fixString(super.toString());
		value += (charge == 0) ? " Atom" : "-"+charge+" Ion";
		return value;
	}
	
	private static String toProperCase(String code) {
		code = code.toLowerCase();
		code = Character.toUpperCase(code.charAt(0)) + code.substring(1);
		return code;
	}
}
