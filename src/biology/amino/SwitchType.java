package biology.amino;

/**
 * Used to differentiate between different types of switches
 * SOME_KIND means we don't know if the switch is assigned or unassigned
 * 
 * @author Benjy Strauss
 *
 */

public enum SwitchType {
	NONE, ASSIGNED, UNASSIGNED, MISSING_RESIDUE, SOME_KIND, ERROR, UNMARKED;	
}
