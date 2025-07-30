package data;

import java.io.Serializable;

import javax.swing.text.StyledDocument;

public class Feat implements Serializable{
	private static final long serialVersionUID = 810856765469994989L;
	
	public enum FeatType{
		Origin, General, Fighting_Style, Epic_Boon
	}
	
	public StyledDocument desc;
	public String name;
	public FeatType type;
}