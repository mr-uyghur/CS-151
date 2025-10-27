package model;

import java.time.LocalDate;
import java.util.Optional;

public final class OneTimeEvent implements Event {
    private final String name;
    private final LocalDate date;
    private final TimeRange time;

    public OneTimeEvent(String name, LocalDate date, TimeRange time) {
        this.name = name;
        this.date = date;
        this.time = time;
    }

    @Override public String name() { return name; }
    public LocalDate date() { return date; }
    public TimeRange time() { return time; }

    @Override public boolean occursOn(LocalDate d) { return date.equals(d); }

    @Override public Optional<TimeRange> timeOn(LocalDate d) {
        return occursOn(d) ? Optional.of(time) : Optional.empty();
    }
}
