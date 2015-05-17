package net.mvla.la.cs.as.main;

import javax.swing.JFrame;

import net.mvla.la.cs.as.controllers.AlgebraScaleController;

public class AlgebraScaleMain {
	
	public static void main(String[] args) {
		
		JFrame window = new JFrame();
		new AlgebraScaleController(window);
		window.setSize(800, 600);		
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);

	}

}
