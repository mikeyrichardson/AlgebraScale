package net.mikeyrichardson.algebrascale.model;

import java.util.ArrayList;

import org.apache.commons.math3.fraction.Fraction;

public class AlgebraScaleModel {
	
//	public static void main(String[] args) {
//		//Testing out the model
//		AlgebraScaleModel model;
//		try {
//		model = new AlgebraScaleModel(new LinearEquation("x=7"));
//		} catch (EquationFormatException e) {
//			return;
//		}
//		model.addPiece(10, 1, .25, .75);
//		model.addPiece(4,0,.75,.25);
//		System.out.println(model.weightDifference());
//		System.out.print(model.equation);
//	}

	public ArrayList<Piece> pieces;
	public LinearEquation equation;
	public Fraction solution;
	
	public AlgebraScaleModel(LinearEquation equation) {
		pieces = new ArrayList<Piece>();
		this.equation = equation;
		solution = equation.solution();
	}
	
	public void addPiece(int value, int degree, double relX, double relY) {
		pieces.add(new Piece(value, degree, relX, relY));
	}
	

	
	/**
	 * Returns the weight difference between sides of the scale. Positive if left side weighs more.
	 * Negative if right side weighs more.
	 */
	public double weightDifference() {
		Fraction leftSideWeight = new Fraction(0);
		Fraction rightSideWeight = new Fraction(0);
		for (Piece piece : pieces) {
			if (piece.getRelativeX() < 0.5) {
				if (piece.getDegree() == 0)
					leftSideWeight = leftSideWeight.add(piece.getValue());
				else
					leftSideWeight = leftSideWeight.add(solution.multiply(piece.getValue()));
			}
			else {
				if (piece.getDegree() == 0)
					rightSideWeight = rightSideWeight.add(piece.getValue());
				else
					rightSideWeight = rightSideWeight.add(solution.multiply(piece.getValue()));
			}
		}

		return leftSideWeight.subtract(rightSideWeight).doubleValue();
	}

	
	public Fraction getSolution() {
		return solution;
	}

}
