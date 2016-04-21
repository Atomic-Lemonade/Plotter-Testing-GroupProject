package com;



import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import com.main.Visualizer;

public class Renderer3D {
	
	public static double pi= Math.PI;
	public static double pi_2= Math.PI/2.0;
	public static double pi3_2= 3*Math.PI/2.0;
	public static double pi2= 2*Math.PI;
	
	public int WIDTH;
	public int HEIGHT;
	
	double alfa=Math.PI/4;
	double cosAlfa=Math.cos(alfa);
	double sinAlfa=Math.sin(alfa);
	
	double scale=1.0; //scale factor
	public double deltay=1;
	public double deltax=1;
	public double deltaz=1;
	
	public static int SCREEN_DISTANCE=300;
	//public int screen_distance=10;
	
	
	public int y0=250;
	public int x0=250;
	
	double fi=(1.4);//rotation angle
	double s2=Math.sqrt(2);
	
	public double lightAngleFi=fi;
	public double lightAngleTeta=Math.PI/2-0.3;
	
	
	public Point3D pAsso;
	public Point3D pLight;
	public double lightIntensity=1.0;
	
	
	public int DEPTH_DISTANCE=1000;
	
	public static ZBuffer[] zbuffer;
	public int backRgb= -1;
	
	
	public double phi=0;
	public double sinphi=Math.sin(phi);
	public double cosphi=Math.cos(phi);
	private boolean isEditor=false;
	public boolean isEditor() {
		return isEditor;
	}

	public void setEditor(boolean isEditor) {
		this.isEditor = isEditor;
	}


	private static int quickAnimation=0;//time for animations
	
	
	public void init(int WIDTH,int HEIGHT){


		this.WIDTH=WIDTH;
		this.HEIGHT=HEIGHT;
		
		zbuffer=new ZBuffer[WIDTH*HEIGHT];
		buildNewZBuffer();

		
		//direction of projection
		pAsso=new Point3D(Math.cos(alfa)/s2,Math.sin(alfa)/s2,1/s2);
		setLight();
		
	}
	
	public void init(Point3D[] ds) {
		for(int i=0;i<ds.length;i++){

			ds[i]=new Point3D();
		}

	}
	
	public void buildScreen(BufferedImage buf, ZBuffer[] zbuffer2) {

		backRgb=Visualizer.BACKGROUND_COLOR.getRGB();
		int lenght=zbuffer2.length;
		int[] rgb=new int[lenght];

		for(int i=0;i<lenght;i++){

			ZBuffer zb=zbuffer2[i];
			rgb[i]=zb.getRgbColor();
			zb.set(0,0,0,backRgb);
		}	   
		buf.getRaster().setDataElements( 0,0,WIDTH,HEIGHT,rgb); 
		//buf.setRGB(0,0,WIDTH,HEIGHT,rgb,0,WIDTH);


	}
	
	public void buildNewZBuffer() {

		backRgb=Visualizer.BACKGROUND_COLOR.getRGB();
		for(int i=0;i<zbuffer.length;i++){

			zbuffer[i]=new ZBuffer(backRgb,0);


		}

	}

	

	public void decomposeClippedPolygonIntoZBuffer(Polygon3D p3d,Color color,ZBuffer[] zbuffer,
			Point3D xDirection,Point3D yDirection,Point3D origin){


		Polygon3D clippedPolygon=p3d;
		Polygon3D[] triangles = Polygon3D.divideIntoTriangles(clippedPolygon);



		for(int i=0;i<triangles.length;i++){

			//System.out.println(p3d);
			decomposeTriangleIntoZBufferEdgeWalking( triangles[i],color,zbuffer);

		}

	}	
	
	/**
	 * 
	 * DECOMPOSE PROJECTED TRIANGLE USING EDGE WALKING 
	 * 	 * 
	 * @param p3d
	 * @param color 
	 * @param color
	 * @param texture
	 * @param useLowResolution
	 * @param xDirection
	 * @param yDirection
	 * @param origin 
	 */
	private void decomposeTriangleIntoZBufferEdgeWalking(Polygon3D p3d,Color color, ZBuffer[] zbuffer) {

		
		
		int rgbColor=-1;

		Point3D p0=new Point3D(p3d.xpoints[0],p3d.ypoints[0],p3d.zpoints[0],p3d.normals[0]);
		Point3D p1=new Point3D(p3d.xpoints[1],p3d.ypoints[1],p3d.zpoints[1],p3d.normals[1]);
		Point3D p2=new Point3D(p3d.xpoints[2],p3d.ypoints[2],p3d.zpoints[2],p3d.normals[2]);
		
		Point3D normal0=p0.normal.clone();
		Point3D normal1=p1.normal.clone();
		Point3D normal2=p2.normal.clone();
	
		 
		double x0=calcAssX(p0);
		double y0=calcAssY(p0);
		
		double z0=DEPTH_DISTANCE-Point3D.calculateDotProduct(p0,pAsso);


		double x1=calcAssX(p1);
		double y1=calcAssY(p1);
		double z1=DEPTH_DISTANCE-Point3D.calculateDotProduct(p1,pAsso);

		double x2=calcAssX(p2);
		double y2=calcAssY(p2);
		double z2=DEPTH_DISTANCE-Point3D.calculateDotProduct(p2,pAsso);

		Point3D[] points=new Point3D[3];

		points[0]=new Point3D(x0,y0,z0,p0.x,p0.y,p0.z,normal0);
		points[1]=new Point3D(x1,y1,z1,p1.x,p1.y,p1.z,normal1);
		points[2]=new Point3D(x2,y2,z2,p2.x,p2.y,p2.z,normal2);
		

		int upper=0;
		int middle=1;
		int lower=2;

		for(int i=0;i<3;i++){

			if(points[i].y>points[upper].y)
				upper=i;

			if(points[i].y<points[lower].y)
				lower=i;

		}
		for(int i=0;i<3;i++){

			if(i!=upper && i!=lower)
				middle=i;
		}


		//double i_depth=1.0/zs;
		//UPPER TRIANGLE
		double inv_up_lo_y=1.0/(points[upper].y-points[lower].y);
		double inv_up_mi_y=1.0/(points[upper].y-points[middle].y);
		double inv_lo_mi_y=1.0/(points[lower].y-points[middle].y);

		for(int j=(int) points[middle].y;j<points[upper].y;j++){

			double middlex=Point3D.foundXIntersection(points[upper],points[lower],j);
			double middlex2=Point3D.foundXIntersection(points[upper],points[middle],j);
			
			

			double l1=(j-points[lower].y)*inv_up_lo_y;
			double l2=(j-points[middle].y)*inv_up_mi_y;
			double zs=l1*points[upper].z+(1-l1)*points[lower].z;
			double ze=l2*points[upper].z+(1-l2)*points[middle].z;
			
			Point3D norm1=interpolatePhongNormal(points[upper].normal,points[lower].normal,l1);
			Point3D norm2=interpolatePhongNormal(points[upper].normal,points[middle].normal,l2);
			
			Point3D pstart=new Point3D(middlex,j,zs,norm1);
			Point3D pend=new Point3D(middlex2,j,ze,norm2);

			if(pstart.x>pend.x){

				Point3D swap= pend.clone();
				pend= pstart.clone();
				pstart=swap;

			}

			int start=(int)pstart.x;
			int end=(int)pend.x;

			double inverse=1.0/(end-start);

			for(int i=start;i<end;i++){

				//System.out.print(i+" "+j);
				
				if(i<0 || j<0 )
    				continue;
    			
    			if(i>=WIDTH || j>= HEIGHT)
    				break;

				double l=(i-start)*inverse;
				
				
				double zi=(1-l)*pstart.z+l*pend.z;
				int tot=WIDTH*j+i;
				
				ZBuffer zb=zbuffer[tot];
				
				if(!zb.isToUpdate(zi))
					continue;

				
				//Phong normal
				
				double nx=l*pend.normal.x+(1-l)*pstart.normal.x;
				double ny=l*pend.normal.y+(1-l)*pstart.normal.y;
				double nz=l*pend.normal.z+(1-l)*pstart.normal.z;
				
				//Point3D normi = interpolatePhongNormal(pend.normal,pstart.normal,l);
				
				rgbColor=calculateShadowColor(nx,ny,nz,color);

				//System.out.println(x+" "+y+" "+tot);

				

				zb.set(i,zi,j,rgbColor);

			}


		}


		//LOWER TRIANGLE
		for(int j=(int) points[lower].y;j<points[middle].y;j++){

			double middlex=Point3D.foundXIntersection(points[upper],points[lower],j);


			double middlex2=Point3D.foundXIntersection(points[lower],points[middle],j);

			double l1=(j-points[lower].y)*inv_up_lo_y;
			double l2=(j-points[middle].y)*inv_lo_mi_y;
			double zs=l1*points[upper].z+(1-l1)*points[lower].z;
			double ze=l2*points[lower].z+(1-l2)*points[middle].z;
			
			Point3D norm1=interpolatePhongNormal(points[upper].normal,points[lower].normal,l1);
			Point3D norm2=interpolatePhongNormal(points[lower].normal,points[middle].normal,l2);

			Point3D pstart=new Point3D(middlex,j,zs,norm1);
			Point3D pend=new Point3D(middlex2,j,ze,norm2);

			if(pstart.x>pend.x){


				Point3D swap= pend.clone();
				pend= pstart.clone();
				pstart=swap;

			}

			int start=(int)pstart.x;
			int end=(int)pend.x;

			double inverse=1.0/(end-start);

			for(int i=start;i<end;i++){

				if(i<0 || j<0 )
    				continue;
    			
    			if(i>=WIDTH || j>= HEIGHT)
    				break;

				double l=(i-start)*inverse;
			

				double zi=(1-l)*pstart.z+l*pend.z;

				int tot=WIDTH*j+i;
				ZBuffer zb=zbuffer[tot];
				
				if(!zb.isToUpdate(zi))
					continue;
				
				//System.out.println(x+" "+y+" "+tot);

				
				//Phong normal
				
				double nx=l*pend.normal.x+(1-l)*pstart.normal.x;
				double ny=l*pend.normal.y+(1-l)*pstart.normal.y;
				double nz=l*pend.normal.z+(1-l)*pstart.normal.z;
				
				//Point3D normi = interpolatePhongNormal(pend.normal,pstart.normal,l);
				
				rgbColor=calculateShadowColor(nx,ny,nz,color);

				zb.set(i,zi,j,rgbColor);

			}


		}	




	}

	
	public void drawAxes() {
		
		int length=50;
		int redRgb = Color.red.getRGB();
		
		//y axis
		decomposeLine(0,0,0,0,0,length,zbuffer,redRgb);
		//x axis
		decomposeLine(0,0,0,0,length,0,zbuffer,redRgb);
		//z axis
		decomposeLine(0,0,0,length,0,0,zbuffer,redRgb);
		
	}
	
	public void decomposeLine(double x1,
			double y1,double z1,double x2,double y2,double z2,ZBuffer[] zbuffer,int rgbColor) {

		int xx1=(int)calcAssX(x1/deltax,y1/deltay,z1/deltaz);
		int yy1=(int)calcAssY(x1/deltax,y1/deltay,z1/deltaz);

		int xx2=(int)calcAssX(x2/deltax,y2/deltay,z2/deltaz);
		int yy2=(int)calcAssY(x2/deltax,y2/deltay,z2/deltaz);



		if(yy1!=yy2){

			double i_yy=1.0/(yy2-yy1);

			if(yy2>yy1)

				for (int yy = yy1; yy <= yy2; yy++) {

					double l=(yy-yy1)*i_yy;

					int xx=(int) (xx2*l+xx1*(1-l));

					double zi=z2*l+z1*(1-l);
					
					if(xx<0 || yy<0 )
	    				continue;
	    			
	    			if(xx>=WIDTH || yy>= HEIGHT)
	    				break;

					int tot=WIDTH*yy+xx;

					ZBuffer zb=zbuffer[tot];

					if(!zb.isToUpdate(zi))
						continue;			

					zb.set(xx,yy,zi,rgbColor);
				}
			else
				for (int yy = yy2; yy <= yy1; yy++) {

					double l=-(yy-yy2)*i_yy;

					int xx=(int) (xx1*l+xx2*(1-l)); 

					double zi=z1*l+z2*(1-l);

					int tot=WIDTH*yy+xx;
					
					if(xx<0 || yy<0 )
	    				continue;
	    			
	    			if(xx>=WIDTH || yy>= HEIGHT)
	    				break;

					ZBuffer zb=zbuffer[tot];

					if(!zb.isToUpdate(zi))
						continue;			


					zb.set(xx,yy,zi,rgbColor);
				}

		}
		else if(xx1!=xx2){
			
			double i_xx=1.0/(xx2-xx1);

			if(xx2>xx1)
				for (int xx = xx1; xx <= xx2; xx++) {

					double l=(xx-xx1)*i_xx;
					double zi=z2*l+z1*(1-l);

					int yy=(int) (yy2*l+yy1*(1-l));

					if(yy<0 || xx<0 )
	    				continue;
	    			
	    			if(yy>=HEIGHT || xx>= WIDTH)
	    				break;
					
					int tot=WIDTH*yy+xx;

					ZBuffer zb=zbuffer[tot];

					if(!zb.isToUpdate(zi))
						continue;


					zb.set(xx,yy,zi,rgbColor);
				}
			else
				for (int xx = xx2; xx <= xx1; xx++) { 

					double l=-(xx-xx2)*i_xx;
					double zi=z1*l+z2*(1-l);

					int yy=(int) (yy1*l+yy2*(1-l));
					
					if(yy<0 || xx<0 )
	    				continue;
	    			
	    			if(yy>=HEIGHT || xx>= WIDTH)
	    				break;

					int tot=WIDTH*yy+xx;

					ZBuffer zb=zbuffer[tot];

					if(!zb.isToUpdate(zi))
						continue;


					zb.set(xx,yy,zi,rgbColor);
				}

		}
		else {
			
			if(yy1<0 || xx1<0 )
				return;
			
			if(yy1>=HEIGHT || xx1>= WIDTH)
				return;

			int tot=WIDTH*yy1+xx1;

			ZBuffer zb=zbuffer[tot];

			if(!zb.isToUpdate(z1))
				return;


			zb.set(xx1,z1,yy1,rgbColor);

		}


	}
	

	
	public static Point3D interpolatePhongNormal(Point3D normal0,Point3D normal1,double l){
		
		
		Point3D newNormal= new Point3D(
				
				l*normal0.x+(1-l)*normal1.x,
				l*normal0.y+(1-l)*normal1.y,
				l*normal0.z+(1-l)*normal1.z
		);
		
				
		return newNormal.calculateVersor();
	}
	
	public void normalsCalculus(Vector points, Vector<LineData> polygonData) {

		Hashtable vnormals=new Hashtable();
		
		for(int l=0;l<polygonData.size();l++){

			LineData ld=polygonData.elementAt(l);

			for(int i=0;i<ld.size();i++){

				int index=ld.getIndex(i);

				Vector lineDataNormals=(Vector) vnormals.get(""+index);
				if(lineDataNormals==null){
					lineDataNormals=new Vector();
					vnormals.put(""+index,lineDataNormals);
				}
				lineDataNormals.add(ld);
			}

		}

		//mix the normal

		Enumeration itera = vnormals.keys();

		while(itera.hasMoreElements()){

			String key=(String) itera.nextElement();
			int index=Integer.parseInt(key);
			Vector lineDataNormals=(Vector) vnormals.get(""+index);

			double x=0;
			double y=0;
			double z=0;

			for(int l=0;l<lineDataNormals.size();l++){

				LineData ld=(LineData) lineDataNormals.elementAt(l);
				int position=ld.positionOf(index);
				Point3D ldNormal=getNormal(position,ld,points);
				x+=ldNormal.x;
				y+=ldNormal.y;
				z+=ldNormal.z;

			}
			((Point3D)points.elementAt(index)).setNormal(new Point3D(x/lineDataNormals.size()
					,y/lineDataNormals.size(),
					z/lineDataNormals.size())
			);

		}

	}
	
	private static Point3D getNormal(int position, LineData ld,
			Vector points) {

		int n=ld.size();


		Point3D p0=(Point3D) points.elementAt(ld.getIndex((n+position-1)%n));
		Point3D p1=(Point3D) points.elementAt(ld.getIndex(position));
		Point3D p2=(Point3D) points.elementAt(ld.getIndex((1+position)%n));

		Point3D normal=Point3D.calculateCrossProduct(p1.substract(p0),p2.substract(p1));

		return normal.calculateVersor();
	}
	

	float[] hsbColor=new float[3];
	

	
	private int calculateShadowColor(double nx,double ny,double nz, Color color) {

        int argbs=color.getRGB();

		double factor= lightIntensity*(0.75+0.25*Point3D.calculateDotProduct(nx,ny,nz,pLight));
		//System.out.println(Point3D.calculateDotProduct(normal,pLight));
		int alphas=0xff & (argbs>>24);
		int rs = 0xff & (argbs>>16);
		int gs = 0xff & (argbs >>8);
		int bs = 0xff & argbs;

		rs=(int) (factor*rs);
		gs=(int) (factor*gs);
		bs=(int) (factor*bs);

		return alphas <<24 | rs <<16 | gs <<8 | bs;

	}
	
	public Line2D.Double newLine(double x1,double y1,double z1,double x2,double y2,double z2){
		double xx1=calcAssX(x1,y1,z1);
		double yy1=calcAssY(x1,y1,z1);
		double xx2=calcAssX(x2,y2,z2);
		double yy2=calcAssY(x2,y2,z2);
		return new Line2D.Double(xx1,yy1,xx2,yy2);	
	}

	public Line2D.Double newLine(Point3D p1,Point3D p2){

		return newLine(p1.z,p1.x,p1.y,p2.z,p2.x,p2.y);	
	}
	
	public double calcAssX(Point3D p){


		return calcAssX(p.x,p.y,p.z);
	}
	public double calcAssY(Point3D p){


		return calcAssY(p.x,p.y,p.z);
	}

	
	private double calcAssX(double x, double y, double z) {
		 
		return (y-x*sinAlfa)+x0;
	}
	
	private double calcAssY(double x, double y, double z) {
		 
			return HEIGHT-((z-x*cosAlfa)+y0);
	}
	
	public void translate(int i, int j) {
		x0=x0+10*i;
		y0=y0+10*j;
	}
	

	public void zoomOut(){
		
		deltay=deltax=deltax*1.25;
	
		
	}
	
	public void zoomIn(){
		
		if(deltax==0.5)
			return;
		deltay=deltax=deltax/1.25;
	
		
	}
	
	public void translateLight(int i, int j) {
		
		lightAngleFi+=0.1*i;
		lightAngleTeta+=0.1*j;
		if(lightAngleTeta<0)
			lightAngleTeta=0;
		else if(lightAngleTeta>Math.PI)
			lightAngleTeta=Math.PI;
		
		setLight();
		
	}
	
	public void setLight(){
		
		pLight=new Point3D(Math.cos(lightAngleFi)*Math.sin(lightAngleTeta),-Math.cos(lightAngleTeta),-Math.sin(lightAngleFi)*Math.sin(lightAngleTeta));
	}
	
	public void changeLightIntensity(double di){
		
		if(di<0){
			
			if(lightIntensity+di>=0)
				lightIntensity=lightIntensity+di;
			
		}
		else if(di>0){
			
			if(lightIntensity+di<=1)
				lightIntensity=lightIntensity+di;
		}
		
	}
	
	public double[][] getRotationMatrix(Point3D versor,double teta){

		return getRotationMatrix(versor.x,versor.y,versor.z,teta);
	}

	/**
	 * 
	 * 
	 *  ROTATION MATRIX OF TETA AROUND (X,Y,Z) AXIS
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param teta
	 * @return
	 */



	public double[][] getRotationMatrix(double x,double y,double z,double teta){

		double[][] matrix=new double[3][3];
		matrix[0][0]=Math.cos(teta)+x*x*(1.0-Math.cos(teta));
		matrix[0][1]=y*x*(1.0-Math.cos(teta))-z*Math.sin(teta);
		matrix[0][2]=z*x*(1.0-Math.cos(teta))+y*Math.sin(teta);
		matrix[1][0]=y*x*(1.0-Math.cos(teta))+z*Math.sin(teta);
		matrix[1][1]=Math.cos(teta)+y*y*(1.0-Math.cos(teta));
		matrix[1][2]=z*y*(1.0-Math.cos(teta))-x*Math.sin(teta);
		matrix[2][0]=z*x*(1.0-Math.cos(teta))-y*Math.sin(teta);
		matrix[2][1]=z*y*(1.0-Math.cos(teta))+x*Math.sin(teta);
		matrix[2][2]=Math.cos(teta)+z*z*(1.0-Math.cos(teta));
		return matrix;
	} 





	
	public void lessFi(){fi=fi-.1f;}
	public void moreFi(){fi=fi+.1f;}


	public void moreAlfa() {

		alfa+=0.1;
		cosAlfa=Math.cos(alfa);
		sinAlfa=Math.sin(alfa);

	}


	public void lessAlfa() {
		alfa-=0.1;
		cosAlfa=Math.cos(alfa);
		sinAlfa=Math.sin(alfa);

	}
	


	public void reset() {
		// TODO Auto-generated method stub
		
	}

	public void loadFormat(Properties p) {
		
		
	}

	public void saveFormat(PrintWriter pw) {
		// TODO Auto-generated method stub
		
	}

	public void buildMovements(Vector keyPressed) {
		// TODO Auto-generated method stub
		
	}
	
	
	public void rotateView(int versus){
		
		if(versus>0)		
			phi+=.1;
		else
			phi+=-.1;
		
		sinphi=Math.sin(phi);
		cosphi=Math.cos(phi);
	}

	public int getY0() {
		return y0;
	}

	public void setY0(int y0) {
		this.y0 = y0;
	}

	public int getX0() {
		return x0;
	}

	public void setX0(int x0) {
		this.x0 = x0;
	}
	
	


}
