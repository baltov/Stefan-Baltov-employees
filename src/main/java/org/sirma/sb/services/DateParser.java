package org.sirma.sb.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public class DateParser {
    // A list of common date formats
    private static final List<String> DATE_PATTERNS = Arrays.asList(
            "yyyy-MM-dd",              // ISO 8601 (basic)
            "yyyy/MM/dd",              // Slashes as delimiter
            "dd/MM/yyyy",              // European format
            "MM/dd/yyyy",              // US format
            "dd-MM-yyyy",              // European alternative
            "MMM dd, yyyy",            // Month name format (e.g., Mar 15, 2023)
            "MMMM dd, yyyy",            // Month name format (e.g., October 15, 2023)
            "dd-MMM-yyyy",             // Day-Month-Year with month abbreviation
            "MMM dd yyyy",             // Month first, without commas
            "MMMM dd yyyy",             // Full month name first, without commas
            "yyyyMMdd",                // Compact format
            "dd MMM yyyy",             // British-style format
            "dd MMMM yyyy",             // British-style format
            "EEEE, MMMM dd, yyyy",      // Full named day with month (e.g., Wednesday, March 15, 2023)
            "yyyy-M-d"
    );

    public static LocalDate parseToLocalDate(String dateString) {
        // Iterate over all patterns
        for (String pattern : DATE_PATTERNS) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                return LocalDate.parse(dateString, formatter);
            } catch (DateTimeParseException e) {
                // Continue to the next pattern
            }
        }
        throw new IllegalArgumentException("Unable to parse date: " + dateString);
    }
}

