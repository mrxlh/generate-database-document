package com.collmall.document;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@SpringBootApplication
public class GenerateDatabaseDocumentApplication {

    public static void main(String[] args) {
        SpringApplication.run(GenerateDatabaseDocumentApplication.class, args);
    }

    @Bean
    public DataSource getDataSource(DataSource dataSource) {
        return dataSource;
    }
}
