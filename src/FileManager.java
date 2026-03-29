import java.io.*;
public class FileManager {
    private static final String FILE_NAME = "empathai_data.txt";
    public void save(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {

            writer.write(user.getName() + "," + user.getAge() + "," + user.getUserId());
            writer.newLine();

            for (DailyRecord record : user.getRecords()) {
                writer.write("DATE:" + record.getDate());
                writer.newLine();

                for (BehaviourSignal signal : record.getSignals()) {
                    if (signal instanceof SleepSignal) {
                        SleepSignal s = (SleepSignal) signal;
                        writer.write("SLEEP," + s.getHoursSlept());
                    } else if (signal instanceof WorkSession) {
                        WorkSession w = (WorkSession) signal;
                        writer.write("WORK," + w.getHoursWorked());
                    } else if (signal instanceof StressEntry) {
                        StressEntry s = (StressEntry) signal;
                        writer.write("STRESS," + s.getStressContribution());
                    }
                    writer.newLine();
                }
            }
            System.out.println("Data saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving file.");
        }
    }
    public User load() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("No saved data found.");
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {

            String line = reader.readLine();
            if (line == null) return null;

            String[] userData = line.split(",");
            User user = new User(userData[0], Integer.parseInt(userData[1]), userData[2]);

            DailyRecord currentRecord = null;

            while ((line = reader.readLine()) != null) {

                if (line.startsWith("DATE:")) {
                    currentRecord = new DailyRecord(line.substring(5));
                    user.addRecord(currentRecord);
                } else if (currentRecord != null) {

                    String[] parts = line.split(",");

                    switch (parts[0]) {
                        case "SLEEP":
                            currentRecord.addSignal(new SleepSignal(currentRecord.getDate(),
                                    Integer.parseInt(parts[1])));
                            break;

                        case "WORK":
                            currentRecord.addSignal(new WorkSession(currentRecord.getDate(),
                                    Integer.parseInt(parts[1])));
                            break;

                        case "STRESS":
                            currentRecord.addSignal(new StressEntry(currentRecord.getDate(),
                                    Integer.parseInt(parts[1])));
                            break;
                    }
                }
            }

            System.out.println("Data loaded successfully.");
            return user;

        } catch (IOException e) {
            System.out.println("Error loading file.");
        }

        return null;
    }

    // ================= AUTO-SAVE THREAD =================
    public void autoSave(User user) {
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(10000); // save every 10 sec
                    save(user);
                } catch (InterruptedException e) {
                    System.out.println("Auto-save stopped.");
                }
            }
        });

        t.setDaemon(true); // background thread
        t.start();
    }
}
