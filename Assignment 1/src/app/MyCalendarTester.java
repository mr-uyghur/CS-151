package app;

import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.stream.Collectors;

import model.OneTimeEvent;
import model.RecurringEvent;
import model.TimeRange;
import service.CalendarPrinter;
import service.EventParser;
import service.EventStore;
import service.InputValidators;

//main class, this class runs the Java app.
// It provides a command-line interface for the user
//To view create delete and navigate
/**
* Responsibilities:
 * - Parse user input commands.
 * - Collect event details from the user.
 * - Delegate storage and retrieval to EventStore.
 * - Display results or error messages to the user.
 */

public final class MyCalendarTester {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        EventStore store = new EventStore();

        // 1) Load events from event.txt 
        store.addAll(EventParser.load(Path.of("events.txt")));

        // 2) Show current month with today highlighted and {} for any event days
        LocalDate today = LocalDate.now();
        CalendarPrinter.printMonth(YearMonth.from(today), today,
                d -> !store.formattedOccurrencesOn(d).isEmpty());

        // 3) Menu loop
        while (true) {
            // the command given by the requirements on Canvas
            System.out.println("[V]iew by  [C]reate  [G]o to  [E]vent list  [D]elete  [Q]uit");
            String choice = in.nextLine().trim().toUpperCase(Locale.ROOT);
                //switch method for the command for more cleaner look and better readibility
            switch (choice) {
                case "V" -> handleView(in, store, today);
                case "C" -> handleCreate(in, store);
                case "G" -> handleGoTo(in, store);
                case "E" -> handleEventList(store);
                case "D" -> handleDelete(in, store);
                case "Q" -> {
                    System.out.println("Good Bye");
                    EventParser.save(Path.of("output.txt"), store.all());
                    return;
                }
                default -> System.out.println("Invalid option. Please choose from the menu.");
            }
        }
    }

    //view handler logic
    private static void handleView(Scanner in, EventStore store, LocalDate today) {
        System.out.println("[D]ay view or [M]view ?");
        String v = in.nextLine().trim().toUpperCase(Locale.ROOT);

//Day view
        if (v.equals("D")) {
            LocalDate cursor = today;
            while (true) {
                CalendarPrinter.printDay(cursor, store.formattedOccurrencesOn(cursor));
                System.out.println("[P]revious or [N]ext or [G]o back to the main menu ?");
                String cmd = in.nextLine().trim().toUpperCase(Locale.ROOT);
                if (cmd.equals("P")) cursor = cursor.minusDays(1);
                else if (cmd.equals("N")) cursor = cursor.plusDays(1);
                else if (cmd.equals("G")) break;
                else System.out.println("Invalid option. Please choose from the menu.");
            }
            //month view
        } else if (v.equals("M")) {
            YearMonth ym = YearMonth.from(today);
            while (true) {
                CalendarPrinter.printMonth(ym, today, d -> !store.formattedOccurrencesOn(d).isEmpty());
                System.out.println("[P]revious or [N]ext or [G]o back to main menu ?");
                String cmd = in.nextLine().trim().toUpperCase(Locale.ROOT);
                if (cmd.equals("P")) ym = ym.minusMonths(1);
                else if (cmd.equals("N")) ym = ym.plusMonths(1);
                else if (cmd.equals("G")) break;
                else System.out.println("Invalid option. Please choose from the menu.");
            }
        } else {
            System.out.println("Invalid option. Please choose from the menu.");
        }
    }
// [C]reate function
//This option allows the user to schedule an event. 
//The calendar asks the user to enter the name, date, starting time, and ending time of an event. 
//For simplicity, we consider one-time event only for the Create function.
    private static void handleCreate(Scanner in, EventStore store) {
        try {
            System.out.print("Name: ");
            String name = in.nextLine().trim();

            System.out.print("Date (MM/DD/YYYY): ");
            LocalDate date = InputValidators.parseDateStrict(in.nextLine().trim());

            System.out.print("Start time (HH:mm): ");
            LocalTime start = InputValidators.parseTimeStrict(in.nextLine().trim());

            System.out.print("End time (HH:mm): ");
            LocalTime end = InputValidators.parseTimeStrict(in.nextLine().trim());

            if (!end.isAfter(start))
                throw new IllegalArgumentException("End time must be after start time.");

            store.addOneTime(new OneTimeEvent(name, date, new TimeRange(start, end)));
            System.out.println("Event created.");
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void handleGoTo(Scanner in, EventStore store) {
        try {
            System.out.print("Date (MM/DD/YYYY): ");
            LocalDate d = InputValidators.parseDateStrict(in.nextLine().trim());
            CalendarPrinter.printDay(d, store.formattedOccurrencesOn(d));
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void handleEventList(EventStore store) {
        System.out.println("ONE TIME EVENTS");
        var oneTimes = store.oneTimeEventsSorted();
        Map<Integer, List<OneTimeEvent>> byYear = oneTimes.stream()
                .collect(Collectors.groupingBy(o -> o.date().getYear(), TreeMap::new, Collectors.toList()));
        byYear.forEach((year, list) -> {
            System.out.println("\n" + year);
            for (OneTimeEvent o : list) {
                System.out.printf("  %s %s %s - %s %s%n",
                        o.date().getDayOfWeek(), formatDate(o.date()), o.time().start(), o.time().end(), o.name());
            }
        });

        System.out.println("\nRECURRING EVENTS");
        for (RecurringEvent r : store.recurringEventsSorted()) {
            System.out.printf("%s %s %s %s %s %s%n",
                    r.name(), daysToken(r.days()), r.time().start(), r.time().end(),
                    formatDate(r.from()), formatDate(r.to()));
        }
    }

    private static String daysToken(EnumSet<DayOfWeek> set) {
        StringBuilder sb = new StringBuilder();
        if (set.contains(DayOfWeek.SUNDAY)) sb.append('S');
        if (set.contains(DayOfWeek.MONDAY)) sb.append('M');
        if (set.contains(DayOfWeek.TUESDAY)) sb.append('T');
        if (set.contains(DayOfWeek.WEDNESDAY)) sb.append('W');
        if (set.contains(DayOfWeek.THURSDAY)) sb.append('R'); // R = Thu
        if (set.contains(DayOfWeek.FRIDAY)) sb.append('F');
        if (set.contains(DayOfWeek.SATURDAY)) sb.append('A'); // A = Sat
        return sb.toString();
    }

    private static String formatDate(LocalDate d) {
        return d.format(DateTimeFormatter.ofPattern("M/d/uuuu"));
    }

    private static void handleDelete(Scanner in, EventStore store) {
        System.out.println("[S]elected  [A]ll   [R]");
        String t = in.nextLine().trim().toUpperCase(Locale.ROOT);
        switch (t) {
            case "S" -> {
                try {
                    System.out.print("Enter the date (MM/DD/YYYY): ");
                    LocalDate d = InputValidators.parseDateStrict(in.nextLine().trim());
                    var lines = store.formattedOccurrencesOn(d).stream()
                            .filter(s -> !s.toLowerCase(Locale.ROOT).contains("(recurring)")) // only display; not needed if not labeled
                            .toList();
                    if (lines.isEmpty()) {
                        System.out.println("Error: No event found with the specified name on this date.");
                        return;
                    }
                    for (String s : lines) System.out.println("  " + s);
                    System.out.print("Enter the name of the event to delete: ");
                    String name = in.nextLine().trim();
                    boolean removed = store.deleteSelected(d, name);
                    if (!removed)
                        System.out.println("Error: No event found with the specified name on this date.");
                } catch (IllegalArgumentException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            case "A" -> {
                try {
                    System.out.print("Enter the date (MM/DD/YYYY): ");
                    LocalDate d = InputValidators.parseDateStrict(in.nextLine().trim());
                    int n = store.deleteAllOn(d);
                    System.out.println(n + " event(s) deleted.");
                } catch (IllegalArgumentException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            case "R" -> {
                System.out.print("Enter recurring event name: ");
                String name = in.nextLine().trim();
                int n = store.deleteRecurringByName(name);
                if (n == 0) System.out.println("Error: No matching recurring event found.");
                else System.out.println(n + " recurring event(s) deleted.");
            }
            default -> System.out.println("Invalid option. Please choose from the menu.");
        }
    }
}
