package com.cruz_sur.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}

@Component
class PrintEnvVariablesRunner implements CommandLineRunner {

	@Value("${DB_USERNAME}")
	private String dbUsername;

	@Value("${DB_PASSWORD}")
	private String dbPassword;

	@Value("${CLOUDINARY_CLOUD_NAME}")
	private String cloudinaryCloudName;

	@Value("${CLOUDINARY_API_KEY}")
	private String cloudinaryApiKey;

	@Value("${CLOUDINARY_API_SECRET}")
	private String cloudinaryApiSecret;

	@Value("${GOOGLE_CLIENT_ID}")
	private String googleClientId;

	@Value("${GOOGLE_CLIENT_SECRET}")
	private String googleClientSecret;

	@Value("${JWT_SECRET}")
	private String jwtSecret;

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Environment Variables:");
		System.out.println("DB_USERNAME: " + dbUsername);
		System.out.println("DB_PASSWORD: " + dbPassword);
		System.out.println("CLOUDINARY_CLOUD_NAME: " + cloudinaryCloudName);
		System.out.println("CLOUDINARY_API_KEY: " + cloudinaryApiKey);
		System.out.println("CLOUDINARY_API_SECRET: " + cloudinaryApiSecret);
		System.out.println("GOOGLE_CLIENT_ID: " + googleClientId);
		System.out.println("GOOGLE_CLIENT_SECRET: " + googleClientSecret);
		System.out.println("JWT_SECRET: " + jwtSecret);
	}
}