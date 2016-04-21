package com;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class ZBuffer{

		int rgbColor;
		double z=0;
		
		public double p_x=0;
		public double p_y=0;
		public double p_z=0;

		
		public int getRgbColor() {
			return rgbColor;
		}
		public void setRgbColor(int rgbColor) {
			this.rgbColor = rgbColor;
		}
		public double getZ() {
			return z;
		}
		public void setZ(double z) {
			this.z = z;
		}
		
		public ZBuffer(int rgbColor, double z) {
			super();
			this.rgbColor = rgbColor;
			this.z = z;
		}
		public ZBuffer() {
			super();
		}
		
		public static Color  fromHexToColor(String col){



			int r=Integer.parseInt(col.substring(0,2),16);
			int g=Integer.parseInt(col.substring(2,4),16);
			int b=Integer.parseInt(col.substring(4,6),16);

			Color color=new Color(r,g,b);

			return color;
		}

		public static String fromColorToHex(Color col){

			String exe="";

			exe+=addZeros(Integer.toHexString(col.getRed()))
					+addZeros(Integer.toHexString(col.getGreen()))
					+addZeros(Integer.toHexString(col.getBlue()));

			return exe;

		}


		public static String addZeros(String hexString) {
			
			if(hexString.length()==1)
				return "0"+hexString;
			else 
				return hexString;
		}
		
		
		public void update(double xs,double ys,double zs, int rgbColor) {
			
			
			if(getZ()==0 ||  getZ()>ys ){

				setZ(ys);
				setRgbColor(rgbColor);
				
				p_x=xs;
				p_y=ys;
				p_z=zs;

			}

		}

		public boolean isToUpdate(double ys){


			return getZ()==0 ||  getZ()>ys;
		}	

		public void set(double xs,double ys,double zs, int rgbColor) {

			setZ(ys);
			setRgbColor(rgbColor);

			p_x=xs;
			p_y=ys;
			p_z=zs;
		}

}
