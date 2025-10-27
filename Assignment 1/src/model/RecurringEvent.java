package model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Optional;

public final class RecurringEvent implements Event {
    private final String name;
    private final EnumSet<DayOfWeek> days; // e.g., {MONDAY, WEDNESDAY}
    private final TimeRange time;
    private final LocalDate from;
    private final LocalDate to;

    public RecurringEvent(String name, EnumSet<DayOfWeek> days, TimeRange time,
                          LocalDate from, LocalDate to) {
        this.name = name;
        this.days = days.clone();
        this.time = time;
        this.from = from;
        this.to = to;
    }

    @Override public String name() { return name; }
    public EnumSet<DayOfWeek> days() { return days.clone(); }
    public TimeRange time() { return time; }
    public LocalDate from() { return from; }
    public LocalDate to() { return to; }

    @Override public boolean occursOn(LocalDate d) {
        return !d.isBefore(from) && !d.isAfter(to) && days.contains(d.getDayOfWeek());
    }

    @Override public Optional<TimeRange> timeOn(LocalDate d) {
        return occursOn(d) ? Optional.of(time) : Optional.empty();
    }
}
