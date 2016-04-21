package com.main;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;


public class FunctionTextField extends JTextField{

	
	public FunctionTextField(int cols) {
		super(cols);
	}
	
	public FunctionTextField() {
			super();
		}
	
	protected Document createDefaultModel() {
		return new FunctionDocument();
	}
	
	static class FunctionDocument extends PlainDocument {
		
		public void insertString(int offs, String str, AttributeSet a) 
		throws BadLocationException {
			
			if (str == null) {
				return;
			}
			char[] upper = str.toCharArray();
			boolean isValid=true;
			
			for (int i = 0; i < upper.length; i++) {
				
				if(upper[i]=='d' || upper[i]=='D' )
					{
					isValid=false;
					break;
				}
			
			}
			if(isValid)
				super.insertString(offs, new String(upper), a);
		}
	}

}
