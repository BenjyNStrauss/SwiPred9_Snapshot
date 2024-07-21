package modules.descriptor.charge;

import assist.exceptions.UnmappedEnumValueException;
import utilities.SwiPredObject;

/**
 * Not to be used outside the "Lookup" package
 * TMBRecordedColumn means column charge recorded by Annick Thomas, Alain Milon, and Robert Brasseur
 * 		in their 2004 paper "Partial Atomic Charges of Amino Acids in Proteins"
 * 
 * @author Benjy Strauss
 * 
 */

public enum TMBRecordedColumn implements SwiPredObject {
	AMBER95, FREE, FREE_SD, BURIED, BURIED_SD, HELIX, HELIX_SD, SHEET, SHEET_SD;
	
	static final int TOTAL_COLUMNS = 9;
	
	public static TMBRecordedColumn parse(int no) {
		return parse(""+no);
	}
	
	public static TMBRecordedColumn parse(String arg) {
		arg = arg.toLowerCase().trim();
		arg = arg.replaceAll("\\s+", "");
		arg = arg.replaceAll("[-_–]", "");
		
		switch(arg) {
		case "amber95":
		case "3":		return AMBER95;
		case "free":
		case "4":		return FREE;
		case "freesd":
		case "5":		return FREE_SD;
		case "buried":
		case "6":		return BURIED;
		case "buriedsd":
		case "7":		return BURIED_SD;
		case "helix":
		case "8":		return HELIX;
		case "helixsd":
		case "9":		return HELIX_SD;
		case "sheet":
		case "a":		//in case some wise guy wants to use hex
		case "10":		return SHEET;
		case "sheetsd":
		case "b":		//in case some wise guy wants to use hex
		case "11":		return SHEET_SD;
		default:		throw new UnmappedEnumValueException();
		}
	}
	
	/** @return database column # */
	public int defaultIndex() {
		switch(this) {
		case AMBER95:		return 3;
		case FREE:			return 4;
		case FREE_SD:		return 5;
		case BURIED:		return 6;
		case BURIED_SD:		return 7;
		case HELIX:			return 8;
		case HELIX_SD:		return 9;
		case SHEET:			return 10;
		case SHEET_SD:		return 11;
		default:			throw new UnmappedEnumValueException();
		}
	}
	
	public String toString() {
		switch(this) {
		case AMBER95:		return "AMBER";
		case FREE:			return "FREE ";
		case FREE_SD:		return "FR_SD";
		case BURIED:		return "BURY ";
		case BURIED_SD:		return "BU_SD";
		case HELIX:			return "HELIX";
		case HELIX_SD:		return "HE_SD";
		case SHEET:			return "SHEET";
		case SHEET_SD:		return "SH_SD";
		default:			return super.toString();
		}
	}
}
