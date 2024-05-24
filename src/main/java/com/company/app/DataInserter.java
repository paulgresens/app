package com.company.app;

import com.company.app.dto.DvdDto;
import com.company.app.entities.Dvd;
import com.company.app.entities.Product;
import com.company.app.repositories.DvdRepository;
import com.company.app.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInserter implements CommandLineRunner {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DvdRepository dvdRepository;

    @Override
    public void run(String... args) throws Exception {
        Product product = new Product("thisifdfdfdfdshit", "titleundso", 3, null);
        //productRepository.save(product);

        Dvd dvd = new Dvd("asfjlasdf", product, "format", "runtime", "DE");
        dvdRepository.save(dvd);
    }
}