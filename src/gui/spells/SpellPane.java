package gui.spells;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import data.DataContainer;
import data.DataContainer.MapType;
import data.Spell;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.HoverTextPane;
import gui.gui_helpers.RichViewer;
import gui.gui_helpers.structures.GuiDirector;

public class SpellPane extends JPanel
{
	public static void main(String[] args) {
		DataContainer data = new DataContainer();
		data.init();
		
		GuiDirector gd = new GuiDirector(new JDesktopPane());
		
		SwingUtilities.invokeLater(()->{
			JFrame frm = new JFrame();
			frm.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			frm.addWindowListener(CompFactory.createSafeExitWindowListener(frm, data));
			frm.setContentPane(new SpellPane(data.getSpells().get("Wish"), data, gd));
			frm.pack();
			frm.setSize(800, 800);
			frm.setVisible(true);
		});
	}
	private final Spell spell;
	private final DataContainer data;
	private GuiDirector gd;
	
	public SpellPane(Spell s, DataContainer data, GuiDirector gd) {
		this.spell = s;	
		this.data = data;
		this.gd = gd;
		
		BuildSpellPane();
	}
	
	private void BuildSpellPane() {
		this.setLayout(new BorderLayout());
		
		JPanel top = new JPanel();
		top.setLayout(new BorderLayout());
		this.add(top, BorderLayout.NORTH);
		
		JPanel nameSrcPane = CompFactory.createTopPane(spell.name, spell.source, 2f);
		top.add(nameSrcPane, BorderLayout.CENTER);
		
		JPanel detailPane = new JPanel();
		detailPane.setLayout(new GridLayout(1,0));
		top.add(detailPane, BorderLayout.SOUTH);
		
		detailPane.add(CompFactory.createDescriptionPane("Spell Level: ", spell.spellLevel.toString()));
		detailPane.add(CompFactory.createDescriptionPane("Spell School: ", spell.spellSchool.toString()));
		
//		HoverTextPane descPane = CompFactory.createHoverTextPane(data, gd, spell.spellDoc, ComponentType.BODY);
		RichViewer descPane = new RichViewer(data, gd, spell.spellDoc);
		this.add(descPane, BorderLayout.CENTER);
	}
}