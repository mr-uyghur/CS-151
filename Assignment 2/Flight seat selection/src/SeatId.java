import java.util.Objects;

/**
 * SeatId.java
 * ---------------------------------------------------------
 * Represents a single seat identifier (row + column).
 * Implements equality and natural ordering for sorting.
 */

class SeatId implements Comparable<SeatId> {
  final int row; final char col;
  SeatId(int row, char col){ this.row=row; this.col=Character.toUpperCase(col); }
  @Override public String toString(){ return row + String.valueOf(col); }
  @Override public boolean equals(Object o){
    if (this==o) return true; if (!(o instanceof SeatId)) return false;
    SeatId s=(SeatId)o; return row==s.row && col==s.col;
  }
  @Override public int hashCode(){ return Objects.hash(row, col); }
  @Override public int compareTo(SeatId o){
    if (row!=o.row) return Integer.compare(row, o.row);
    return Character.compare(col, o.col);
  }
  static String norm(String seat){ return seat.trim().toUpperCase(); }
}
