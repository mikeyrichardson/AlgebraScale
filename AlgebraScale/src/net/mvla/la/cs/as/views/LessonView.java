package net.mvla.la.cs.as.views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;

import net.mvla.la.cs.as.model.UserDatabase;
import net.mvla.la.cs.as.model.UserDatabase.Exercise;
import net.mvla.la.cs.as.model.UserDatabase.Lesson;
import net.mvla.la.cs.as.model.UserDatabase.User;

public class LessonView extends JPanel {
	
	JPanel[] lessonPanelArray;

	public LessonView(UserDatabase.User user, ActionListener listener) {
		lessonPanelArray = new JPanel[user.lessonList.size()];
		for (int i = 0; i < lessonPanelArray.length; i++) {
			lessonPanelArray[i] = new JPanel();
			lessonPanelArray[i].setPreferredSize(new Dimension(800,50));
			lessonPanelArray[i].setLayout(new FlowLayout(FlowLayout.LEFT));
			UserDatabase.Lesson lesson = user.lessonList.get(i);
			lessonPanelArray[i].add(new JLabel( lesson.lessonName));
			ArrayList<UserDatabase.Exercise> exercises = user.lessonList.get(i).exerciseList;
			for (int j = 0; j < exercises.size() ; j++) {
				UserDatabase.Exercise exercise = exercises.get(j);
				JButton button = new JButton("#" + exercise.exerciseNumber);
				if (exercise.isCompleted) {
					button.setForeground(Color.GREEN);
				}
				button.setActionCommand("Lesson:" + lesson.lessonNumber + "#" + exercise.exerciseNumber);
				button.addActionListener(listener);
				lessonPanelArray[i].add(button);
			}
			this.add(lessonPanelArray[i]);
			
		}
		JButton typeOwnEquationsButton = new JButton("Type in your own equations");
		typeOwnEquationsButton.addActionListener(listener);
		this.add(new JPanel().add(typeOwnEquationsButton));
	}

}
