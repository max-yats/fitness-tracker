package project.model;
import java.util.HashMap;

public final class TrainingLibrary extends Training {
	private HashMap<Integer, Exercise> exerciseMap = new HashMap<>();

	public void initialize() {
		exerciseMap.put(1001, new OrdinaryExercise("Приседания", "упражнение для ног и ягодиц, выполняется, опускаясь в положение, как будто вы садитесь на стул, а затем поднимаетесь.", 3, 20, 1001));
		exerciseMap.put(1002, new OrdinaryExercise("Подтягивания", "упражнение для верхней части тела, выполняется подтягиванием тела вверх, держась за перекладину.", 2, 5,1002));
		exerciseMap.put(1003, new OrdinaryExercise("Прыжки на скакалке", "кардиоупражнение, которое также тренирует координацию, выполняется с прыжками через скакалку.", 3, 90, 1003));
		exerciseMap.put(1004, new OrdinaryExercise("Прыжки в длину", "очень полезное упражнение - мамой клянусь", 1, 10, 1004));
		exerciseMap.put(1005, new OrdinaryExercise("Махи ногами", "упражнение для ног и ягодиц, выполняется махая ногой вперед и назад.", 3, 20, 1005));
		exerciseMap.put(1006, new OrdinaryExercise("Лодка", "упражнение для спины и пресса, выполняется, лежа на животе и поднимая туловище и ноги от пола, формируя форму лодки.", 3, 30, 1006));

		exerciseMap.put(2001, new TimeExercise("Планка на прямых руках", "статическая нагрузка мышц груди", 1, 10.0f, 2001));
		exerciseMap.put(2002, new TimeExercise("Прыжки на скакалке", "кардиоупражнение, которое также тренирует координацию, выполняется с прыжками через скакалку.", 3, 10.0f, 2002));
		exerciseMap.put(2003, new TimeExercise("Бег на месте", "кардиоупражнение, которое можно выполнять дома, бегая на месте.", 2, 30.0f, 2003));
		exerciseMap.put(2004, new TimeExercise("Планка", "Упражнение для укрепления кора", 2, 90.0f, 2004));

		exerciseMap.put(3001, new WeightExercise("Жим лежа", "Упражнение для развития грудных мышц", 2, 10, 25, 3001));
		exerciseMap.put(3002, new WeightExercise("Подъём гантели", "Чтоб бицуху качать", 6, 10, 10, 3002));
	}

	public HashMap<Integer, Exercise> getExerciseMap() {
		return exerciseMap;
	}
}
