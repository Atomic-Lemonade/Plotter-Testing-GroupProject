package com.maths;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Vector;


public class ParseFunction {
	
	public static final String SIN="sin";
	public static final String COS="cos";
	public static final String TAN="tan";
	public static final String ASIN="asin";
	public static final String ACOS="acos";
	public static final String ATAN="atan";
	public static final String SINH="sinh";
	public static final String COSH="cosh";
	public static final String TANH="tanh";
	public static final String LOG="log";
	public static final String EXP="exp";
	public static final String ABS="abs";
	public static final String SQRT="sqrt";
	
	public String function="";
	
	public Vector recordedFunctions=new Vector();
	
		
	public static String[] FZ={"(",SIN+"(",COS+"(",TAN+"(",LOG+"(",EXP+"(",
		ASIN+"(",ACOS+"(",ATAN+"(",
		SINH+"(",COSH+"(",TANH+"(",ABS+"(",SQRT+"("
	};

	public static void main(String[] args) {
		
	
		
		
		ParseFunction dc=new ParseFunction();
		String sfunction="2*(2+3*(1.0+1))+(5+6*(1-2/(1+1)))-1";
		sfunction="sin(0.000001)";
		sfunction="2*-2";
		
		dc.setFunction(sfunction);
		    
	   
		System.out.println( dc.parseFunction());
			//System.out.println(1+Math.sin(3+4*Math.cos(3)));
	
		
	}
	
	public double parseFunction() {
		
		
		if(recordedFunctions.size()==0)
			decomposeFunction();
		else 
			decomposeFunctionWithRegister();
		
		double result=calculateArgument(function);
		return result;
	}

	/**
	 * @param function
	 */
	private void decomposeFunction()  {

		String text="";
		int indx0=-1;


		//System.out.println("statFunction:"+function);

		for(int i=0;i<function.length();i++){
			char let=function.charAt(i);

			text+=let;

			if(let==')') {

				int indxOpen=function.indexOf("(",indx0);
				String function_name=function.substring(indx0,indxOpen);
				recordedFunctions.add(function_name+"(");
				function=replaceFunction(function,indx0, i);
				break;
			}
			else if(let=='(') for(int j=0;j<FZ.length;j++)
			{

				if(FZ[j].equals(text.toString())){
					indx0=i-FZ[j].length()+1;
					text="";
					break;
				} 

			}
			else if(let==MULTIPLY || let==DIVIDE || let==POWER || 
					let==MINUS || let==SUM ||let==' ' ) 
				text="";



		}  
		if(isDecomposable(function))
			decomposeFunction();




	}
	
	private void decomposeFunctionWithRegister()  {
		
		String text="";
		int indx0=-1;
		
			
		//System.out.println("statFunction:"+function);
		
		for(int i=0;i<recordedFunctions.size();i++){
			String funct=(String)recordedFunctions.elementAt(i);
			
			int indx1=function.indexOf(")");
			indx0=function.lastIndexOf(funct,indx1);
			
			function=replaceFunction(function,indx0, indx1);
		
		}  
		
	
		
	}

	/**
	 * @param text
	 * @param fz2
	 */
	private String replaceFunction(String text, int indx0,int end) {
		
		//System.out.println(text.substring(indx0,end+1));
		int indxOpen=text.indexOf("(",indx0);
		
		String function_name=text.substring(indx0,indxOpen);
		String argument=text.substring(indxOpen+1,end);
		

		
		
		//System.out.println("f:"+function_name);
		//System.out.println("argument:"+argument);
		
		double val=0;
		val=calculateParentheses(argument); 
		
		val=calculateFunction( val,function_name);
		
					
		return text.substring(0,indx0)+formatVal(val)+text.substring(end+1);
		
	}
	
	private double calculateFunction(double val,String fz){
		
		double result=val;
		
		if(SIN.equals(fz)) result= Math.sin(val);
		else if(COS.equals(fz)) result= Math.cos(val);
		else if(TAN.equals(fz)) result= Math.tan(val);
		else if(EXP.equals(fz)) result= Math.exp(val);
		else if(LOG.equals(fz)) result= Math.log(val);
		else if(COSH.equals(fz)) result= Math.cosh(val);
		else if(SINH.equals(fz)) result= Math.sinh(val);
		else if(TANH.equals(fz)) result= Math.tanh(val);
		else if(ACOS.equals(fz)) result= Math.acos(val);
		else if(ASIN.equals(fz)) result= Math.asin(val);
		else if(ATAN.equals(fz)) result= Math.atan(val);
		else if(ABS.equals(fz)) result= Math.abs(val);
		else if(SQRT.equals(fz)) result= Math.sqrt(val);

		return result;
	}
	
	public static String formatVal(double numb){
		
		String pow="";
		
			
		NumberFormat df =NumberFormat.getInstance(Locale.US);
		df.setMaximumFractionDigits(6);
		df.setGroupingUsed(false);
		pow=df.format(numb);
		
		return pow;
	
	
	}
	
	private boolean isDecomposable(String function){
		
		if(function.indexOf("(")>=0) return true;

		return false;
	}



	public static final char X='x';
	public static final char MULTIPLY='*';
	public static final char SUM='+';
	public static final char MINUS='-';
	public static final char DIVIDE='/';
	public static final char POWER='^';
	


	/**
	 * @param molt
	 * @return
	 */
	
	private double calculateParentheses(String molt) {
		
		
		int indx=0;
		if(molt.length()==0) return 0;
		//molt=molt.replaceAll(" ","");
		return calculateArgument(molt);
		
		
	}	

	/**
	 * @param molt
	 * @return
	 */
	private String simplifyPowers(String molt) {
		
		
        while(molt.indexOf(POWER)>=0){
        
          int indx=molt.indexOf(POWER);
          
          int prev=Math.max(molt.lastIndexOf(SUM,indx),molt.lastIndexOf(MULTIPLY,indx));
		  prev=Math.max(prev,molt.lastIndexOf(DIVIDE,indx));
		 
		  if(molt.lastIndexOf(MINUS,indx)>prev+1)
		  	prev=Math.max(prev,molt.lastIndexOf(MINUS,indx));
          
          int next=molt.length();
                
		 
          if(molt.indexOf(SUM,indx)>0)next=Math.min(next,molt.indexOf(SUM,indx));
		  if(molt.indexOf(MULTIPLY,indx)>0)next=Math.min(next,molt.indexOf(MULTIPLY,indx));
		  if(molt.indexOf(DIVIDE,indx)>0)next=Math.min(next,molt.indexOf(DIVIDE,indx));
		  if(molt.indexOf(POWER,indx+1)>0)next=Math.min(next,molt.indexOf(POWER,indx+1));
		  if(molt.indexOf(MINUS,indx)>0)
		  	if(molt.indexOf(MINUS,indx)>indx+1)
		  	next=Math.min(next,molt.indexOf(MINUS,indx));
		 
		  
     
                        
          double base=Double.parseDouble(molt.substring(prev+1,indx));
          
          double exp=Double.parseDouble(molt.substring(indx+1,next));
          
		  double val=Math.pow(base,exp);
		  
		 
          
          molt=molt.substring(0,prev+1)+formatVal(val)+molt.substring(next);
          
          //System.out.println(molt);
        
        }
		
		
				
		return molt;
	}

	/**
	 * @param molt
	 * @return
	 */
	
	private double calculateArgument(String molt) {
		
		char signum=MULTIPLY;
		char signumSum=SUM;
		double result=1;
		double total=0;
		String number="";
		
		molt=simplifyPowers(molt);
		
		for(int i=0;i<molt.length();i++){
			
			char first=molt.charAt(i);
			
			if(i==molt.length()-1 )//end of the string
			{	
				number+=first;
				result=addValue(result,signum,Double.parseDouble(number));
			}
			else if(first==MULTIPLY || first==DIVIDE){
				result=addValue(result,signum,Double.parseDouble(number));
				signum=first;
				number="";
			}
			else if((first==SUM || first==MINUS) && number.length()>0){
				
				
				result=addValue(result,signum,Double.parseDouble(number));
				
				
				total=addValue(total,signumSum,result);
				
				signumSum=first;
				signum=MULTIPLY;
				result=1;
				
				number="";
			}
			else number+=first;	
			
			
		}
		total=addValue(total,signumSum,result);		
		
		
		return total;
	}




	/**
	 * @param result
	 * @param signum
	 * @param partial
	 * @return
	 */
	private double addValue(double result, char signum, double partial) {
		
		
		
		if(signum==MULTIPLY)
			result*=partial;
		else if(signum==DIVIDE)
			result/=partial;
		else if(signum==SUM)
					result+=partial;
		else if(signum==MINUS)
					result-=partial;
				
		return result;
	}


	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}





}
