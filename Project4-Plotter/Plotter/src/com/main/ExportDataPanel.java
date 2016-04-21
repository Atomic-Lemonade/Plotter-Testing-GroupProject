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
				
					
					fc.setDialogType(JFileChooser.OPEN_DIALOG);
					fc.setDialogTitle("Load format  ");
					
					if(filePath.getText()!=null)
					{
						File file=new File(filePath.getText());
						fc.setCurrentDirectory(file);
						
					}	
			
					int returnVal = fc.showOpenDialog(null);
					
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fc.getSelectedFile();
						
						if(file==null) return;
						
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




	private void init() {
		
		v_separator=",";
		d_separator="\n";
	}




	
	
	

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




	private void exit() {
		dispose();
		
	}




	private void save() {

		PrintWriter pw=null;

		try{
			
			v_separator=values_separator.getText();
			
			if(v_separator==null || v_separator.equals(""))
				v_separator=",";
			
			
			d_separator=data_separator.getText();
			
			if(d_separator==null || d_separator.equals(""))
				d_separator="\n";
			
			if(filePath.getText()==null || filePath.getText().equals(""))
			{	
			
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
			
			e.printStackTrace();
			String msg="Error while trying to save file";
			JOptionPane.showMessageDialog(this,msg,"Error",JOptionPane.ERROR_MESSAGE);
		}
		finally{
			if(pw!=null)
				pw.close();
		}
		
		
		
	}




	private String decomposeFunction3D(double[] data) {
		return data[0]+v_separator+data+v_separator+data[1]+v_separator+data[2]+d_separator;
		
	}




	private String decomposeFunction(double[] data) {
		
		return data[0]+v_separator+data[1]+d_separator;
	}

}
