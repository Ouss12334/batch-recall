package com.slope.batch_recall.config;

import static com.slope.batch_recall.batch.Constants.CHUNK_SIZE;

import java.sql.SQLException;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.repeat.RepeatCallback;
import org.springframework.batch.repeat.RepeatException;
import org.springframework.batch.repeat.RepeatOperations;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.repeat.support.RepeatTemplate;
import org.springframework.batch.repeat.support.TaskExecutorRepeatTemplate;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.slope.batch_recall.batch.ProductItemProcessor;
import com.slope.batch_recall.model.Product;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class MultiThreadJobConfiguration {

  @Bean
  Job importProductJob(JobRepository jobRepository, Step simpleStep, JobExecutionListener listener
    ,JobParametersIncrementer incrementer) {
    return new JobBuilder("product-job", jobRepository)
        .incrementer(incrementer) // required to start new jobs when COMPLETED, not working with new RunIdIncrementer()
        .start(simpleStep)
        .listener(listener)
        .build();
  }

  // @Bean
  // DataSource productDatasource(DataSourceProperties dataSourceProperties) {
  //   log.info("db props {}", dataSourceProperties.determineUrl());
  //   return dataSourceProperties
  //     .initializeDataSourceBuilder()
  //     .type(HikariDataSource.class)
  //     .build();
  // }

  @Bean
  DataSourceTransactionManager transactionManager(DataSource dataSource) throws SQLException {
    // HikariConfig c = new HikariConfig();
    // log.info("datasource connection {}", dataSource.getConnection().getMetaData().getURL());
    // c.setMaximumPoolSize(CHUNK_SIZE);
    // c.setConnectionTestQuery(dataSource.getConnection().getMetaData().getURL());
    // c.setConnectionInitSql(dataSource.getConnection().getMetaData().getURL());
    // var manager = new DataSourceTransactionManager(new HikariDataSource(c));
    var manager = new DataSourceTransactionManager(dataSource);
    return manager;
  }

  /**
   * multi threaded batch
   */
  @Bean
  public TaskExecutor multiTasker() {
    // ThreadPoolTaskExecutor = sync = pool size
    // AsyncTaskExecutor = concurrency limit
    var task = new SimpleAsyncTaskExecutor();
    task.setVirtualThreads(true);
    task.setConcurrencyLimit(30);
    return task;
  }

  /**
   * not used in step
   */
  @Bean
  RepeatOperations throttleLimiter() {
    var repeater = new RepeatTemplate();
    return repeater;
  }

  @Bean
  Step simpleStep(JobRepository repository, DataSourceTransactionManager transactionManager,
      FlatFileItemReader<Product> reader, ProductItemProcessor processor, JdbcBatchItemWriter<Product> writer,
      ChunkListener chunkListener, TaskExecutor multiTasker, RepeatOperations throttleLimiter) {
      // debug writer
        var consoleWriter = new ItemWriter<Product>() {
        public void write(Chunk<? extends Product> chunk) throws Exception {
        // log.info("chunk {}", chunk.size());
      }
      };
    
    return new StepBuilder("simple-step", repository)
        .<Product, Product>chunk(CHUNK_SIZE, transactionManager)
        .reader(reader)
        // .writer(consoleWriter) // debug writer
        .writer(writer)
        .processor(processor)
        .listener(chunkListener)
        .taskExecutor(multiTasker)
        .throttleLimit(CHUNK_SIZE) // use deprecated throttleLimit() until clear example from doc
        // .stepOperations(throttleLimiter)
        .build();
  }

  // multi resource
  // https://stackoverflow.com/questions/68412736/how-to-get-file-name-in-to-the-item-reader-or-item-processor-of-spring-batch/68517905
}
