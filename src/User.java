import java.util.ArrayList;
import java.util.List;

public class User {
    private String userId;
    private String name;
    private int age;
    private List<DailyRecord> records; // Composition

    public User(String name, int age, String userId ) {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Name cannot be empty");
        if (age < 0 || age > 150)
            throw new IllegalArgumentException("Invalid age");
        this.userId = userId;
        this.name = name;
        this.age = age;
        this.records = new ArrayList<>();
    }

    // Getters only — no public setters to enforce encapsulation
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public int getAge() { return age; }

    public void addRecord(DailyRecord record) {
        records.add(record);
    }

    public List<DailyRecord> getRecords() {
        return new ArrayList<>(records);
    }

    public DailyRecord getRecordByDate(String date) {
        for (DailyRecord r : records) {
            if (r.getDate().equals(date)) return r;
        }
        return null;
    }

    @Override
    public String toString() {
        return "User[" + userId + "] " + name + ", age " + age + 
               " | Records: " + records.size();
    }
}