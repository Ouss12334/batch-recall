package com.slope.batch_recall.config;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.slope.batch_recall.batch.ProductItemProcessor;
import com.slope.batch_recall.model.Product;

@Configuration
public class BatchJobConfiguration {

  @Bean
  Job importProductJob(JobRepository jobRepository, Step simpleStep, JobExecutionListener listener) {
    return new JobBuilder("product-job", jobRepository)
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
    ,FlatFileItemReader<Product> reader, ProductItemProcessor processor, JdbcBatchItemWriter<Product> writer) {
    return new StepBuilder("simple-step", repository)
    .<Product, Product>chunk(3, transactionManager)
    .reader(reader)
    .writer(writer)
    .processor(processor)
    .build();
  }
}
