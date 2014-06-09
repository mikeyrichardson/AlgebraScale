package net.mvla.la.cs.as.views;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;

import net.mvla.la.cs.as.model.UserDatabase;
import net.mvla.la.cs.as.model.UserDatabase.User;

public class UserNameView extends JPanel {
	
	
	JLabel welcomeMessage;
	BufferedImage splashScreen;
	JPanel buttonPanel;
	JButton[] userButtons;
	JPanel userButtonPanel;
	JLabel createNewUserLabel;
	JTextField createNewUserTextField;
	JButton createNewUserButton;
	JPanel createNewUserPanel;

	public UserNameView(ArrayList<UserDatabase.User> users) {
		this.setLayout(null);
		welcomeMessage = new JLabel("<html><center>Welcome to Algebra Scale!<br>Choose your user name.</center></html>");
		welcomeMessage.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 36));
		this.add(welcomeMessage);
		welcomeMessage.setBounds(170, 10, 600, 100);
		try {
			splashScreen = ImageIO.read(getClass().getClassLoader().getResource("splashScreen.png"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		userButtonPanel = new JPanel();
		if (users != null) {
			userButtons = new JButton[users.size()];
			for (int i = 0; i < users.size(); i++) {
				userButtons[i] = new JButton(users.get(i).userName);
				userButtons[i].setActionCommand("User:" + users.get(i).userName);
				userButtonPanel.add(userButtons[i]);
			}
		}
		
		createNewUserLabel = new JLabel("New user name: ");
		createNewUserTextField = new JTextField("Type your user name here.");
		createNewUserButton = new JButton("Create New User");
		createNewUserPanel = new JPanel();
		createNewUserPanel.add(createNewUserLabel);
		createNewUserPanel.add(createNewUserTextField);
		createNewUserPanel.add(createNewUserButton);

		buttonPanel = new JPanel();
		buttonPanel.setBounds(0, 120, 800, 130);
		buttonPanel.setLayout(new GridLayout(2,1));
		buttonPanel.add(userButtonPanel);
		buttonPanel.add(createNewUserPanel);
		this.add(buttonPanel);
				
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(splashScreen, 10, 230, this);
	}
	
	public void addButtonListener(ActionListener listener) {
		for (int i = 0; i < userButtons.length; i++) {
			userButtons[i].addActionListener(listener);
		}
		createNewUserButton.addActionListener(listener);
		createNewUserTextField.addActionListener(listener);
	}

}
