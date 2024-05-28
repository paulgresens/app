package com.company.app;

import lombok.AllArgsConstructor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class CSVParser {
    private String filepath;
    private String delimeter;
    private boolean skipFirstLine;


    public List<List<String>> parseCsv() throws IOException {
        List<List<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            if (skipFirstLine) {
                br.readLine();
            }
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(delimeter);
                for (int i = 0; i < values.length; i++) {
                    values[i] = values[i].replaceAll("^\"|\"$", "");
                }
                records.add(Arrays.asList(values));

            }
        } catch (IOException e) {
            throw e;
        }
        return records;
    }
}
