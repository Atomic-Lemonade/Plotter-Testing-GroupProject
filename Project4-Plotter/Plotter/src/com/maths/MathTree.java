package com.maths;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.tree.TreeNode;

import org.w3c.dom.Node;


public class MathTree {

	TNode root=null;

	public String SUM_LABEL="+";
	public String SUBTRACTION_LABEL="-";
	public String MULTIPLICATION_LABEL="*";
	public String DIVISION_LABEL="/";
	public String POWER_LABEL="^";
	public String X_LABEL="X";
	public String Y_LABEL="Y";
	public String TETA_LABEL="TETA";

	public String[] FUNCTIONS={"","SQRT","SIN","COS","TAN","EXP","LOG","LN","ABS","ASIN","ACOS","ATAN","SINH","COSH","TANH"};


	public static void main(String[] args) {

		MathTree te=new MathTree();

		//String words="(x+((y+z)+k)";
		String words="-(-3+5)^2";
		//words="2*(2+3*(1.0+1))+(5+6*(1-2/(1+1)))-1";
		//String words="2*3-4*1/5";

		te.buildStringTree(words);
		te.printTree();
		double sum=te.evaluate(2,1);

		System.out.println(sum);
	}



	public MathTree() {
		super();
	}


	public MathTree(String words) {
		buildStringTree(words);
	}

	public double evaluate(double x,double y) {

		double sum=evaluate(root,x,y);	

		

		return sum;
	}

	public double evaluate(TNode node,double x,double y) {


		double val=0;

		if(node.getChildCount()==0){

			String value=(String) node.getValue();
			if(value==null || value.equals(""))
				return 0;

			if(value.equals(X_LABEL) || value.equals(TETA_LABEL))
				return x;

			if(value.equals(Y_LABEL))
				return y;

			val= Double.parseDouble(value);
			return val;

		}	
		else{


			if(node.getLabel().equals(SUM_LABEL)){

				val=0;



				TNode child0 = node.getChildAt(0);
				TNode child1 = node.getChildAt(1);
				val=evaluate(child0,x,y)+evaluate(child1,x,y);

			}
			else if(node.getLabel().equals(SUBTRACTION_LABEL)){

				TNode child0 = node.getChildAt(0);
				TNode child1 = node.getChildAt(1);
				val=evaluate(child0,x,y)-evaluate(child1,x,y);
			}
			else if(node.getLabel().equals(MULTIPLICATION_LABEL)){

				TNode child0 = node.getChildAt(0);
				TNode child1 = node.getChildAt(1);
				val=evaluate(child0,x,y)*evaluate(child1,x,y);
			}
			else if(node.getLabel().equals(DIVISION_LABEL)){

				TNode child0 = node.getChildAt(0);
				TNode child1 = node.getChildAt(1);
				val=evaluate(child0,x,y)/evaluate(child1,x,y);

			}
			else if(node.getLabel().equals(POWER_LABEL)){

				TNode child0 = node.getChildAt(0);
				TNode child1 = node.getChildAt(1);
				val=Math.pow(evaluate(child0,x,y),evaluate(child1,x,y));

			}
			else if(node.getLabel().indexOf("F:")==0){

				String fnz=node.getLabel().substring(node.getLabel().indexOf(":")+1);
				TNode child0 = node.getChildAt(0);


				if(fnz.equals(""))
				{
					val=evaluate(child0,x,y);


				}
				else if(fnz.equals("SQRT"))
				{
					val=Math.sqrt(evaluate(child0,x,y));


				}
				else if(fnz.equals("SIN"))
				{
					val=Math.sin(evaluate(child0,x,y));


				}
				else if(fnz.equals("COS"))
				{
					val=Math.cos(evaluate(child0,x,y));


				}
				else if(fnz.equals("TAN"))
				{
					val=Math.tan(evaluate(child0,x,y));


				}
				else if(fnz.equals("EXP"))
				{
					val=Math.exp(evaluate(child0,x,y));


				}
				else if(fnz.equals("LN"))
				{
					val=Math.log(evaluate(child0,x,y));


				}
				else if(fnz.equals("LOG"))
				{
					val=Math.log10(evaluate(child0,x,y));


				}
				else if(fnz.equals("ABS"))
				{
					val=Math.abs(evaluate(child0,x,y));


				}
				else if(fnz.equals("ASIN"))
				{
					val=Math.asin(evaluate(child0,x,y));


				}
				else if(fnz.equals("ACOS"))
				{
					val=Math.acos(evaluate(child0,x,y));


				}
				else if(fnz.equals("ATAN"))
				{
					val=Math.atan(evaluate(child0,x,y));


				}
				else if(fnz.equals("SINH"))
				{
					val=Math.sinh(evaluate(child0,x,y));


				}
				else if(fnz.equals("COSH"))
				{
					val=Math.cosh(evaluate(child0,x,y));


				}
				else if(fnz.equals("TANH"))
				{
					val=Math.tanh(evaluate(child0,x,y));


				}

			}

		}
		return val;

	}

	public void printTree() {

		//root.printTree();
		printBranchesWithLabel(root);
	}

	public void buildStringTree(String words) {

		root=new TNode();
		words=words.toUpperCase();
		words=words.replaceAll(" ", "");
		//words=words.replaceAll("-", "+-1*");
		//words=words.replaceAll("-", "+-");
		root.setValue(words);
		root.setLabel(SUM_LABEL);

		buildStringTree(root);
	}

	public void buildStringTree(TNode node) {


		String value=(String)node.getValue();

		String fnz=null;
		if((fnz=evaluateFunction(value))!=null){

			value=value.substring(fnz.length()+1,value.length()-1);
			node.setLabel("F:"+fnz);
			TNode child=new TNode();
			child.setValue(value);
			node.appendChild(child);
			buildStringTree(child);
		}
		else if(value.indexOf(SUM_LABEL)>=0 && canDecomposeBySymbol(value,SUM_LABEL)) {


			node.setLabel(SUM_LABEL);
			Vector tokens=decomposeBySymbol(value,SUM_LABEL);
			appendChildren(node,tokens);


		}
		else if(value.indexOf(SUBTRACTION_LABEL)>=0 && canDecomposeBySymbol(value,SUBTRACTION_LABEL)) {


			node.setLabel(SUBTRACTION_LABEL);
			Vector tokens=decomposeBySymbol(value,SUBTRACTION_LABEL);
			appendChildren(node,tokens);
		}
		else if(value.indexOf(MULTIPLICATION_LABEL)>=0 && canDecomposeBySymbol(value,MULTIPLICATION_LABEL)  ){

			node.setLabel(MULTIPLICATION_LABEL);

			Vector tokens=decomposeBySymbol(value,MULTIPLICATION_LABEL);
			appendChildren(node,tokens);
		}
		else if(value.indexOf(DIVISION_LABEL)>=0 && canDecomposeBySymbol(value,DIVISION_LABEL)  ){

			node.setLabel(DIVISION_LABEL);

			Vector tokens=decomposeBySymbol(value,DIVISION_LABEL);
			appendChildren(node,tokens);
		}
		else if(value.indexOf(POWER_LABEL)>=0 && canDecomposeBySymbol(value,POWER_LABEL)  ){

			node.setLabel(POWER_LABEL);

			Vector tokens=decomposeBySymbol(value,POWER_LABEL);
			appendChildren(node,tokens);
		}
	}

	public String evaluateFunction(String value) { 


		int depth=1;
		boolean isFunction=true;

		String function=null;

		for(int i=0;i<FUNCTIONS.length;i++){

			String fnz=FUNCTIONS[i];
			if(value.indexOf(fnz+"(")==0 && value.lastIndexOf(")")==value.length()-1 )
			{ 
				function=fnz;
				break;
			}
		}

		if(function==null)
			return null;

		for(int i=function.length()+1;i<value.length()-1;i++){


			char ch=value.charAt(i);


			if(ch=='('){

				depth++;
			}	
			else if(ch==')')
				depth--;

			if(depth==0){

				isFunction=false;
				break;
			}

		}
		if(isFunction)
			return function;
		else
			return null;

	}

	public void appendChildren(TNode node, Vector tokens) {

		for (int i = 0; i < tokens.size(); i++) {

			String tkn=(String) tokens.elementAt(i);

			TNode child=new TNode();
			child.setValue(tkn);
			
			node.appendChild(child);
			buildStringTree(child);
			
		}

	}



	public boolean canDecomposeBySymbol(String value, String symbol) {



		int depth=0;

		for(int i=0;i<value.length();i++){


			char ch=value.charAt(i);


			if(ch==symbol.charAt(0) && depth==0){
				
				//check for consecutive symbols
				if(i>0){
					
					String previous=""+value.charAt(i-1);
					
					if(previous.equals(MULTIPLICATION_LABEL ) || previous.equals(DIVISION_LABEL ) || previous.equals(POWER_LABEL )  )
						continue;
				}
				
				return true;
			}	
			else if(ch=='(')
				depth++;
			else if(ch==')')
				depth--;
		}

		return false;
	}

	public Vector decomposeBySymbol(String value, String symbol) {
		Vector tokens=new Vector();

		int depth=0;
		String current="";

		for(int i=0;i<value.length();i++){


			char ch=value.charAt(i);


			if(ch==symbol.charAt(0) && depth==0)
			{

				tokens.add(value.substring(0,i));
				tokens.add(value.substring(i+1));
				current="";
				break;
			}
			else if(ch=='('){
				current+=ch;
				depth++;
			}	
			else if(ch==')'){
				current+=ch;
				depth--;
			}	
			else current+=ch;
		}

		if(current.length()>0)
			tokens.add(current);

		return tokens;

	}

	public void printBranchesWithLabel(TNode root) {

		if(root.getChildCount()==0)
		{
			Vector nodes=new Vector();

			nodes.add(root);
			TNode currentNode=root;
			TNode parent=null;

			while((parent=currentNode.getParent())!=null){

				nodes.add(parent);
				currentNode=parent;
			}
			System.out.println();
			for (int i = nodes.size()-1; i >=0; i--) {

				TNode member=(TNode) nodes.elementAt(i);
				System.out.print("("+member.getLabel()+")"+member.toString());
				if(i>0)
					System.out.print("->");
			}
			System.out.println();
		}



		for (int i = 0; i <   root.getChildCount(); i++) {

			TNode child = root.getChildAt(i);
			printBranchesWithLabel(child);


		}

	}

	public static String formatVal(double numb){
		
		String pow="";
		
			
		NumberFormat df =NumberFormat.getInstance(Locale.US);
		df.setMaximumFractionDigits(6);
		df.setGroupingUsed(false);
		pow=df.format(numb);
		
		return pow;
	
	
	}


}
