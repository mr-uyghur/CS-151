package service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import model.Event;
import model.OneTimeEvent;
import model.RecurringEvent;
import model.TimeRange;

/**
 * The EventStore class manages all Event objects in the calendar.
 * It acts as the in-memory repository and may support persistence
 * to a file for saving and loading events.
 *
 * Responsibilities:
 * - Add and remove events.
 * - Retrieve events by date or date range.
 * - Detect scheduling conflicts (event overlaps).
 * - Persist events to external storage (optional).
 */


public final class EventStore {
    private final List<Event> events = new ArrayList<>();

    // Private DTO for a single-day occurrence (no separate file)
    private static final class Occ implements Comparable<Occ> {
        final String name;
        final LocalDate date;
        final boolean recurring;
        final TimeRange time;

        Occ(String name, LocalDate date, boolean recurring, TimeRange time) {
            this.name = name; this.date = date; this.recurring = recurring; this.time = time;
        }

        @Override public int compareTo(Occ o) {
            int c = date.compareTo(o.date);
            return (c != 0) ? c : time.start().compareTo(o.time.start());
        }
    }

    public void addAll(Collection<Event> es) { events.addAll(es); }
    public List<Event> all() { return events; }

    // Build occurrences for a date
    public List<String> formattedOccurrencesOn(LocalDate d) {
        List<Occ> list = new ArrayList<>();
        for (Event e : events) {
            e.timeOn(d).ifPresent(tr -> list.add(new Occ(e.name(), d, !(e instanceof OneTimeEvent), tr)));
        }
        Collections.sort(list);
        // Convert to strings for printing 
        return list.stream()
                   .map(o -> String.format("%s : %s - %s",
                         o.name, o.time.start(), o.time.end()))
                   .toList();
    }

    public boolean hasConflict(LocalDate d, TimeRange newRange) {
        for (Event e : events) {
            Optional<TimeRange> maybe = e.timeOn(d);
            if (maybe.isPresent() && maybe.get().conflicts(newRange)) return true;
        }
        return false;
    }

    public void addOneTime(OneTimeEvent evt) {
        if (hasConflict(evt.date(), evt.time()))
            throw new IllegalArgumentException("Conflict with existing event.");
        events.add(evt);
    }

    // Delete Selected 
    public boolean deleteSelected(LocalDate date, String name) {
        return events.removeIf(e ->
            (e instanceof OneTimeEvent o) &&
            o.date().equals(date) &&
            o.name().equalsIgnoreCase(name)
        );
    }

    // Delete All 
    public int deleteAllOn(LocalDate date) {
        int before = events.size();
        events.removeIf(e -> (e instanceof OneTimeEvent o) && o.date().equals(date));
        return before - events.size();
    }

    // Delete Recurring by name
    public int deleteRecurringByName(String name) {
        int before = events.size();
        events.removeIf(e -> (e instanceof RecurringEvent r) && r.name().equalsIgnoreCase(name));
        return before - events.size();
    }

    // For Event list screen
    public List<OneTimeEvent> oneTimeEventsSorted() {
        return events.stream()
                .filter(e -> e instanceof OneTimeEvent)
                .map(e -> (OneTimeEvent) e)
                .sorted(Comparator.comparing(OneTimeEvent::date)
                        .thenComparing(o -> o.time().start()))
                .toList();
    }

    public List<RecurringEvent> recurringEventsSorted() {
        return events.stream()
                .filter(e -> e instanceof RecurringEvent)
                .map(e -> (RecurringEvent) e)
                .sorted(Comparator.comparing(RecurringEvent::from))
                .toList();
    }
}

