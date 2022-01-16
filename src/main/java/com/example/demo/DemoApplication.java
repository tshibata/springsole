package com.example.demo;

import java.io.*;
import java.nio.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.handler.MappedInterceptor;

@SpringBootApplication
@EnableScheduling
public class DemoApplication {

	private static ConfigurableApplicationContext context;

	public static void main(String[] args) throws SQLException, IOException {
		context = SpringApplication.run(DemoApplication.class, args);
	}

	public static void shutdown() {
		context.close();
		System.exit(0);
	}

	@Bean
	PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	MappedInterceptor interceptor() {
		return new MappedInterceptor(new String[]{"/**"}, new XSInterceptor());
	}
}
