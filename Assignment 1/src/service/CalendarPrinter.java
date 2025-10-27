package service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.function.Predicate;

public final class CalendarPrinter {

    public static void printMonth(YearMonth ym, LocalDate today,
                                  Predicate<LocalDate> hasEvent) {
        System.out.printf("%s %d%n", ym.getMonth(), ym.getYear());
        System.out.println("Su Mo Tu We Th Fr Sa");

        LocalDate first = ym.atDay(1);
        int offset = first.getDayOfWeek().getValue() % 7; // Sunday=0
        for (int i = 0; i < offset; i++) System.out.print("   ");

        for (int d = 1; d <= ym.lengthOfMonth(); d++) {
            LocalDate curr = ym.atDay(d);
            String cell = String.format("%2d", d);

            boolean isToday = curr.equals(today);
            boolean has = hasEvent.test(curr);

            if (isToday) cell = "[" + cell.trim() + "]";
            else if (has) cell = "{" + cell.trim() + "}";

            System.out.printf("%-3s", cell);
            if (curr.getDayOfWeek() == DayOfWeek.SATURDAY) System.out.println();
        }
        System.out.println();
    }

    public static void printDay(LocalDate d, List<String> lines) {
        var fmt = java.time.format.DateTimeFormatter.ofPattern("E, MMM d, uuuu");
        System.out.println(d.format(fmt));
        if (lines.isEmpty()) {
            System.out.println("(No events)");
        } else {
            for (String s : lines) System.out.println(s);
        }
    }
}
