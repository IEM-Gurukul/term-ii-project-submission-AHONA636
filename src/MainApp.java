
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Entry point for EmpathAI.
 * Demonstrates: Exception handling, multi-user management, clean console UI
 */
public class MainApp {

    private static final Map<String, User> users       = new HashMap<>();
    private static User          currentUser           = null;
    private static final Scanner scanner               = new Scanner(System.in);
    private static final FileManager     fileManager   = new FileManager();
    private static final ReportGenerator reportGen     = new ReportGenerator();
    private static final BehaviourAnalyzer analyzer     = new BehaviourAnalyzer();

    // -----------------------------------------------------------------------
    // Entry point
    // -----------------------------------------------------------------------

    public static void main(String[] args) {
        printBanner();
        boolean running = true;

        while (running) {
            printMainMenu();
            String choice = readLine("Choice");

            try {
                switch (choice) {
                    case "1":  createUser();       break;
                    case "2":  selectUser();       break;
                    case "3":  addDailyRecord();   break;
                    case "4":  viewWeeklyReport(); break;
                    case "5":  viewDailyReport();  break;
                    case "6":  viewAllRecords();   break;
                    case "7":  saveCurrentUser();  break;
                    case "8":  loadUser();         break;
                    case "9":  listUsers();        break;
                    case "0":  running = false;
                               System.out.println("\nTake care of yourself. Goodbye!\n");
                               break;
                    default:   System.out.println("  Invalid option — enter a number 0–9.");
                }
            } catch (Exception e) {
                // Top-level safety net — no crash reaches the user
                System.out.println("  Unexpected error: " + e.getMessage());
            }
        }

        scanner.close();
    }

    // -----------------------------------------------------------------------
    // Menu options
    // -----------------------------------------------------------------------

    /** Option 1 — create a new user and set as current */
    private static void createUser() {
        System.out.println("\n--- Create New User ---");
        try {
            String id   = readLine("User ID (e.g. u001)");
            if (users.containsKey(id)) {
                System.out.println("  A user with ID '" + id + "' already exists.");
                return;
            }
            String name = readLine("Full name");
            int    age  = readInt("Age");

            User u = new User(name, age,id);
            users.put(id, u);
            currentUser = u;
            System.out.println("  Created and selected: " + u);

        } catch (IllegalArgumentException e) {
            System.out.println("  Validation error: " + e.getMessage());
        }
    }

    /** Option 2 — switch between loaded users */
    private static void selectUser() {
        System.out.println("\n--- Select User ---");
        if (users.isEmpty()) {
            System.out.println("  No users in memory. Create one (option 1) or load one (option 8).");
            return;
        }
        listUsers();
        String id = readLine("Enter user ID to select");
        if (users.containsKey(id)) {
            currentUser = users.get(id);
            System.out.println("  Now working with: " + currentUser.getName());
        } else {
            System.out.println("  User ID '" + id + "' not found in memory.");
        }
    }

    /** Option 3 — add a full daily record for the current user */
    private static void addDailyRecord() {
        System.out.println("\n--- Add Daily Record ---");
        if (!requireUser()) return;

        try {
            String date = readLine("Date (YYYY-MM-DD)");

            // Warn if a record for this date already exists
            if (currentUser.getRecordByDate(date) != null) {
                System.out.println("  A record for " + date + " already exists.");
                String overwrite = readLine("  Overwrite? (yes/no)");
                if (!overwrite.equalsIgnoreCase("yes")) {
                    System.out.println("  Cancelled.");
                    return;
                }
            }

            DailyRecord record = new DailyRecord(date);

            // --- Sleep ---
            System.out.println("\n  [Sleep]");
            double sleep = readDouble("  Hours slept last night (e.g. 7.5)");
            record.addSignal(new SleepSignal(date, sleep));
            System.out.println("  Stress contribution: +" + new SleepSignal(date, sleep).getStressContribution());

            // --- Work ---
            System.out.println("\n  [Work]");
            double work = readDouble("  Hours worked today (e.g. 8.0)");
            record.addSignal(new WorkSession(date, work));
            System.out.println("  Stress contribution: +" + new WorkSession(date, work).getStressContribution());

            // --- Self-reported stress ---
            System.out.println("\n  [Stress]");
            System.out.println("  Rate your overall stress today:");
            System.out.println("  1-3 = low   4-6 = moderate   7-10 = high");
            int stress = readInt("  Stress level (1-10)");
            record.addSignal(new StressEntry(date, stress));
            System.out.println("  Stress contribution: +" + new StressEntry(date, stress).getStressContribution());

            // Commit to user
            currentUser.addRecord(record);

            // Instant feedback
            System.out.println("\n  Record saved.");
            System.out.println("  Day score  : " + record.getTotalStressScore() + " / 12");
            System.out.println("  Risk level : " + record.getRiskLevel());

            // Immediate warning for high-risk days
            if (record.getRiskLevel().equals("HIGH")) {
                System.out.println("\n  ⚠ High stress day detected.");
                System.out.println("  Consider rest, light activity, or talking to someone you trust.");
            }

        } catch (IllegalArgumentException e) {
            System.out.println("  Validation error: " + e.getMessage());
        }
    }

    /** Option 4 — full 7-day report */
    private static void viewWeeklyReport() {
        System.out.println("\n--- Weekly Report ---");
        if (!requireUser()) return;
        if (!requireRecords()) return;
        System.out.println(reportGen.generateReport(currentUser));
    }

    /** Option 5 — single-day report for a chosen date */
    private static void viewDailyReport() {
        System.out.println("\n--- Daily Report ---");
        if (!requireUser()) return;
        if (!requireRecords()) return;

        // Show available dates to help the user
        System.out.println("  Dates on record:");
        for (DailyRecord r : currentUser.getRecords()) {
            System.out.println("    " + r.getDate() + "  [" + r.getRiskLevel() + "]");
        }

        String date = readLine("Enter date (YYYY-MM-DD)");
        System.out.println(reportGen.generateReport(currentUser, date));
    }

    /** Option 6 — raw dump of all records for the current user */
    private static void viewAllRecords() {
        System.out.println("\n--- All Records for " + (currentUser != null ? currentUser.getName() : "?") + " ---");
        if (!requireUser()) return;
        if (!requireRecords()) return;

        List<DailyRecord> records = currentUser.getRecords();
        System.out.println("Total records: " + records.size() + "\n");
        for (DailyRecord r : records) {
            System.out.println(r);
        }

        // Quick weekly analysis inline
        System.out.println(analyzer.analyzeWeek(currentUser));
    }

    /** Option 7 — save current user to file */
    private static void saveCurrentUser() {
        System.out.println("\n--- Save User ---");
        if (!requireUser()) return;
        try {
            fileManager.saveUser(currentUser);
            System.out.println("  Saved '" + currentUser.getName() + "' to data/" + currentUser.getUserId() + ".txt");
        } catch (Exception e) {
            System.out.println("  Save failed: " + e.getMessage());
        }
    }

    /** Option 8 — load a user from file into memory */
    private static void loadUser() {
        System.out.println("\n--- Load User ---");

        // Show saved files as a hint
        List<String> saved = fileManager.listSavedUsers();
        if (!saved.isEmpty()) {
            System.out.println("  Saved users found: " + String.join(", ", saved));
        } else {
            System.out.println("  No saved files found in data/");
        }

        String id = readLine("Enter user ID to load");
        try {
            User loaded = fileManager.loadUser(id);
            users.put(loaded.getUserId(), loaded);
            currentUser = loaded;
            System.out.println("  Loaded: " + loaded);
            System.out.println("  Records found: " + loaded.getRecords().size());
        } catch (Exception e) {
            System.out.println("  Load failed: " + e.getMessage());
        }
    }

    /** Option 9 — list all users currently in memory */
    private static void listUsers() {
        System.out.println("\n--- Users in Memory ---");
        if (users.isEmpty()) {
            System.out.println("  None. Create or load a user first.");
            return;
        }
        for (Map.Entry<String, User> entry : users.entrySet()) {
            String marker = (currentUser != null &&
                             currentUser.getUserId().equals(entry.getKey())) ? " ◄ active" : "";
            System.out.println("  " + entry.getKey() + "  →  " + entry.getValue() + marker);
        }
    }

    // -----------------------------------------------------------------------
    // Display helpers
    // -----------------------------------------------------------------------

    private static void printBanner() {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════╗");
        System.out.println("  ║          E m p a t h A I            ║");
        System.out.println("  ║      Mental Wellness Tracker         ║");
        System.out.println("  ║            Java OOP Project          ║");
        System.out.println("  ╚══════════════════════════════════════╝");
        System.out.println();
    }

    private static void printMainMenu() {
        String activeUser = (currentUser != null)
            ? "  Active user: " + currentUser.getName() + "\n"
            : "  No user selected\n";

        System.out.println();
        System.out.println("  ┌─────────────────────────────────┐");
        System.out.println("  │           Main Menu             │");
        System.out.println("  ├─────────────────────────────────┤");
        System.out.println("  │  1. Create new user             │");
        System.out.println("  │  2. Select user                 │");
        System.out.println("  │  3. Add today's record          │");
        System.out.println("  │  4. View weekly report          │");
        System.out.println("  │  5. View daily report           │");
        System.out.println("  │  6. View all records            │");
        System.out.println("  │  7. Save user to file           │");
        System.out.println("  │  8. Load user from file         │");
        System.out.println("  │  9. List users in memory        │");
        System.out.println("  │  0. Exit                        │");
        System.out.println("  └─────────────────────────────────┘");
        System.out.print(activeUser);
    }

    // -----------------------------------------------------------------------
    // Input helpers — all exception handling lives here
    // -----------------------------------------------------------------------

    /** Reads a non-empty trimmed string from the user. */
    private static String readLine(String prompt) {
        while (true) {
            System.out.print("  " + prompt + ": ");
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println("  Input cannot be empty. Try again.");
        }
    }

    /** Reads a valid integer, re-prompting on bad input. */
    private static int readInt(String prompt) {
        while (true) {
            try {
                String raw = readLine(prompt);
                return Integer.parseInt(raw);
            } catch (NumberFormatException e) {
                System.out.println("  Please enter a whole number (e.g. 7).");
            }
        }
    }

    /** Reads a valid double, re-prompting on bad input. */
    private static double readDouble(String prompt) {
        while (true) {
            try {
                String raw = readLine(prompt);
                return Double.parseDouble(raw);
            } catch (NumberFormatException e) {
                System.out.println("  Please enter a number (e.g. 7.5).");
            }
        }
    }

    // -----------------------------------------------------------------------
    // Guard helpers — keep menu methods clean
    // -----------------------------------------------------------------------

    /** Returns false and prints a message if no user is selected. */
    private static boolean requireUser() {
        if (currentUser == null) {
            System.out.println("  No user selected. Create one (1) or load one (8) first.");
            return false;
        }
        return true;
    }

    /** Returns false and prints a message if the current user has no records. */
    private static boolean requireRecords() {
        if (currentUser.getRecords().isEmpty()) {
            System.out.println("  No records found for " + currentUser.getName() + ". Add one with option 3.");
            return false;
        }
        return true;
    }
}