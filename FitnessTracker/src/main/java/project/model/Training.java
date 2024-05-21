package project.model;

import java.util.HashMap;

public class Training {
	private HashMap<Integer, Exercise> exerciseMap = new HashMap<>();
	private static int idCounter = 1;
	public static int getIdCounter() {
		return idCounter;
	}

	public static void incrementIdCounter() {
		idCounter++;
	}
}
