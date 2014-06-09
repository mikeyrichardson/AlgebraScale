package net.mvla.la.cs.as.model;
import org.apache.commons.math3.fraction.Fraction;

public class LinearEquation {
	
	int leftVariableCoefficient = 0;
	int leftConstant = 0;
	int rightVariableCoefficient = 0;
	int rightConstant = 0;
	
	public LinearEquation() {
		
	}
	
	public LinearEquation(String equation) throws EquationFormatException {
		equation = equation.replace("X", "x");
		equation = equation.replace(" ", "");
		equation = equation.replace("-", "+-");
		String[] splitEquation = equation.split("=");

		if (splitEquation.length != 2)
			throw new EquationFormatException("Equation must contain one equals sign and an expresion on each side.");
		
		for (int i = 0; i < 2; i++) {
			while (splitEquation[i].contains("(")) {
				int leftParenIndex = splitEquation[i].indexOf('(');
				int rightParenIndex = splitEquation[i].indexOf(')');
				if (rightParenIndex == -1 || rightParenIndex == leftParenIndex + 1)
					throw new EquationFormatException("Parentheses must be balanced and contain an expression.");
				if (leftParenIndex == 0) {
					splitEquation[i] = splitEquation[i].substring(1,rightParenIndex) + splitEquation[i].substring(rightParenIndex + 1);
					continue;
				}
				int numIndex = leftParenIndex;
				while (numIndex != 0 && splitEquation[i].charAt(numIndex - 1) != '+' ) {
					numIndex--;
				}
				if (numIndex == leftParenIndex) {
					splitEquation[i] = splitEquation[i].substring(0,leftParenIndex) + splitEquation[i].substring(leftParenIndex+1, rightParenIndex) + splitEquation[i].substring(rightParenIndex + 1);

					continue;
				}
				int coefficient;
				try {
					coefficient = Integer.parseInt(splitEquation[i].substring(numIndex, leftParenIndex));
				} catch (Exception ex) {
					throw new EquationFormatException("The equation does not make sense.");
				}
				String replacementString = "";
				for (int j = 0; j < coefficient; j++) {
					replacementString += splitEquation[i].substring(leftParenIndex + 1, rightParenIndex) + "+";
				}
				replacementString = replacementString.substring(0, replacementString.length() - 1);
				splitEquation[i] = splitEquation[i].substring(0,numIndex) + replacementString + splitEquation[i].substring(rightParenIndex + 1);
				
			}
		}
		
		
		String[] leftSide = splitEquation[0].split("\\+");
		String[] rightSide = splitEquation[1].split("\\+");
		
		try {
		for (int i = 0; i < leftSide.length; i++) {
			if (leftSide[i].equals(""))
				continue;
			if (leftSide[i].contains("x")) {
				if (leftSide[i].equals("x"))
					leftVariableCoefficient += 1;
				else if (leftSide[i].equals("-x"))
					leftVariableCoefficient += -1;
				else
					leftVariableCoefficient += Integer.parseInt(leftSide[i].substring(0,leftSide[i].length() - 1));
			}
			else
				leftConstant += Integer.parseInt(leftSide[i]);
		}
		
		for (int i = 0; i < rightSide.length; i++) {
			if (rightSide[i].equals(""))
				continue;
			if (rightSide[i].contains("x")) {
				if (rightSide[i].equals("x"))
					rightVariableCoefficient += 1;
				else if (rightSide[i].equals("-x")) {
					rightVariableCoefficient += -1;
				}
				else {
					rightVariableCoefficient += Integer.parseInt(rightSide[i].substring(0,rightSide[i].length() - 1));
				}
			}
			else
				rightConstant += Integer.parseInt(rightSide[i]);
			}
		}
		catch (Exception ex) {
			throw new EquationFormatException("The equation doesn't make sense.");
		}
	}
	
	public LinearEquation(int a, int b, int c, int d) {
		leftVariableCoefficient = a;
		leftConstant = b;
		rightVariableCoefficient = c;
		rightConstant = d;
	}
	
	
	public Fraction leftSideValue(Fraction possibleSolution) {
		return possibleSolution.multiply(leftVariableCoefficient).add(leftConstant);
	}
	

	
	public Fraction rightSideValue(Fraction possibleSolution) {
		return possibleSolution.multiply(rightVariableCoefficient).add(rightConstant);
	}
	

	
	public Fraction solution() throws InfiniteSolutionsException, NoSolutionException {
		if (leftVariableCoefficient - rightVariableCoefficient == 0 && rightConstant - leftConstant == 0)
			throw new InfiniteSolutionsException();
		if (leftVariableCoefficient - rightVariableCoefficient == 0 && rightConstant - leftConstant != 0)
			throw new NoSolutionException();
		if (rightConstant - leftConstant == 0)
			return new Fraction(0);
		return new Fraction(rightConstant - leftConstant,leftVariableCoefficient - rightVariableCoefficient);
	}
	
	public String toString() {
		return "" + this.leftVariableCoefficient + "x+" + this.leftConstant + "=" 
				+ this.rightVariableCoefficient + "x+" + this.rightConstant;
	}
}
