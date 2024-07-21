package dev.hssp;

/**
 * 
 * @translator Benjamin Strauss
 *
 */

public class MResidueInfo {
	
	public final MResidueType type;
	public final char code;
	public final String name;
	
	public MResidueInfo(MResidueType type, char code, String name) {
		this.type = type;
		this.code = code;
		this.name = name;
	}
}
