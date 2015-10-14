package net.mikeyrichardson.algebrascale.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinearExpression {
	
	private int variableCoefficient;
	private int constant;
	private String expression;
	private int index = 0;
	
	public LinearExpression(int variableCoefficient, int constant) {
		this.variableCoefficient = variableCoefficient;
		this.constant = constant;
	}
	
	public LinearExpression(String expression) throws LinearExpressionFormatException {
		expression = expression.replace("X", "x");
		expression = expression.replace(" ", "");
		for (int i = 1; i < expression.length() - 1; i++) {
			if (expression.charAt(i) == '(' && 
				(Character.isDigit(expression.charAt(i - 1)) || expression.charAt(i - 1) == 'x'))
				expression = expression.substring(0, i) + "*" + expression.substring(i);
			else if (expression.charAt(i) == ')' && 
					(Character.isDigit(expression.charAt(i + 1)) || expression.charAt(i + 1) == 'x'
					 || expression.charAt(i + 1) == '('))
					expression = expression.substring(0, i + 1) + "*" + expression.substring(i + 1);
		}

		if (expression.contains("."))
			throw new LinearExpressionFormatException("Expression can only contain integer coefficients (no decimals).");
		if (expression.contains("/"))
			throw new LinearExpressionFormatException("Expression can only contain integer coefficients (no fractions).");
		Pattern forbiddenCharactersPattern = Pattern.compile("([^x+-\\\\*()0-9])");
		Matcher m = forbiddenCharactersPattern.matcher(expression);
		if (m.find())
			throw new LinearExpressionFormatException("Expression contains an illegal character: " + m.group(0));
		this.expression = expression;
		LinearExpression ex = expressionValue();
		if (index < expression.length())
			throw new LinearExpressionFormatException("Extra characters at end of expression: " 
					+ expression.substring(index));
		this.variableCoefficient = ex.variableCoefficient;
		this.constant = ex.constant;


		
		
	}
	
    private LinearExpression expressionValue() throws LinearExpressionFormatException {
    		if (index == expression.length()) {
    			throw new LinearExpressionFormatException("Missing a term at end of expression.");
    		}
        boolean negative;  // True if there is a leading minus sign.
        negative = false;
        if (expression.charAt(index) == '-') {
        		index++;
            negative = true;
        }
        LinearExpression val;  // Value of the expression.
        val = termValue();
        if (negative)
            val = val.inverse();
        while ( index < expression.length() && (expression.charAt(index) == '+' || expression.charAt(index) == '-' )) {
                // Read the next term and add it to or subtract it from
                // the value of previous terms in the expression.
            char op = expression.charAt(index++);
            LinearExpression nextVal = termValue();
            if (op == '+')
                val = val.add(nextVal);
            else
                val = val.add(nextVal.inverse());
        }
        return val;
    } 


    /**
     * Read a term from the current line of input and return its value.
     * @throws ParseError if the input contains a syntax error
     */
    private LinearExpression termValue() throws LinearExpressionFormatException {
        LinearExpression val;
        val = factorValue();
        while ( index < expression.length() && expression.charAt(index) == '*') {
                // Read the next factor, and multiply or divide
                // the value-so-far by the value of this factor.
            char op = expression.charAt(index++);
            LinearExpression nextVal = factorValue();
            if (op == '*')
                val = val.multiply(nextVal);

        }
        return val;
    } // end termValue()


    /**
     * Read a factor from the current line of input and return its value.
     * @throws ParseError if the input contains a syntax error
     */
    private LinearExpression factorValue() throws LinearExpressionFormatException {
    		if (index == expression.length())
    			throw new LinearExpressionFormatException("Missing factor at end of expression.");
        char ch = expression.charAt(index);
        int startIndex = index;
        if (Character.isDigit(ch) || ch == 'x' || ch == '-') {
	        while ( ++index < expression.length() && 
	        		(Character.isDigit(expression.charAt(index)) || expression.charAt(index) == 'x')) {
	        }
	        if (expression.charAt(index - 1) == 'x') {
	        		if (startIndex == index - 1) {
	        			return new LinearExpression(1, 0);
	        		}
	        		else {
	        			return new LinearExpression(Integer.parseInt(expression.substring(startIndex, index - 1)), 0);
	        		}
	        }
	        else {
	        		return new LinearExpression(0, Integer.parseInt(expression.substring(startIndex, index)));
	        }
        }
        else if ( ch == '(' ) {
                // The factor is an expression in parentheses.
        		index++;
            LinearExpression val = expressionValue();
            if ( index == expression.length() || expression.charAt(index++) != ')' )
                throw new LinearExpressionFormatException("Missing right parenthesis.");
            return val;
        }
        else if ( ch == '\n' )
            throw new LinearExpressionFormatException("End-of-line encountered in the middle of an expression.");
        else if ( ch == ')' )
            throw new LinearExpressionFormatException("Extra right parenthesis.");
        else if ( ch == '+' || ch == '*')
            throw new LinearExpressionFormatException("Misplaced operator.");
        else
            throw new LinearExpressionFormatException("Unexpected character \"" + ch + "\" encountered.");
    }
	
	public int getVariableCoefficient() {
		return variableCoefficient;
	}
	
	public int getConstant() {
		return constant;
	}
	
	public LinearExpression multiply(LinearExpression ex) throws LinearExpressionFormatException {
		if (this.variableCoefficient != 0 && ex.variableCoefficient != 0)
			throw new LinearExpressionFormatException("Can't multiply x terms.");
		return new LinearExpression(this.variableCoefficient * ex.constant 
				+ this.constant * ex.variableCoefficient, 
				this.constant * ex.constant);
	}
	
	public LinearExpression add(LinearExpression ex) {
		return new LinearExpression(this.variableCoefficient + ex.variableCoefficient,
				this.constant + ex.constant);
	}
	
	public LinearExpression inverse() {
		return new LinearExpression(-this.variableCoefficient, -this.constant);
	}
	
	public String toString() {
		return "" + this.variableCoefficient + "x+" + this.constant;
	}
}
