package net.mvla.la.cs.scale;

import javax.swing.JApplet;
import javax.swing.JFrame;

public class AlgebraScaleApplet extends JApplet {

	public void init() {
		AlgebraScaleController theController = new AlgebraScaleController();
		setContentPane(theController.getCurrentView());

	}



}
