package model;

import java.time.LocalTime;

public record TimeRange(LocalTime start, LocalTime end) {
    public boolean conflicts(TimeRange other) {
        // overlap if start < other.end AND other.start < end
        return start.isBefore(other.end()) && other.start().isBefore(end);
    }
}
