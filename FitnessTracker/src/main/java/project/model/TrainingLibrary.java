package project.model;
import lombok.Getter;

import java.sql.*;
import java.util.HashMap;

public final class TrainingLibrary extends Training {
	@Getter
	private HashMap<Integer, Exercise> exerciseMap = new HashMap<>();
	
	public void initialize() {
		String url = "jdbc:postgresql://localhost:5432/template1";
		String username = "postgres";
		String password = "1111";
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
			String query = "SELECT id, name, description FROM exercises";
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);

			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String name = resultSet.getString("name");
				String description = resultSet.getString("description");

				exerciseMap.put(id, new FlexibleExercise(name, description, id));
			}
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
