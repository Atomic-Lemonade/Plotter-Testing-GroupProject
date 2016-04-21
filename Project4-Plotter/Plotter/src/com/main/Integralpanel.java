package com.main;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.maths.AdvancedCalculator;
import com.maths.Calculator;


public class Integralpanel extends JDialog implements ActionListener{

	Calculator calc=null;
	JTextField result=null;
    JPanel pan=null;
	private DigitTextField displayedA;
	private DigitTextField displayedB;
	private JButton recalculate;
	DecimalFormat df=new DecimalFormat("####.#########");
	private JRadioButton typet;
	private JRadioButton types;
	private JRadioButton typeg;
	
	
	
	public Integralpanel(Calculator calc) throws HeadlessException {
		
		setBounds(30,30,200,230);
		setTitle("Integral");
		pan=new JPanel();
		pan.setLayout(null);
        pan. setBackground(Visualizer.BACKGROUND_COLOR);
		//setSize(200,500);

        int r=30;
        
        JLabel rlabel = new JLabel("Calculated range:");
		rlabel.setBounds(5,10,100,20);
		pan.add(rlabel);
		JLabel alabel = new JLabel("x1:");
		alabel.setBounds(5,r,20,20);
		pan.add(alabel);
		displayedA=new DigitTextField();
		//displayedA.setEditable(false);
		displayedA.setBounds(35,r,80,20);
		pan.add(displayedA);
		r+=20;
		JLabel blabel = new JLabel("x2:");
		blabel.setBounds(5,r,20,20);
		pan.add(blabel);
		displayedB=new DigitTextField();
		//displayedB.setEditable(false);
		displayedB.setBounds(35,r,80,20);
		pan.add(displayedB);
		r+=30;
		ButtonGroup bg=new ButtonGroup();
		typet=new JRadioButton("Trapezium");
		bg.add(typet);
		typet.setBounds(5,r,100,20);
		typet.setOpaque(false);
		pan.add(typet);
		types=new JRadioButton("Simpson");
		bg.add(types);
		types.setOpaque(false);
		types.setSelected(true);
		types.setBounds(110,r,100,20);
		pan.add(types);
		r+=20;
		typeg = new JRadioButton("Gauss");
		bg.add(typeg);
		typeg.setBounds(5,r,100,20);
		typeg.setOpaque(false);
		pan.add(typeg);
		r+=30;
		JLabel reslabel = new JLabel("Res:");
		reslabel.setBounds(5,r,30,20);
		pan.add(reslabel);
		result=new JTextField(5);
		result.setBounds(40,r,100,20);
		result.setEditable(false);
		pan.add(result);
		r+=30;
		recalculate=new JButton("Recalculate");
		recalculate.setBounds(5,r,140,20);
		recalculate.addActionListener(this);
		pan.add(recalculate);

		calculateIntegral(calc);
		this.calc = calc;

		add(pan);
		setModal(true);
		setVisible(true);
		
	}



	/**
	 * @param calc2
	 */
	private void calculateIntegral(Calculator calc2) {
		double val=0;
		if(types.isSelected())
			val=AdvancedCalculator.SimpsonIntegral(calc2);
		else if(typet.isSelected())
			val=AdvancedCalculator.trapeziumIntegral(calc2);
		else if(typeg.isSelected())
			val=AdvancedCalculator.gaussIntegral(calc2);

		displayedA.setText(""+calc2.a);
		displayedB.setText(""+calc2.b);

        
		result.setText(""+df.format(val));
		
	}



	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		Object o=arg0.getSource();
		if(o==recalculate) recalculate();
		
	}



	/**
	 * 
	 */
	private void recalculate() {
		calc.a=Double.parseDouble(displayedA.getText());
		calc.b=Double.parseDouble(displayedB.getText());
		calculateIntegral(calc);
	}
}
