package project.model;

import lombok.Getter;

import java.util.HashMap;

public class Training {
	private HashMap<Integer, Exercise> exerciseMap = new HashMap<>();
	@Getter
	private static int idCounter = 1;

	public static void incrementIdCounter() {
		idCounter++;
	}
	
}
