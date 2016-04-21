package com.maths;

public class AdvancedCalculator {

	
	
	
	public AdvancedCalculator(){};
	
	public static double df(double x,Calculator calc){
		
			double total=0;
		
			
			double dx=0.001;
		    double i_2dx=500;
			
		    //total=(calc.f(x+dx)-calc.f(x-dx))/(2*dx);		
		    total=(calc.f(x+dx)-calc.f(x-dx))*i_2dx;	
			return total;
	
	}
	
	public static double SimpsonIntegral(Calculator calc){
		
		double total=0;
		
		double x1=calc.a;
		double x2=calc.b;
		
		int num=1000;
		
		double dx=(x2-x1)/num;
		double dx_6=dx/6.0;
		
		for(double x=x1;x<x2;x=x+dx){
		
		  //total+=dx*(calc.f(x)+calc.f(x+dx)+4*calc.f((x*2+dx)/2.0))/6.0;
		 total+=calc.f(x)+calc.f(x+dx)+4*calc.f((x*2+dx)*0.5);
		}
		
		
		return total*dx_6;
	
	}
	
	
	public static double trapeziumIntegral(Calculator calc){
		
		double total=0;
		
		double x1=calc.a;
		double x2=calc.b;
		
		int num=1000;
		
		double dx=(x2-x1)/num;
		double dx_2=dx/2;
		
		for(double x=x1;x<x2;x=x+dx){
		
		  //total+=dx*(calc.f(x+dx)-calc.f(x-dx))/2;	
			total+=(calc.f(x+dx)+calc.f(x));
		}
		
		
		return total*dx_2;
	
	}

	public static double gaussIntegral(Calculator calc) {
		
		double total=0;
		
		double x1=calc.a;
		double x2=calc.b;
		
		int num=1000;
		
		double dx=(x2-x1)/num;
		double dx_2=dx/2;
		
		double[] xrg={-Math.sqrt((35+2*Math.sqrt(70))/63.0),
				-Math.sqrt((35-2*Math.sqrt(70))/63.0),
				0,
				Math.sqrt((35-2*Math.sqrt(70))/63.0),
				Math.sqrt((35+2*Math.sqrt(70))/63.0)};
		
		double[] bg={(-455+161*Math.sqrt(70))/(450*Math.sqrt(70)),
				(455+161*Math.sqrt(70))/(450*Math.sqrt(70)),
				128.0/225.0,
				(455+161*Math.sqrt(70))/(450*Math.sqrt(70)),
				(-455+161*Math.sqrt(70))/(450*Math.sqrt(70))
		
		};
		
		for(double x=x1;x<x2;x=x+dx){
		
	       for(int j=0;j<xrg.length;j++){
	    	   
	    	   double xg=xrg[j]*(dx)*0.5+(2*x+dx)*0.5;
	    	   total+=bg[j]*calc.f(xg);
	       }
		}
		
		return total*dx_2;
	}
}
