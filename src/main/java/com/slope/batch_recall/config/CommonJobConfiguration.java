package com.slope.batch_recall.config;

import static com.slope.batch_recall.batch.Constants.CHUNK_SIZE;

import java.util.UUID;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * common config to simple and multi thread
 */
@Slf4j
@Configuration
public class CommonJobConfiguration {

  @Bean
  public JobParametersIncrementer incrementer() {
    return new JobParametersIncrementer() {
      public JobParameters getNext(JobParameters parameters) {
        log.info("job parameters {}", parameters);
        return new JobParametersBuilder(parameters)
            .addString("job-id", UUID.randomUUID().toString(), true)
            .toJobParameters();
      }
    };
  }

  @Bean
  ChunkListener chunkListener() {
    return new ChunkListener() {
      private static int chunkCounter = 0;

      public void afterChunk(ChunkContext context) {
        // 100K || 1M /// total 29 710 000 oct-2019 13m28s console.log(writer)
        if (
          // (chunkCounter <= 100000 || chunkCounter >= 1000000) && 
        chunkCounter % 10000 == 0) {
          log.info("processed {} rows", chunkCounter);
        }
        chunkCounter += CHUNK_SIZE;
      }
    };
  }

}
