package org.sirma.sb.services;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DateParserTest {

    @Test
    void shouldParseValidDateWithHyphenPattern() {
        String dateString = "2023-10-05";
        LocalDate expectedDate = LocalDate.of(2023, 10, 5);
        LocalDate result = DateParser.parseToLocalDate(dateString);
        assertEquals(expectedDate, result, "Date pattern 'yyyy-MM-dd' failed to parse");
    }

    @Test
    void shouldParseValidDateWithSlashPattern() {
        String dateString = "2023/10/05";
        LocalDate expectedDate = LocalDate.of(2023, 10, 5);
        LocalDate result = DateParser.parseToLocalDate(dateString);
        assertEquals(expectedDate, result, "Date pattern 'yyyy/MM/dd' failed to parse");
    }

    @Test
    void shouldParseValidDateWithAmericanFormatPattern() {
        String dateString = "10/15/2023";
        LocalDate expectedDate = LocalDate.of(2023, 10, 15);
        LocalDate result = DateParser.parseToLocalDate(dateString);
        assertEquals(expectedDate, result, "Date pattern 'MM/dd/yyyy' failed to parse");
    }

    @Test
    void shouldParseValidDateWithDayFirstSlashPattern() {
        String dateString = "05/10/2023";
        LocalDate expectedDate = LocalDate.of(2023, 10, 5);
        LocalDate result = DateParser.parseToLocalDate(dateString);
        assertEquals(expectedDate, result, "Date pattern 'dd/MM/yyyy' failed to parse");
    }

    @Test
    void shouldParseValidDateWithDayFirstHyphenPattern() {
        String dateString = "05-10-2023";
        LocalDate expectedDate = LocalDate.of(2023, 10, 5);
        LocalDate result = DateParser.parseToLocalDate(dateString);
        assertEquals(expectedDate, result, "Date pattern 'dd-MM-yyyy' failed to parse");
    }

    @Test
    void shouldParseValidDateWithMonthNamePattern() {
        String dateString = "October 15, 2023";
        LocalDate expectedDate = LocalDate.of(2023, 10, 15);
        LocalDate result = DateParser.parseToLocalDate(dateString);
        assertEquals(expectedDate, result, "Date pattern 'MMM dd, yyyy' failed to parse");
    }

    @Test
    void shouldThrowExceptionForInvalidDateFormat() {
        String dateString = "05.10.2023";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> DateParser.parseToLocalDate(dateString));
        assertEquals("Unable to parse date: 05.10.2023", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForEmptyString() {
        String dateString = "";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> DateParser.parseToLocalDate(dateString));
        assertEquals("Unable to parse date: ", exception.getMessage());
    }

    @Test
    void shouldParseValidDateWithCompactYearFirstFormat() {
        String dateString = "20231005";
        LocalDate expectedDate = LocalDate.of(2023, 10, 5);
        LocalDate result = DateParser.parseToLocalDate(dateString);
        assertEquals(expectedDate, result, "Date pattern 'yyyyMMdd' failed to parse");
    }

    @Test
    void shouldParseValidDateWithFullDayFormat() {
        String dateString = "Thursday, October 05, 2023";
        LocalDate expectedDate = LocalDate.of(2023, 10, 5);
        LocalDate result = DateParser.parseToLocalDate(dateString);
        assertEquals(expectedDate, result, "Date pattern 'EEEE, MMMM dd, yyyy' failed to parse");
    }
}