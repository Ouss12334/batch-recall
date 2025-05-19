package com.slope.batch_recall.config;

public class Constants {
  public static final String SAMPLE_FILE_URL = "C:/Users/Oussama TURKI/Downloads/ecommerce-kaggle/oct-219-sample.csv";
  
  public static final String PRODUCT_INPUT_FOLDER = "C:/Users/Oussama TURKI/Downloads/ecommerce-kaggle/input";
  public static final String FILE_URL = PRODUCT_INPUT_FOLDER + "/2019-Oct.csv";
  
  public static final int CHUNK_SIZE = 5000;
  
  public static final String INSERT_PRODUCT_SQL = """
    INSERT INTO product (
      event_time
      ,event_type
      ,product_id
      ,category_id
      ,category_code
      ,brand
      ,price
      ,user_id
      ,user_session
      )
      VALUES (
        :eventTime
        ,:eventType
        ,:productId
        ,:categoryId
        ,:categoryCode
        ,:Brand
        ,:Price
        ,:userId
        ,:userSession
      )
      """;

}