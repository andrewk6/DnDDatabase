package builders.rule_builder;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import data.DataContainer;
import data.Rule;
import gui.gui_helpers.RichEditor;

import java.awt.BorderLayout;
import java.awt.Dimension;

public class RuleBuilder {
	
	DataContainer data;

	private JFrame frmRuleBuilder;
	private JPanel descPane, rulesPane, rulesListPane;
//	private RuleEditor rulesDesc;
	private RichEditor rulesDesc;
	private JTextField rulesName;

	private final String rulesFileName = "Rules.xol";
	private HashMap<String, Rule> rulesList;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RuleBuilder window = new RuleBuilder(new DataContainer());
					window.frmRuleBuilder.pack();
					window.frmRuleBuilder.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public RuleBuilder(DataContainer d) {
		data = d;
		loadRulesList();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		frmRuleBuilder = new JFrame();
		frmRuleBuilder.setPreferredSize(new Dimension(450, 450));
		frmRuleBuilder.setTitle("Rule Builder");
		frmRuleBuilder.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frmRuleBuilder.addWindowListener(new WindowListener() {
			public void windowOpened(WindowEvent e) {
				
			}

			public void windowClosing(WindowEvent e) {
				int opt = JOptionPane.showConfirmDialog(frmRuleBuilder, "Do you want to save on close?");
				if(opt == 0) {
					boolean success = safeSerializeRules();
			        if (success) {
			            frmRuleBuilder.dispose(); // Only close if successful
			        } else {
			            JOptionPane.showMessageDialog(frmRuleBuilder, "Failed to save data. Window will remain open.", "Save Error", JOptionPane.ERROR_MESSAGE);
			        }
				}else if(opt == 1) {
					frmRuleBuilder.dispose();
				}else {
					frmRuleBuilder.setVisible(true);
				}
				
			}

			public void windowClosed(WindowEvent e) {
			}

			public void windowIconified(WindowEvent e) {
			}

			public void windowDeiconified(WindowEvent e) {
			}

			public void windowActivated(WindowEvent e) {
			}

			public void windowDeactivated(WindowEvent e) {
			}
		});
		frmRuleBuilder.setBounds(100, 100, 450, 300);

		descPane = new JPanel();
		descPane.setLayout(new BorderLayout());
		frmRuleBuilder.getContentPane().add(descPane, BorderLayout.CENTER);

		JPanel rulesHeader = new JPanel();
		rulesHeader.setLayout(new BorderLayout());
		descPane.add(rulesHeader, BorderLayout.NORTH);

		rulesName = new JTextField();
		rulesName.setToolTipText("Enter a name for the rules.");
		rulesName.setFont(new Font("Monospaced", Font.BOLD, 20));
		rulesHeader.add(rulesName, BorderLayout.CENTER);

		JButton addBtn = new JButton();
		addBtn.setText("Add Rules");
		addBtn.setFont(new Font("Monospaced", Font.BOLD, 16));
		addBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (rulesName.getText().length() > 0 && rulesDesc.getText().length() > 0) {
					StyledDocument styledDoc = rulesDesc.getStyledDocument();
//							rulesDesc.getTextPane().getStyledDocument();
					rulesList.put(rulesName.getText(), new Rule(rulesName.getText(), rulesDesc.getText(),
							rulesDesc.convertDocumentToHTML(), styledDoc));
					descPane.remove(rulesDesc);
					descPane.revalidate();
//					rulesDesc = new RuleEditor();
					rulesDesc = new RichEditor(data);
					rulesName.setText("");
					BuildRulesList();
					rulesName.requestFocusInWindow();
					descPane.add(rulesDesc, BorderLayout.CENTER);
					rulesDesc.revalidate();
					rulesDesc.repaint();
				} else {
					JOptionPane.showMessageDialog(frmRuleBuilder, "Please enter both name and description for rules",
							"Rules Creation Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		addBtn.setFocusable(false);
		rulesHeader.add(addBtn, BorderLayout.EAST);

//		rulesDesc = new RuleEditor();
		rulesDesc = new RichEditor(data);
		descPane.add(rulesDesc, BorderLayout.CENTER);
		frmRuleBuilder.pack();
		frmRuleBuilder.revalidate();
		frmRuleBuilder.repaint();

		rulesPane = new JPanel();
		rulesPane.setLayout(new BorderLayout());
		frmRuleBuilder.getContentPane().add(rulesPane, BorderLayout.WEST);

		JTextField rulesHead = new JTextField("RULES LIST");
		rulesHead.setFont(new Font("Monospaced", Font.BOLD, 20));
		rulesHead.setEditable(false);
		rulesHead.setBorder(null);
		rulesHead.setHorizontalAlignment(SwingConstants.CENTER);
		rulesHead.setColumns(15);
		rulesHead.setFocusable(false);
		rulesPane.add(rulesHead, BorderLayout.NORTH);

		rulesListPane = new JPanel();
		rulesListPane.setLayout(new GridLayout(0, 1));
		JScrollPane rulesScroller = new JScrollPane(rulesListPane);
		rulesScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		rulesPane.add(rulesScroller, BorderLayout.CENTER);
		BuildRulesList();
		
		JButton saveBtn = new JButton("Save");
		saveBtn.setFont(new Font("Monospaced", Font.PLAIN, 16));
		saveBtn.setFocusable(false);
		saveBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean success = safeSerializeRules();
		        if (!success) {
		        	JOptionPane.showMessageDialog(frmRuleBuilder, "Failed to save data. Window will remain open.", "Save Error", JOptionPane.ERROR_MESSAGE);
		        }else {
		        	JOptionPane.showMessageDialog(frmRuleBuilder, "Save Complete.", "Save Finsihed", JOptionPane.INFORMATION_MESSAGE);
		        }
			}
		});
		rulesPane.add(saveBtn, BorderLayout.SOUTH);
	}

	public void BuildRulesList() {
		if (rulesListPane.getComponents().length > 0) {
			rulesListPane.removeAll();
		}

		ArrayList<Rule> rulesSorted = new ArrayList<Rule>();

		for (String r : rulesList.keySet()) {
			rulesSorted.add(rulesList.get(r));
		}

		Collections.sort(rulesSorted);

		for (Rule r : rulesSorted) {
			JTextField ruleField = new JTextField(r.name);
			ruleField.setFont(new Font("Monospaced", Font.PLAIN, 16));
			ruleField.setEditable(false);
			ruleField.setFocusable(false);
			ruleField.addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent e) {
					SwingUtilities.invokeLater(() -> {
						JFrame displayRule = new JFrame();
						displayRule.setAlwaysOnTop(true);
						displayRule.setSize(800, 600);
						displayRule.getContentPane().setLayout(new BorderLayout());
						JTextPane rDisp = new JTextPane();
						rDisp.setStyledDocument(r.ruleDoc);
						rDisp.setEditable(true);
						rDisp.revalidate();
						rDisp.repaint();
						rDisp.setFont(new Font("Monospaced", Font.PLAIN,16));
						displayRule.getContentPane().add(rDisp, BorderLayout.CENTER);
						
						JButton deleteRule = new JButton("Delete Rule");
						deleteRule.setFont(new Font("Monospaced", Font.BOLD, 16));
						deleteRule.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								rulesList.remove(r.name);
								BuildRulesList();
								displayRule.dispose();
							}
						});
						displayRule.add(deleteRule, BorderLayout.SOUTH);
						displayRule.setVisible(true);
					});
				}

				public void mousePressed(MouseEvent e) {
				}

				public void mouseReleased(MouseEvent e) {
				}

				public void mouseEntered(MouseEvent e) {
				}

				public void mouseExited(MouseEvent e) {
				}
			});
			rulesListPane.add(ruleField);
		}
		rulesListPane.revalidate();
		rulesListPane.repaint();
	}

	@SuppressWarnings("unchecked")
	public void loadRulesList() {
		File rulesFile = new File(rulesFileName);
		if (rulesFile.exists()) {
//			try {
//				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(rulesFile));
//				rulesList = (HashMap<String, Rule>) ois.readObject();
//				ois.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (ClassNotFoundException e) {
//				e.printStackTrace();
//			}
			rulesList = new HashMap<String, Rule>();
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(rulesFile))) {
				while (true) {
					try {
						Rule obj = (Rule) ois.readObject();
						rulesList.put(obj.name, obj);
					} catch (EOFException eof) {
						// End of file reached
						break;
					}
				}
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			rulesList = new HashMap<String, Rule>();
		}

	}
	
	private boolean safeSerializeRules() {
	    File saveFile = new File(rulesFileName);
	    if (!saveFile.exists()) {
	        try {
	            saveFile.createNewFile();
	        } catch (IOException e) {
	            System.err.println("Failed to create file: " + e.getMessage());
	            return false;
	        }
	    }

	    byte[] originalContents = null;

	    try {
	        originalContents = Files.readAllBytes(saveFile.toPath());
	    } catch (IOException e) {
	        System.err.println("Failed to read original file: " + e.getMessage());
	        return false;
	    }

	    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFile))) {
	        for (Iterator<Map.Entry<String, Rule>> it = rulesList.entrySet().iterator(); it.hasNext(); ) {
	            Map.Entry<String, Rule> entry = it.next();
	            try {
	                oos.writeObject(entry.getValue());
	            } catch (IOException ex) {
	                System.err.println("Failed to serialize rule: " + entry.getKey() + " - removing it.");
	                it.remove(); // remove faulty rule
	            }
	        }
	        oos.flush();
	        return true;
	    } catch (IOException e) {
	        System.err.println("Serialization failed: " + e.getMessage());
	        try {
	            Files.write(saveFile.toPath(), originalContents); // restore
	            System.out.println("Original file restored.");
	        } catch (IOException ex) {
	            System.err.println("Failed to restore original file: " + ex.getMessage());
	        }
	        return false;
	    }
	}

}
