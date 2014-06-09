package net.mvla.la.cs.scale;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JFrame;

public class AlgebraScaleProgram {
	
	public static void main(String[] args) {
		
		JFrame window = new JFrame();
		AlgebraScaleController controller = new AlgebraScaleController();
		window.setSize(800, 600);
		window.setContentPane(controller.getCurrentView());
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		window.setVisible(true);


	}
}
