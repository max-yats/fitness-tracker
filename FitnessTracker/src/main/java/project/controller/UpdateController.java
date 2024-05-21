package project.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import project.model.Exercise;
import project.model.OrdinaryExercise;
import project.model.WeightExercise;

@Component
public class UpdateController {

	private FitnessBot bot;
	private Map<Long, OrdinaryExercise> activeOrdinaryExercises = new HashMap<>();

	private Map<Long, WeightExercise> activeWeightExercises = new HashMap<>();

	private static final Logger log = org.apache.log4j.Logger.getLogger(FitnessBot.class);

	public void registerBot(FitnessBot bot) {
        this.bot = bot;
        }

	public void processUpdate(Update update) {
		if (update == null) {
			log.error("Received update is null");
            return;
		}

		if (update.hasMessage()) {
			defineCommand(update.getMessage());
		}
		else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();

            SendMessage response = new SendMessage();

            if (callbackData.equals("YES_BUTTON")) {
            	response.setText("(Тестовое) Вы нажали \"да\"");
            	response.setChatId(update.getCallbackQuery().getMessage().getChatId());
                bot.sendAnswerMessage(response);
            }
            else if (callbackData.equals("NO_BUTTON")) {
            	response.setChatId(update.getCallbackQuery().getMessage().getChatId());
            	response.setText("(Тестовое) Вы нажали \"нет\"");
                bot.sendAnswerMessage(response);
            }
			else {
			log.error("Unsupported message type is received: " + update);
            }
		}
	}

	private void describeExercise(String command, Message msg) {
		SendMessage response = new SendMessage();
		response.setChatId(msg.getChatId().toString());
		response.setText(bot.getExercises().getExerciseMap().get(Integer.valueOf(command)).getName() + "\n" +
						bot.getExercises().getExerciseMap().get(Integer.valueOf(command)).getDescription()
							+ "\n\nДобавить упражнение в вашу тренировку?");

	InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        InlineKeyboardButton yesButton = new InlineKeyboardButton();

        yesButton.setText("Да");
        yesButton.setCallbackData("YES_BUTTON");

        InlineKeyboardButton noButton = new InlineKeyboardButton();

        noButton.setText("Нет");
        noButton.setCallbackData("NO_BUTTON");

        rowInLine.add(yesButton);
        rowInLine.add(noButton);

        rowsInLine.add(rowInLine);

        markupInLine.setKeyboard(rowsInLine);
        response.setReplyMarkup(markupInLine);

		bot.sendAnswerMessage(response);
		log.debug(msg.getText());
	}

	private void defineCommand(Message msg) {
		String command = msg.getText();

		if (command.startsWith("/start ")) {
			command = command.replaceAll("/start ", "");
			describeExercise(command, msg);
		} else if (command.startsWith("/startExercise")) {
			startExercise(command, msg);
		} else {
			switch (command) {
				case "/start":
					sendWelcomeMessage(msg);
					break;
				case "/register":
					registerUser(msg);
					break;
				case "/tren":
					viewExercises(msg);
					break;
				case "/test":
					testOutput(msg);
					break;
				case "/stat":
					viewStat(msg);
					break;
				case "/delete":
					confirmAccountDeletion(msg);
					break;
				case "ПОДТВЕРДИТЬ УДАЛЕНИЕ":
					deleteUser(msg);
					break;
				case "/stop":
					stopExercise(msg);
					break;
				case "/finishSet":
					finishSet(msg);
					break;
				default:
					// Обработка неизвестной команды
					SendMessage response = new SendMessage();
					response.setChatId(msg.getChatId().toString());
					response.setText("Неизвестная команда. Попробуйте снова.");
					bot.sendAnswerMessage(response);
					break;
			}
		}
	}

	private void sendWelcomeMessage(Message msg) {
		System.out.println("Метод sendWelcomeMessage (/start) вызван");

		SendMessage response = new SendMessage();
		response.setChatId(msg.getChatId().toString());
		response.setText("Привет! Я бот для контроля за спортивными результатами в тренировках. " +
				"Отправьте команду /tren, чтобы посмотреть доступные упражнения.");
		bot.sendAnswerMessage(response);
	}

	public void viewExercises(Message msg) {
		System.out.println("Метод viewExercises (/tren) вызван");

		SendMessage sendMessage = new SendMessage();
		  StringBuilder helpStr = new StringBuilder(new String());
		  sendMessage.setChatId(msg.getChatId());
		  for (HashMap.Entry<Integer, Exercise> entry : bot.getExercises().getExerciseMap().entrySet()) {
			  helpStr.append("<a href='" + "https://t.me/FitTrackDomovonokBot?start=").append(entry.getKey().toString())
					  .append("'>").append(entry.getValue().getName()).append("</a>\n");
		  }
		  sendMessage.setText(helpStr.toString());
		  sendMessage.enableHtml(true);
		  bot.sendAnswerMessage(sendMessage);
		  log.debug(msg.getText());
	}

	public void testOutput(Message msg) {
		System.out.println("Метод testOutput (/test) вызван");
		SendMessage response = new SendMessage();
		response.setChatId(msg.getChatId().toString());
		response.setText("Тестовое сообщение - Цой жив");
		bot.sendAnswerMessage(response);
		log.debug(msg.getText());
	}

	public void viewStat(Message msg) {
		String chatIdStr = msg.getChatId().toString();
		Long chatId = Long.parseLong(chatIdStr);

		String sql = "SELECT exercises, info FROM users WHERE chat_id =?";

		try {
			List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, chatId);

			if (!results.isEmpty()) {
				Map<String, Object> result = results.get(0);
				Integer[] exercises = (Integer[]) result.get("exercises");
				String info = (String) result.get("info");

				StringBuilder statMessage = new StringBuilder("Ваша статистика:\n");
				statMessage.append("Информация: ").append(info).append("\n");
				statMessage.append("Выполненные упражнения: \n");

				for (Integer exerciseId : exercises) {
					// Здесь можно добавить дополнительный запрос для получения названия упражнения по ID
					// Для примера просто выводим ID упражнения
					statMessage.append("- Упражнение ID: ").append(exerciseId).append("\n");
				}

				SendMessage response = new SendMessage();
				response.setChatId(chatIdStr);
				response.setText(statMessage.toString());
				bot.sendAnswerMessage(response);
			}
			else {
				SendMessage response = new SendMessage();
				response.setChatId(chatIdStr);
				response.setText("Статистика не найдена.");
				bot.sendAnswerMessage(response);
			}
		}
		catch (Exception e) {
			log.error("Ошибка при получении статистики пользователя: " + e.getMessage());
			SendMessage response = new SendMessage();
			response.setChatId(chatIdStr);
			response.setText("Произошла ошибка при получении статистики.");
			bot.sendAnswerMessage(response);
		}
	}

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public void registerUser(Message msg) {
		System.out.println("Метод registerUser (/register) вызван");
		log.debug(msg.getText());

		String chatIdStr = msg.getChatId().toString();
		Long chatId = Long.parseLong(chatIdStr);

		List<Long> userIds = jdbcTemplate.queryForList("SELECT id FROM users WHERE chat_id =?",
				new Object[]{chatId}, Long.class);

		if (userIds.isEmpty()) {
			jdbcTemplate.update("INSERT INTO users (chat_id) VALUES (?)", chatId);
			SendMessage response = new SendMessage();
			response.setChatId(chatIdStr);
			//response.setText("Регистрация прошла успешно!");
			response.setText("Счастилового путешествия в Казахстан!");
			bot.sendAnswerMessage(response);
		}
		else {
			SendMessage response = new SendMessage();
			response.setChatId(chatIdStr);
			//response.setText("Вы уже зарегистрированы!");
			response.setText("АMOGUS!");
			bot.sendAnswerMessage(response);
		}
	}

	private void confirmAccountDeletion(Message msg) {
		System.out.println("Метод confirmAccountDeletion вызван");
		log.debug(msg.getText());

		SendMessage response = new SendMessage();
		response.setChatId(msg.getChatId().toString());
		response.setText("Вы уверены, что хотите удалить свой аккаунт? " +
				"Отправьте 'ПОДТВЕРДИТЬ УДАЛЕНИЕ' для подтверждения.");
		bot.sendAnswerMessage(response);
	}

	private void deleteUser(Message msg) {
		System.out.println("Метод deleteUser (/delete) вызван");
		log.debug(msg.getText());

		String chatIdStr = msg.getChatId().toString();
		Long chatId = Long.parseLong(chatIdStr);
		jdbcTemplate.update("DELETE FROM users WHERE chat_id =?", chatId);
		SendMessage response = new SendMessage();
		response.setChatId(chatIdStr);
		//response.setText("Ваш аккаунт был успешно удалён.");
		response.setText("Попутного ветра!");
		bot.sendAnswerMessage(response);
	}

	private void startExercise(String command, Message msg) {
		System.out.println("Метод startExercise (/startExercise [id упражнения]) вызван");
		log.debug(msg.getText());
		try {
			String[] parts = command.split(" ");
			if (parts.length!= 2) {
				throw new NumberFormatException("Invalid command format");
			}
			int exerciseId = Integer.parseInt(parts[1]);
			Exercise exercise = bot.getExercises().getExerciseMap().get(exerciseId);
			if (exercise!= null) {
				if (exercise instanceof OrdinaryExercise) {
					activeOrdinaryExercises.put(msg.getChatId(), (OrdinaryExercise) exercise);
					System.out.println("Обычное упражнение добавлено в activeExercises для чата: " + msg.getChatId());
					exercise.startExercise(msg.getChatId(), bot);
				} else if (exercise instanceof WeightExercise) {
					activeWeightExercises.put(msg.getChatId(), (WeightExercise) exercise);
					System.out.println("Упражнение с весом добавлено в activeWeightExercises для чата: " + msg.getChatId());
					exercise.startExercise(msg.getChatId(), bot);
				}
			} else {
				throw new IllegalArgumentException("Exercise not found");
			}
		} catch (NumberFormatException e) {
			log.error("Invalid exercise ID format: " + e.getMessage());
			SendMessage response = new SendMessage();
			response.setChatId(msg.getChatId().toString());
			response.setText("Неверный формат команды. Используйте: /startExercise <exerciseId>");
			bot.sendAnswerMessage(response);
		} catch (IllegalArgumentException e) {
			log.error("Error starting exercise: " + e.getMessage());
			SendMessage response = new SendMessage();
			response.setChatId(msg.getChatId().toString());
			response.setText("Упражнение не найдено. Проверьте ID и попробуйте снова.");
			bot.sendAnswerMessage(response);
		}
	}

	private void finishSet(Message msg) {
		System.out.println("Метод finishSet (/finishSet) вызван");
		log.debug(msg.getText());

		long chatId = msg.getChatId();
		OrdinaryExercise ordinaryExercise = activeOrdinaryExercises.get(chatId);
		WeightExercise weightExercise = activeWeightExercises.get(chatId);
		SendMessage response = new SendMessage();

		if (ordinaryExercise!= null) {
			System.out.println("Текущий подход для обычного упражнения из чата " + chatId + " завершено");
			ordinaryExercise.finishSet(chatId, bot);
		} else if (weightExercise!= null) {
			System.out.println("Текущий подход для упражнения с весом из чата " + chatId + " завершено");
			weightExercise.finishSet(chatId, bot);
		} else {
			response.setText("Упражнение не запущено.");
			System.out.println("Активное упражнение из чата " + chatId + " не найдено");
			bot.sendAnswerMessage(response);
		}
	}

	private void stopExercise(Message msg) {
		System.out.println("Метод stopExercise (/stop) вызван");
		log.debug(msg.getText());

		long chatId = msg.getChatId();
		OrdinaryExercise ordinaryExercise = activeOrdinaryExercises.remove(chatId);
		WeightExercise weightExercise = activeWeightExercises.remove(chatId);

		if (ordinaryExercise!= null) {
			ordinaryExercise.stopExercise(chatId, bot);
			System.out.println("Обычное упражнение остановлено и удалено из activeOrdinaryExercises для чата: " + chatId);
		} else if (weightExercise!= null) {
			weightExercise.stopExercise(chatId, bot);
			System.out.println("Упражнение с весом остановлено и удалено из activeWeightExercises для чата: " + chatId);
		} else {
			System.out.println("Активное упражнение из чата: " + chatId + " не найдено");
		}
	}
}

