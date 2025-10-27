import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * UserStore.java
 * ---------------------------------------------------------
 * Manages all registered users.
 * Responsibilities:
 *   - Sign up new users
 *   - Authenticate sign-in
 *   - Load/save user data to file
 *   - Ensure at least one admin account exists
 *
 * File format (CSV):
 *   userId,password,name,isAdmin
 */

/** Simple CSV: userId,password,name,isAdmin */
class UserStore {
  final Map<String,User> byId = new HashMap<>();

  User authenticate(String id, String pw){
    User u = byId.get(id);
    return (u!=null && u.password.equals(pw)) ? u : null;
  }
  User signUp(String id, String name, String pw){
    if (byId.containsKey(id)) return null;
    User u = new User(); u.id=id; u.name=name; u.password=pw; u.isAdmin=false;
    byId.put(id,u); return u;
  }
  boolean isEmployeeId(String id){
    User u = byId.get(id); return u!=null && u.isAdmin;
  }

  void load(File f) throws Exception {
    try (BufferedReader br = new BufferedReader(new FileReader(f))) {
      String line;
      while((line=br.readLine())!=null){
        if (line.isBlank()) continue;
        String[] a = line.split(",", -1);
        if (a.length<4) continue;
        User u = new User();
        u.id=a[0]; u.password=a[1]; u.name=a[2]; u.isAdmin=Boolean.parseBoolean(a[3]);
        byId.put(u.id,u);
      }
    }
    // ensure one admin if none exists
    if (byId.values().stream().noneMatch(u->u.isAdmin)){
      User admin = new User(); admin.id="admin"; admin.password="admin"; admin.name="Administrator"; admin.isAdmin=true;
      byId.put(admin.id, admin);
    }
  }

  void save(File f) throws Exception {
    try (PrintWriter pw = new PrintWriter(new FileWriter(f,false))) {
      for (User u: byId.values()){
        pw.println(String.join(",", u.id, u.password, u.name, String.valueOf(u.isAdmin)));
      }
    }
  }
}
