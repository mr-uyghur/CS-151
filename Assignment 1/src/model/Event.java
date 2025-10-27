/**
 * The Event class represents a single calendar entry.
 * Each event has identifying information, timing details,
 * and optional descriptive fields.
 *
 * Attributes:
 * - id: unique identifier for each event.
 * - title: short description of the event.
 * - date, start, end: when the event takes place.
 * - description and location: optional details.
 *
 * Responsibilities:
 * - Provide access to event details.
 * - Compute duration of the event.
 * - Determine whether two events overlap in time.
 */


package model;

import java.time.LocalDate;
import java.util.Optional;

public interface Event {
    String name();
    boolean occursOn(LocalDate date);
    Optional<TimeRange> timeOn(LocalDate date); // present if occurs that day
}
