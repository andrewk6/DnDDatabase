package data.players;

import java.io.Serializable;

import javax.swing.text.StyledDocument;

import data.DataContainer.Source;
import data.interfaces.SourceProvider;

public class BastionRoom implements Serializable, SourceProvider
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6490643663662213993L;
	
	public enum SpaceRequired { Cramped, Roomy, Vast };
	public enum Order { Craft, Empower, Harvest, Maintain, Recruit, Research, Trade };
	
	public String name, prereq, hirelings;
	public StyledDocument desc;
	public int unlockLevel;
	public SpaceRequired spaceReq;
	public Order roomOrder;
	public Source src;
	
	public Source getSource() {
		return src;
	}
}