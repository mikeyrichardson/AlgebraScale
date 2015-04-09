package net.mvla.la.cs.as.model;


public class LinearEquationTest {
	public static void main(String[] args) {
		try {
			LinearEquation eq = new LinearEquation("3(x-7)(4)=5(x(23))");
			System.out.println(eq.leftSide);
			System.out.println(eq.rightSide);
		} catch (EquationFormatException e) {
			e.printStackTrace();
		}
	}
}
