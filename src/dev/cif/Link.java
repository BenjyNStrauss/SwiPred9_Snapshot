package dev.cif;

/**
 * 
 */

public class Link {
	Category linked;
	final link_validator v;
	
	public Link(Category linked, final link_validator v) {
		this.linked = linked;
		this.v = v;
	}
}
