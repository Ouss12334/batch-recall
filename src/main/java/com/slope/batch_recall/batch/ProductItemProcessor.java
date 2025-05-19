package com.slope.batch_recall.batch;

import org.springframework.batch.item.ItemProcessor;

import com.slope.batch_recall.model.Product;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProductItemProcessor implements ItemProcessor<Product, Product> {

  @Override
  public Product process(Product input) throws Exception {
    log.info("processing {}", input);
    return input; // return as output
  }

}
