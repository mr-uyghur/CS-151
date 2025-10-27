/**
 * Seat.java
 * ---------------------------------------------------------
 * Represents an airplane seat.
 * Each seat knows its:
 *   - Identifier (SeatId)
 *   - Class (FIRST, ECON_PLUS, ECONOMY)
 *   - Reservation status (reserved / free)
 *   - User who reserved it (userId)
 */


class Seat {
  final SeatId id; final SeatClass seatClass;
  boolean reserved=false; String reservedBy=null; // userId
  Seat(SeatId id, SeatClass sc){ this.id=id; this.seatClass=sc; }
}
