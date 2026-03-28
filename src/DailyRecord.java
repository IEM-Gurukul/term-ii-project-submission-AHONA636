import java.util.ArrayList;
import java.util.List;

public class DailyRecord {
    private List<String> records;

    public DailyRecord() {
        records = new ArrayList<>();
    }

    public void addRecord(String record) {
        records.add(record);
    }

    public List<String> getRecords() {
        return records;
    }
}