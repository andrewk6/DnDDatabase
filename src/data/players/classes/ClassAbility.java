package data.players.classes;

import java.io.Serializable;

import javax.swing.text.StyledDocument;

import data.players.Ability;

public class ClassAbility extends Ability implements Serializable, Comparable<ClassAbility>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2674380693064099541L;
	
	public int level;
	
	public int compareTo(ClassAbility other) {
		// First compare by level (lower levels come first)
		int levelCompare = Integer.compare(this.level, other.level);
		if (levelCompare != 0) {
			return levelCompare;
		}

		// If levels are the same, compare by name (alphabetically)
		if (this.name == null && other.name == null) {
			return 0;
		}
		if (this.name == null) {
			return -1;
		}
		if (other.name == null) {
			return 1;
		}
		return this.name.compareToIgnoreCase(other.name);
	}
}