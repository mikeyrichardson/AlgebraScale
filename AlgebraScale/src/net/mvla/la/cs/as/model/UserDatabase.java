package net.mvla.la.cs.as.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;


public class UserDatabase implements Serializable {

	private static final long serialVersionUID = 4282917483957283L;
	public ArrayList<User> userList;
	public String exerciseSourceFile;
	
	public UserDatabase(String fileName) {
		userList = new ArrayList<User>();
		exerciseSourceFile = fileName;
	}
	
	public void export(File file) throws IOException {
		ObjectOutputStream out;
		FileOutputStream stream = new FileOutputStream(file);
		out = new ObjectOutputStream(stream);
		out.writeObject(this);
		out.close();
	}

	public String toString() {
		if (userList.size() == 0) {
			return "No users.";
		}
		String result = "";
		for (User user : userList) {
			result += user.userName + "\n";
		}
		return result;
	}

	
	public void addUser(String userName) {
		userList.add(new User(userName));
	}
	
	
	
	public class User implements Serializable {
		private static final long serialVersionUID = 4282917483957284L;

		public String userName;
		public ArrayList<Lesson> lessonList;
		
		public User(String name) {
			userName = name;
			lessonList = new ArrayList<Lesson>();
			try {
				createLessonList();
			} catch (FileNotFoundException ex) {
				System.out.println(ex.getMessage());
			}
			
		}
		
		public void addLesson(int number, String name) {
			lessonList.add(new Lesson(number, name));
		}
		
		public Lesson getLesson(int lessonNumber) { 
			return lessonList.get(lessonNumber - 1);
		}
		
		public void createLessonList() throws FileNotFoundException {
			Scanner lessonFileScanner = new Scanner(ClassLoader.getSystemResourceAsStream(exerciseSourceFile));
			int lessonCounter = 0;
			int exerciseCounter = 1;
			while (lessonFileScanner.hasNextLine()) {
				String line = lessonFileScanner.nextLine();
				if (line.startsWith("Lesson:")) {
					lessonCounter++;
					addLesson(lessonCounter, line.substring(7).trim()); //Remove "Lesson:" from line
					exerciseCounter = 1;
				}
				else if (line.startsWith("#")) {
					getLesson(lessonCounter).addExercise(exerciseCounter, line.substring(1).trim());
					exerciseCounter++;

				}
			}

		}
		
		public void setComplete(int lessonNumber, int exerciseNumber) {
			lessonList.get(lessonNumber - 1).exerciseList.get(exerciseNumber - 1).isCompleted = true;
		}
		
	}
	
	public class Lesson implements Serializable {
		private static final long serialVersionUID = 4282917483957285L;
		public int lessonNumber;
		public String lessonName;
		public ArrayList<Exercise> exerciseList;
		
		public Lesson(int number, String name) {
			lessonNumber = number;
			lessonName = name;
			exerciseList = new ArrayList<Exercise>();
		}
		
		public void addExercise(int number, String str) {
			exerciseList.add(new Exercise(number, str));
		}
		
		public String getExerciseString(int number) {
			return exerciseList.get(number - 1).exerciseString;
		}
		
		public String toString() {
			return "" + lessonNumber + ": " + lessonName;
		}
	}
	
	public class Exercise implements Serializable {
		
		private static final long serialVersionUID = 4282917483957286L;	
		public int exerciseNumber;
		public String exerciseString;
		public boolean isCompleted;
		
		public Exercise(int number, String str) {
			exerciseNumber = number;
			exerciseString = str;
			isCompleted = false;
		}
		
}

}
