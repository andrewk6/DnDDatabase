package gui.gui_helpers.structures;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import data.Monster;
import data.dungeon.Dungeon;
import gui.ComboIFrame;
import gui.FeatIFrame;
import gui.ItemIFrame;
import gui.MonsterIFrame;
import gui.RuleIFrame;
import gui.SpellIFrame;
import gui.campaign.PartyIFrame;
import gui.campaign.PlayerPane;
import gui.dungeon.DungeonIBuilder;
import gui.dungeon.DungeonIViewer;
import gui.gui_helpers.PlayerNameTargets;
import gui.initative.InitiativeIFrame;
import utils.ErrorLogger;

public class GuiDirector
{
	private JDesktopPane dPane;
	private MonsterIFrame mFrame;
	private SpellIFrame sFrame;
	private RuleIFrame rFrame;
	private ItemIFrame iFrame;
	private FeatIFrame fFrame;
	
	private PartyIFrame pFrame;
	private InitiativeIFrame initFrame;
	
	private DungeonIBuilder dunBuild;
	private DungeonIViewer dunView;
	
	private AllTab cFrame;
	
	private HashMap<PlayerPane, PlayerNameTargets> nameUpdates;
	private ArrayList<PlayerPane> registeredPlayerPanes;
	private boolean lockStatus = false;
	
	
	public GuiDirector(JDesktopPane dPane) {
		mFrame = null;
		sFrame = null;
		rFrame = null;
		this.dPane = dPane;
		nameUpdates = new HashMap<PlayerPane, PlayerNameTargets>();
		registeredPlayerPanes = new ArrayList<PlayerPane>();
	}
	
	public void NotifyFocus(ContentTab frame) {
		if(frame instanceof RuleIFrame)
			rFrame = (RuleIFrame) frame;
		else if(frame instanceof SpellIFrame)
			sFrame = (SpellIFrame) frame;
		else if(frame instanceof MonsterIFrame)
			mFrame = (MonsterIFrame) frame;
		else if(frame instanceof FeatIFrame)
			fFrame = (FeatIFrame) frame;
	}
	
	public void DeRegister(ContentTab frame) {
		if(frame instanceof RuleIFrame)
			if(frame == rFrame)
				rFrame = null;
		else if(frame instanceof SpellIFrame)
			if(sFrame == frame)
				sFrame = null;
		else if(frame instanceof MonsterIFrame)
			if(mFrame == frame)
				mFrame = null;
			else if(frame instanceof FeatIFrame)
				if(fFrame == frame)
					fFrame = null;
	}
	
	public void RegisterFrame(ContentFrame frame) {
		if(frame instanceof PartyIFrame)
			pFrame = (PartyIFrame) frame;
		else if(frame instanceof ItemIFrame)
			iFrame = (ItemIFrame) frame;
	}
	
	public void deRegisterFrame(ContentFrame frame) {
		if(frame instanceof PartyIFrame)
			pFrame = null;
		else if(frame instanceof ItemIFrame)
			iFrame = null;
	}
	
	public void RegisterInitiativeFrame(InitiativeIFrame iFrame) {
		initFrame = iFrame;
	}
	
	public void DegisterInitiativeFrame(InitiativeIFrame iFrame) {
		if(initFrame.equals(iFrame))
			initFrame = null;
	}
	
	public void RegisterDungeonFrame(JInternalFrame frame) {
		if(frame instanceof DungeonIBuilder)
			dunBuild = (DungeonIBuilder) frame;
		else if(frame instanceof DungeonIViewer)
			dunView = (DungeonIViewer) frame;
	}
	
	public void DeregisterDungeonFrame(JInternalFrame f) {
		if(f instanceof DungeonIBuilder)
			if(dunBuild.equals(f))
				dunBuild = null;
		else if(f instanceof DungeonIViewer)
			if(dunView.equals(f))
				dunView = null;
	}
	
	public void addInitiaitive(ArrayList<Monster> m) {
		if(initFrame != null) {
			initFrame.importMonsterInit(m);
			initFrame.setVisible(true);
			initFrame.requestFocus();
			initFrame.toFront();
			dPane.revalidate();
			dPane.repaint();
		}else {
			JOptionPane.showMessageDialog(dPane, "Please open an Initiative Window.");
		}
	}
	
	public void RegisterCombo(AllTab frame) {
		cFrame = frame;
	}
	
	public void DeRegisterCombo(AllTab frame) {
		if(cFrame == frame) {
			cFrame = null;
		}
	}
	
	public void popRFrame() {
		SwingUtilities.invokeLater(()->{
			if(rFrame.isIcon())
				try {
					rFrame.setIcon(false);
				} catch (PropertyVetoException e) {
					ErrorLogger.log(e);
					e.printStackTrace();
				}
			rFrame.toFront();
			if(!rFrame.isVisible())
				rFrame.setVisible(true);
			dPane.revalidate();
			dPane.repaint();
		});
	}
	
	public void popSFrame() {
		SwingUtilities.invokeLater(()->{
			if(sFrame.isIcon())
				try {
					sFrame.setIcon(false);
				} catch (PropertyVetoException e) {
					ErrorLogger.log(e);
					e.printStackTrace();
				}
			sFrame.toFront();
			if(!sFrame.isVisible())
				sFrame.setVisible(true);
			dPane.revalidate();
			dPane.repaint();
		});
	}
	
	public void popMFrame() {
		SwingUtilities.invokeLater(()->{
			if(mFrame.isIcon())
				try {
					mFrame.setIcon(false);
				} catch (PropertyVetoException e) {
					ErrorLogger.log(e);
					e.printStackTrace();
				}
			mFrame.toFront();
			if(!mFrame.isVisible())
				mFrame.setVisible(true);
			dPane.revalidate();
			dPane.repaint();
		});
	}
	
	public void handleFrame(String key, boolean item) {
		if(item) {
			popIFrame();
			iFrame.handleLink(key);
		}else {
			pFrame.handleLink(key);
			popPFrame();
		}
	}
	
	public void popPFrame() {
		SwingUtilities.invokeLater(()->{
			if(pFrame.isIcon())
				try {
					pFrame.setIcon(false);
				} catch (PropertyVetoException e) {
					ErrorLogger.log(e);
					e.printStackTrace();
				}
			pFrame.toFront();
			if(!pFrame.isVisible())
				pFrame.setVisible(true);
			dPane.revalidate();
			dPane.repaint();
		});
	}
	
	public void popIFrame() {
		SwingUtilities.invokeLater(()->{
			if(iFrame.isIcon())
				try {
					iFrame.setIcon(false);
				} catch (PropertyVetoException e) {
					ErrorLogger.log(e);
					e.printStackTrace();
				}
			iFrame.toFront();
			if(!iFrame.isVisible())
				iFrame.setVisible(true);
			dPane.revalidate();
			dPane.repaint();
		});
	}
	
	public void popFrame(JInternalFrame frame) {
		SwingUtilities.invokeLater(()->{
			if(frame.isIcon())
				try {
					frame.setIcon(false);
				} catch (PropertyVetoException e) {
					ErrorLogger.log(e);
					e.printStackTrace();
				}
			frame.toFront();
			if(!frame.isVisible())
				frame.setVisible(true);
			dPane.revalidate();
			dPane.repaint();
		});
	}
	
	public void deregisterPlayerPaneTabs(PlayerPane p) {
		nameUpdates.remove(p);
	}
	
	public void regsiterPlayerPaneTabs(PlayerPane pP, PlayerNameTargets tars) {
		pP.registered = true;
		nameUpdates.put(pP, tars);
	}
	
	public void registerPlayerPane(PlayerPane p) {
		registeredPlayerPanes.add(p);
	}
	
	public void deregisterPlayerPane(PlayerPane p) {
		registeredPlayerPanes.remove(p);
	}
	
	public void lockPlayerEdits(boolean lock) {
		for(PlayerPane p : registeredPlayerPanes)
			p.lockEdits(lock);
		lockStatus = lock;
	}
	
	public boolean getLockStatus() {
		return lockStatus;
	}
	
	public void updatePartyTabTitle(String newTitle, PlayerPane p) {
		JTabbedPane tabs = nameUpdates.get(p).tabs();
		JButton btn = nameUpdates.get(p).btn();
		int index = tabs.indexOfComponent(p.getParent());
		tabs.setTitleAt(index, newTitle);
		
		btn.setText("Delete: " + newTitle);
	}
	
	public void EditDungeon(Dungeon d) {
		System.out.println("Edit: D-" + d != null + " Frm ");
		if(dunBuild != null && d != null) {
			dunBuild.loadDungeon(d);
			popFrame(dunBuild);
			dunView.setVisible(false);
		}
	}
	
	public void ViewDungeon(Dungeon d) {
		if(dunView != null && d != null) {
			dunView.LoadDungeon(d);
			popFrame(dunView);
			dunBuild.setVisible(false);
		}
	}
	
	public String showTypeSelectionDialog(Window parentFrame, String itemName, List<String> types) {
	    JDialog dialog = new JDialog(parentFrame, "Select Type", JDialog.ModalityType.APPLICATION_MODAL); // true = modal
	    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	    dialog.setLayout(new BorderLayout());

	    JLabel label = new JLabel("What type is '" + itemName + "'?");
	    label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	    dialog.add(label, BorderLayout.NORTH);

	    JPanel buttonPanel = new JPanel(new FlowLayout());
	    final String[] result = { null };

	    for (String type : types) {
	        JButton button = new JButton(type);
	        button.addActionListener(e -> {
	            result[0] = type;
	            dialog.dispose();
	        });
	        buttonPanel.add(button);
	    }

	    JButton cancelButton = new JButton("Cancel");
	    cancelButton.addActionListener(e -> dialog.dispose());
	    buttonPanel.add(cancelButton);

	    dialog.add(buttonPanel, BorderLayout.CENTER);
	    dialog.pack();

	    // Optional: position relative to main frame or center of screen
	    dialog.setLocationRelativeTo(parentFrame);

	    dialog.setVisible(true); // BLOCKS until dialog is closed

	    return result[0]; // null if cancelled
	}
	
	public static InternalFrameListener getContentTabListener(GuiDirector gd, ContentTab register) {
		return new InternalFrameListener() {
			public void internalFrameOpened(InternalFrameEvent e) {}
			
			public void internalFrameIconified(InternalFrameEvent e) {}
			
			public void internalFrameDeiconified(InternalFrameEvent e) {
				gd.NotifyFocus(register);
			}
			
			public void internalFrameDeactivated(InternalFrameEvent e) {}
			
			public void internalFrameClosing(InternalFrameEvent e) {
				gd.DeRegister(register);
			}
			
			public void internalFrameClosed(InternalFrameEvent e) {}
			
			public void internalFrameActivated(InternalFrameEvent e) {
				gd.NotifyFocus(register);
			}
		};
	}
	
	public static InternalFrameListener getContentFrameListener(GuiDirector gd, ContentFrame register) {
		return new InternalFrameListener() {
			public void internalFrameOpened(InternalFrameEvent e) {
				gd.RegisterFrame(register);
			}
			
			public void internalFrameIconified(InternalFrameEvent e) {}
			
			public void internalFrameDeiconified(InternalFrameEvent e) {
				gd.RegisterFrame(register);
			}
			
			public void internalFrameDeactivated(InternalFrameEvent e) {}
			
			public void internalFrameClosing(InternalFrameEvent e) {
				gd.deRegisterFrame(register);
			}
			
			public void internalFrameClosed(InternalFrameEvent e) {}
			
			public void internalFrameActivated(InternalFrameEvent e) {
				gd.RegisterFrame(register);
			}
		};
	}
	
	public static InternalFrameListener getAllTabListener(GuiDirector gd, AllTab register) {
		return new InternalFrameListener() {
			public void internalFrameOpened(InternalFrameEvent e) {
				if(gd.getComboFrame() != null) {
					if(!(gd.getComboFrame() instanceof InitiativeIFrame))
						gd.RegisterCombo(register);
				}else
					gd.RegisterCombo(register);
			}

			public void internalFrameIconified(InternalFrameEvent e) {
				gd.DeRegisterCombo(register);
			}

			public void internalFrameDeiconified(InternalFrameEvent e) {
//				if(gd.getComboFrame() != null)
//					if(!(gd.getComboFrame() instanceof InitiativeIFrame))
//						gd.RegisterCombo(register);
				if(gd.getComboFrame() != null) {
					if(!(gd.getComboFrame() instanceof InitiativeIFrame))
						gd.RegisterCombo(register);
				}else
					gd.RegisterCombo(register);
			}

			public void internalFrameDeactivated(InternalFrameEvent e) {}

			public void internalFrameClosing(InternalFrameEvent e) {
				gd.DeRegisterCombo(register);
			}

			public void internalFrameClosed(InternalFrameEvent e) {
			}

			public void internalFrameActivated(InternalFrameEvent e) {
//				if(gd.getComboFrame() != null)
//					if(!(gd.getComboFrame() instanceof InitiativeIFrame))
//						gd.RegisterCombo(register);
				if(gd.getComboFrame() != null) {
					if(!(gd.getComboFrame() instanceof InitiativeIFrame))
						gd.RegisterCombo(register);
				}else
					gd.RegisterCombo(register);
			}	
		};
	}
	
	public AllTab getComboFrame() {
		return cFrame;
	}

	public MonsterIFrame getmFrame() {
		return mFrame;
	}

	public SpellIFrame getsFrame() {
		return sFrame;
	}

	public RuleIFrame getrFrame() {
		return rFrame;
	}
	
	public ItemIFrame getIFrame() {
		return iFrame;
	}
	
	public PartyIFrame getPFrame() {
		return pFrame;
	}
	
	public JDesktopPane getDesktop() {
		return dPane;
	}
}