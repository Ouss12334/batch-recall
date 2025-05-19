package com.slope.batch_recall.config;

import static com.slope.batch_recall.config.Constants.CHUNK_SIZE;

import javax.sql.DataSource;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.slope.batch_recall.batch.ProductItemProcessor;
import com.slope.batch_recall.model.Product;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class BatchJobConfiguration {

  @Bean
  Job importProductJob(JobRepository jobRepository, Step simpleStep, JobExecutionListener listener) {
    return new JobBuilder("product-job", jobRepository)
    // .incrementer(new RunIdIncrementer())
    .start(simpleStep)
    .listener(listener)
    .build();
  }

  @Bean
  DataSourceTransactionManager transactionManager(DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
  }

  @Bean
  Step simpleStep(JobRepository repository, DataSourceTransactionManager transactionManager
    ,FlatFileItemReader<Product> reader, ProductItemProcessor processor, JdbcBatchItemWriter<Product> writer
    ,ChunkListener chunkListener) {
    return new StepBuilder("simple-step", repository)
    .<Product, Product>chunk(CHUNK_SIZE, transactionManager)
    .reader(reader)
    .writer(writer)
    .processor(processor)
    .listener(chunkListener)
    .build();
  }

  @Bean
  ChunkListener chunkListener() {
    return new ChunkListener() {
      private static int chunkCounter = 0;
      @Override
      public void afterChunk(ChunkContext context) {
        if (chunkCounter % 10000 == 0) {
          log.info("processed {} rows", chunkCounter);
        }
        chunkCounter += CHUNK_SIZE;
      }
    };
  }

  // multi resource
  // https://stackoverflow.com/questions/68412736/how-to-get-file-name-in-to-the-item-reader-or-item-processor-of-spring-batch/68517905
}
