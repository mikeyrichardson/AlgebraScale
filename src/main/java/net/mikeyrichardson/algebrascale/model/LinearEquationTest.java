package net.mikeyrichardson.algebrascale.model;


public class LinearEquationTest {
	public static void main(String[] args) {
		try {
			LinearEquation eq = new LinearEquation("3x * -4=5(x(23))");
			System.out.println(eq.leftSide);
			System.out.println(eq.rightSide);
		} catch (EquationFormatException e) {
			e.printStackTrace();
		}
	}
}
