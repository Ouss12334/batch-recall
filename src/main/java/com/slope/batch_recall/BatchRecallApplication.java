package com.slope.batch_recall;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @EnableBatchProcessing
@SpringBootApplication
public class BatchRecallApplication {

	public static void main(String[] args) {
		SpringApplication.run(BatchRecallApplication.class, args);
	}

}
