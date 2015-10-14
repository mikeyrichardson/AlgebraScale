package net.mikeyrichardson.algebrascale.views;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import net.mikeyrichardson.algebrascale.model.Piece;

public class ScaleDisplayPanel extends JPanel implements MouseMotionListener {
	
	public final static int BOTH_SIDES_EQUAL = 0;
	public final static int LEFT_SIDE_HEAVIER = 1;
	public final static int RIGHT_SIDE_HEAVIER = -1;
	public final static int DEFAULT_WIDTH = 800;
	public final static int DEFAULT_HEIGHT = 380;
	
	//these static variables give the relative sizes of all of the images in the GUI
	//they are meant to be tweaked to achieve the best overall look to the GUI
	//the actual size values (in pixels) are set in the function setSizes()

	public final static double PIECE_WIDTH_PERCENTAGE = 0.03; //percentage of screen width
	public final static double BOX_HEIGHT_PERCENTAGE = 0.05;
	public final static double VARIABLE_PIECE_SCALE_FACTOR = 1.7; //how many times bigger the variables are
	public final static double PIECE_ROW_SPACING_PERCENTAGE = 0.02;
	public final static double SCALE_LEFT_MARGIN_PERCENTAGE = 0.02; //spacing between rows of pieces in pile
	public final static double PILE_LEFT_MARGIN_PERCENTAGE = 0.28;
	public final static double BOTTOM_MARGIN_PERCENTAGE = 0.05;
	public final static double PIECE_SPACING_PERCENTAGE = 0.01;
	public final static double SHELF_WIDTH_PERCENTAGE = 0.45;
	public final static double SHELF_HEIGHT_PERCENTAGE = 0.04;
	public final static double SHELF_VERTICAL_POSITION_PERCENTAGE = 0.35;
	public final static double STRING_LENGTH_PERCENTAGE = 0.20;
	public final static double TOP_MARGIN_PERCENTAGE = 0.10;
	public final static double BASE_WIDTH_PERCENTAGE = 0.40;
	public final static double BASE_HEIGHT_PERCENTAGE = 0.04;
	public final static double BASE_VERTICAL_POSITION_PERCENTAGE = 0.22 + SHELF_VERTICAL_POSITION_PERCENTAGE;
	public final static double BEAM_WIDTH_PERCENTAGE = 0.55;
	public final static double BEAM_HEIGHT_PERCENTAGE = 0.04;
	public final static double BEAM_VERTICAL_POSITION_PERCENTAGE = 0.13 + SHELF_VERTICAL_POSITION_PERCENTAGE;
	public final static double POST_WIDTH_PERCENTAGE = 0.02;
	public final static double POST_HEIGHT_PERCENTAGE = 0.09;
	public final static double POST_VERTICAL_POSITION_PERCENTAGE = 0.15 + SHELF_VERTICAL_POSITION_PERCENTAGE;
	public final static double JOINT_WIDTH_PERCENTAGE = 0.04;
	public final static double JOINT_HEIGHT_PERCENTAGE = 0.04;
	public final static double JOINT_VERTICAL_POSITION_PERCENTAGE = 0.13 + SHELF_VERTICAL_POSITION_PERCENTAGE;
	public final static double SCALE_MOVEMENT_DISTANCE_PERCENTAGE = 0.04;
	
	//the pixel values determined by the above relative values
	public int pieceWidth;
	public int boxHeight;
	public int balloonHeight;
	public int variablePieceWidth;
	public int variablePieceHeight;
	public int stringLength;
	public int boxBaseline;
	public int balloonBaseline;
	public int pieceRowSpacing;
	public int scaleLeftMargin;
	public int pileLeftMargin;
	public int topMargin;
	public int bottomMargin;
	public int pieceSpacing;
	public int shelfWidth, shelfHeight, shelfVerticalPosition;
	public int baseWidth, baseHeight, baseVerticalPosition;
	public int beamWidth, beamHeight, beamVerticalPosition;
	public int postWidth, postHeight, postVerticalPosition;
	public int jointWidth, jointHeight, jointVerticalPosition;
	public int scaleMovementDistance;
	
	private int scaleBalanceStatus; //one of three values: BOTH_SIDES_EQUAL, LEFT_SIDE_HEAVIER, RIGHT_SIDE_HEAVIER
	
	public Piece draggedPiece;		// This is the variable for storing the currently dragged piece
							// The piece will be added to the offScreenCanvas if the user decides to place it on the scale
	public int draggedPieceOffsetX; // horizontal difference between corner of piece and where user clicked
	public int draggedPieceOffsetY; // vertical difference between corner of piece and where user clicked
	
	BufferedImage defaultImage;		//this is an image of the GUI without any pieces drawn on it
	BufferedImage defaultLeftSideHeavierImage;	//this is an image of the GUI with the scale lower on the left
	BufferedImage defaultRightSideHeavierImage;  //this is an image of the GUI with the scale lower on the right
	BufferedImage offScreenCanvas;		//this is an image which stores the current state of the GUI (along with
										//drawings of the pieces for quick drawing in repaint
	
	
	//These are where the original images for all of the images in the GUI are stored
	BufferedImage[] originalBoxImages = new BufferedImage[11];
	BufferedImage[] originalBalloonImages = new BufferedImage[11]	;
	BufferedImage   originalShelfImage;
	BufferedImage   originalBaseImage;
	BufferedImage   originalBeamImage;
	BufferedImage   originalPostImage;
	BufferedImage   originalJointImage;		
	
	//These are where the scaled images are stored
	//They are set in the function createScaledImages (which is called every time the user resizes the window)
	BufferedImage[] boxImages = new BufferedImage[11];
	BufferedImage[] balloonImages = new BufferedImage[11]; 
	BufferedImage   shelfImage;
	BufferedImage   baseImage;
	BufferedImage   beamImage;
	BufferedImage   postImage;
	BufferedImage   jointImage;
	
	public ScaleDisplayPanel() {
		
		// load images into original image arrays
		try {
			originalShelfImage = ImageIO.read(getClass().getClassLoader().getResource("scaleShelf.png"));
			originalBaseImage = ImageIO.read(getClass().getClassLoader().getResource("scaleShelf.png"));
			originalBeamImage = ImageIO.read(getClass().getClassLoader().getResource("scaleBeam.png"));
			originalPostImage = ImageIO.read(getClass().getClassLoader().getResource("scalePost.png"));
			originalJointImage = ImageIO.read(getClass().getClassLoader().getResource("scaleJoint.png"));
			originalBalloonImages[0] = ImageIO.read(getClass().getClassLoader().getResource("negativeXBox.png"));
			originalBoxImages[0] = ImageIO.read(getClass().getClassLoader().getResource("xBox.png"));
			for (int i = 1; i <= 10; i++) {
				originalBalloonImages[i] = ImageIO.read(getClass().getClassLoader().getResource("balloon" + i + ".png"));
				originalBoxImages[i] = ImageIO.read(getClass().getClassLoader().getResource("box" + i + ".png"));
			}
		} catch (Exception e) {
			System.err.println("Couldn't read image files.");
			e.printStackTrace();
		}
		
		//initialize the GUI
		setScaleBalanceStatus(BOTH_SIDES_EQUAL);
		setSizes();
		createScaledImages();
		
		// set up listener to redraw when dragging a piece
		addMouseMotionListener(this);
				
	}
	
	//called by the controller to set the correct visual appearance of the scale according to the weight
	//of the objects on the scale
	public boolean setScaleBalanceStatus(int status) {
		if (status == BOTH_SIDES_EQUAL || status == LEFT_SIDE_HEAVIER || status == RIGHT_SIDE_HEAVIER) {
			scaleBalanceStatus = status;
			return true;
		}
		return false;
	}
	
	//Used to create scaled images from the original images
	//This is called whenever a user resizes the window
	public void createScaledImages() {
		Image image;
		Graphics g;
		image = originalShelfImage.getScaledInstance(shelfWidth, shelfHeight, Image.SCALE_DEFAULT);
		shelfImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		g = shelfImage.getGraphics();
		g.drawImage(image,0,0,null);
		g.dispose();
		image = originalBaseImage.getScaledInstance(baseWidth, baseHeight, Image.SCALE_DEFAULT);
		baseImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		g = baseImage.getGraphics();
		g.drawImage(image,0,0,null);
		g.dispose();
		image = originalBeamImage.getScaledInstance(beamWidth, beamHeight, Image.SCALE_DEFAULT);
		beamImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		g = beamImage.getGraphics();
		g.drawImage(image,0,0,null);
		g.dispose();
		image = originalPostImage.getScaledInstance(postWidth, postHeight, Image.SCALE_DEFAULT);
		postImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		g = postImage.getGraphics();
		g.drawImage(image,0,0,null);
		g.dispose();
		image = originalJointImage.getScaledInstance(jointWidth, jointHeight, Image.SCALE_DEFAULT);
		jointImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		g = jointImage.getGraphics();
		g.drawImage(image,0,0,null);
		g.dispose();
		image = originalBoxImages[0].getScaledInstance(variablePieceWidth, variablePieceHeight, Image.SCALE_DEFAULT);
		boxImages[0] = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		g = boxImages[0].getGraphics();
		g.drawImage(image,0,0,null);
		g.dispose();
		image = originalBalloonImages[0].getScaledInstance(variablePieceWidth, variablePieceHeight, Image.SCALE_DEFAULT);
		balloonImages[0] = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		g = balloonImages[0].getGraphics();
		g.drawImage(image,0,0,null);
		g.dispose();
		
		for (int i = 1; i <= 10; i++) {
			image = originalBoxImages[i].getScaledInstance(pieceWidth, boxHeight, Image.SCALE_DEFAULT);
			boxImages[i] = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
			g = boxImages[i].getGraphics();
			g.drawImage(image,0,0,null);
			g.dispose();
			image = originalBalloonImages[i].getScaledInstance(pieceWidth, balloonHeight, Image.SCALE_DEFAULT);
			balloonImages[i] = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
			g = balloonImages[i].getGraphics();
			g.drawImage(image,0,0,null);
			g.dispose();
		}
	}
	
	//Used to translate the relative dimensions at the beginning of this class
	//into actual pixel values used for drawing the images
	public void setSizes() {
		int width;
		int height;
		if (getWidth() == 0 || getHeight() == 0) {
			width = DEFAULT_WIDTH;
			height = DEFAULT_HEIGHT;
		}
		else {
			width = getWidth();
			height = getHeight();
		}
		
		pieceWidth = (int)(width * PIECE_WIDTH_PERCENTAGE);
		variablePieceWidth = (int)(VARIABLE_PIECE_SCALE_FACTOR * pieceWidth);
		boxHeight = pieceWidth; //(int)(height * BOX_HEIGHT_PERCENTAGE);
		balloonHeight = (int)(pieceWidth * 1.3);
		variablePieceHeight = variablePieceWidth;
		stringLength = (int)(height * STRING_LENGTH_PERCENTAGE);
		boxBaseline = height - bottomMargin - variablePieceHeight - pieceRowSpacing;
		balloonBaseline = height - bottomMargin;
		pieceRowSpacing = (int)(height * PIECE_ROW_SPACING_PERCENTAGE);
		scaleLeftMargin = (int)(SCALE_LEFT_MARGIN_PERCENTAGE * width);
		pileLeftMargin = (int)(PILE_LEFT_MARGIN_PERCENTAGE * width);
		topMargin = (int)(TOP_MARGIN_PERCENTAGE * height);
		bottomMargin = (int)(BOTTOM_MARGIN_PERCENTAGE * height);
		pieceSpacing = (int)(PIECE_SPACING_PERCENTAGE * width);
		shelfWidth = (int)(width * SHELF_WIDTH_PERCENTAGE);
		shelfHeight = (int)(height * SHELF_HEIGHT_PERCENTAGE);
		shelfVerticalPosition = (int)(height * SHELF_VERTICAL_POSITION_PERCENTAGE);
		baseWidth = (int)(width * BASE_WIDTH_PERCENTAGE);
		baseHeight = (int)(height * BASE_HEIGHT_PERCENTAGE);
		baseVerticalPosition = (int)(height * BASE_VERTICAL_POSITION_PERCENTAGE);
		beamWidth = (int)(width * BEAM_WIDTH_PERCENTAGE);
		beamHeight = (int)(height * BEAM_HEIGHT_PERCENTAGE);
		beamVerticalPosition = (int)(height * BEAM_VERTICAL_POSITION_PERCENTAGE);
		postWidth = (int)(width * POST_WIDTH_PERCENTAGE);
		postHeight = (int)(height * POST_HEIGHT_PERCENTAGE);
		postVerticalPosition = (int)(height * POST_VERTICAL_POSITION_PERCENTAGE);
		if (width > height) {
			jointWidth = (int)(width * JOINT_WIDTH_PERCENTAGE);
			jointHeight = (int)(width * JOINT_HEIGHT_PERCENTAGE);
		} else {
			jointWidth = (int)(height * JOINT_WIDTH_PERCENTAGE);
			jointHeight = (int)(height * JOINT_HEIGHT_PERCENTAGE);
		}
		jointVerticalPosition = (int)(height * JOINT_VERTICAL_POSITION_PERCENTAGE);
		scaleMovementDistance = (int)(height * SCALE_MOVEMENT_DISTANCE_PERCENTAGE);
	}
	
	//Called after creating scaled images. This function arranges all of the images into 3 default images:
	//balanced scale, left-side-heavy scale, and right-side-heavy scale
	public void setDefaultImages() {
		BufferedImage imageWithoutScale = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = imageWithoutScale.getGraphics();
		g.drawImage(boxImages[0], pileLeftMargin, boxBaseline - variablePieceHeight , this);
		g.drawImage(balloonImages[0], pileLeftMargin, balloonBaseline - variablePieceHeight, this);
		
		for (int i = 1; i <= 10; i++) {
			g.drawImage(boxImages[i], pileLeftMargin + variablePieceWidth + pieceSpacing + (i - 1) * (pieceWidth + pieceSpacing), boxBaseline - boxHeight, this);
			g.drawImage(balloonImages[i], pileLeftMargin + variablePieceWidth + pieceSpacing + (i - 1) * (pieceWidth + pieceSpacing), balloonBaseline - balloonHeight, this);
		}
		g.dispose();
		
		
		defaultImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		g = defaultImage.getGraphics();
		g.drawImage(imageWithoutScale, 0, 0, this);
		g.drawImage(postImage, getWidth() / 2 - beamWidth / 2 - postWidth / 2, shelfVerticalPosition + shelfHeight, this);
		g.drawImage(postImage, getWidth() / 2 + beamWidth / 2 - postWidth / 2, shelfVerticalPosition + shelfHeight, this);
		g.drawImage(shelfImage, scaleLeftMargin, shelfVerticalPosition, this);
		g.drawImage(shelfImage, getWidth() - scaleLeftMargin - shelfWidth, shelfVerticalPosition, this);
		g.drawImage(baseImage, getWidth() / 2 - baseWidth / 2, baseVerticalPosition, this);
		g.drawImage(postImage, getWidth() / 2 - postWidth / 2, postVerticalPosition, this);
		g.drawImage(beamImage, getWidth() / 2 - beamWidth / 2, beamVerticalPosition, this);
		g.drawImage(jointImage,getWidth() / 2 - beamWidth / 2 - jointWidth / 2, beamVerticalPosition + beamHeight / 2 - jointHeight / 2, this);
		g.drawImage(jointImage,getWidth() / 2 + beamWidth / 2 - jointWidth / 2, beamVerticalPosition + beamHeight / 2 - jointHeight / 2, this);
		g.drawImage(jointImage,getWidth() / 2 - jointWidth / 2, beamVerticalPosition + beamHeight / 2 - jointHeight / 2, this);
		g.dispose();
		

		defaultLeftSideHeavierImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		g = defaultLeftSideHeavierImage.getGraphics();
		g.drawImage(imageWithoutScale, 0, 0, this);
		g.drawImage(baseImage, getWidth() / 2 - baseWidth / 2, baseVerticalPosition, this);
		g.drawImage(postImage, getWidth() / 2 - postWidth / 2, postVerticalPosition, this);
		g.drawImage(postImage, getWidth() / 2 - beamWidth / 2 - postWidth / 2, shelfVerticalPosition + shelfHeight + scaleMovementDistance, this);
		g.drawImage(postImage, getWidth() / 2 + beamWidth / 2 - postWidth / 2, shelfVerticalPosition + shelfHeight - scaleMovementDistance, this);
		g.drawImage(shelfImage, scaleLeftMargin, shelfVerticalPosition + scaleMovementDistance, this);
		g.drawImage(shelfImage, getWidth() - scaleLeftMargin - shelfWidth, shelfVerticalPosition - scaleMovementDistance, this);
		AffineTransform at = new AffineTransform();
		at.setToRotation(-Math.asin((double) scaleMovementDistance / (beamWidth/2)), getWidth() / 2, beamVerticalPosition + beamHeight / 2);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2d.setTransform(at);
		g2d.drawImage(beamImage, getWidth() / 2 - beamWidth / 2, beamVerticalPosition, this);
		g2d.dispose();
		g = defaultLeftSideHeavierImage.getGraphics();
		g.drawImage(jointImage,getWidth() / 2 - beamWidth / 2 - jointWidth / 2, beamVerticalPosition + beamHeight / 2 - jointHeight / 2 + scaleMovementDistance, this);
		g.drawImage(jointImage,getWidth() / 2 + beamWidth / 2 - jointWidth / 2, beamVerticalPosition + beamHeight / 2 - jointHeight / 2 - scaleMovementDistance, this);
		g.drawImage(jointImage,getWidth() / 2 - jointWidth / 2, beamVerticalPosition + beamHeight / 2 - jointHeight / 2, this);
		g.dispose();
		
		defaultRightSideHeavierImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		g = defaultRightSideHeavierImage.getGraphics();
		g.drawImage(imageWithoutScale, 0, 0, this);
		g.drawImage(baseImage, getWidth() / 2 - baseWidth / 2, baseVerticalPosition, this);
		g.drawImage(postImage, getWidth() / 2 - postWidth / 2, postVerticalPosition, this);
		g.drawImage(postImage, getWidth() / 2 - beamWidth / 2 - postWidth / 2, shelfVerticalPosition + shelfHeight - scaleMovementDistance, this);
		g.drawImage(postImage, getWidth() / 2 + beamWidth / 2 - postWidth / 2, shelfVerticalPosition + shelfHeight + scaleMovementDistance, this);
		g.drawImage(shelfImage, scaleLeftMargin, shelfVerticalPosition - scaleMovementDistance, this);
		g.drawImage(shelfImage, getWidth() - scaleLeftMargin - shelfWidth, shelfVerticalPosition + scaleMovementDistance, this);
		at = new AffineTransform();
		at.setToRotation(Math.asin((double) scaleMovementDistance / (beamWidth/2)), getWidth() / 2, beamVerticalPosition + beamHeight / 2);
		g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2d.setTransform(at);
		g2d.drawImage(beamImage, getWidth() / 2 - beamWidth / 2, beamVerticalPosition, this);
		g2d.dispose();
		g = defaultRightSideHeavierImage.getGraphics();
		g.drawImage(jointImage,getWidth() / 2 - beamWidth / 2 - jointWidth / 2, beamVerticalPosition + beamHeight / 2 - jointHeight / 2 - scaleMovementDistance, this);
		g.drawImage(jointImage,getWidth() / 2 + beamWidth / 2 - jointWidth / 2, beamVerticalPosition + beamHeight / 2 - jointHeight / 2 + scaleMovementDistance, this);
		g.drawImage(jointImage,getWidth() / 2 - jointWidth / 2, beamVerticalPosition + beamHeight / 2 - jointHeight / 2, this);
		g.dispose();
		
	}
	
	//This function redraws the offScreenCanvas. It is called whenever another piece is added to the scale (or removed)
	public void redrawOffScreenCanvas(ArrayList<Piece> pieces) {
		offScreenCanvas = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = offScreenCanvas.getGraphics();
		

			switch (scaleBalanceStatus) {
			case BOTH_SIDES_EQUAL: 
				g.drawImage(defaultImage, 0, 0, this);
				break;
			case LEFT_SIDE_HEAVIER:
				g.drawImage(defaultLeftSideHeavierImage, 0, 0, this);
				
				break;
			default:
				g.drawImage(defaultRightSideHeavierImage, 0, 0, this);
				
			}
			for (Piece piece : pieces) {
				drawPiece(g, piece);
			}
		g.dispose();
	}
	
	//this draws a piece. It acts differently for a dragged piece than for pieces on the scale.
	//it calculates offsets to make up for the fact that pieces should be drawn at different vertical positions
	//if the scale is tipped one way or the other
	public void drawPiece(Graphics g, Piece piece) {
		int pieceX = (int)(piece.getRelativeX() * getWidth());
		int pieceY = (int)(piece.getRelativeY() * getHeight());
		int verticalOffset;
		int pieceVerticalOffset;
		if (scaleBalanceStatus == BOTH_SIDES_EQUAL) {
			verticalOffset = 0;
		}
		else if (scaleBalanceStatus == LEFT_SIDE_HEAVIER) {
			if (piece.getRelativeX() < 0.5)
			verticalOffset = scaleMovementDistance;
			else
			verticalOffset = -scaleMovementDistance;
			
		}
		else  {
			if (piece.getRelativeX() > 0.5)
				verticalOffset = scaleMovementDistance;
				else
				verticalOffset = -scaleMovementDistance;
		}
		if (piece == draggedPiece) 
			pieceVerticalOffset = 0;
		else
			pieceVerticalOffset = verticalOffset;
		
		g.setColor(Color.BLACK);
		if ( piece.getDegree() > 0) {
			if (piece.getValue() < 0) {
				g.drawImage(balloonImages[0], pieceX, pieceY + pieceVerticalOffset, this);
			}
			else
				g.drawImage(boxImages[0], pieceX, pieceY + pieceVerticalOffset, this);
		}
		else if (piece.getValue() < 0) {
			g.drawImage(balloonImages[-1 * piece.getValue()], pieceX, pieceY + pieceVerticalOffset, this);
			if (pieceY < shelfVerticalPosition - balloonHeight)
				g.drawLine(pieceX + pieceWidth / 2, pieceY + balloonHeight + pieceVerticalOffset,
							pieceX + pieceWidth / 2, shelfVerticalPosition + shelfHeight / 3 + verticalOffset);
		}
		else 
			g.drawImage(boxImages[piece.getValue()], pieceX, pieceY + pieceVerticalOffset, this);
			
	}
	
	
	//this draws the offScreenCanvas and if there is a piece being dragged, it draws that too
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (defaultImage == null) {
			setDefaultImages();
		}
		if (offScreenCanvas == null) {
			offScreenCanvas = defaultImage;
		}

		g.drawImage(offScreenCanvas,0,0,this);
		if (draggedPiece != null) {
			drawPiece(g, draggedPiece);
		}
	}

	
	/**
	 * This method will set the dragged piece to be a new piece from the pile if the click position
	 * was in the pile. The pile is a row of piece images near the bottom of the GUI.
	 * @param x the x position of the click
	 * @param y the y position of the click
	 * @return true if a dragged piece was set
	 */
	public boolean setDraggedPieceFromPile(int x, int y) {
		double width = getWidth();
		double height = getHeight();
		if (y > balloonBaseline - variablePieceHeight  && y < balloonBaseline) {
			if (x > pileLeftMargin && x < pileLeftMargin + variablePieceWidth) {
				draggedPiece = new Piece(-1, 1, pileLeftMargin / width, (balloonBaseline - variablePieceHeight) / height);
			}
		}
		if (y > boxBaseline - variablePieceHeight	&& y < boxBaseline) {		
			if (x > pileLeftMargin && x < pileLeftMargin + variablePieceWidth) {
					draggedPiece = new Piece(1, 1, pileLeftMargin / width,(boxBaseline - variablePieceHeight ) / height);
			}
		}
		if (y > balloonBaseline - balloonHeight && y < balloonBaseline) {
			double balloonBeginX = pileLeftMargin + variablePieceWidth + pieceSpacing;
			if (x > balloonBeginX && x < balloonBeginX + (pieceWidth + pieceSpacing) * 10) {
				int balloonNumber = ((int)(x - balloonBeginX)) / (pieceWidth + pieceSpacing);
				draggedPiece = new Piece(-1 * (balloonNumber + 1), 0, (balloonBeginX + balloonNumber * (pieceWidth + pieceSpacing))/width, (balloonBaseline - balloonHeight) / height);
			}
		}
		if (y > boxBaseline - boxHeight	&& y < boxBaseline) {
			double boxBeginX = pileLeftMargin + variablePieceWidth + pieceSpacing;
			if (x > boxBeginX && x < boxBeginX + (pieceWidth + pieceSpacing) * 10) {
				int boxNumber = ((int)(x - boxBeginX)) / (pieceWidth + pieceSpacing);
				draggedPiece = new Piece(boxNumber + 1, 0, (boxBeginX + boxNumber * (pieceWidth + pieceSpacing))/width, (boxBaseline - boxHeight) / height);
			}
		}
		
		if (draggedPiece != null) {
			draggedPieceOffsetX = (int)(x - draggedPiece.getRelativeX() * width);
			draggedPieceOffsetY = (int)(y - draggedPiece.getRelativeY() * height);
			return true;
		}
		return false;
		
	}


	//this updates the position of the dragged piece
	public void mouseDragged(MouseEvent e) {
		if (draggedPiece != null) {
			draggedPiece.setRelativeX((double)(e.getX() - draggedPieceOffsetX) / getWidth());
			draggedPiece.setRelativeY((double)(e.getY() - draggedPieceOffsetY) / getHeight());
		}
		repaint();
	}

	public void mouseMoved(MouseEvent e) {
		//does nothing
	}
}