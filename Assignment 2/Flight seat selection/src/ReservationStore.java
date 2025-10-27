import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** CSV: userId,seatId,class,price  (only reserved seats are saved) */

/**
 * ReservationStore.java
 * ---------------------------------------------------------
 * Manages all seat reservations.
 * Responsibilities:
 *   - Reserve or cancel seats
 *   - Load/save reservation data
 *   - Provide user seat lists and admin manifest
 *
 * File format (CSV):
 *   userId,seatId,class,price
 */

class ReservationStore {
  private final Plane plane; private final UserStore users;
  ReservationStore(Plane p, UserStore u){ this.plane=p; this.users=u; }

  boolean reserve(String uid, String seatId){
    Seat s = plane.seat(seatId);
    if (s==null || s.reserved) return false;
    s.reserved=true; s.reservedBy=uid; return true;
  }
  boolean cancel(String uid, String seatId){
    Seat s = plane.seat(seatId);
    if (s==null || !s.reserved || !uid.equals(s.reservedBy)) return false;
    s.reserved=false; s.reservedBy=null; return true;
  }
  List<Seat> seatsOf(String uid){
    List<Seat> list = new ArrayList<>();
    for (Seat s: plane.seats.values()) if (uid.equals(s.reservedBy)) list.add(s);
    list.sort(Comparator.comparing((Seat x)->x.id));
    return list;
  }
  Map<SeatId,String> manifest(){
    Map<SeatId,String> m = new TreeMap<>();
    for (Seat s: plane.seats.values())
      if (s.reserved) { User u = users.byId.get(s.reservedBy); m.put(s.id, u==null? "UNKNOWN" : u.name); }
    return m;
  }

  void load(File f) throws Exception {
    try (BufferedReader br = new BufferedReader(new FileReader(f))) {
      String line;
      while((line=br.readLine())!=null){
        if (line.isBlank()) continue;
        String[] a = line.split(",", -1);
        if (a.length<4) continue;
        String uid=a[0], seatId=a[1];
        Seat s = plane.seat(seatId);
        if (s!=null){ s.reserved=true; s.reservedBy=uid; }
      }
    }
  }
  void save(File f) throws Exception {
    try (PrintWriter pw = new PrintWriter(new FileWriter(f,false))) {
      for (Seat s: plane.seats.values()){
        if (s.reserved){
          int price = SeatClass.priceOf(s.seatClass);
          pw.println(String.join(",", s.reservedBy, s.id.toString(), s.seatClass.name(), String.valueOf(price)));
        }
      }
    }
  }
}
