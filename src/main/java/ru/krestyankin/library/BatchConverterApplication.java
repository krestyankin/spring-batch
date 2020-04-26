package ru.krestyankin.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.sql.SQLException;

@SpringBootApplication
@EnableMongoRepositories
public class BatchConverterApplication {

	public static void main(String[] args) throws SQLException {
		SpringApplication.run(BatchConverterApplication.class, args);
	}

}
