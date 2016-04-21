package com.main;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.RepaintManager;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import com.maths.Calculator;


public class Visualizer extends JFrame implements ActionListener,KeyListener,
    MenuListener,MouseListener,MouseWheelListener,MouseMotionListener, FocusListener{
	
	public static int CARTESIAN2D_STATE=0;
	public static int CARTESIAN3D_STATE=2;
	public static int POLAR2D_STATE=1;
	public static int VISUALIZATION_STATE=CARTESIAN2D_STATE;
	//public static int VISUALIZATION_STATE=POLAR2D_STATE;
	
	String VERSION="Math graphics 2.0.1 ";
	
	JPanel center=null;
	JPanel bottom=null;
	Graphics2D graphics2D; 
	JButton draw=null;
	JButton more=null;
	JButton less=null;
	JButton calculateIntegral=null;
	
	//size of the diplay panel
	public static int HEIGHT=500;
	public static int WIDTH=800;
	private Calculator calc;
	
	public static int BUTTOMBORDER=100;
	public static int UPBORDER=40;
	public static int LEFTBORDER=0;
	public static int RIGHTBORDER=150;
	private JPanel right;
	public JTextField displayedFunction;
	public JTextField displayedA;
	public JTextField displayedB;
	private JPanel up;
	
	public static Color LINE_COLOR=Color.BLUE;
	public static Color LINE_2_COLOR=Color.GREEN;
	public static Color LINE_3D_COLOR=Color.LIGHT_GRAY;
	public static Color AXIS_COLOR=Color.BLACK;
	public static Color BACKGROUND_COLOR=new Color(	255,236,139);
	public static Color PANEL_COLOR=new Color(	205,190,112);
	private JButton displayDerivative;
	public static boolean isDerivativeDisplay;
	private JMenuBar jmb;
	private JMenuItem jmt1;
	private JMenuItem jmt3;
	private JMenuItem jmt2;
	private JMenuItem jmt4;
	private JMenu jm;
	private boolean redrawAfterMenu=false;
	private JMenu jm2;
	private JMenuItem jmt21;
	private JMenuItem jmt22;
	private Properties p;
	private JMenu jm3;
	private JMenuItem jmt31;
	private JMenuItem jmt23;
	public DigitTextField displayedA2;
	public DigitTextField displayedB2;
	private JMenu jm4;
	private JMenuItem jmt41;
	static boolean start=true;
	JFileChooser fc = new JFileChooser();
	private JLabel screenPoint;
	private int xPressed;
	private int yPressed;
	private File currentDirectory=null;
	private BufferedImage buf=null;
	private JMenuItem jmt42;
	
	
	public Visualizer(){
		
		loadProperties();
		
		center=new JPanel();	
		center.addMouseWheelListener(this);
		
		setLocation(20,20);
		setTitle(VERSION);
		setSize(LEFTBORDER+WIDTH+RIGHTBORDER,UPBORDER+HEIGHT+BUTTOMBORDER);
		Container container = getContentPane();
		setLayout(null);
		center.setBounds(LEFTBORDER,UPBORDER,WIDTH,HEIGHT);
		center.addMouseListener(this);
		center.addMouseMotionListener(this);
		
		add(center);
		
		calc=new Calculator(WIDTH,HEIGHT);
		calc.setY0(250);
		calc.setX0(50);

		if(VISUALIZATION_STATE==CARTESIAN2D_STATE)
		{
			buildUpPanel();
			buildRightPanel();
			buildBottomPanel();
		}
		else if(VISUALIZATION_STATE==POLAR2D_STATE)
		{
				buildPolarUpPanel();
				buildPolarRightPanel();
				buildBottomPanel();
		}
		else if(VISUALIZATION_STATE==CARTESIAN3D_STATE)
		{
				build3DUpPanel();
				build3DRightPanel();
				buildBottomPanel();
		}
		buildMenuBar();
		
		
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		//override to avoid a tricky paint problem
		RepaintManager.setCurrentManager(
				
		new RepaintManager(){

			public void paintDirtyRegions() {
				
				
				super.paintDirtyRegions();
				if(redrawAfterMenu ) {draw();redrawAfterMenu=false;}
				}
				
			}				
		);
		setInitColors(p);
		center.setBackground(BACKGROUND_COLOR);
		up.setBackground(PANEL_COLOR);
		bottom.setBackground(PANEL_COLOR);
		right.setBackground(PANEL_COLOR);

		setVisible(true);
	
	}






	/**
	 * @return
	 */
	private void buildMenuBar() {
		
		jmb=new JMenuBar();
		jm=new JMenu("Do");
		jm.addMenuListener(this);
		
		jmt1=new JMenuItem("Exit");
		jmt1.addActionListener(this);
		jmt2=new JMenuItem("Draw");
		jmt2.addActionListener(this);
		jmt3=new JMenuItem("Integral");
		jmt3.addActionListener(this);
		jmt4=new JMenuItem("Show DF");
		jmt4.addActionListener(this);
		jm.add(jmt2);
		
		jm.add(jmt3);
		jm.add(jmt4);
		jm.addSeparator();
		jm.add(jmt1);
		jmb.add(jm);
		
		jm2=new JMenu("Visualization");
		jm2.addMenuListener(this);
		jmt21=new JMenuItem("Cartesian 2D");
		jmt21.addActionListener(this);
		jm2.add(jmt21);
		jmt22 = new JMenuItem("Polar 2D");
		jmt22.addActionListener(this);
		jm2.add(jmt22);
		jmt23 = new JMenuItem("Cartesian 3D");
		jmt23.addActionListener(this);
		jm2.add(jmt23);
		jm2.addMenuListener(this);
		jmb.add(jm2);
		
		
		jm3=new JMenu("Colors");
		jm3.addMenuListener(this);
		jmt31=new JMenuItem("Change colors");
		jmt31.addActionListener(this);
		jm3.add(jmt31);
		jmb.add(jm3);
		
		jm4=new JMenu("Save");
		jm4.addMenuListener(this);
		jmt41=new JMenuItem("Save image");
		jmt41.addActionListener(this);
		jm4.add(jmt41);
		
		jmt42=new JMenuItem("Export data");
		jmt42.addActionListener(this);
		jm4.add(jmt42);
		
		jmb.add(jm4);
		
		setJMenuBar(jmb);
		
		
	}



	/**
	 * 
	 */
	private void buildBottomPanel() {
		
		draw=new JButton("<html><body><u>D</u>raw</body</html>");
		draw.addActionListener(this);
		draw.setBounds(100,2,100,20);
		
		more=new JButton("+");
		more.addActionListener(this);
		more.setBounds(210,2,50,20);
		
		less=new JButton("-");
		less.addActionListener(this);
		less.setBounds(280,2,50,20);
		
		bottom=new JPanel();
		bottom.setLayout(null);
		
		bottom.add(draw);
		bottom.add(less);  
		bottom.add(more);
		
		JLabel moves=new JLabel("arrow up,down,left,right to move axis ");
		moves.setBounds(350,2,300,20);
		bottom.add(moves);
		
		JLabel lscreenpoint = new JLabel();
		lscreenpoint.setText("Position x,y: ");
		lscreenpoint.setBounds(650,2,100,20);
		bottom.add(lscreenpoint);
		
		screenPoint=new JLabel();
		screenPoint.setText(",");
		screenPoint.setBounds(750,2,100,20);
		bottom.add(screenPoint);
		
		bottom.setBounds(0,UPBORDER+HEIGHT,LEFTBORDER+WIDTH+RIGHTBORDER,BUTTOMBORDER);
		add(bottom);
		
		draw.addKeyListener(this);
		less.addKeyListener(this);
		more.addKeyListener(this);

		
	}



	/**
	 * 
	 */
	private void buildUpPanel() {
		
		up=new JPanel();
		up.setLayout(null);
		up.setBounds(0,0,LEFTBORDER+WIDTH+RIGHTBORDER,UPBORDER);
		add(up);
		
		JLabel flabel = new JLabel("Displayed function: y=");
		flabel.setBounds(5,5,130,20);
		up.add(flabel);
		
		displayedFunction=new FunctionTextField();
		displayedFunction.addKeyListener(this);
		//displayedFunction.setEditable(false);
		displayedFunction.setBounds(140,5,400,20);
		displayedFunction.addFocusListener(this);
		
		up.add(displayedFunction);
		displayedFunction.setText(calc.DISPLAYED_FUNCTION);
		
		
		
	}
	
	/**
		 * 
		 */
		private void buildPolarUpPanel() {
		
			up=new JPanel();
			up.setLayout(null);
			up.setBounds(0,0,LEFTBORDER+WIDTH+RIGHTBORDER,UPBORDER);
			
			add(up);
			JLabel flabel = new JLabel("Displayed function: r(teta)=");
			flabel.setBounds(5,5,160,20);
			up.add(flabel);
			
			displayedFunction=new FunctionTextField();
			displayedFunction.addKeyListener(this);
			//displayedFunction.setEditable(false);
			displayedFunction.setBounds(170,5,400,20);
			displayedFunction.addFocusListener(this);
			up.add(displayedFunction);
			
			displayedFunction.setText(calc.DISPLAYED_FUNCTION);
		
		
		
		}
	
		/**
		 * 
		 */
		private void build3DUpPanel() {
			
			up=new JPanel();
			up.setLayout(null);
			up.setBounds(0,0,LEFTBORDER+WIDTH+RIGHTBORDER,UPBORDER);
			add(up);
			
			JLabel flabel = new JLabel("Displayed function: z(x,y)=");
			flabel.setBounds(5,5,150,20);
			up.add(flabel);
			
			displayedFunction=new FunctionTextField();
			displayedFunction.addKeyListener(this);
			//displayedFunction.setEditable(false);
			displayedFunction.setBounds(160,5,400,20);
			displayedFunction.addFocusListener(this);
			
			up.add(displayedFunction);
			displayedFunction.setText(calc.DISPLAYED_FUNCTION);
			
			
			
		}
		
		/**
			 * 
			 */
		private void build3DRightPanel() {
			
			right=new JPanel();
			right.setLayout(null);
			right.setBounds(LEFTBORDER+WIDTH,UPBORDER,RIGHTBORDER,HEIGHT);
				
			
			JLabel rlabel = new JLabel("Coord. ranges:");
			rlabel.setBounds(5,60,100,20);
			right.add(rlabel);
			JLabel alabel = new JLabel("ax:");
			alabel.setBounds(5,90,20,20);
			right.add(alabel);
			
			displayedA=new DigitTextField();
			displayedA.addKeyListener(this);
			//displayedA.setEditable(false);
			displayedA.setBounds(35,90,70,20);
			right.add(displayedA);
			
			JLabel blabel = new JLabel("bx:");
			blabel.setBounds(5,120,20,20);
			right.add(blabel);
			add(right);
			
			displayedB=new DigitTextField();
			displayedB.addKeyListener(this);
			//displayedB.setEditable(false);
			displayedB.setBounds(35,120,70,20);
			right.add(displayedB);
					
			displayedA.setText(""+calc.a);
			displayedB.setText(""+calc.b);
			
			JLabel a2label = new JLabel("ay:");
			a2label.setBounds(5,150,20,20);
			right.add(a2label);
			
			displayedA2=new DigitTextField();
			displayedA2.addKeyListener(this);
			//displayedA.setEditable(false);
			displayedA2.setBounds(35,150,70,20);
			right.add(displayedA2);
			
			JLabel b2label = new JLabel("by:");
			b2label.setBounds(5,180,20,20);
			right.add(b2label);

			displayedB2=new DigitTextField();
			displayedB2.addKeyListener(this);
			//displayedB.setEditable(false);
			displayedB2.setBounds(35,180,70,20);
			right.add(displayedB2);
					
			displayedA2.setText(""+calc.a2);
			displayedB2.setText(""+calc.b2);
		}


	/**
	 * @return
	 */
	private void buildRightPanel() {
		
		right=new JPanel();
		right.setLayout(null);
		right.setBounds(LEFTBORDER+WIDTH,UPBORDER,RIGHTBORDER,HEIGHT);
			
		
		JLabel rlabel = new JLabel("Displayed range:");
		rlabel.setBounds(5,60,100,20);
		right.add(rlabel);
		
		JLabel alabel = new JLabel("a:");
		alabel.setBounds(5,90,20,20);
		right.add(alabel);
		
		displayedA=new DigitTextField();
		displayedA.addKeyListener(this);
		//displayedA.setEditable(false);
		displayedA.setBounds(35,90,70,20);
		right.add(displayedA);
		
		JLabel blabel = new JLabel("b:");
		blabel.setBounds(5,120,20,20);
		right.add(blabel);
		add(right);
		
		displayedB=new DigitTextField();
		displayedB.addKeyListener(this);
		//displayedB.setEditable(false);
		displayedB.setBounds(35,120,70,20);
		right.add(displayedB);
		
		calculateIntegral=new JButton("Integral");
		calculateIntegral.setBounds(5,150,100,20);
		calculateIntegral.addActionListener(this);
		right.add(calculateIntegral);
		
		displayDerivative=new JButton("Show DF");
		displayDerivative.setBounds(5,180,100,20);
		displayDerivative.addActionListener(this);
		right.add(displayDerivative);
		
		displayedA.setText(""+calc.a);
		displayedB.setText(""+calc.b);
	}
	
	
	/**
		 * @return
		 */
		private void buildPolarRightPanel() {
		
			right=new JPanel();
			right.setLayout(null);
			right.setBounds(LEFTBORDER+WIDTH,UPBORDER,RIGHTBORDER,HEIGHT);
			
		
			JLabel rlabel = new JLabel("Diplayed range:");
			rlabel.setBounds(5,60,100,20);
			right.add(rlabel);
			
			JLabel alabel = new JLabel("teta1:");
			alabel.setBounds(5,90,40,20);
			right.add(alabel);
			
			displayedA=new DigitTextField();
			displayedA.addKeyListener(this);
			//displayedA.setEditable(false);
			displayedA.setBounds(50,90,60,20);
			right.add(displayedA);
		
			JLabel blabel = new JLabel("teta2:");
			blabel.setBounds(5,120,40,20);
			right.add(blabel);
			add(right);
			
			displayedB=new DigitTextField();
			displayedB.addKeyListener(this);
			//displayedB.setEditable(false);
			displayedB.setBounds(50,120,60,20);
			right.add(displayedB);
				
			displayedA.setText(""+calc.a);
			displayedB.setText(""+calc.b);
		}

    public void draw(){
    	
    	draw(getGraphics2D());
    }

	public void draw(Graphics2D graphics2D){
		
		
		try{
			
			Graphics2D g2=(Graphics2D) buf.getGraphics();
			
			clean(g2);
			
			readRange();
			
			g2.setColor(AXIS_COLOR);
			
			if(VISUALIZATION_STATE==CARTESIAN2D_STATE || VISUALIZATION_STATE==POLAR2D_STATE){
				
				calc.drawy0axis(g2,getWIDTH(),getHEIGHT());
				calc.drawX0axis(g2,getWIDTH(),getHEIGHT());
				
			}

			g2.setColor(LINE_COLOR);
			
			if(VISUALIZATION_STATE==CARTESIAN2D_STATE){
				
				calc.draw(g2,getWIDTH(),getHEIGHT());
				
				if(isDerivativeDisplay){
					
					
				
					g2.setColor(LINE_2_COLOR);
					
					calc.drawDerivative(g2,getWIDTH(),getHEIGHT());
				}
			}
			else if(VISUALIZATION_STATE==CARTESIAN3D_STATE){
				
								
				
				calc.draw3D(buf);
				
				
				
				
			}
			else if(VISUALIZATION_STATE==POLAR2D_STATE){
				calc.drawPolar(g2,getWIDTH(),getHEIGHT());
			
			}
			calc.setRecalculate(true);	    
			
			graphics2D.drawImage(buf,0,0,WIDTH,HEIGHT,null);
		}
		catch (Exception e) {
			e.printStackTrace();
			error("Error in parsing function,please read the README.TXT !");
			displayedFunction.setText("0");
			
			return;
		}
		
		
	}
	
	/**
	 * 
	 */
	private void readRange() {
		double a=Double.parseDouble(displayedA.getText());
		calc.a=a;
		double b=Double.parseDouble(displayedB.getText());
		calc.b=b;
		if(VISUALIZATION_STATE==CARTESIAN3D_STATE){
			double a2=Double.parseDouble(displayedA2.getText());
			calc.a2=a2;
			double b2=Double.parseDouble(displayedB2.getText());
			calc.b2=b2;
			
			
		}
		
		calc.DISPLAYED_FUNCTION=displayedFunction.getText();
		draw.requestFocus(true);
	}



	/**
	 * To redraw the graph if the panel is moved or iconified
	 *
	 **/
	public void paint(Graphics arg0) {
		super.paint(arg0);
		draw();
		
		
	}
	
    public void update(Graphics arg0) {
    	calc.setRecalculate(false);	
    	super.update(arg0);
    	    	
    }
	
	/* (non-Javadoc)
	 * @see java.awt.Component#repaint()
	 */
	public void repaint() {
		super.repaint();
	}
	
	

	/**
	 * 
	 */
	private void clean(Graphics2D g2d) {
		g2d.setColor(center.getBackground());
		g2d.fillRect(0,0,getWIDTH(),getHEIGHT());
		
	}


	/**
	 * 
	 */
	public void initialize() {
		
		
		graphics2D=(Graphics2D) center.getGraphics();
		buf=new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
	
		
	}
	
	public void loadProperties(){
		
		p=new Properties();
		try {
			p.load(new FileInputStream("mathgraphics.properties"));
			
		} catch (Exception e) {
		
			e.printStackTrace();
		}
		
	}


	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		
		Object o=arg0.getSource();
		
		if(o==draw || o==jmt2 )
			draw();
		else if (o==more){
			zoom(+1);
		}
		else if (o==less){
			
			zoom(-1);
		}
		else if (o==calculateIntegral ||o==jmt3){
			
			calculateIntegral();
		}
		else if (o==displayDerivative || o==jmt4){
			
				isDerivativeDisplay=!isDerivativeDisplay;
				if(isDerivativeDisplay) 
					{ displayDerivative.setText("No DF");
				      jmt4.setText("No DF");
				}
				else {
					displayDerivative.setText("Show DF");
					jmt4.setText("Show DF");
				}
				
				draw();
				
		}
		else if (o==jmt1) exit();
		else if(o==jmt21) {
			VISUALIZATION_STATE=CARTESIAN2D_STATE;
			calc.DISPLAYED_FUNCTION="sin(x)";
			calc.setY0(250);
			calc.setX0(50);
			displayedFunction.setText(calc.DISPLAYED_FUNCTION);
			remove(up);
			remove(right);
			buildUpPanel();
			buildRightPanel();
			jmt3.setVisible(true);
			jmt4.setVisible(true);
			setColors(p);
			repaint();
			
			
		}
		else if(o==jmt22) {
			VISUALIZATION_STATE=POLAR2D_STATE;	
			calc.DISPLAYED_FUNCTION="2";
			calc.setY0(250);
			calc.setX0(250);
			displayedFunction.setText(calc.DISPLAYED_FUNCTION);
			remove(up);
			remove(right);
			buildPolarUpPanel();
			buildPolarRightPanel();
			jmt3.setVisible(false);
			jmt4.setVisible(false);
			setColors(p);
			repaint();
		}
		else if(o==jmt23) {
			VISUALIZATION_STATE=CARTESIAN3D_STATE;	
			calc.DISPLAYED_FUNCTION="sin(x+y)";
			calc.setY0(250);
			calc.setX0(250);
			displayedFunction.setText(calc.DISPLAYED_FUNCTION);
			remove(up);
			remove(right);
			build3DUpPanel();
			build3DRightPanel();
			jmt3.setVisible(false);
			jmt4.setVisible(false);
			setColors(p);
			repaint();
		}
		else if(o==jmt31){
			selectColors();
		
			
		}
		else if(o==jmt41){
			
			saveImage();
		}
		else if(o==jmt42){
			
			exportData();
		}
		
	}

    










	private void saveImage() {
		
		
		fc.setDialogType(JFileChooser.SAVE_DIALOG);
		if(currentDirectory!=null)
			fc.setCurrentDirectory(currentDirectory);
		
		int returnVal = fc.showOpenDialog(this);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			
			currentDirectory=fc.getCurrentDirectory();
			File file = fc.getSelectedFile();
			saveImage(file);
			
		} 
		
		
	}
	
	private void saveImage(File file) {
		
		//drawFace();
		BufferedImage buf=new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
	
		try {
			Graphics2D bufGraphics=(Graphics2D)buf.getGraphics();
			draw(bufGraphics);
			ImageIO.write(buf,"jpg",file);
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}


	private void exportData() {
		
		Object fun=null;
		
		if(VISUALIZATION_STATE==CARTESIAN2D_STATE || VISUALIZATION_STATE==POLAR2D_STATE){
			fun=calc.getFunction();
		}
		else if(VISUALIZATION_STATE==CARTESIAN3D_STATE){
			
			fun=calc.getFunction3D();
			
		}
		
		ExportDataPanel edp=new ExportDataPanel(fun);
		
		
	}



	/**
	 * 
	 */
	private void selectColors() {
		
		Colorpanel cp=new Colorpanel(p);
		setColors(p);
		
		
	}



	private void setColors(Properties p2) {
		
		setInitColors(p2);
		
		center.setBackground(BACKGROUND_COLOR);
		up.setBackground(PANEL_COLOR);
		bottom.setBackground(PANEL_COLOR);
		right.setBackground(PANEL_COLOR);
		
		repaint();
	}
	
	private void setInitColors(Properties p2) {

		if(p2.getProperty("BACKGROUND_COLOR")!=null){
			BACKGROUND_COLOR=Colorpanel.buildColor(p2.getProperty("BACKGROUND_COLOR"));

		}
		else{
			p2.setProperty("BACKGROUND_COLOR",Colorpanel.decomposeColor(BACKGROUND_COLOR));
		}
		
		if(p2.getProperty("PANEL_COLOR")!=null){
			PANEL_COLOR=Colorpanel.buildColor(p2.getProperty("PANEL_COLOR"));

		}
		else{
			p2.setProperty("PANEL_COLOR",Colorpanel.decomposeColor(PANEL_COLOR));
		}
		
		if(p2.getProperty("LINE_COLOR")!=null){
			LINE_COLOR=Colorpanel.buildColor(p2.getProperty("LINE_COLOR"));

		}
		else{
			p2.setProperty("LINE_COLOR",Colorpanel.decomposeColor(LINE_COLOR));
		}
		
		if(p2.getProperty("LINE_2_COLOR")!=null){
			LINE_2_COLOR=Colorpanel.buildColor(p2.getProperty("LINE_2_COLOR"));

		}
		else{
			p2.setProperty("LINE_2_COLOR",Colorpanel.decomposeColor(LINE_2_COLOR));
		}
		if(p2.getProperty("AXIS_COLOR")!=null){
			AXIS_COLOR=Colorpanel.buildColor(p2.getProperty("AXIS_COLOR"));

		}
		else{
			p2.setProperty("AXIS_COLOR",Colorpanel.decomposeColor(AXIS_COLOR));
		}
		
		if(p2.getProperty("LINE_3D_COLOR")!=null){
			LINE_3D_COLOR=Colorpanel.buildColor(p2.getProperty("LINE_3D_COLOR"));
		}
		else{
			p2.setProperty("LINE_3D_COLOR",Colorpanel.decomposeColor(LINE_3D_COLOR));
		}

	}



	/**
	 * 
	 */
	private void exit() {
		dispose();
		System.exit(0);
		
	}



	/**
	 * 
	 */
	private void zoom(int i) {
		calc.zoom(i);
		double alfa=1.0;
		if(i>0){
			alfa=0.5;
		}
		else {
			alfa=2.0;
			
		}
		int dx=(int) ((WIDTH/2-calc.x0)*(1-1.0/alfa));
		int dy=(int) ((HEIGHT/2-calc.y0)*(1-1.0/alfa));
		calc.moveCenter(dx,dy);
		draw();
	}





	/**
	 * @return Returns the g2.
	 */
	public Graphics2D getGraphics2D() {
		return graphics2D;
	}

	/**
	 * @param g2 The g2 to set.
	 */
	public void setGraphics2D(Graphics2D g2) {
		this.graphics2D = g2;
	}

	/**
	 * @return Returns the hEIGHT.
	 */
	public int getHEIGHT() {
		return HEIGHT;
	}

	/**
	 * @param height The hEIGHT to set.
	 */
	public void setHEIGHT(int height) {
		HEIGHT = height;
	}

	/**
	 * @return Returns the wIDTH.
	 */
	public int getWIDTH() {
		return WIDTH;
	}

	/**
	 * @param width The wIDTH to set.
	 */
	public void setWIDTH(int width) {
		WIDTH = width;
	}


	
	public void keyTyped(KeyEvent arg0) {
	
		
	}
	

	public void keyPressed(KeyEvent arg0) {
		int code =arg0.getKeyCode();
		if(code==KeyEvent.VK_LEFT && !displayedFunction.hasFocus())
			left(+1);
		else if(code==KeyEvent.VK_RIGHT  && !displayedFunction.hasFocus() )
			left(-1);
		else if(code==KeyEvent.VK_UP)
							up(-1);
		else if(code==KeyEvent.VK_DOWN )
							up(+1);
		else if(code==KeyEvent.VK_D)
								draw();
		else if(code==KeyEvent.VK_PLUS && !displayedFunction.hasFocus())
								zoom(-1);
		else if(code==KeyEvent.VK_MINUS && !displayedFunction.hasFocus())
								zoom(+1);
		
	}



	public void keyReleased(KeyEvent arg0) {
	
		
	}




	/**
	 * 
	 */
	private void up(int signum) {
		calc.up(signum);
		draw();
		
	}

	/**
	 * 
	 */
	private void left(int signum) {
		calc.left(signum);
	    draw();
		
	}

	/**
	 * 
	 */
	private void calculateIntegral() {
		
		readRange();
		Integralpanel ip=new Integralpanel(calc);
		
	}



	/**
	 * @param string    
	 */
	public void error(String string) {
		JOptionPane.showMessageDialog(null,string,"Error",JOptionPane.ERROR_MESSAGE);
		
	}



	
	public void menuSelected(MenuEvent arg0) {
		redrawAfterMenu=false;	
		
	}

	public void menuDeselected(MenuEvent arg0) {
		redrawAfterMenu=true;
			
	}


	public void menuCanceled(MenuEvent arg0) {
		
	}




	public void mouseClicked(MouseEvent arg0) {
		
		Point point = arg0.getPoint();
		movetToCenter(point.x,point.y);
		/*int BORDER_LIMIT=50;
		
		Point point = arg0.getPoint();	
		if(point.y<BORDER_LIMIT) up(-1);
		if(point.y>pannel.getHeight()-BORDER_LIMIT) up(+1);
		if(point.x<BORDER_LIMIT) left(+1);
		if(point.x>pannel.getWidth()-BORDER_LIMIT) left(-1);*/
	}

	private void movetToCenter(int x, int y) {
		
		calc.moveCenter(WIDTH/2-x,HEIGHT/2-(HEIGHT-y));
		draw();
	}

	public void mouseEntered(MouseEvent arg0) {
				
	}

	public void mouseExited(MouseEvent arg0) {
		
	}



	public void mousePressed(MouseEvent arg0) {
		
		 xPressed = arg0.getX();
	     yPressed = arg0.getY();
	}



	public void mouseReleased(MouseEvent arg0) {
		
		 int x = arg0.getX();
	     int y = arg0.getY();
	     if(x-xPressed!=0 || -y+yPressed!=0 ){
		     calc.drag(x-xPressed,-y+yPressed);
		     draw();
	     }
	}


   public void mouseWheelMoved(MouseWheelEvent arg0) {
		int pix=arg0.getUnitsToScroll();
		if(pix>0) up(-1);
		else up(+1);
	}


	public void mouseDragged(MouseEvent arg0) {
	
	
	}
	
	
	public void mouseMoved(MouseEvent arg0) {
		
		Point p=arg0.getPoint();
		if(VISUALIZATION_STATE==CARTESIAN2D_STATE || VISUALIZATION_STATE==POLAR2D_STATE )
		   screenPoint.setText(calc.invertX((int)p.getX())+" ; "+calc.invertY((int)p.getY(),HEIGHT));
		else
			screenPoint.setText("");	
	}






	@Override
	public void focusGained(FocusEvent arg0) {
		
		
		Object obj = arg0.getSource();
		
	}






	@Override
	public void focusLost(FocusEvent arg0) {
		
		Object obj = arg0.getSource();

		
	}
   
   
}