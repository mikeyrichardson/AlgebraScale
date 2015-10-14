package net.mikeyrichardson.algebrascale.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;

public class ProblemView extends JPanel {
	
	public ScaleDisplayPanel displayPanel = new ScaleDisplayPanel();
	
	JPanel navigationPanel = new JPanel();
	public JButton previousExerciseButton = new JButton("Previous Exercise");
	private JButton lessonMenuButton = new JButton("Lesson Menu");
	public JButton nextExerciseButton = new JButton("Next Exercise");
	private JPanel equationPanel = new JPanel();
	public JLabel equationDisplayLabel = new JLabel("Enter an equation");
	private JPanel messagePanel = new JPanel();
	public JLabel messageDisplayLabel = new JLabel("");
	
	
	
	private JPanel checkAnswerPanel = new JPanel();
	private JLabel variableInputLabel = new JLabel("x = ");
	public JTextField variableInputTextField = new JTextField(5);
	private JLabel leftSideInputLabel = new JLabel("Left Side of Equation = ");
	public JTextField leftSideInputTextField = new JTextField(5);
	private JLabel rightSideInputLabel = new JLabel("Right Side of Equation = ");
	public JTextField rightSideInputTextField = new JTextField(5);
	private JButton checkAnswerButton = new JButton("Check Answer");
	
	private JPanel inputPanel = new JPanel();
	public JLabel equationInputLabel = new JLabel("Equation:");
	public JTextField equationInputTextField = new JTextField(20);
	public JButton submitEquationButton = new JButton("Submit Equation");
	private JButton clearAllButton = new JButton("Clear All");
	private JButton boxesToBalloonsButton = new JButton("Boxes <==> Balloons");
	
	
	public ProblemView() {
		
		setLayout(new BorderLayout());
		
		JPanel textDisplayPanel = new JPanel();
		textDisplayPanel.setLayout(new GridLayout(2,1));
		
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new GridLayout(2,1));
						
		add(displayPanel);
		
		JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
		Dimension d = separator.getPreferredSize();  
        d.width = getWidth() / 4;
        separator.setPreferredSize(d); 
        
		navigationPanel.add(previousExerciseButton);
		navigationPanel.add(separator);
		navigationPanel.add(lessonMenuButton);
		navigationPanel.add(separator);
		navigationPanel.add(nextExerciseButton);
		textDisplayPanel.setLayout(new GridLayout(3,1));

		textDisplayPanel.add(navigationPanel);
		
		equationDisplayLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 32));
		equationPanel.add(equationDisplayLabel);
		textDisplayPanel.add(equationPanel);
		
		messageDisplayLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
		messagePanel.add(messageDisplayLabel);
		textDisplayPanel.add(messagePanel);
		
		inputPanel.add(equationInputLabel);
		inputPanel.add(equationInputTextField);
		equationInputTextField.setActionCommand("Submit Equation");
		inputPanel.add(submitEquationButton);
		
		inputPanel.add(clearAllButton);
		inputPanel.add(boxesToBalloonsButton);
		controlPanel.add(inputPanel);
		
		checkAnswerPanel.add(variableInputLabel);
		checkAnswerPanel.add(variableInputTextField);
		checkAnswerPanel.add(leftSideInputLabel);
		checkAnswerPanel.add(leftSideInputTextField);
		checkAnswerPanel.add(rightSideInputLabel);
		checkAnswerPanel.add(rightSideInputTextField);
		checkAnswerPanel.add(checkAnswerButton);
		controlPanel.add(checkAnswerPanel);
		
		
		
		add(textDisplayPanel, BorderLayout.NORTH);
		add(controlPanel, BorderLayout.SOUTH);
	}
	
	public void addDisplayPanelMouseListener(MouseListener listener) {
		displayPanel.addMouseListener(listener);
	}
	
	public void addDisplayPanelComponentListener(ComponentListener listener) {
		displayPanel.addComponentListener(listener);
	}
	
	public void addButtonListener(ActionListener listener) {
		submitEquationButton.addActionListener(listener);
		checkAnswerButton.addActionListener(listener);
		clearAllButton.addActionListener(listener);
		equationInputTextField.addActionListener(listener);
		boxesToBalloonsButton.addActionListener(listener);
		previousExerciseButton.addActionListener(listener);
		lessonMenuButton.addActionListener(listener);
		nextExerciseButton.addActionListener(listener);
	}
	
	
	

}
