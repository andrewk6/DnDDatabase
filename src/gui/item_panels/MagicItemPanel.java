package gui.item_panels;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JDesktopPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import data.DataChangeListener;
import data.DataContainer;
import data.DataContainer.MapType;
import data.items.MagicItem;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.HoverTextPane;
import gui.gui_helpers.structures.GuiDirector;
import gui.gui_helpers.structures.StyleContainer;

public class MagicItemPanel extends JPanel implements DataChangeListener{
	private GuiDirector gd;
	private DataContainer data;
	private JDesktopPane dPane;
	private HoverTextPane hPane;
	private JTextField miTitle;
	private JTextField miType;
	private JPanel miGridPane, cardPane;
	private JTextField miFilter;
	
//	private JTabbedPane miTab;
	private JPanel miPane;
	private final int subTypeSizeAdjust = 4;

	public MagicItemPanel(DataContainer data, GuiDirector guiD, JDesktopPane dPane) {
		this.data = data;
		this.data.registerListener(this);
		this.dPane = dPane;
		gd = guiD;
		
		setLayout(new BorderLayout());
		BuildContent();
		BuildSidePane();
	}

	public void BuildSidePane(){
		JPanel sPane = new JPanel();
		sPane.setLayout(new BorderLayout());
		
		miFilter = new JTextField();
		miFilter.setToolTipText("Enter a spell filter");
		miFilter.getDocument().addDocumentListener(new DocumentListener() {
			public void removeUpdate(DocumentEvent e) {FillSidePane();};
			public void insertUpdate(DocumentEvent e) {FillSidePane();}
			public void changedUpdate(DocumentEvent e) {FillSidePane();}
		});
		StyleContainer.SetFontHeader(miFilter);
		sPane.add(miFilter, BorderLayout.NORTH);
		
		miGridPane = new JPanel();
		miGridPane.setLayout(new GridLayout(0,1));
		FillSidePane();
		
		JScrollPane miGridScroll = CompFactory.wrapPanelInScroll(miGridPane);		
		sPane.add(miGridScroll, BorderLayout.CENTER);
		add(sPane, BorderLayout.WEST);
	}
	
	public void FillSidePane() {
		SwingUtilities.invokeLater(()->{
			miGridPane.removeAll();
			ArrayList<String> keys = new ArrayList<String>(data.getMagicItemKeysSorted());
			Collections.sort(keys);
			
			for(String s : keys) {
				if(s.toLowerCase().startsWith(miFilter.getText().toLowerCase()) || miFilter.getText().length() == 0) {
					JLabel lbl = CompFactory.createNewLabel(s, ComponentType.BODY);
					lbl.addMouseListener(CompFactory.createSideMouseListener(lbl, ()->{
						//							spellTitle.setText(s);
//							hPane.setDocument(data.getSpells().get(s).spellDoc);
//							AddSpellTab(s);
							CardLayout cl = (CardLayout) cardPane.getLayout();
							cl.show(cardPane, "mipane");
							SetMIPane((MagicItem) data.getItems().get(s));
					}));
					miGridPane.add(lbl);
				}
			}
			miGridPane.revalidate();
			miGridPane.repaint();
		});
	}
	
	private void BuildContent() {
		cardPane = new JPanel();
		cardPane.setLayout(new CardLayout());
		add(cardPane, BorderLayout.CENTER);
		
		JLabel noLoad = new JLabel("No Magic Item Selected");
		StyleContainer.SetFontHeader(noLoad);
		cardPane.add(noLoad, "noload");
		
		miPane = new JPanel();
		miPane.setLayout(new BorderLayout());
		cardPane.add(miPane, "mipane");
		
		CardLayout cl = (CardLayout) cardPane.getLayout();
		cl.show(cardPane, "noload");
	}
	
	private void SetMIPane(MagicItem i) {
		SwingUtilities.invokeLater(()->{
			miPane.removeAll();
			System.out.println("Adding Panel: " + i.name);
			JPanel miTPane = new JPanel();
			miTPane.setLayout(new GridLayout(0,1));
			miPane.add(miTPane, BorderLayout.NORTH);
			
			miTitle = new JTextField(i.name);
			miTitle.setEditable(false);
			miTitle.setFocusable(false);
			StyleContainer.SetFontHeader(miTitle);
			miTPane.add(miTitle);
			
			miType = new JTextField(i.getTypeString());
			miType.setEditable(false);
			miType.setFocusable(false);
			miType.setFont(StyleContainer.FNT_HEADER_BOLD.deriveFont(Font.ITALIC).
					deriveFont(StyleContainer.FNT_HEADER_BOLD.getSize() - subTypeSizeAdjust ));
			miTPane.add(miType);
			
			hPane = new HoverTextPane(data, gd, dPane);
			hPane.setDocument(i.desc);
			JScrollPane hScroll = CompFactory.wrapPanelInScroll(hPane);
			miPane.add(hScroll, BorderLayout.CENTER);
			
			revalidate();
			repaint();
		});		
	}
	
	public void LoadItem(String i) {
		SetMIPane((MagicItem) data.getItems().get(i));
		SwingUtilities.invokeLater(() -> {
			CardLayout cl = (CardLayout) cardPane.getLayout();
			cl.show(cardPane, "mipane");
		});
				
	}

	@Override
	public void onMapUpdated() {
		FillSidePane();
	}

	@Override
	public void onMapUpdated(MapType mapType) {
		if(mapType == MapType.ITEMS)
			FillSidePane();
	}
}