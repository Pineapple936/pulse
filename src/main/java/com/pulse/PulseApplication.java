package com.pulse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PulseApplication {
	private static final Logger log = LoggerFactory.getLogger(PulseApplication.class);

	public static void main(String[] args) {
		log.info("Starting Pulse application");
		SpringApplication.run(PulseApplication.class, args);
	}

}
