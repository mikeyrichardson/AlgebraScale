package net.mvla.la.cs.as.model;

public class Piece {
	
	//Each piece is meant to represent a term in an equation.
	//A piece has value and degree.
	//It also stores position of the piece since I didn't want to have to track it in the GUI.
	//It is an awful breech of MVC.
	
	public final static int POS_CONST = 1;
	public final static int NEG_CONST = 2;
	public final static int POS_VAR = 3;
	public final static int NEG_VAR = 4;
	public final static int OTHER = 5;
	
	private int value;
	private int degree;
	private double relativeX; //a number from 0 to 1 giving relative horizontal location
	private double relativeY; //a number from 0 to 1 giving relative vertical location
	
	
	public Piece(int value, int degree, double relX, double relY) {
		this.value = value;
		this.degree = degree;
		relativeX = relX;
		relativeY = relY;
	}
		
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public int getDegree() {
		return degree;
	}
	
	public void setDegree(int degree) {
		this.degree = degree;
	}
	
	public double getRelativeX() {
		return relativeX;
	}
	
	
	//pieces are not allowed to have positions off the screen
	//actual position is found by multiplying by the actual width or height of the GUI
	public void setRelativeX(double relX) {
		if (relX > 1)
			relativeX = 1;
		else if (relX < 0)
			relativeX = 0;
		else
			relativeX = relX;
	}
	
	public double getRelativeY() {
		return relativeY;
	}
	
	public void setRelativeY(double relY) {
		if (relY > 1)
			relativeY = 1;
		else if (relY < 0)
			relativeY = 0;
		else
			relativeY = relY;
	}
	
	public int getType() {
		if (value > 0) {
			if (degree == 1)
				return POS_VAR;
			else if (degree == 0)
				return POS_CONST;
		}
		if (value < 0) {
			if (degree == 1)
				return NEG_VAR;
			else if (degree == 0)
				return NEG_CONST;
		}
		return OTHER;
	}
	
	public String toString() {
		String str = "" + value;
		if (degree == 1)
			str += "x";
		return str;
	}
	
}
