
import java.util.List;

public class BehaviourAnalyzer {

    public String analyzeDay(DailyRecord record) {
        int score = record.getTotalStressScore();
        String risk = record.getRiskLevel();
        StringBuilder sb = new StringBuilder();
        sb.append("Date: ").append(record.getDate()).append("\n");
        sb.append("Stress Score: ").append(score).append("\n");
        sb.append("Risk Level: ").append(risk).append("\n");
        for (BehaviourSignal signal : record.getSignals()) {
            if (signal instanceof SleepSignal) {
                SleepSignal s = (SleepSignal) signal;
                if (s.getHoursSlept() < 5) {
                    sb.append("⚠ Warning: Very low sleep detected\n");
                }
            }
            if (signal instanceof WorkSession) {
                WorkSession w = (WorkSession) signal;
                if (w.getHoursWorked() > 10) {
                    sb.append("⚠ Warning: Overwork detected\n");
                }
            }
        }
        return sb.toString();
    }
    public String analyzeWeek(User user) {
        List<DailyRecord> records = user.getRecords();
        if (records.isEmpty()) return "No data available for " + user.getName();

        int totalScore = 0;
        int highRiskDays = 0;
        StringBuilder sb = new StringBuilder();
        sb.append("=== Weekly Analysis for ").append(user.getName()).append(" ===\n");

        int start = Math.max(0, records.size() - 7);
        for (int i = start; i < records.size(); i++) {
            DailyRecord r = records.get(i);
            totalScore += r.getTotalStressScore();
            if (r.getRiskLevel().equals("HIGH")) highRiskDays++;
        }

        int days = records.size() - start;
        double avgScore = (double) totalScore / days;
        sb.append("Days analyzed: ").append(days).append("\n");
        sb.append("Average stress score: ").append(String.format("%.1f", avgScore)).append("\n");
        sb.append("High-risk days: ").append(highRiskDays).append("\n");
        if (avgScore >= 6) {
            sb.append("Recommendation: Consider speaking to a counselor or doctor.\n");
        } else if (avgScore >= 3) {
            sb.append("Recommendation: Monitor your patterns and prioritize rest.\n");
        } else {
            sb.append("Recommendation: You're doing well. Keep maintaining healthy habits.\n");
        }

        return sb.toString();
    }
}
