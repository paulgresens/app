package com.company.app;

import com.company.app.entities.Dvd;
import com.company.app.entities.Product;
import com.company.app.repositories.DvdRepository;
import com.company.app.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AppApplication {
    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);


    }
}
