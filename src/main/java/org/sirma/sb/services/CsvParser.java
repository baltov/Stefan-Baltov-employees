package org.sirma.sb.services;


import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.sirma.sb.model.CsvRow;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CsvParser {

    public static void parseCsv(String filePath,
                                BiConsumer<Long, CsvRow> consumer,
                                BiConsumer<Long, Exception> exceptionConsumer,
                                Consumer<Long> progressConsumer) {
        CsvMapper csvMapper = new CsvMapper();
        Long lineNumber = 0L;
        // Define the schema based on the "CsvRow" class
        CsvSchema csvSchema = CsvSchema.emptySchema()
                .withHeader() // Use the header row
                .withColumnSeparator(','); //the delimiter (commas as in task description)
        try {
            MappingIterator<CsvRow> iterator = csvMapper.readerFor(CsvRow.class)
                    .with(csvSchema)
                    .readValues(new File(filePath));

            while (iterator.hasNext()) {
                try {
                    // Parse a single row
                    CsvRow row = iterator.next();
                    lineNumber++;
                    try {
                        isValid(row);
                        consumer.accept(lineNumber, row);
                        if (lineNumber % 1000 == 0) {
                            progressConsumer.accept(lineNumber);
                        }
                    } catch (IllegalArgumentException e) {
                        exceptionConsumer.accept(lineNumber, e);
                    }
                } catch (Exception e) {
                    // Handle and log parsing errors for the row, continue processing
                   exceptionConsumer.accept(lineNumber, e);
                }
            }
        } catch (Exception e) {
            // Handle file-level or schema-level exceptions
            exceptionConsumer.accept(lineNumber, e);
        }
    }

    private static boolean isValid(CsvRow row) {
        if (!StringUtils.hasText(row.getUserId())) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        } else if (!StringUtils.hasText(row.getProjectId())) {
            throw new IllegalArgumentException("Project ID cannot be null or empty");
        } else if (!StringUtils.hasText(row.getStartDate())) {
            throw new IllegalArgumentException("Start date cannot be null or empty");
        }

        return true;
    }


}

