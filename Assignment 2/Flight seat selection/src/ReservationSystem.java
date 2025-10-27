import java.io.File;

/**
 * ReservationSystem.java
 * ---------------------------------------------------------
 * Main entry point for the Airplane Seat Reservation System.
 * Handles:
 *   - Command line arguments (reservation & user files)
 *   - File creation/loading on startup
 *   - Program initialization and shutdown
 *   - Launches the UI loop for public/admin users
 */


/** Entry point: handles boot, file create/load, and app loop. */
public class ReservationSystem {
  private static final String CREATED_MSG = "CL34 and Users are now created.";
  private static final String LOADED_MSG  = "Existing Reservations and Users are loaded.";

  public static void main(String[] args) throws Exception {
    if (args.length < 2) {
      System.out.println("Usage: java ReservationSystem <CL34> <Users>");
      return;
    }
    File resFile = new File(args[0]);
    File usrFile = new File(args[1]);

    boolean created = false;
    if (!resFile.exists()) { resFile.getParentFile().mkdirs(); resFile.createNewFile(); created = true; }
    if (!usrFile.exists()) { usrFile.getParentFile().mkdirs(); usrFile.createNewFile(); created = true; }
    if (created) System.out.println(CREATED_MSG);

    Plane plane = Plane.defaultLayout();
    UserStore users = new UserStore();
    ReservationStore store = new ReservationStore(plane, users);

    users.load(usrFile);
    store.load(resFile);
    if (!created) System.out.println(LOADED_MSG);

    new UI(plane, users, store).run(resFile, usrFile); // will save on program exit
  }
}
