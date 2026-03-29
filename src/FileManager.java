import java.io.*;
import java.util.ArrayList;
import java.util.List;
public class FileManager {

    private static final String DATA_DIR      = "data/";
    private static final String FILE_EXT      = ".txt";
    private static final String TAG_USER      = "USER:";
    private static final String TAG_RECORD    = "RECORD:";
    private static final String TAG_SLEEP     = "SLEEP:";
    private static final String TAG_WORK      = "WORK:";
    private static final String TAG_STRESS    = "STRESS:";
    public FileManager() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (created) {
                System.out.println("  Created data directory: " + DATA_DIR);
            }
        }
    }
    public void saveUser(User user) throws IOException {
        if (user == null)
            throw new IllegalArgumentException("User cannot be null");

        String filepath = buildPath(user.getUserId());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
            writer.write(TAG_USER
                + user.getUserId() + ","
                + user.getName()   + ","
                + user.getAge());
            writer.newLine();
            for (DailyRecord record : user.getRecords()) {
                writer.write(TAG_RECORD + record.getDate());
                writer.newLine();

                for (BehaviourSignal signal : record.getSignals()) {
                    writer.write(signalToLine(signal));
                    writer.newLine();
                }
            }

        }
        System.out.println("  Saved " + user.getRecords().size()
            + " record(s) for '" + user.getName()
            + "' → " + filepath);
    }
    public User loadUser(String userId) throws IOException {
        if (userId == null || userId.trim().isEmpty())
            throw new IllegalArgumentException("User ID cannot be empty");

        String filepath = buildPath(userId);
        File   file     = new File(filepath);

        if (!file.exists())
            throw new FileNotFoundException(
                "No saved data found for user ID '" + userId + "' in " + DATA_DIR);

        User        user          = null;
        DailyRecord currentRecord = null;
        int         lineNumber    = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty()) continue; 

                if (line.startsWith(TAG_USER)) {
                    user = parseUserLine(line, lineNumber);

                } else if (line.startsWith(TAG_RECORD)) {
                    if (user == null)
                        throw new IOException("RECORD tag found before USER tag at line " + lineNumber);
                    currentRecord = new DailyRecord(line.substring(TAG_RECORD.length()).trim());
                    user.addRecord(currentRecord);

                } else if (line.startsWith(TAG_SLEEP)
                        || line.startsWith(TAG_WORK)
                        || line.startsWith(TAG_STRESS)) {
                    if (currentRecord == null)
                        throw new IOException("Signal tag found before any RECORD tag at line " + lineNumber);
                    BehaviourSignal signal = parseSignalLine(line, currentRecord.getDate(), lineNumber);
                    currentRecord.addSignal(signal);

                } else {
                 
                    System.out.println("  Warning: unrecognised line " + lineNumber + " skipped: " + line);
                }
            }
        }

        if (user == null)
            throw new IOException("File appears empty or missing USER tag: " + filepath);

        System.out.println("  Loaded '" + user.getName()
            + "' with " + user.getRecords().size() + " record(s) from " + filepath);
        return user;
    }
    public List<String> listSavedUsers() {
        List<String> ids  = new ArrayList<>();
        File         dir  = new File(DATA_DIR);
        File[]       files = dir.listFiles(
            (d, name) -> name.endsWith(FILE_EXT)
        );
        if (files != null) {
            for (File f : files) {
                ids.add(f.getName().replace(FILE_EXT, ""));
            }
        }
        return ids;
    }
    public boolean deleteUser(String userId) {
        File file = new File(buildPath(userId));
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) System.out.println("  Deleted saved data for: " + userId);
            return deleted;
        }
        return false;
    }
    public boolean userFileExists(String userId) {
        return new File(buildPath(userId)).exists();
    }
    private String buildPath(String userId) {
        return DATA_DIR + userId + FILE_EXT;
    }
    private String signalToLine(BehaviourSignal signal) {
        if (signal instanceof SleepSignal) {
            return TAG_SLEEP + ((SleepSignal) signal).getHoursSlept();
        } else if (signal instanceof WorkSession) {
            return TAG_WORK + ((WorkSession) signal).getHoursWorked();
        } else if (signal instanceof StressEntry) {
            return TAG_STRESS + ((StressEntry) signal).getSelfRating();
        }
        throw new IllegalArgumentException(
            "Unknown BehaviorSignal subclass: " + signal.getClass().getName());
    }
    private User parseUserLine(String line, int lineNumber) throws IOException {
        String data = line.substring(TAG_USER.length()).trim();
        String[] parts = data.split(",", 3); 
        if (parts.length < 3)
            throw new IOException(
                "Malformed USER line at line " + lineNumber + ": expected id,name,age → got: " + data);

        try {
            String id   = parts[0].trim();
            String name = parts[1].trim();
            int    age  = Integer.parseInt(parts[2].trim());
            return new User( name, age, id);
        } catch (NumberFormatException e) {
            throw new IOException(
                "Invalid age in USER line at line " + lineNumber + ": " + data);
        } catch (IllegalArgumentException e) {
            throw new IOException(
                "Invalid user data at line " + lineNumber + ": " + e.getMessage());
        }
    }
    private BehaviourSignal parseSignalLine(String line, String date, int lineNumber)
            throws IOException {
        try {
            if (line.startsWith(TAG_SLEEP)) {
                double hours = Double.parseDouble(line.substring(TAG_SLEEP.length()).trim());
                return new SleepSignal(date, hours);

            } else if (line.startsWith(TAG_WORK)) {
                double hours = Double.parseDouble(line.substring(TAG_WORK.length()).trim());
                return new WorkSession(date, hours);

            } else if (line.startsWith(TAG_STRESS)) {
                int rating = Integer.parseInt(line.substring(TAG_STRESS.length()).trim());
                return new StressEntry(date, rating);
            }
        } catch (NumberFormatException e) {
            throw new IOException(
                "Malformed numeric value at line " + lineNumber + ": " + line);
        } catch (IllegalArgumentException e) {
            throw new IOException(
                "Invalid signal data at line " + lineNumber + ": " + e.getMessage());
        }

        throw new IOException("Unrecognised signal tag at line " + lineNumber + ": " + line);
    }
}