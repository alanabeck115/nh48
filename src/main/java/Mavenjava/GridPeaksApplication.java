package Mavenjava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class GridPeaksApplication {
	
	 public static void main(String[] args) {

	        SpringApplication app =
	                new SpringApplication(GridPeaksApplication.class);

	        app.setDefaultProperties(
	                java.util.Map.of("server.port", "8081")
	        );

	        app.run(args);
}
	 @Bean
	 CommandLineRunner testDatabase(JdbcTemplate jdbc) {
	     return args -> {
	         String result = jdbc.queryForObject(
	             "SELECT current_database()",
	             String.class
	         );

	         System.out.println("CONNECTED TO DATABASE: " + result);

	         Integer count = jdbc.queryForObject(
	             "SELECT COUNT(*) FROM gridpeaks.mountains",
	             Integer.class
	         );

	         System.out.println("MOUNTAINS FOUND: " + count);
	     };
	 }
}