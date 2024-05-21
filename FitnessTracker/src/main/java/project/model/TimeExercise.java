package project.model;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import project.controller.FitnessBot;

import java.util.Timer;
import java.util.TimerTask;

public class TimeExercise implements Exercise {
    private Integer id;
    private String name;
    private String description;
    private float timeInSeconds;
    private int sets; // Количество подходов/повторений
    private static int idCounter = 1;
    private boolean isRunning = false;

    public TimeExercise(String name, String description, int sets, float timeInSeconds) {
        this.name = name;
        this.description = description;
        this.timeInSeconds = timeInSeconds;
        this.sets = sets;

        generateId();
    }

    public TimeExercise(String name, String description, int sets, float timeInSeconds, int id) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.sets = sets;
        this.timeInSeconds = timeInSeconds;
    }
    public void startExercise(long chatId, FitnessBot bot) {
        try {
            Timer timer = new Timer();
            final int[] currentSet = {1}; // Переменная для отслеживания текущего подхода

            // Запуск одного подхода
            Runnable singleSetRunnable = () -> {
                if (currentSet[0] <= sets) {
                    System.out.println("Выполнение подхода №" + currentSet[0]);
                    currentSet[0]++;

                    // Отправляем сообщение по завершении текущего подхода
                    SendMessage response = new SendMessage();
                    response.setChatId(String.valueOf(chatId));
                    response.setText("Время подхода №" + (currentSet[0] - 1) + " завершено.");

                    bot.sendAnswerMessage(response);
                }
            };

            // Здапуск нескольких подходов
            for (int i = 0; i < sets; i++) {
                long delay = (i + 1) * (long) timeInSeconds * 1000; // Время в мс
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        singleSetRunnable.run();
                    }
                }, delay);
            }
        } catch (Exception e) {
            System.err.println("An unexpected error occurred:");
            e.printStackTrace();
        }
    }

    public void stopExercise(long chatId, FitnessBot bot) {
        if (isRunning) {
            isRunning = false;
            try {
                SendMessage stopMessage = new SendMessage();
                stopMessage.setChatId(String.valueOf(chatId));
                stopMessage.setText("Упражнение " + name + " досрочно завершено");
                bot.execute(stopMessage);
            }
            catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getSets() {
        return sets;
    }

    public float getTimeInSeconds() {
        return timeInSeconds;
    }

    public Integer getExerciseId() {
        return id;
    }

    private void generateId() {
        id = 11000 + idCounter;
        idCounter++;
    }

    public int getRepetitions() {
        return 0;
    }

    public int getWeightPerRep() {
        return 0;
    }

}