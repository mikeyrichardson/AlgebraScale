package net.mvla.la.cs.as.model;


public class TestLinearEquation {
	public static void main(String[] args) {
		try {
			LinearEquation eq = new LinearEquation("2x+3=-7x-3");
			System.out.println(eq.leftVariableCoefficient);
			System.out.println(eq.leftConstant);
			System.out.println(eq.rightVariableCoefficient);
			System.out.println(eq.rightConstant);
			System.out.println(eq.solution());
		} catch (EquationFormatException e) {
			System.out.println("Error");
		}
	}
}
