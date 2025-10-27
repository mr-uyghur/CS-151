package service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import model.Event;
import model.OneTimeEvent;
import model.RecurringEvent;
import model.TimeRange;

/**
 * Utility class for parsing events from text input
 * and formatting them for saving or displaying.
 */
 
// parseEvent() - Converts a line of text into an Event object.
// formatEvent() - Converts an Event object into a text format for saving.
// parseDateTime() - Parses strings into LocalDate/LocalDateTime.
// Helpers - Safely handle invalid inputs and formatting errors.


public class EventParser {
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("M/d/uuuu");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("H:mm");
    
    public static List<Event> load(Path path) {
        List<Event> events = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(path);
            for (int i = 0; i < lines.size(); i += 2) {
                if (i + 1 >= lines.size()) break;
                
                String name = lines.get(i).trim();
                String data = lines.get(i + 1).trim();
                
                if (name.isEmpty() || data.isEmpty() || name.startsWith("#")) continue;
                
                try {
                    Event event = parseEvent(name, data);
                    if (event != null) {
                        events.add(event);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing event: " + name + " - " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return events;
    }
    
    public static void save(Path path, List<Event> events) {
        try {
            List<String> lines = new ArrayList<>();
            for (Event event : events) {
                lines.add(formatEvent(event));
            }
            Files.write(path, lines);
        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
        }
    }
    
    private static Event parseEvent(String name, String data) {
        String[] parts = data.split("\\s+");
        if (parts.length < 3) return null;
        
        // Try to determine if it's a recurring event by checking for day patterns
        if (isDayPattern(parts[0])) {
            return parseRecurringEventFromFormat(name, parts);
        } else {
            // Assume it's a one-time event
            return parseOneTimeEventFromFormat(name, parts);
        }
    }
    
    private static boolean isDayPattern(String str) {
        return str.matches("[MTWRFAS]+");
    }
    
    private static OneTimeEvent parseOneTimeEventFromFormat(String name, String[] parts) {
        if (parts.length < 3) return null;
        
        LocalDate date = LocalDate.parse(parts[0], DATE_FORMAT);
        LocalTime start = LocalTime.parse(parts[1], TIME_FORMAT);
        LocalTime end = LocalTime.parse(parts[2], TIME_FORMAT);
        
        return new OneTimeEvent(name, date, new TimeRange(start, end));
    }
    
    private static RecurringEvent parseRecurringEventFromFormat(String name, String[] parts) {
        if (parts.length < 5) return null;
        
        EnumSet<DayOfWeek> days = parseDays(parts[0]);
        LocalTime start = LocalTime.parse(parts[1], TIME_FORMAT);
        LocalTime end = LocalTime.parse(parts[2], TIME_FORMAT);
        LocalDate from = LocalDate.parse(parts[3], DATE_FORMAT);
        LocalDate to = LocalDate.parse(parts[4], DATE_FORMAT);
        
        return new RecurringEvent(name, days, new TimeRange(start, end), from, to);
    }
    
    private static OneTimeEvent parseOneTimeEvent(String name, String[] parts) {
        if (parts.length < 5) return null;
        
        LocalDate date = LocalDate.parse(parts[2], DATE_FORMAT);
        LocalTime start = LocalTime.parse(parts[3], TIME_FORMAT);
        LocalTime end = LocalTime.parse(parts[4], TIME_FORMAT);
        
        return new OneTimeEvent(name, date, new TimeRange(start, end));
    }
    
    private static RecurringEvent parseRecurringEvent(String name, String[] parts) {
        if (parts.length < 7) return null;
        
        EnumSet<DayOfWeek> days = parseDays(parts[2]);
        LocalTime start = LocalTime.parse(parts[3], TIME_FORMAT);
        LocalTime end = LocalTime.parse(parts[4], TIME_FORMAT);
        LocalDate from = LocalDate.parse(parts[5], DATE_FORMAT);
        LocalDate to = LocalDate.parse(parts[6], DATE_FORMAT);
        
        return new RecurringEvent(name, days, new TimeRange(start, end), from, to);
    }
    
    private static EnumSet<DayOfWeek> parseDays(String daysStr) {
        EnumSet<DayOfWeek> days = EnumSet.noneOf(DayOfWeek.class);
        for (char c : daysStr.toCharArray()) {
            switch (c) {
                case 'S' -> days.add(DayOfWeek.SUNDAY);
                case 'M' -> days.add(DayOfWeek.MONDAY);
                case 'T' -> days.add(DayOfWeek.TUESDAY);
                case 'W' -> days.add(DayOfWeek.WEDNESDAY);
                case 'R' -> days.add(DayOfWeek.THURSDAY);
                case 'F' -> days.add(DayOfWeek.FRIDAY);
                case 'A' -> days.add(DayOfWeek.SATURDAY);
            }
        }
        return days;
    }
    
    private static String formatEvent(Event event) {
        if (event instanceof OneTimeEvent ote) {
            return String.format("%s ONETIME %s %s %s",
                ote.name(),
                ote.date().format(DATE_FORMAT),
                ote.time().start().format(TIME_FORMAT),
                ote.time().end().format(TIME_FORMAT)
            );
        } else if (event instanceof RecurringEvent re) {
            return String.format("%s RECURRING %s %s %s %s %s",
                re.name(),
                formatDays(re.days()),
                re.time().start().format(TIME_FORMAT),
                re.time().end().format(TIME_FORMAT),
                re.from().format(DATE_FORMAT),
                re.to().format(DATE_FORMAT)
            );
        }
        return "";
    }
    
    private static String formatDays(EnumSet<DayOfWeek> days) {
        StringBuilder sb = new StringBuilder();
        if (days.contains(DayOfWeek.SUNDAY)) sb.append('S');
        if (days.contains(DayOfWeek.MONDAY)) sb.append('M');
        if (days.contains(DayOfWeek.TUESDAY)) sb.append('T');
        if (days.contains(DayOfWeek.WEDNESDAY)) sb.append('W');
        if (days.contains(DayOfWeek.THURSDAY)) sb.append('R');
        if (days.contains(DayOfWeek.FRIDAY)) sb.append('F');
        if (days.contains(DayOfWeek.SATURDAY)) sb.append('A');
        return sb.toString();
    }
}
