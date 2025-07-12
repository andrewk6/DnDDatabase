package gui.campaign;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;

import data.DataContainer;
import data.DataContainer.PlayerClass;
import data.DataContainer.Source;
import data.campaign.Player;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.HoverTextPane;
import gui.gui_helpers.ReminderField;
import gui.gui_helpers.RichEditor;
import gui.gui_helpers.structures.GuiDirector;
import gui.gui_helpers.structures.StyleContainer;

public class PlayerPane extends JPanel {
	private Player p;
	private DataContainer data;
	private GuiDirector gd;

	private List<JTextComponent> editableList;

	private final String NOTES_VIEW = "READ_NOTES";
	private final String NOTES_EDIT = "EDIT NOTES";

	private final HashMap<String, String> reps = new HashMap<String, String>();
	
	private JPanel notePane;
	private JComboBox<PlayerClass> classBox;
	private JCheckBox editBox;
	
	private HoverTextPane readNotesPane;
	
	public boolean registered = false;
	private boolean noEdits = false;

	public static void main(String[] args) {
		DataContainer d = new DataContainer();
		SwingUtilities.invokeLater(() -> {
			StyleContainer.SetLookAndFeel();
			JFrame tFrame = new JFrame();
			tFrame.setContentPane(
					new PlayerPane(new Player(), new DataContainer(), new GuiDirector(new JDesktopPane())));
			tFrame.setSize(800, 800);
			tFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			tFrame.addWindowListener(CompFactory.createSafeExitWindowListener(tFrame, d, () -> {
				StyledDocument doc = ((PlayerPane) tFrame.getContentPane()).getPlayer().note;
				try {
					System.out.println(doc.getText(0, doc.getLength()));
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}));
			tFrame.setVisible(true);
		});
	}

	public PlayerPane(Player p, DataContainer data, GuiDirector gd) {
		this.p = p;
		this.data = data;
		this.gd = gd;
		editableList = new ArrayList<JTextComponent>();

		reps.put("<NAME>", "");

		ConfigPanel();
		gd.registerPlayerPane(this);
		lockEdits(gd.getLockStatus());
	}
	
	public PlayerPane(Player p, DataContainer data, GuiDirector gd, boolean noEdits) {
		this.p = p;
		this.data = data;
		this.gd = gd;
		editableList = new ArrayList<JTextComponent>();

		reps.put("<NAME>", "");
		this.noEdits = noEdits;
		ConfigPanel();
		if(!noEdits) {
			gd.registerPlayerPane(this);
			lockEdits(gd.getLockStatus());
		}
	}

	private void ConfigPanel() {
		this.setLayout(new BorderLayout());

		BuildHeader();
		BuildPDetailsPane();
		BuildNotesPane();
		SetEditables(false);
	}

	private void BuildNotesPane() {
		notePane = new JPanel();
		notePane.setLayout(new BorderLayout());
		this.add(notePane, BorderLayout.CENTER);
	}

	private void BuildPDetailsPane() {
		JPanel pdPane = new JPanel();
		pdPane.setLayout(new BoxLayout(pdPane, BoxLayout.Y_AXIS));
		this.add(pdPane, BorderLayout.WEST);

		/*
		 * HP Pane Config
		 */
		JPanel hpPane = new JPanel();
		hpPane.setLayout(new BorderLayout());
		pdPane.add(hpPane);

		JLabel maxHPLbl = new JLabel("Max HP:");
		maxHPLbl.setFocusable(false);
		Dimension sizeHP = new Dimension(StyleContainer.getStringWidth(StyleContainer.FNT_HEADER_BOLD, "Passive Perc:"),
				maxHPLbl.getPreferredSize().height);
		maxHPLbl.setPreferredSize(sizeHP);
		StyleContainer.SetFontHeader(maxHPLbl);
		hpPane.add(maxHPLbl, BorderLayout.WEST);

		ReminderField hpMaxField = new ReminderField();
		hpMaxField.setNumbersOnly();
		hpMaxField.setText(p.maxHP + "");
		hpMaxField.setEditable(false);
		hpMaxField.setColumns(7);
		hpMaxField.setFocusable(false);
		hpMaxField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				updateHP();
			}

			public void removeUpdate(DocumentEvent e) {
				updateHP();
			}

			public void changedUpdate(DocumentEvent e) {
				updateHP();
			}

			private void updateHP() {
				if (hpMaxField.getText().length() > 0)
					p.maxHP = Integer.parseInt(hpMaxField.getText());
				else
					p.maxHP = 0;
			}
		});
		editableList.add(hpMaxField);
		StyleContainer.SetFontMain(hpMaxField);
		hpPane.add(hpMaxField, BorderLayout.CENTER);
		setTightSize(hpPane);

		/*
		 * AC Pane
		 */
		JPanel acPane = new JPanel();
		acPane.setLayout(new BorderLayout());
		pdPane.add(acPane);

		JLabel acLbl = new JLabel("AC:");
		acLbl.setFocusable(false);
		Dimension sizeAC = new Dimension(StyleContainer.getStringWidth(StyleContainer.FNT_HEADER_BOLD, "Passive Perc:"),
				acLbl.getPreferredSize().height);
		acLbl.setPreferredSize(sizeAC);
		StyleContainer.SetFontHeader(acLbl);
		acPane.add(acLbl, BorderLayout.WEST);

		ReminderField acField = new ReminderField();
		acField.setNumbersOnly();
		acField.setText(p.ac + "");
		acField.setEditable(false);
		acField.setColumns(7);
		acField.setFocusable(false);
		acField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				updateAC();
			}

			public void removeUpdate(DocumentEvent e) {
				updateAC();
			}

			public void changedUpdate(DocumentEvent e) {
				updateAC();
			}

			private void updateAC() {
				if (acField.getText().length() > 0)
					p.ac = Integer.parseInt(acField.getText());
				else
					p.ac = 0;
			}
		});
		editableList.add(acField);
		StyleContainer.SetFontMain(acField);
		acPane.add(acField, BorderLayout.CENTER);
		setTightSize(acPane);

		/*
		 * Passive Perception Pane
		 */
		JPanel ppPane = new JPanel();
		ppPane.setLayout(new BorderLayout());
		pdPane.add(ppPane);

		JLabel ppLbl = new JLabel("Passive Perc:");
		ppLbl.setFocusable(false);
		Dimension sizePP = new Dimension(StyleContainer.getStringWidth(StyleContainer.FNT_HEADER_BOLD, "Passive Perc:"),
				ppLbl.getPreferredSize().height);
		ppLbl.setPreferredSize(sizePP);
		StyleContainer.SetFontHeader(ppLbl);
		ppPane.add(ppLbl, BorderLayout.WEST);

		ReminderField ppField = new ReminderField();
		ppField.setNumbersOnly();
		ppField.setText(p.ac + "");
		ppField.setEditable(false);
		ppField.setColumns(7);
		ppField.setFocusable(false);
		ppField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				updateAC();
			}

			public void removeUpdate(DocumentEvent e) {
				updateAC();
			}

			public void changedUpdate(DocumentEvent e) {
				updateAC();
			}

			private void updateAC() {
				if (ppField.getText().length() > 0)
					p.ac = Integer.parseInt(ppField.getText());
				else
					p.ac = 0;
			}
		});
		editableList.add(ppField);
		StyleContainer.SetFontMain(ppField);
		ppPane.add(ppField, BorderLayout.CENTER);
		setTightSize(ppPane);

		/*
		 * Class Selection
		 */

		JPanel cPane = new JPanel();
		cPane.setLayout(new BorderLayout());
		pdPane.add(cPane);

		classBox = new JComboBox<>(PlayerClass.values());
		classBox.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED)
				p.pClass = (PlayerClass) classBox.getSelectedItem();
		});
		classBox.setEnabled(false);
		classBox.setFocusable(false);
		classBox.setSelectedItem(p.pClass);
		StyleContainer.SetFontHeader(classBox);
		cPane.add(classBox, BorderLayout.CENTER);
		cPane.setPreferredSize(new Dimension(ppPane.getPreferredSize().width, cPane.getPreferredSize().height));
		setTightSize(cPane);
	}

	private void BuildHeader() {
		JPanel headPane = new JPanel();
		headPane.setLayout(new BorderLayout());
		this.add(headPane, BorderLayout.NORTH);

		JTextField nameField = new JTextField((p.name.length() > 0) ? p.name : "");
		StyleContainer.SetFontHeader(nameField);
		nameField.setEditable(false);
		nameField.setFocusable(false);
//		nameField.getDocument().addDocumentListener(new DocumentListener() {
//			public void insertUpdate(DocumentEvent e) {
//				p.name = nameField.getText();
//				reps.put("<NAME>", nameField.getText());
//				if(registered)
//					gd.updatePartyTabTitle(p.name, PlayerPane.this);
//			}
//
//			public void removeUpdate(DocumentEvent e) {
//				p.name = nameField.getText();
//				reps.put("<NAME>", nameField.getText());
//				if(registered)
//					gd.updatePartyTabTitle(p.name, PlayerPane.this);
//			}
//
//			public void changedUpdate(DocumentEvent e) {
//				p.name = nameField.getText();
//				reps.put("<NAME>", nameField.getText());
//				if(registered)
//					gd.updatePartyTabTitle(p.name, PlayerPane.this);
//			}
//		});
//		editableList.add(nameField);
		headPane.add(nameField, BorderLayout.CENTER);

		if(!noEdits) {
			editBox = new JCheckBox("Edit Mode");
			editBox.setFocusable(false);
			editBox.addActionListener(e -> {
				SetEditables(editBox.isSelected());
			});
			StyleContainer.SetFontMain(editBox);
			headPane.add(editBox, BorderLayout.EAST);
		}
	}

	private void SetEditables(boolean b) {
//		CardLayout cl = (CardLayout) notePane.getLayout();
		SwingUtilities.invokeLater(()->{
			if(b) {
//				cl.show(notePane, NOTES_EDIT);
				notePane.removeAll();
				RichEditor edit = new RichEditor(data, reps);
				edit.LoadDocument(p.note);
				edit.disableTables();
				notePane.add(edit, BorderLayout.CENTER);
			}else {
				notePane.removeAll();
				HoverTextPane read = new HoverTextPane(data, gd, gd.getDesktop());
				read.setDocument(p.note);
				JScrollPane readScroll = new JScrollPane(read);
				notePane.add(readScroll, BorderLayout.CENTER);
//				cl.show(notePane, NOTES_VIEW);
			}
			
			notePane.revalidate();
			notePane.repaint();
		});
		
		
//		notePane.revalidate();
//		notePane.repaint();
//		
//		System.out.println("HoverTextPane width: " + readNotesPane.getWidth());
//		System.out.println("Viewport width: " + readNotesPane.getParent().getWidth());
		for (JTextComponent c : editableList) {
			c.setFocusable(b);
			c.setEditable(b);
		}
		classBox.setEnabled(b);
		classBox.setFocusable(b);
	}

	private static void setTightSize(JComponent comp) {
		Dimension pref = comp.getPreferredSize();
		comp.setMaximumSize(pref);
		comp.setMinimumSize(pref);
	}

	public Player getPlayer() {
		return p;
	}
	
	public void lockEdits(boolean lock) {
		if(lock) {
			editBox.setSelected(false);
			SetEditables(false);
		}
		editBox.setEnabled(!lock);
	}
}