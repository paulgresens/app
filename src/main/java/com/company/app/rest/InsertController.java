package com.company.app.rest;

import com.company.app.DataInserter;
import com.company.app.Insert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.charset.StandardCharsets;

@RestController
public class InsertController {
    @Autowired
    DataInserter dataInserter;

    @GetMapping("/insert")
    public void insert() {
        File file1 = new File("src/data/dresden.xml");
        File file2 = new File("src/data/leipzig_transformed.xml");
        File file3 = new File("src/data/categories.xml");


        Insert.processXMLFile(file1, "ISO-8859-1");
        Insert.processXMLFile(file2, StandardCharsets.UTF_8.name());
        Insert.processXMLFile(file3, "ISO-8859-1");

        dataInserter.insertReviewData();
    }
}

