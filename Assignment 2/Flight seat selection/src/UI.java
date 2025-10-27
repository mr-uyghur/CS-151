
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
/**
 * UI.java
 * ---------------------------------------------------------
 * Handles all console input/output for both public and admin users.
 * Responsibilities:
 *   - Display menus
 *   - Route user actions to ReservationStore and UserStore
 *   - Provide formatted outputs (availability, manifest, etc.)
 */

class UI {

    private final Scanner in = new Scanner(System.in);
    private final Plane plane;
    private final UserStore users;
    private final ReservationStore store;

    UI(Plane p, UserStore u, ReservationStore s) {
        plane = p;
        users = u;
        store = s;
    }

    void run(File resFile, File usrFile) throws Exception {
        while (true) {
            System.out.println("\n[1] Sign up  [2] Sign in  [3] Admin  [0] Exit program");
            String ch = in.nextLine().trim();
            switch (ch) {
                case "1":
                    signUp();
                    break;
                case "2": {
                    User u = signIn();
                    if (u != null) {
                        userMenu(u);
                    }
                    break;
                }
                case "3":
                    adminMenu();
                    break;
                case "0":
                    store.save(resFile);
                    users.save(usrFile);
                    System.out.println("Saved. Bye!");
                    return;
            }
        }
    }

    private void signUp() {
        System.out.print("Choose user id: ");
        String id = in.nextLine().trim();
        System.out.print("Your name: ");
        String name = in.nextLine().trim();
        System.out.print("Password: ");
        String pw = in.nextLine().trim();
        User u = users.signUp(id, name, pw);
        System.out.println(u == null ? "User id already exists." : "Signed up. You can sign in now.");
    }

    private User signIn() {
        while (true) {
            System.out.print("User id: ");
            String id = in.nextLine().trim();
            System.out.print("Password: ");
            String pw = in.nextLine().trim();
            User u = users.authenticate(id, pw);
            if (u != null && !u.isAdmin) {
                return u;
            }
            System.out.println("Invalid credentials for public user. Try again.");
        }
    }

    private void userMenu(User u) {
        while (true) {
            System.out.println("\nCheck [A]vailability  Make [R]eservation  [C]ancel  [V]iew  [D]one");
            String ch = in.nextLine().trim().toUpperCase();
            switch (ch) {
                case "A":
                    availability();
                    break;
                case "R":
                    reserve(u);
                    break;
                case "C":
                    cancel(u);
                    break;
                case "V":
                    view(u);
                    break;
                case "D":
                    return;
            }
        }
    }

    private void availability() {
        showClass(SeatClass.FIRST, "First ($1000/seat)");
        showClass(SeatClass.ECON_PLUS, "Economy Plus ($500/seat)");
        showClass(SeatClass.ECONOMY, "Economy ($250/seat)");
    }

    private void showClass(SeatClass c, String title) {
        System.out.println("\n" + title);
        Map<Integer, List<Character>> m = plane.availableByRow(c);
        for (var e : m.entrySet()) {
            System.out.print(e.getKey() + ": ");
            for (char col : e.getValue()) {
                System.out.print(col + " ");
            }
            System.out.println();
        }
    }

    private void reserve(User u) {
        while (true) {
            System.out.print("Enter seat (e.g., 1A): ");
            String sId = in.nextLine().trim().toUpperCase();
            if (!plane.isValidSeat(sId)) {
                System.out.println("Invalid seat.");
                continue;
            }
            Seat s = plane.seat(sId);
            if (s.reserved) {
                System.out.println("Not available.");
                continue;
            }
            int price = SeatClass.priceOf(s.seatClass);
            System.out.printf("Seat %s, %s, $%d. Confirm? (Y/N): ", sId, s.seatClass, price);
            if (in.nextLine().trim().equalsIgnoreCase("Y")) {
                store.reserve(u.id, sId);
                System.out.println("Reserved " + sId + ".");
            }
            System.out.print("Another? (Y/N): ");
            if (!in.nextLine().trim().equalsIgnoreCase("Y")) {
                break;
            }
        }
    }

    private void cancel(User u) {
        List<Seat> mine = store.seatsOf(u.id);
        if (mine.isEmpty()) {
            System.out.println("No reservations.");
            return;
        }
        System.out.println("Your seats:");
        for (Seat s : mine) {
            System.out.println(" - " + s.id);
        }
        while (true) {
            System.out.print("Enter a seat to cancel: ");
            String sId = in.nextLine().trim().toUpperCase();
            if (store.cancel(u.id, sId)) {
                System.out.println("Canceled " + sId);
                break;
            }
            System.out.println("Please enter one from your list.");
        }
    }

    private void view(User u) {
        List<Seat> mine = store.seatsOf(u.id);
        int total = 0;
        System.out.println("Name: " + u.name);
        System.out.print("Seats: ");
        for (int i = 0; i < mine.size(); i++) {
            Seat s = mine.get(i);
            int price = SeatClass.priceOf(s.seatClass);
            total += price;
            System.out.print(s.id + " $" + price + (i < mine.size() - 1 ? ", " : ""));
        }
        System.out.println();
        System.out.println("Total Balance Due: $" + total);
    }

    private void adminMenu() {
        // Admin login by employee id (must exist and isAdmin=true)
        while (true) {
            System.out.print("Admin id: ");
            String id = in.nextLine().trim();
            System.out.print("Password: ");
            String pw = in.nextLine().trim();
            User u = users.authenticate(id, pw);
            if (u != null && u.isAdmin) {
                break;
            }
            System.out.println("Invalid admin credentials.");
        }
        while (true) {
            System.out.println("\nShow [M]anifest  E[X]it to main");
            String ch = in.nextLine().trim().toUpperCase();
            if ("M".equals(ch)) {
                printManifest();
            } else if ("X".equals(ch)) {
                return;
            }
        }
    }

    private void printManifest() {
        Map<SeatId, String> m = store.manifest();
        System.out.println("\nFirst");
        printRange(m, 1, 4);
        System.out.println("\nEconomy Plus");
        printRange(m, 5, 15);
        System.out.println("\nEconomy");
        printRange(m, 16, 50);
    }

    private void printRange(Map<SeatId, String> m, int r1, int r2) {
        for (var e : m.entrySet()) {
            int r = e.getKey().row;
            if (r >= r1 && r <= r2) {
                System.out.println(e.getKey() + ": " + e.getValue());
            }
        }
    }
}
