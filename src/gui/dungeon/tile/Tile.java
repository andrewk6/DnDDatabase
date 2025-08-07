package gui.dungeon.tile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.text.StyledDocument;

import data.Monster;
import data.dungeon.DungeonNote;
import data.dungeon.EncounterNote;
import utils.ErrorLogger;

public class Tile implements Serializable{
	private static final long serialVersionUID = -5384404010802867146L;

	public enum TILE_TYPE{
		NONE, MONSTER, NOTE
	}
    public Color color;
    public transient Image icon;
    public String imageFileName;
    public TILE_TYPE type;
    
    public DungeonNote note;
    public EncounterNote eNote;
    

    public Tile() {
        empty();
    }

//    public Color getColor() {
//        return color;
//    }
//
//    public void setColor(Color color) {
//        this.color = color;
//    }

    public boolean isDefault() {
        return Color.WHITE.equals(color);
    }
    
    public void empty() {
    	this.color = Color.BLACK;
    	icon = null;
    	type = TILE_TYPE.NONE;
    	imageFileName = null;
    }
    
    public void loadImage(String imageFile) {
    	System.out.println(imageFile);
    	try {
			imageFileName = imageFile;
			icon = ImageIO.read(this.getClass().getResource(imageFile)).getScaledInstance(32,
					32, BufferedImage.SCALE_SMOOTH);
		} catch (IOException e) {
			ErrorLogger.log(e);
			e.printStackTrace();
		}
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if(imageFileName != null)
        	if(imageFileName.length() > 0)
        		loadImage(imageFileName); // restore transient image
    }
}
