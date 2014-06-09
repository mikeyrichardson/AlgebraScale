package net.mvla.la.cs.as.controllers;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import net.mvla.la.cs.as.model.AlgebraScaleModel;
import net.mvla.la.cs.as.model.EquationFormatException;
import net.mvla.la.cs.as.model.LinearEquation;
import net.mvla.la.cs.as.model.Piece;
import net.mvla.la.cs.as.model.UserDatabase;
import net.mvla.la.cs.as.views.AlgebraScaleView;
import net.mvla.la.cs.as.views.LessonView;
import net.mvla.la.cs.as.views.ScaleDisplayPanel;
import net.mvla.la.cs.as.views.UserNameView;

import org.apache.commons.math3.fraction.Fraction;

public class AlgebraScaleController extends JFrame implements MouseListener,
		ComponentListener, ActionListener {
	public static void main(String[] args) {
		
		AlgebraScaleController window = new AlgebraScaleController();
		window.setSize(800, 600);		
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);


	}
	
	public static final long serialVersionUID = 28376583947L;
	private UserNameView userNameView;
	private AlgebraScaleView scaleView;
	private AlgebraScaleModel model;
	private ScaleDisplayPanel panel;
	private UserDatabase userDatabase;
	private File dataFile;
	private UserDatabase.User currentUser;
	private int currentLesson;
	private int currentExercise;

	public AlgebraScaleController() {
		
		

		File dataDir = new File(System.getProperty("user.home") + File.separator + ".AlgebraScale");
		if (!dataDir.exists()) {
			dataDir.mkdirs();
		}
		if (!dataDir.isDirectory()) {
			dataDir.delete();
			dataDir.mkdirs();
		}
		dataFile = new File(dataDir + File.separator + "userData.config");
		if (dataFile.exists()) {
			try {
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(dataFile));
				userDatabase = (UserDatabase)in.readObject();
				in.close();
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
				userDatabase = new UserDatabase("LessonData.txt");
			}
		}
		else {
			userDatabase = new UserDatabase("LessonData.txt");
		}
		
		this.model = new AlgebraScaleModel(new LinearEquation(1, 0, 0, 0));
		scaleView = new AlgebraScaleView();
		userNameView = new UserNameView(userDatabase.userList);
		userNameView.addButtonListener(this);
		this.panel = scaleView.displayPanel;
		scaleView.addDisplayPanelMouseListener(this);
		scaleView.addDisplayPanelComponentListener(this);
		scaleView.addButtonListener(this);
		this.setContentPane(userNameView);
	}
	
	public JPanel getCurrentView() {
		return scaleView;
	}

	public void redrawOffScreenCanvas() {
		panel.redrawOffScreenCanvas(model.pieces);
	}

	
	public void setScaleBalanceStatus() {
		double weightDiff = model.weightDifference();
		if (weightDiff > 0) {
			panel
					.setScaleBalanceStatus(ScaleDisplayPanel.LEFT_SIDE_HEAVIER);
		} else if (weightDiff < 0) {
			panel
					.setScaleBalanceStatus(ScaleDisplayPanel.RIGHT_SIDE_HEAVIER);
		} else
			panel
					.setScaleBalanceStatus(ScaleDisplayPanel.BOTH_SIDES_EQUAL);
	}
	
	
	@Override
	public void mouseClicked(MouseEvent e) {
		panel.draggedPiece = null;
		int x = e.getX();
		int y = e.getY();
		boolean isRightClick = e.isMetaDown();
		if (panel.setDraggedPieceFromPile(x, y)) {
			int startX;
			if (!isRightClick) {
				startX = panel.scaleLeftMargin;
			}
			else {
				startX = panel.getWidth() - panel.scaleLeftMargin - panel.shelfWidth;
			}
			Piece draggedPiece = panel.draggedPiece;
			int pieceVerticalPosition;
			int pieceWidth = getHeight(draggedPiece);
			int pieceHeight = getHeight(draggedPiece);
			int pieceType = draggedPiece.getType();
			switch (pieceType) {
			case Piece.NEG_CONST:
				pieceVerticalPosition = pieceHeight + panel.stringLength;
				break;
			default:
				pieceVerticalPosition = pieceHeight;
			}
			int pieceX = -1;
			if (model.pieces.size() == 0) {
				pieceX = startX;
			}
			else {
				int loopCount = 0;
				int rowOffset = 0;
				while (pieceX < 0) {
					bigLoop: for (int pixelX = startX + rowOffset; pixelX < startX + panel.shelfWidth - pieceWidth; pixelX++) {
						int overlappingPieceCount = 0;
						for (Piece piece : model.pieces) {
							if (getX(piece) <= pixelX && pixelX < getX(piece) + getWidth(piece)) {
								overlappingPieceCount++;
								if (overlappingPieceCount > loopCount) {
									continue bigLoop;
								}
							}	
						}
						pieceX = pixelX;
						break;
					}
					loopCount++;
					//change rowOffset by 10 pixels to avoid exact overlapping
					if (rowOffset < 10) {
						rowOffset += 12;
					} else {
						rowOffset -= 8;
					}
					

				}
			}
			setX(draggedPiece, pieceX);
			setY(draggedPiece, panel.shelfVerticalPosition + panel.shelfHeight / 2 - pieceVerticalPosition);

			model.pieces.add(draggedPiece);
			panel.draggedPiece = null;
			setScaleBalanceStatus();

			redrawOffScreenCanvas();
			panel.repaint();
			
		}
		else if (isRightClick) { //delete the piece from the scale
			//start by setting up offset variables
			int width = panel.getWidth();
			int height = panel.getHeight();
			int leftVerticalOffset;
			int rightVerticalOffset;
			double weightDiff = model.weightDifference();
			if (weightDiff > 0) {
				leftVerticalOffset = panel.scaleMovementDistance;
				rightVerticalOffset = -panel.scaleMovementDistance;
			} else if (weightDiff < 0) {
				leftVerticalOffset = -panel.scaleMovementDistance;
				rightVerticalOffset = panel.scaleMovementDistance;
			} else {
				leftVerticalOffset = 0;
				rightVerticalOffset = 0;
			}
			for (int i = model.pieces.size() - 1; i >= 0; i--) {
				Piece piece = model.pieces.get(i);
				int pieceX = (int) (piece.getRelativeX() * width);
				int pieceY;
				if (piece.getRelativeX() < 0.5) {
					pieceY = (int) (piece.getRelativeY() * height)
							+ leftVerticalOffset;
				} else {
					pieceY = (int) (piece.getRelativeY() * height)
							+ rightVerticalOffset;
				}
				int pieceWidth;
				int pieceHeight;
				if (piece.getDegree() > 0) {
					pieceWidth = panel.variablePieceWidth;
					if (piece.getValue() > 0) {
						pieceHeight = panel.variablePieceHeight;
					} else
						pieceHeight = panel.variablePieceHeight;
				} else {
					pieceWidth = panel.pieceWidth;
					if (piece.getValue() > 0) {
						pieceHeight = panel.boxHeight;
					} else
						pieceHeight = panel.balloonHeight;
				}
				if (x > pieceX && x < pieceX + pieceWidth && y > pieceY
						&& y < pieceY + pieceHeight) {
					//remove the piece from the scale
					model.pieces.remove(piece);
					this.setScaleBalanceStatus();
					panel.redrawOffScreenCanvas(model.pieces);
					panel.repaint();
					break;
				}
			}
		}
		
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		panel.draggedPiece = null;
		int x = e.getX();
		int y = e.getY();
		// Let GUI handle if the piece is in the pile
		if (panel.setDraggedPieceFromPile(x, y))
			return;
		
		//otherwise, check if a piece on the scale was clicked
		//start by setting up offset variables
		int width = panel.getWidth();
		int height = panel.getHeight();
		int leftVerticalOffset;
		int rightVerticalOffset;
		double weightDiff = model.weightDifference();
		if (weightDiff > 0) {
			leftVerticalOffset = panel.scaleMovementDistance;
			rightVerticalOffset = -panel.scaleMovementDistance;
		} else if (weightDiff < 0) {
			leftVerticalOffset = -panel.scaleMovementDistance;
			rightVerticalOffset = panel.scaleMovementDistance;
		} else {
			leftVerticalOffset = 0;
			rightVerticalOffset = 0;
		}

		// If piece isn't in pile, search through the pieces in the model
		// to see if one was clicked
		for (int i = model.pieces.size() - 1; i >= 0; i--) {
			Piece piece = model.pieces.get(i);
			int pieceX = (int) (piece.getRelativeX() * width);
			int pieceY;
			if (piece.getRelativeX() < 0.5) {
				pieceY = (int) (piece.getRelativeY() * height)
						+ leftVerticalOffset;
			} else {
				pieceY = (int) (piece.getRelativeY() * height)
						+ rightVerticalOffset;
			}
			int pieceWidth;
			int pieceHeight;
			if (piece.getDegree() > 0) {
				pieceWidth = panel.variablePieceWidth;
				if (piece.getValue() > 0) {
					pieceHeight = panel.variablePieceHeight;
				} else
					pieceHeight = panel.variablePieceHeight;
			} else {
				pieceWidth = panel.pieceWidth;
				if (piece.getValue() > 0) {
					pieceHeight = panel.boxHeight;
				} else
					pieceHeight = panel.balloonHeight;
			}
			if (x > pieceX && x < pieceX + pieceWidth && y > pieceY
					&& y < pieceY + pieceHeight) {
				//remove the piece from the scale and set it as dragged piece
				model.pieces.remove(piece);
				panel.draggedPiece = piece;
				// compensate for no offset in a dragged piece
				piece.setRelativeY((double) pieceY / height);
				panel.draggedPieceOffsetX = x - pieceX;
				panel.draggedPieceOffsetY = y - pieceY;
				panel.redrawOffScreenCanvas(model.pieces);
				panel.repaint();
				break;
			}
		}

	}

	

	@Override
	public void mouseReleased(MouseEvent e) {
		Piece draggedPiece = panel.draggedPiece;
		if (draggedPiece == null)
			return;
		int width = panel.getWidth();
		int height = panel.getHeight();
		int pieceX = (int) (draggedPiece.getRelativeX() * width);
		int pieceY = (int) (draggedPiece.getRelativeY() * height);
		int pieceVerticalPostion;
		int pieceWidth;
		int pieceHeight;
		int pieceType = draggedPiece.getType();
		switch (pieceType) {
		case Piece.NEG_CONST:
			pieceHeight = panel.balloonHeight;
			pieceWidth = panel.pieceWidth;
			pieceVerticalPostion = pieceHeight + panel.stringLength;
			break;
		case Piece.POS_CONST:
			pieceHeight = panel.boxHeight;
			pieceWidth = panel.pieceWidth;
			pieceVerticalPostion = pieceHeight;
			break;
		default:
			pieceHeight = panel.variablePieceHeight;
			pieceWidth = panel.variablePieceWidth;
			pieceVerticalPostion = pieceHeight;
		}

		if (pieceY > panel.shelfVerticalPosition
				+ panel.scaleMovementDistance
				|| pieceY < panel.topMargin
				|| pieceX < panel.scaleLeftMargin
				|| pieceX + pieceWidth > width - panel.scaleLeftMargin) {
			panel.draggedPiece = null;
			setScaleBalanceStatus();
			redrawOffScreenCanvas();
			panel.repaint();
			return;
		}
		double leftEdgeOfMiddleGap = panel.scaleLeftMargin / 2 + panel.shelfWidth;
		double rightEdgeOfMiddleGap = width - leftEdgeOfMiddleGap;
		if (panel.draggedPiece != null) {
			if (leftEdgeOfMiddleGap < pieceX + pieceWidth / 2
					&& pieceX + pieceWidth / 2 < width / 2)
				draggedPiece.setRelativeX((leftEdgeOfMiddleGap - pieceWidth)
						/ width);
			
			if (width / 2 < pieceX + pieceWidth / 2
					&& pieceX + pieceWidth / 2 < rightEdgeOfMiddleGap)
				draggedPiece.setRelativeX((rightEdgeOfMiddleGap) / width);
			
			draggedPiece.setRelativeY((double) (panel.shelfVerticalPosition
							+ panel.shelfHeight / 2 - pieceVerticalPostion)
							/ height);

			model.pieces.add(panel.draggedPiece);
			panel.draggedPiece = null;
			setScaleBalanceStatus();

			redrawOffScreenCanvas();
			panel.repaint();
		}

	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void componentResized(ComponentEvent e) {
		panel.setSizes();
		panel.createScaledImages();
		panel.setDefaultImages();
		panel.redrawOffScreenCanvas(model.pieces);
	}

	@Override
	public void componentMoved(ComponentEvent e) {}

	@Override
	public void componentShown(ComponentEvent e) {
		componentResized(e);

	}

	@Override
	public void componentHidden(ComponentEvent e) {}

	@Override
	public void actionPerformed(ActionEvent e) {		
		if (e.getActionCommand().equals("Check Answer")) {
			checkAnswer();
		}

		else if (e.getActionCommand().equals("Submit Equation")) {
			submitEquation(scaleView.equationInputTextField
					.getText().replace("X", "x"));
		}

		else if (e.getActionCommand().equals("Clear All")) {
			clearAllPieces();
		}
		
		else if (e.getActionCommand().equals("Boxes <==> Balloons")) {
			balloonsToBoxes();
		}
		
		else if (e.getActionCommand().equals("Create New User")) {
			String userName = userNameView.createNewUserTextField.getText().trim();
			userDatabase.addUser(userName);
			setLessonView(userName);
		}
		
		else if (e.getActionCommand().equals("Type in your own equations")) {
			currentLesson = 0;
			currentExercise = 0;
			this.setContentPane(scaleView);
			scaleView.previousExerciseButton.setVisible(false);
			scaleView.nextExerciseButton.setVisible(false);
			scaleView.equationInputLabel.setVisible(true);
			scaleView.equationInputTextField.setVisible(true);
			scaleView.submitEquationButton.setVisible(true);
			this.validate();
			submitEquation("x=0");
			scaleView.equationDisplayLabel.setText("Enter your equation");
			scaleView.messageDisplayLabel.setText("");
			scaleView.equationInputTextField.setText("Type your equation here");
			scaleView.equationInputTextField.requestFocus();
		}
		
		else if (e.getActionCommand().startsWith("Lesson:")) {
			String command = e.getActionCommand();
			currentLesson = Integer.parseInt(command.substring(7,command.indexOf('#')));
			currentExercise = Integer.parseInt(command.substring(command.indexOf('#') + 1));
			setContentPane(scaleView);
			scaleView.previousExerciseButton.setVisible(true);
			scaleView.nextExerciseButton.setVisible(true);
			scaleView.equationInputLabel.setVisible(false);
			scaleView.equationInputTextField.setVisible(false);
			scaleView.submitEquationButton.setVisible(false);
			this.validate();
			submitEquation(this.currentUser.getLesson(currentLesson).getExerciseString(currentExercise));
		}
		
		else if (e.getActionCommand().equals("Previous Exercise")) {
			currentExercise--;
			if (currentExercise <= 0) {
				setLessonView(currentUser.userName);
			}
			else {
				submitEquation(this.currentUser.getLesson(currentLesson).getExerciseString(currentExercise));
			}
		}
		
		else if (e.getActionCommand().equals("Lesson Menu")) {
			setLessonView(currentUser.userName);
		}
		
		else if (e.getActionCommand().equals("Next Exercise")) {
			currentExercise++;
			if (currentExercise > currentUser.getLesson(currentLesson).exerciseList.size()) {
				setLessonView(currentUser.userName);
			}
			else {
				submitEquation(this.currentUser.getLesson(currentLesson).getExerciseString(currentExercise));
			}
		}
		
		else if (e.getActionCommand().startsWith("User:")){
			setLessonView(e.getActionCommand().substring(5));
		}
	}
	
	void setLessonView(String userName) {
		for (UserDatabase.User user : userDatabase.userList ) {
			if (user.userName.equals(userName)) {
				currentUser = user;
				setContentPane(new LessonView(user, this));
				this.validate();
				break;
			}
		}
	}
	
	void setScaleView(String equation) {
		
	}
	
	int getHeight(Piece piece) {
		if (piece.getDegree() > 0) {
			if (piece.getValue() > 0) {
				return panel.variablePieceHeight;
			}
			else {
				return panel.variablePieceHeight;
			}
		}
		else {
			if (piece.getValue() > 0) {
				return panel.boxHeight;
			}
			else {
				return panel.balloonHeight;
			}
		}
	}
	
	int getWidth(Piece piece) {
		if (piece.getDegree() > 0) {
			return panel.variablePieceWidth;
		}
		else {
			return panel.pieceWidth;
		}
	}
	
	int getX(Piece piece) {
		return (int)(piece.getRelativeX() * panel.getWidth());
	}
	
	int getY(Piece piece) {
		return (int)(piece.getRelativeY() * panel.getHeight());
	}
	
	void setX(Piece piece, int x) {
		piece.setRelativeX((double)x / panel.getWidth());
	}
	
	void setY(Piece piece, int y) {
		piece.setRelativeY((double)y / panel.getHeight());
	}
	
	void balloonsToBoxes() {
		for (Piece piece : model.pieces) {
			int beforeHeight = getHeight(piece);
			piece.setValue(piece.getValue() * -1);
			int afterHeight = getHeight(piece);
			if (piece.getValue() < 0 && piece.getDegree() == 0) {
				setY(piece, getY(piece) - panel.stringLength - (afterHeight - beforeHeight));
			}
			else if (piece.getDegree() == 0) {
				setY(piece, getY(piece) + panel.stringLength - (afterHeight - beforeHeight));
			}
			setScaleBalanceStatus();
			panel.redrawOffScreenCanvas(model.pieces);
			panel.repaint();
		}
	}
	
	void submitEquation(String equation) {
		try {
			scaleView.messageDisplayLabel.setText("");
			scaleView.messageDisplayLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
			model = new AlgebraScaleModel(new LinearEquation(equation));
			scaleView.equationDisplayLabel.setText(equation);
			scaleView.messageDisplayLabel.setText("");
			scaleView.messageDisplayLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));

			scaleView.variableInputTextField.setText("");
			scaleView.leftSideInputTextField.setText("");
			scaleView.rightSideInputTextField.setText("");
		} catch (EquationFormatException ex) {
			scaleView.messageDisplayLabel.setText(ex.getMessage());
			scaleView.equationInputTextField.requestFocus();
		}
		setScaleBalanceStatus();
		panel.redrawOffScreenCanvas(model.pieces);
		panel.repaint();
	}
	
	void clearAllPieces() {
		model.pieces = new ArrayList<Piece>();
		setScaleBalanceStatus();
		panel.redrawOffScreenCanvas(model.pieces);
		panel.repaint();
	}
	
	void checkAnswer() {
		Fraction answers[] = new Fraction[3];
		String[] answerStrings = new String[3];
		answerStrings[0] = scaleView.variableInputTextField.getText().trim();
		answerStrings[1] = scaleView.leftSideInputTextField.getText().trim();
		answerStrings[2] = scaleView.rightSideInputTextField.getText().trim();
		for (int i = 0; i < 3; i++) {
			answerStrings[i] = answerStrings[i].replaceAll("- ", "-");
			while (answerStrings[i].contains("/ "))
				answerStrings[i] = answerStrings[i].replace("/ ", "/");
			while (answerStrings[i].contains(" /"))
				answerStrings[i] = answerStrings[i].replace(" /", "/");
			while (answerStrings[i].contains("  "))
			answerStrings[i] = answerStrings[i].replace("  ", " ");
		}
		for (int i = 0; i < 3; i++) {
			if (answerStrings[i].equals("")) {
				scaleView.messageDisplayLabel
						.setText("<html>You must enter a value for the variable, left side, <br>and right side before checking your answer.</html>");
				return;
			}
		}
		try {
			for (int i = 0; i < 3; i++) {
				if (answerStrings[i].contains("/")) {
					if (answerStrings[i].contains(" ")) {
						String[] parts = answerStrings[i].split(" ");
						int wholeNum = Integer.parseInt(parts[0]);
						String[] fracParts = parts[1].split("/");
						int numerator = Integer.parseInt(fracParts[0]);
						int denominator = Integer.parseInt(fracParts[1]);
						if (wholeNum >= 0) {
							answers[i] = new Fraction(wholeNum * denominator
									+ numerator, denominator);
						}
						else {
							answers[i] = new Fraction(wholeNum * denominator
									- numerator, denominator);
						}
						
					} else {
						String[] fracParts = answerStrings[i].split("/");
						int numerator = Integer.parseInt(fracParts[0]);
						int denominator = Integer.parseInt(fracParts[1]);
						answers[i] = new Fraction(numerator, denominator);
					}
				} 
				else if (answerStrings[i].contains(".")){
					answers[i] = new Fraction(
							Double.parseDouble(answerStrings[i]), 1000000000);
				}
				else {
					answers[i] = new Fraction(Integer.parseInt(answerStrings[i]));
				}
			}
		} catch (Exception ex) {
			scaleView.messageDisplayLabel
					.setText("Make sure that your numbers are all formatted as fractions or decimals.");

		}
		for (int i=0; i < 3; i++)
		if (!model.equation.leftSideValue(answers[0]).equals(answers[1]) &&
				Math.abs(model.equation.leftSideValue(answers[0])
				.subtract(answers[1]).doubleValue()) < 0.01) {
			scaleView.messageDisplayLabel
					.setText("<html>If x = " + answerStrings[0] + ", then the left side of the equation wouldn't equal " + answerStrings[1] + ".<br>Hint: Use fractions instead of decimals.</html>");
			return;
		}
		if (!model.equation.leftSideValue(answers[0]).equals(answers[1])) {
			scaleView.messageDisplayLabel
					.setText("<html>If x = " + answerStrings[0] + ", then the left side of the equation wouldn't equal " + answerStrings[1] + ".</html>");
			return;
		}

		if (!model.equation.rightSideValue(answers[0]).equals(answers[2]) &&
				Math.abs(model.equation.rightSideValue(answers[0])
				.subtract(answers[2]).doubleValue()) < 0.01) {
			scaleView.messageDisplayLabel
					.setText("<html>If x = " + answerStrings[0] + ", then the right side of the equation wouldn't equal " + answerStrings[2] + ".<br>Hint: Use fractions instead of decimals.</html>");
			return;
		}
		if (!model.equation.rightSideValue(answers[0]).equals(answers[2])) {
			scaleView.messageDisplayLabel
					.setText("<html>If x = " + answerStrings[0] + ", then the right side of the equation wouldn't equal " + answerStrings[2] + ".</html>");
			return;
		}

		if (!model.solution.equals(answers[0]) &&
				Math.abs(model.solution.subtract(answers[0]).doubleValue()) < 0.01) {
			scaleView.messageDisplayLabel
					.setText("<html> x = " + answerStrings[0] + " is a little off. <br>Use fractions instead of rounding decimals.</html>");
			return;
		}
		if (!model.solution.equals(answers[0])) {
			scaleView.messageDisplayLabel
					.setText("x = " + answerStrings + " is incorrect.");
			return;
		}

		// if you made it to here, then you're correct
		if (currentLesson != 0 && currentExercise != 0) {
			currentUser.setComplete(currentLesson, currentExercise);
			try {
    			userDatabase.export(dataFile);
    		} catch (Exception ex) {
    			ex.printStackTrace();
    		}
		}
		scaleView.messageDisplayLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 32));
		scaleView.messageDisplayLabel
				.setText("x = " + answerStrings[0] + " is correct!!!");
		scaleView.equationInputTextField.setText("Type next equation");
		scaleView.equationInputTextField.requestFocus();
		return;
	}


}
