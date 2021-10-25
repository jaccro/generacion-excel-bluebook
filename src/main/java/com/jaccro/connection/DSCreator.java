package com.jaccro.connection;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

public class DSCreator {
  
  private static BasicDataSource basicDS;

  static {
    try {
      basicDS = new BasicDataSource();
      Properties properties = new Properties();
      // Loading properties file from classpath
      File propertiesFile = new File(new File(".").getCanonicalPath().concat(File.separator).concat("db.properties"));
      if( !propertiesFile.exists() ){
        throw new IOException("Properties file not found");
      }
      //InputStream inputStream = DSCreator.class.getClassLoader().getResourceAsStream("db.properties");  // El archivo debe encontrarse en /main/resources
      InputStream inputStream = new FileInputStream(propertiesFile);
      
      properties.load(inputStream);	
      basicDS.setDriverClassName(properties.getProperty("DB.DRIVER_CLASS"));
      basicDS.setUrl(properties.getProperty("DB.DB_URL"));
      basicDS.setUsername(properties.getProperty("DB.DB_USER"));
      basicDS.setPassword(properties.getProperty("DB.DB_PASSWORD"));
      //The initial number of connections that are created when the pool is started.
      basicDS.setInitialSize(Integer.parseInt(properties.getProperty("DB.INITIAL_POOL_SIZE")));
      //The maximum number of active connections that can be allocated from this pool at the same time
      basicDS.setMaxTotal(Integer.parseInt(properties.getProperty("DB.MAX_POOL_SIZE")));
    } catch(IOException e) {
      e.printStackTrace();
    }
  }

  public static DataSource getDataSource() {
    return basicDS;
  } 
}
