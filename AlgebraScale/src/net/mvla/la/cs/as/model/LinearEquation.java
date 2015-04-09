package net.mvla.la.cs.as.model;

import org.apache.commons.math3.fraction.Fraction;

public class LinearEquation {
	
	LinearExpression leftSide;
	LinearExpression rightSide;
	
	public LinearEquation(String equation) throws EquationFormatException {
		
		String[] splitEquation = equation.split("=");
		if (splitEquation.length != 2)
			throw new EquationFormatException("Equation must contain one equals sign and an expresion on each side.");

		try {
			leftSide = new LinearExpression(splitEquation[0]);
		}
		catch (LinearExpressionFormatException e) {
			throw new EquationFormatException("Left Side Error: " + e.getMessage());
		}
		catch (Exception e) {
			throw new EquationFormatException("The left side of the equation is formatted incorrectly.");
		}
		try {
			rightSide = new LinearExpression(splitEquation[1]);
		}
		catch (LinearExpressionFormatException e) {
			throw new EquationFormatException("Right Side Error: " + e.getMessage());
		}
		catch (Exception e) {
			throw new EquationFormatException("The right side of the equation is formatted incorrectly.");
		}

	}
	
	public LinearEquation(int a, int b, int c, int d) {
		leftSide = new LinearExpression(a,b);
		rightSide = new LinearExpression(c,d);
	}
	
	public Fraction leftSideValue(Fraction possibleSolution) {
		return possibleSolution.multiply(leftSide.getVariableCoefficient()).add(leftSide.getConstant());
	}
	
	public Fraction rightSideValue(Fraction possibleSolution) {
		return possibleSolution.multiply(rightSide.getVariableCoefficient()).add(rightSide.getConstant());
	}
	

	
	public Fraction solution() throws InfiniteSolutionsException, NoSolutionException {
		if (leftSide.getVariableCoefficient() - rightSide.getVariableCoefficient() == 0 && rightSide.getConstant() - leftSide.getConstant() == 0)
			throw new InfiniteSolutionsException();
		if (leftSide.getVariableCoefficient() - rightSide.getVariableCoefficient() == 0 && rightSide.getConstant() - leftSide.getConstant() != 0)
			throw new NoSolutionException();
		if (rightSide.getConstant() - leftSide.getConstant() == 0)
			return new Fraction(0);
		return new Fraction(rightSide.getConstant() - leftSide.getConstant(),leftSide.getVariableCoefficient() - rightSide.getVariableCoefficient());
	}
	
	public String toString() {
		return "" + leftSide + " = " + rightSide;
	}
}
