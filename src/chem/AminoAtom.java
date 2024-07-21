package chem;

import assist.EnumParserHelper;
import assist.numerical.DecimalCoordinate;
import assist.sci.chem.Atom;
import assist.sci.chem.Bond;
import assist.sci.chem.BondType;

/**
 * Represents an atom in an Animo Acid, as read in from a PDB File
 * 
 * @author Benjamin Strauss
 *
 */

public class AminoAtom extends Atom {
	private static final long serialVersionUID = 1L;
	
	public static final String AMINO_Cα = "Cα";
	public static final String AMINO_Cβ = "Cβ";
	public static final String AMINO_CP = "C'";
	public static final String AMINO_N 	= "N";
	public static final String AMINO_HN = "HN";
	public static final String AMINO_O	= "O";
	
	private double amber;
	private String symbol;
	//3D coordinates as in PDB file
	private DecimalCoordinate position;
	private double occupancy;
	private double tempFactor;
	private int serialNo;
	
	/**
	 * 
	 * @param protons
	 * @param neutrons
	 */
	public AminoAtom(int protons, int neutrons) {
		this(protons, neutrons, null, null);
	}
	
	/**
	 * 
	 * @param protons
	 * @param neutrons
	 * @param name
	 */
	public AminoAtom(int protons, int neutrons, String name) {
		this(protons, neutrons, name, null);
	}
	
	/**
	 * 
	 * @param protons
	 * @param neutrons
	 * @param name
	 */
	public AminoAtom(int protons, int neutrons, String name, String symbol) {
		super(protons, neutrons, name);
		this.symbol = symbol;
	}
	
	/**
	 * Construct a new Atom
	 * @param symbol: The Atom's atomic symbol (EX: "Se" for "Selenium")
	 * @param name: The Atom's name (this is not necessarily the element name)
	 *  		Example: "Nitrogen" or "Alpha-Carbon"
	 * @param x
	 * @param y
	 * @param z
	 * @param charge
	 * @param occupancy
	 * @param tempFactor
	 */
	public AminoAtom(int protons, int neutrons, String symbol, String name, double x, double y, double z, double charge, double occupancy, double tempFactor) {
		this(protons, neutrons, symbol, name, new DecimalCoordinate(x,y,z), charge, occupancy, tempFactor);
	}
	
	/**
	 * Construct a new Atom
	 * @param symbol
	 * @param name
	 * @param position
	 * @param charge
	 * @param occupancy
	 * @param tempFactor
	 */
	public AminoAtom(int protons, int neutrons, String symbol, String name, DecimalCoordinate position, double charge, double occupancy, double tempFactor) {
		super(protons, neutrons, name);
		this.symbol = symbol;
		this.position = position;
		setCharge(charge);
		this.occupancy = occupancy;
		this.tempFactor = tempFactor;
	}
	
	/** @param position: what to set the atom's position to */
	public void setPos(DecimalCoordinate position) { this.position = position; }
	public void setOccupancy(double occupancy) { this.occupancy = occupancy; }
	public void setTempFactor(double tempFactor) { this.tempFactor = tempFactor; }
	public void setSerialNo(int serialNo) { this.serialNo = serialNo; }	
	public void setAmber(double amber) { this.amber = amber; }
	
	public String symbol() { return (symbol != null) ? symbol: super.symbol(); }
	
	public DecimalCoordinate position() { return position; }
	public double amber() { return amber; }
	public double occupancy() { return occupancy; }
	public double tempFactor() { return tempFactor; }
	public int serialNo() { return serialNo; }
	
	/** @return PDB specified X-coordinate, if known */
	public double getX() {
		if(position == null) {
			return Double.NaN;
		} else {
			return position.x;
		}
	}
	
	/** @return PDB specified Y-coordinate, if known */
	public double getY() {
		if(position == null) {
			return Double.NaN;
		} else {
			return position.y;
		}
	}
	
	/** @return PDB specified Z-coordinate, if known */
	public double getZ() {
		if(position == null) {
			return Double.NaN;
		} else {
			return position.z;
		}
	}
	
	/**
	 * Make a deep copy of the atom
	 * @return
	 */
	public AminoAtom clone() {
		AminoAtom myClone = new AminoAtom(protons, neutrons, symbol, label(), 
				position.clone(), charge(), occupancy, tempFactor);
		return myClone;
	}
	
	/**
	 * 
	 * @param symbol
	 * @param type
	 * @return
	 */
	public static AminoAtom parse(String symbol) {
		symbol = EnumParserHelper.parseStringForEnumConversion(symbol);
		symbol = symbol.toUpperCase();
		//replace all 'Α' (Alpha) with 'A'
		symbol = symbol.replaceAll("Α", "A");
		//replace all 'Β' (Beta) with 'B'
		symbol = symbol.replaceAll("Β", "B");
		
		switch(symbol) {
		case "ALPHA-CARBON":
		case "CARBON-A":
		case "CARBONA":
		case "CA":
		case AMINO_Cα:		return new AminoAtom(6, 6, "Alpha Carbon", AMINO_Cα);
		case "CARBON-B":
		case "CARBONB":
		case "BETA-CARBON":
		case "CB":
		case AMINO_Cβ:		return new AminoAtom(6, 6, "Beta Carbon", AMINO_Cβ);
		case "PRIME-CARBON":
		case "CARBON-PRIME":
		case "CP":
		case "PC":
		case "'C":
		case AMINO_CP:		return new AminoAtom(6, 6, "Prime Carbon", AMINO_CP);
		case "NITROGEN":
		case AMINO_N:		return new AminoAtom(7, 7, "Nitrogen", AMINO_N);
		case "HYDROGEN-NITROGEN":
		case "NITROGEN-HYDROGEN":
		case "NH":
		case AMINO_HN:		
			AminoAtom nitrogen_plus = new AminoAtom(7, 7, "Nitrogen", AMINO_HN);
			AminoAtom hydrogen = new AminoAtom(1, 0, "Hydrogen");
			new Bond(BondType.COVALENT, nitrogen_plus, hydrogen);
			return nitrogen_plus;
		case "OXYGEN":
		case AMINO_O:		return new AminoAtom(8, 8, "Oxygen", AMINO_CP);
		
		default:
			throw new AtomicSymbolNotYetParseableException("symbol: " + symbol);
		}
	}
}
