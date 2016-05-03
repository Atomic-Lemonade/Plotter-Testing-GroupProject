package com.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Creates a dialog to store graph point data in a text file.
 */
public class ExportDataPanel extends JDialog implements ActionListener{
	
	int HEIGHT=200;
	int WIDTH=400;	

	private JPanel pan;
	private JButton save=null;
	private JButton exit=null;
	
	JTextField filePath=null;
	private JButton loadFile;
	private JFileChooser fc;
	
	Object function=null;
	private JTextField values_separator;
	private String v_separator;
	private String d_separator;
	private JTextField data_separator;
	
	/**
	 * Creates an export data panel with the graphed function.
	 * 
	 * @param fun the Object that represents the function being graphed
	 */
	public ExportDataPanel(Object fun) {
		
		init();
		function=fun;
		
		setBounds(30,30,WIDTH,HEIGHT);
		setTitle("Export panel");
		setModal(true);
		
		pan=new JPanel();
		pan.setLayout(null);
        pan.setBackground(Visualizer.BACKGROUND_COLOR);
                
        int r=10;
        
        JLabel jlb=new JLabel("File:");
        jlb.setBounds(10,r,60,20);
        pan.add(jlb);
        
        filePath=new JTextField();
        filePath.setBounds(80,r,250,20);
		add(filePath);					
		
		loadFile=new JButton(">");
		loadFile.setBounds(330,r,50,20);	
		add(loadFile);
		
		fc=new JFileChooser();
		
		loadFile.addActionListener(
				
			new ActionListener() {
				
				public void actionPerformed(ActionEvent arg0) {									
					fc.setDialogType(JFileChooser.SAVE_DIALOG);
					fc.setDialogTitle("Save to...  ");
					FileFilter txtImage = new FileNameExtensionFilter(".txt", "txt");
					fc.addChoosableFileFilter(txtImage);
					fc.setFileFilter(txtImage);
					fc.setAcceptAllFileFilterUsed(false);
					
					if(filePath.getText()!=null){
						File file=new File(filePath.getText());
						fc.setCurrentDirectory(file);						
					}	
			
					int returnVal = fc.showSaveDialog(null);
					
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fc.getSelectedFile();
						/**Sets the file extension to .txt*/
						file = new File(file.toString() + ".txt");
						filePath.setText(file.getAbsolutePath());								
					} 					
				}			
			}					
		);
		
		r+=30;
		
		jlb=new JLabel("Values separator");
		jlb.setBounds(10,r,100,20);
        pan.add(jlb);
        
    	values_separator=new JTextField();
    	values_separator.setText(",");
    	values_separator.setBounds(120,r,50,20);
        pan.add(values_separator);				
        
		r+=30;
		
		jlb=new JLabel("Data separator");
		jlb.setBounds(10,r,100,20);
        pan.add(jlb);
        
    	data_separator=new JTextField();
    	data_separator.setBounds(120,r,50,20);
        pan.add(data_separator);
		
		r+=30;
        
        save=new JButton("Save");
        save.addActionListener(this);
        
		save.setBounds(10,r,80,20);
        pan.add(save);
        
        exit=new JButton("Exit");
        exit.addActionListener(this);
        
        exit.setBounds(100,r,80,20);
        pan.add(exit);
        
        add(pan);
        
        setVisible(true);     
	}

	/**
	 * Sets default point delimiters.
	 */
	private void init() {		
		v_separator=",";
		d_separator="\n";
	}

	/**
	 * Sets save and exit actions with their respective buttons.
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		Object obj = arg0.getSource();
		
		if(obj==save){			
			save();
		}
		else if(obj==exit){			
			exit();
		}		
	}

	/**
	 * Closes export data window.
	 */
	private void exit() {
		dispose();	
	}

	/**
	 * Saves the function's point data in a text file.
	 */
	private void save() {
		PrintWriter pw=null;
		boolean displaySaveSuccessful = true;
		
		try{		
			v_separator=values_separator.getText();
			
			if(v_separator==null || v_separator.equals(""))
				v_separator=",";			
			
			d_separator=data_separator.getText();
			
			if(d_separator==null || d_separator.equals(""))
				d_separator="\n";
			
			if(filePath.getText()==null || filePath.getText().equals(""))
			{	
				displaySaveSuccessful = false;
				String msg="Missing file path!";
				JOptionPane.showMessageDialog(this,msg,"Error",JOptionPane.ERROR_MESSAGE);
			    
				return;
			}
			pw=new PrintWriter(new File(filePath.getText()));
			
			if(function instanceof double[][]){							
				double[][] fun=(double[][]) function;				
				
				for(int i=0;i<fun.length;i++){					
					String str=decomposeFunction(fun[i]);
					pw.print(str);					
				}				
			}
			
			else if(function instanceof double[][][]){								
				double[][][] fun=(double[][][]) function;
				
				for(int i=0;i<fun.length;i++){					
					double[][] inner_fun=fun[i];					
					
					for (int j = 0; j < inner_fun.length; j++) {						
						String str=decomposeFunction3D(inner_fun[j]);
						pw.print(str);
					}
				}
			}
			
		}catch (Exception e) {
			displaySaveSuccessful = false;
			e.printStackTrace();
			String msg="Error while trying to save file";
			JOptionPane.showMessageDialog(this,msg,"Error",JOptionPane.ERROR_MESSAGE);
		}
		finally{
			if(pw!=null)
				pw.close();
			if(displaySaveSuccessful)
			{
				String msg="Save successful";
				JOptionPane.showMessageDialog(this,  msg, "Success!", 1);				
			}
			displaySaveSuccessful=true;
		}		
	}

	/**
	 * Converts the point data of a 3D graph into a string.
	 * 
	 * @param data the double array containing a 3D graph's point data
	 * @return a String containing the point data of a 3D graph
	 */
	private String decomposeFunction3D(double[] data) {
		return data[0]+v_separator+data+v_separator+data[1]+v_separator+data[2]+d_separator;		
	}

	/**
	 * Converts the point data of the 2D graph into a string
	 *  
	 * @param data the double array containing the 2D graph's point data
	 * @return a String containing the point data of a 2D graph
	 */
	private String decomposeFunction(double[] data) {	
		return data[0]+v_separator+data[1]+d_separator;
	}
}