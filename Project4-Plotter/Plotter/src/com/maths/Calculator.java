package com.maths;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import com.LineData;
import com.Point3D;
import com.Polygon3D;
import com.Renderer3D;
import com.ZBuffer;
import com.main.Visualizer;


public class Calculator extends Renderer3D{
	
    double dx=0;
	double dx1=0;
	double dx0=0;
    //double deltax=0.01;//scala :quanto vale un intervallo sull'asse x


	public double alfa=Math.PI/4;
	public double sinAlfa=Math.sin(alfa);
	public double cosAlfa=Math.cos(alfa);
	public double a=0;//start
	public double b=6.29;//end
	
	public double a2=a;
	public double b2=b;
	
	int n=1000;//precision
	int ny=30;//precision
	int nx=30;//precision
	//private ParseFunction pf;
	private MathTree mathTree=null;
	private double[] fun;
	private double[] dfun;
	private double[][] fun3D;
	boolean recalculate=true;
	DecimalFormat df=new DecimalFormat("##.##");
	
		

	public Calculator(int WIDTH, int HEIGHT) {
		
		super();
		deltax=10.0/Visualizer.WIDTH;
	    deltay=deltax;
	    deltaz=deltax;
		init( WIDTH, HEIGHT);
	}
	
	public double[][] getFunction(){
		
		dx=(b-a)/(n-1);//incremento d'intervallo
		
		double[][] ret_fun = new double[n][2];
		
		for(int k=0;k<n;k++)
		{		  
			double x=a+k*dx;
			ret_fun[k][0]=x;
			ret_fun[k][1]=f(x); 
		}
		 
		return ret_fun; 
	}
	
	public double[][][] getFunction3D(){
		
		dx1=(b2-a2)/(ny-1);
		dx0=(b-a)/(nx-1);
		
		double[][][] ret_fun = new double[nx][ny][3];
		
		mathTree=new MathTree(DISPLAYED_FUNCTION.substring(0));
		
		
		for(int k=0;k<nx;k++)
		{		  
			double x=a+k*dx0;
			for(int j=0;j<ny;j++){
				double y=a2+j*dx1;
				
				 ret_fun[k][j][0]=x;
				 ret_fun[k][j][1]=y;
				 ret_fun[k][j][2]=f(x,y);			
			} 	
		}
		
		return ret_fun;
		
	}

	/**
	 * @param graphics2D
	 */
	public void draw(Graphics2D graphics2D, int i, int j) {
		
		dx=(b-a)/(n-1);//incremento d'intervallo
		mathTree=new MathTree(DISPLAYED_FUNCTION.substring(0));
		if(recalculate)
		 fun=calculateFunction();
		plotFunction(fun,graphics2D,  i,  j);
		
	}
	
	public void draw3D(BufferedImage buf) {
		
		dx1=(b2-a2)/(ny-1);
		dx0=(b-a)/(nx-1);
		
		mathTree=new MathTree(DISPLAYED_FUNCTION.substring(0));
		
		if(recalculate)
		 fun3D=calculateFunction3D();
		 plotFunction3D(fun3D,buf);
		
	}
	
	private void plotFunction(double[] fun2, Graphics2D graphics2D, int i, int j) {
		
//		deltay=deltax=i*1.0/n;
		
		for(int k=0;k<n;k++)
		  {
		  
			double x=a+k*dx;
			double y=fun2[k];
			
		  	int cx=(int)(x/deltax)+x0;
		  	int cy=(int)(y/deltay)+y0;
		  	
			if(cy<j && cx<i && cx>=0 && cy>=0)
				graphics2D.drawOval(cx,j-cy,1,1);
		  } 
	
		
	}
	
	private void plotFunction3D(double[][] fun2, BufferedImage buf) {
		
		//		deltay=deltax=i*1.0/n;
		
			
		Vector points=new Vector();
		Vector lines=new Vector();
		points.setSize(nx*ny);
		
		for(int k=0;k<nx;k++)
		{
			
			double x=a+k*dx0;

			
			for(int l=0;l<ny;l++){
				
				double y=a2+l*dx1;
	
				
				double z=fun2[k][l];

				Point3D p= new Point3D(x/deltax,y/deltay,z/deltaz);
	
				int pos00=pos(k,l,nx,ny);
				
				points.setElementAt(p,pos00);
				
				if(k<nx-1 && l<ny-1){
					
					LineData ld=new LineData();
					
					int pos10=pos(k+1,l,nx,ny);
					int pos11=pos(k+1,l+1,nx,ny);
					int pos01=pos(k,l+1,nx,ny);
					
					ld.addIndex(pos00);
					ld.addIndex(pos10);
					ld.addIndex(pos11);
					ld.addIndex(pos01);
					
					lines.add(ld);
				}
	

			}				
			
		}
		
		drawAxes();
		normalsCalculus(points,lines);
		
		//for(int index=120;index<121+0*lines.size();index++){
		
		for(int index=0;index<lines.size();index++){
			
			LineData ld=(LineData) lines.elementAt(index);
			Polygon3D p3d=LineData.buildPolygon(ld,points);
			
			decomposeClippedPolygonIntoZBuffer(p3d, Visualizer.LINE_3D_COLOR, zbuffer,null,null,null);

			
		}
		buildScreen(buf,zbuffer); 
		
	}
	
	public int pos(int i,int j,int nx,int ny){
		
		return nx*j+i;
		
	}

	private double[] calculateFunction() {
		double[] ret_fun = new double[n];
		
		
		for(int k=0;k<n;k++)
		{		  
			double x=a+k*dx;
			ret_fun[k]=f(x); 
		}
		return ret_fun;
	}
	
	private double[][] calculateFunction3D() {
		double[][] ret_fun = new double[nx][ny];
		
		
		for(int k=0;k<nx;k++)
		{		  
			double x=a+k*dx0;
			for(int j=0;j<ny;j++){
				double y=a2+j*dx1;
				  ret_fun[k][j]=f(x,y);			
			} 	
		}
		return ret_fun;
	}
	
	private double[] calculateDerivativeFunction() {
		double[] ret_fun = new double[n];
		
		
		for(int k=0;k<n;k++)
		{		  
			double x=a+k*dx;
			ret_fun[k]=AdvancedCalculator.df(x,this); 
		}
		return ret_fun;
	}

	/**
	 * @param graphics2D
	 */
	public void drawPolar(Graphics2D graphics2D, int i, int j) {
		
		dx=(b-a)/(n-1);//incremento d'intervallo
		mathTree=new MathTree(DISPLAYED_FUNCTION.substring(0));
		if(recalculate)
			 fun=calculateFunction();
		//deltay=deltax=i*1.0/n;
		
		for(int k=0;k<n;k++)
		 {
			  
				double x=a+k*dx;
				double y=fun[k];
				
				int cx=(int)((y*Math.cos(x))/deltax)+x0;
				int cy=(int)((y*Math.sin(x))/deltay)+y0;
			  	
				if(cy<j && cx<i && cx>=0 && cy>=0)
					graphics2D.drawOval(cx,j-cy,1,1);
		 } 
	  	  
				
	}
	
	
	public void drawDerivative(Graphics2D graphics2D, int i, int j) {
		
		dx=(b-a)/n;//incremento d'intervallo
		mathTree=new MathTree(DISPLAYED_FUNCTION.substring(0));
		
		if(recalculate)
			 dfun=calculateDerivativeFunction();
		
		//deltay=deltax=i*1.0/n;
		
			for(int k=0;k<n;k++)
			  {
			  
				double x=a+k*dx;
				double y=dfun[k];
				
				int cx=(int)(x/deltax)+x0;
				int cy=(int)(y/deltay)+y0;
			  	
				if(cy<j && cx<i && cx>=0 && cy>=0)
					graphics2D.drawOval(cx,j-cy,1,1);
			  } 
		
		  	  
				
	}
	
	/**
	 * @param graphics2D
	 * @param i
	 * @param j
	 */
	public void drawX0axis(Graphics2D graphics2D, int i, int j) {
		
		
		if(y0>=0 && y0<j)
			graphics2D.drawLine(0,j-y0,i,j-y0);
		
		
		

		
		double xa=deltax*(-x0);
		double xb=deltax*(i-x0);
		
		double dl=i*deltax/10.0;
		
			
		for(double x=0;x<xb;x=x+dl){
						
			int cx=(int)(x/deltax)+x0;
			
						
			if( cx<i && cx>=0 ){
				graphics2D.drawString(""+df.format(x),cx,j-y0);
				graphics2D.drawLine(cx,0,cx,j);
			} 		
		}
		
		for(double x=0;x>xa;x=x-dl){
						
					int cx=(int)(x/deltax)+x0;
			
						
					if( cx<i && cx>=0 ){
						graphics2D.drawString(""+df.format(x),cx,j-y0);
						graphics2D.drawLine(cx,0,cx,j);
					} 		
		}
		
		
	}
	
	public void drawy0axis(Graphics2D graphics2D, int i, int j) {
		
		if(y0>=0 && y0<j)
			graphics2D.drawLine(x0,0,x0,j);
		
		DecimalFormat df=new DecimalFormat("##.##");
		

		
				double ya=deltay*(-y0);
				double yb=deltay*(j-y0);
		
				double dl=i*deltay/10.0;
		
				for(double y=0;y<yb;y=y+dl){
						
					int cy=(int)(y/deltay)+y0;
			
						
					if( cy<j && cy>=0 ){
						graphics2D.drawString(""+df.format(y),x0,j-cy);
						graphics2D.drawLine(0,j-cy,i,j-cy);
					} 		
				}
				
				for(double y=0;y>ya;y=y-dl){
						
							int cy=(int)(y/deltay)+y0;
			
						
							if( cy<j && cy>=0 ){
								graphics2D.drawString(""+df.format(y),x0,j-cy);
								graphics2D.drawLine(0,j-cy,i,j-cy);
							} 		
						}
		
			
		
		
		
	}
	




	/**
	 * This is the funcion diplayed
	 * @param x
	 * @return
	 */
	public String DISPLAYED_FUNCTION="sin(x)";
	
	
	public double f(double x) {
		
		
		
		String sx=MathTree.formatVal(x);
		
				  
		
		
		/*sfunction=sfunction.replaceAll("exp","esp");
		sfunction=sfunction.replaceAll("x",sx);
		sfunction=sfunction.replaceAll("teta",sx);
		sfunction=sfunction.replaceAll("esp","exp");*/
		
		
		
		double val=0;
		
		val=mathTree.evaluate(x, 0);
		
		

		return val;
		
		//return (Math.sin(x));
	}
	
		public double f(double x,double y) {
		
		
		
		String sx=MathTree.formatVal(x);
		String sy=MathTree.formatVal(y);
				  
		
		/*sfunction=sfunction.replaceAll("exp","esp");
		sfunction=sfunction.replaceAll("x",sx);
		sfunction=sfunction.replaceAll("y",sy);
		sfunction=sfunction.replaceAll("esp","exp");*/
		
		
		
		double val=0;
		
		val=mathTree.evaluate(x, y);
		
		

		return val;
		
		//return (Math.sin(x));
	}

	/**
	 * 
	 */
	public void zoom(int signum) {
		
		recalculate=false;
		if(signum>0){
		 deltax*=0.5;
		} 
		else{
		 deltax/=0.5;
		} 
		deltay=deltax;
	}



	/**
	 * 
	 */
	public void left(int signum) {
		recalculate=false;
		x0+=(int)(signum*0.1/deltax);
	}
	
	/**
	 * 
	 */
	public void drag(int xdrag,int ydrag) {
		recalculate=false;
		x0+=xdrag;
		y0+=ydrag;
	}

	/**
	 * 
	 */
	public void up(int signum) {
		recalculate=false;
		y0+=signum*10;
	}
	
	public void moveCenter(int dx, int dy) {
		x0+=dx;
		y0+=dy;
		
	}


	public boolean isRecalculate() {
		return recalculate;
	}

	public void setRecalculate(boolean recalculate) {
		this.recalculate = recalculate;
	}

	public String invertY(int y,int j) {
		
		return df.format((j-y-y0)*deltay);
	  	
	}

	public String invertX(int x) {
	  		  	
	  	return df.format((x-x0)*deltax);
	}








}
