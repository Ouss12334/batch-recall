package com.slope.batch_recall.config;

import static com.slope.batch_recall.config.Constants.FILE_URL;
import static com.slope.batch_recall.config.Constants.INSERT_PRODUCT_SQL;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;

import javax.sql.DataSource;

import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;

import com.slope.batch_recall.batch.ProductItemProcessor;
import com.slope.batch_recall.model.Product;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class ReadWriteProcessConfiguration {

  /**
   * for converting data
   * @return
   */
  @Bean
  ConversionService productConversionService() {
    DefaultConversionService service = new DefaultConversionService();
    
    DefaultConversionService.addDefaultConverters(service); // add as default

    service.addConverter(new Converter<String,LocalDateTime>() {
      @Override
      public LocalDateTime convert(String source) {
        log.debug("parsing string to date '{}'", source);
        var formatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .append(DateTimeFormatter.ISO_LOCAL_DATE)
                .appendLiteral(' ')
                .append(DateTimeFormatter.ISO_LOCAL_TIME)
                .appendLiteral(" UTC")
                .toFormatter();
        return LocalDateTime.parse(source, formatter);
      }
    });
    return service;
  }

  /**
   * for mapping
   * @param productConversionService
   * @return
   */
  @Bean
  FieldSetMapper<Product> rowMapper(ConversionService productConversionService) {
    BeanWrapperFieldSetMapper<Product> mapper = new BeanWrapperFieldSetMapper<>();
    mapper.setConversionService(productConversionService);
    mapper.setTargetType(Product.class);
    return mapper;
  }

  @Bean
  FlatFileItemReader<Product> reader(FieldSetMapper<Product> rowMapper) {
    return new FlatFileItemReaderBuilder<Product>()
    .name("productReader")
    .resource(new PathResource(FILE_URL))
    .linesToSkip(1) // skip header
    .delimited()
    // .includedFields(0, 1, 2, 3, 4, 5, 6, 7, 8)
    .delimiter(",")
    // column names like written in Produt.class
    .names("event_time"
      ,"event_type"
      ,"product_id"
      ,"category_id"
      ,"category_code"
      ,"brand"
      ,"price",
      "user_id"
      ,"user_session")
    .fieldSetMapper(rowMapper)
    // .targetType(Product.class)
    .build();
  }

  @Bean
  ProductItemProcessor processor() {
    return new ProductItemProcessor();
  }

  @Bean
  JdbcBatchItemWriter<Product> writer(DataSource dataSource) {
    log.info("writer datasource {}", dataSource);
    return new JdbcBatchItemWriterBuilder<Product>()
    .sql(INSERT_PRODUCT_SQL)
    .beanMapped() // for parameters in sql query
    .dataSource(dataSource)
    .build();
  }

}
