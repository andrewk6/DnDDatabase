package data.hazards;

import java.util.EnumMap;

import javax.swing.text.StyledDocument;

import data.DataContainer.PartyTier;

public class Hazard
{
	public enum HazardDanger {Nuisance, Deadly};
	public EnumMap dangerMap;
	public String name;
	public StyledDocument desc;
}