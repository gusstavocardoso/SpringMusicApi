package com.example.musicapi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

@SpringBootApplication
public class MusicapiApplication implements ApplicationListener<ContextRefreshedEvent> {

	@Value("${server.port:8080}")
	private String serverPort;

	public static void main(String[] args) {
		SpringApplication.run(MusicapiApplication.class, args);
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		System.out.println("Application is running on port: " + serverPort);
	}
}
