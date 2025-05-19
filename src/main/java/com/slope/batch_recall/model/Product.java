package com.slope.batch_recall.model;

import java.time.LocalDateTime;

import lombok.Data;

// public record Product(
//   LocalDateTime eventTime
//   ,String eventType
//   ,long productId
//   ,long categoryId
//   ,String categoryCode
//   ,String brand
//   ,float price
//   ,long userId
//   ,String userSession
// ) {}

// cannot be record because required <init>
@Data
public class Product {
  private LocalDateTime eventTime;
  private String eventType;
  private long productId;
  private long categoryId;
  private String categoryCode;
  private String brand;
  private float price;
  private long userId;
  private String userSession;
}