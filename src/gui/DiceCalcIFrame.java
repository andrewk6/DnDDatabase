package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.script.ScriptException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import gui.gui_helpers.structures.StyleContainer;
import utils.DiceCalculator;
import utils.IllegalDiceNotationException;

public class DiceCalcIFrame extends JInternalFrame
{
//	private final DiceCalculator dCalc;
	
	private JTextField equation;
	private JTabbedPane tabs;
	private JPanel resultPanel;
	
	public DiceCalcIFrame()
	{	
		
		setSize(450, 500);
		setResizable(false);
		this.setIconifiable(false);
		this.setClosable(true);
		this.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);
		this.setTitle("Dice Calculator");
		StyleContainer.SetIcon(this, StyleContainer.DICE_CALC_ICON_FILE);
		BuildCalc(this.getContentPane());
		equation.requestFocusInWindow();
	}

	private void BuildCalc(Container cPane) {
		cPane.setLayout(new BorderLayout());
		tabs = new JTabbedPane();
		cPane.add(tabs, BorderLayout.CENTER);
		
		JPanel calcPanel = new JPanel();
		calcPanel.setLayout(new BorderLayout());
		tabs.addTab("Calculator", calcPanel);
		
		resultPanel = new JPanel();
		resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
		JScrollPane resultScroll = new JScrollPane(resultPanel);
		tabs.addTab("Results", resultScroll);
		
		BuildEquationField(calcPanel);
		BuildCalcBody(calcPanel);
	}

	private void BuildEquationField(Container cPane) {
		this.equation = new JTextField();
		StyleContainer.SetFontHeader(equation);
		cPane.add(equation, BorderLayout.NORTH);
	}
	
	private void BuildCalcBody(Container cPane) {
		JPanel calcPane = new JPanel();
		calcPane.setLayout(new  BorderLayout());
		cPane.add(calcPane, BorderLayout.CENTER);
		
		JToolBar diceTools = new JToolBar();
		diceTools.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
		diceTools.setFloatable(false);
		calcPane.add(diceTools, BorderLayout.NORTH);
		BuildDiceButtons(diceTools);
		
		JToolBar funcBtns = new JToolBar(JToolBar.VERTICAL);
		funcBtns.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.BLACK));
		funcBtns.setFloatable(false);
		BuildFuncButtons(funcBtns);
		calcPane.add(funcBtns, BorderLayout.EAST);
		
		JPanel mainCPane = new JPanel();
		mainCPane.setLayout(new BorderLayout());
		calcPane.add(mainCPane, BorderLayout.CENTER);
		
		JButton calcBtn = new JButton("Calulate");
		StyleContainer.SetFontBtn(calcBtn);
		calcBtn.setFocusable(false);
		calcBtn.addActionListener(e ->{
			try {
				ComputeResult();
			} catch (IllegalDiceNotationException e1) {
				JOptionPane.showMessageDialog(this, "Invalid dice notation", "Bad Equation", JOptionPane.ERROR_MESSAGE);
			} catch (ScriptException e1) {
				e1.printStackTrace();
			}
		});
		mainCPane.add(calcBtn, BorderLayout.SOUTH);
		
		JPanel intBtnPane = new JPanel();
		intBtnPane.setLayout(new GridLayout(3, 3));
		mainCPane.add(intBtnPane, BorderLayout.CENTER);
		
		for(int i = 1; i < 10; i ++) {
			JButton intBtn = new JButton("" + i);
			StyleContainer.SetFontBtn(intBtn);
			intBtn.setFocusable(false);
			intBtn.addActionListener(e ->{
				EquationInsert(intBtn.getText());
			});
			intBtnPane.add(intBtn);
		}
	}
	
	private void BuildFuncButtons(JToolBar fTools) {
		fTools.addSeparator();
		JButton btnPl = new JButton(" + ");
		StyleContainer.SetFontBtn(btnPl);
		btnPl.setFocusable(false);
		btnPl.addActionListener(e ->{
			EquationInsert("+");
		});
		fTools.add(btnPl);
		fTools.addSeparator();
		
		JButton btnMn = new JButton(" - ");
		StyleContainer.SetFontBtn(btnMn);
		btnMn.setFocusable(false);
		btnMn.addActionListener(e ->{
			EquationInsert("-");
		});
		fTools.add(btnMn);
		fTools.addSeparator();
		
		JButton btnMu = new JButton(" * ");
		StyleContainer.SetFontBtn(btnMu);
		btnMu.setFocusable(false);
		btnMu.addActionListener(e ->{
			EquationInsert("*");
		});
		fTools.add(btnMu);
		fTools.addSeparator();
		
		JButton btnDiv = new JButton(" / ");
		StyleContainer.SetFontBtn(btnDiv);
		btnDiv.setFocusable(false);
		btnDiv.addActionListener(e ->{
			EquationInsert("/");
		});
		fTools.add(btnDiv);
	}
	
	private void BuildDiceButtons(JToolBar dTools) {
		JButton btn4 = new JButton("D4");
		StyleContainer.SetFontBtn(btn4);
		btn4.setFocusable(false);
		btn4.addActionListener(e ->{
			EquationInsert("D4");
		});
		dTools.add(btn4);
		dTools.addSeparator();
		
		JButton btn6 = new JButton("D6");
		StyleContainer.SetFontBtn(btn6);
		btn6.setFocusable(false);
		btn6.addActionListener(e ->{
			EquationInsert("D6");
		});
		dTools.add(btn6);
		dTools.addSeparator();
		
		JButton btn8 = new JButton("D8");
		StyleContainer.SetFontBtn(btn8);
		btn8.setFocusable(false);
		btn8.addActionListener(e ->{
			EquationInsert("D8");
		});
		dTools.add(btn8);
		dTools.addSeparator();
		
		JButton btn10 = new JButton("D10");
		StyleContainer.SetFontBtn(btn10);
		btn10.setFocusable(false);
		btn10.addActionListener(e ->{
			EquationInsert("D10");
		});
		dTools.add(btn10);
		dTools.addSeparator();
		
		JButton btn12 = new JButton("D12");
		StyleContainer.SetFontBtn(btn12);
		btn12.setFocusable(false);
		btn12.addActionListener(e ->{
			EquationInsert("D12");
		});
		dTools.add(btn12);
		dTools.addSeparator();
		
		JButton btn20 = new JButton("D20");
		StyleContainer.SetFontBtn(btn20);
		btn20.setFocusable(false);
		btn20.addActionListener(e ->{
			EquationInsert("D20");
		});
		dTools.add(btn20);
		dTools.addSeparator();
		
		JButton btn100 = new JButton("D100");
		StyleContainer.SetFontBtn(btn100);
		btn100.setFocusable(false);
		btn100.addActionListener(e ->{
			EquationInsert("D100");
		});
		dTools.add(btn100);
	}
	
	private void ComputeResult() throws IllegalDiceNotationException, ScriptException {
		int val = DiceCalculator.parseDiceExpression(equation.getText());
		System.out.println(val);
		
		JPanel rPane = new JPanel();
		rPane.setLayout(new BorderLayout());
		resultPanel.add(rPane);
		
		JTextField equate = new JTextField(equation.getText());
		equate.setEditable(false);
		equate.setFocusable(false);
		equate.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				if(equation.getText().length() == 0) {
					equation.setText(equate.getText());
					equation.setCaretPosition(equation.getText().length());
					if(tabs.getSelectedIndex() == 1)
						tabs.setSelectedIndex(0);
					equation.requestFocusInWindow();
				}
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
		});
		StyleContainer.SetFontMain(equate);
		rPane.add(equate, BorderLayout.CENTER);
		
		JTextField calced = new JTextField("   " + val + "   ");
		calced.setEditable(false);
		calced.setFocusable(false);
		StyleContainer.SetFontHeader(calced);
		rPane.add(calced, BorderLayout.EAST);
		
		if(tabs.getSelectedIndex() == 0)
			tabs.setSelectedIndex(1);
		
		equation.setText("");
		equation.setCaretPosition(0);
	}
	
	private void EquationInsert(String insert) {
		int cPos = equation.getCaretPosition();
		int cPosE = -1;
		String toUpdate = equation.getText();
		if(cPos == equation.getText().length()) {
			toUpdate += insert;
			cPosE = cPos;
		} else 
			toUpdate = toUpdate.substring(0, cPos) + insert + 
					toUpdate.substring(cPos, toUpdate.length());
		equation.setText(toUpdate);
		equation.setCaretPosition(cPos + insert.length());
	}
}