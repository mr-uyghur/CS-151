package service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class InputValidators {
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("M/d/uuuu");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("H:mm");
    
    public static LocalDate parseDateStrict(String input) {
        try {
            return LocalDate.parse(input, DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please use MM/DD/YYYY format.");
        }
    }
    
    public static LocalTime parseTimeStrict(String input) {
        try {
            return LocalTime.parse(input, TIME_FORMAT);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid time format. Please use HH:mm format.");
        }
    }
}
