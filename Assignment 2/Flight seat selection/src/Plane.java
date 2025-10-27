import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Plane.java
 * ---------------------------------------------------------
 * Represents the airplane and its complete seat layout.
 * Responsibilities:
 *   - Initialize seat map by class
 *   - Check valid seats
 *   - Return availability by class and row
 */

class Plane {
  final Map<String,Seat> seats = new LinkedHashMap<>();

  static Plane defaultLayout(){
    Plane p = new Plane();
    p.addBlock(1, 4,  SeatClass.FIRST);
    p.addBlock(5, 15, SeatClass.ECON_PLUS);
    p.addBlock(16, 50, SeatClass.ECONOMY);
    return p;
  }
  private void addBlock(int r1,int r2, SeatClass c){
    for (int r=r1; r<=r2; r++)
      for(char col='A'; col<='J'; col++){
        SeatId id = new SeatId(r,col);
        puth(id, new Seat(id,c));
      }
  }
  private void puth(SeatId id, Seat s){ seats.put(id.toString(), s); }

  boolean isValidSeat(String id){ return seats.containsKey(SeatId.norm(id)); }
  Seat seat(String id){ return seats.get(SeatId.norm(id)); }
  SeatClass classOf(String id){ return seat(id).seatClass; }

    /** 
   * Returns a map of available (not reserved) seats grouped by row for given class. 
   */

  Map<Integer, List<Character>> availableByRow(SeatClass c){
    Map<Integer,List<Character>> m = new TreeMap<>();
    for (Seat s: seats.values()){
      if (s.seatClass==c && !s.reserved){
        m.computeIfAbsent(s.id.row, k->new ArrayList<>()).add(s.id.col);
      }
    }
    return m;
  }
}
