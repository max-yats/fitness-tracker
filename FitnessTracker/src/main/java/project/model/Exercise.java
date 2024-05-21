package project.model;

import project.controller.FitnessBot;

public interface Exercise {

	String getName();
	String getDescription();
	Integer getExerciseId();
	int getSets();
	int getRepetitions();
	int getWeightPerRep();
	float getTimeInSeconds();
	void startExercise(long chatId, FitnessBot bot);
}
