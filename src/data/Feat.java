package data;

import java.io.Serializable;

import javax.swing.text.StyledDocument;

import data.DataContainer.Source;
import data.interfaces.SourceProvider;

public class Feat implements Serializable, SourceProvider{
	private static final long serialVersionUID = 810856765469994989L;
	
	public enum FeatType{
		Origin("Origin Feat"), 
		General("General Feat"), 
		Fighting_Style("Fighting Style"), 
		Epic_Boon("Epic Boon"),
		DRAGONMARK("Dragonmarks");
		
		private String name;
		
		FeatType(String name){
			this.name = name;
		}
		
		public String toString() {
			return name;
		}
	}
	
	public StyledDocument desc;
	public String name;
	public FeatType type;
	public Source src;
	
	public String toString() {
		return name;
	}
	
	public Source getSource() {
		return src;
	}
}