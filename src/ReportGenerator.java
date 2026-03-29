import java.util.List;
interface Reportable {
    String generateReport(User user);
    String generateReport(User user, String specificDate);
}

public class ReportGenerator implements Reportable {

    private BehaviourAnalyzer analyzer; // Composition

    public ReportGenerator() {
        this.analyzer = new BehaviourAnalyzer();
    }
    @Override
    public String generateReport(User user) {
        if (user == null)
            throw new IllegalArgumentException("User cannot be null");

        List<DailyRecord> records = user.getRecords();

        StringBuilder sb = new StringBuilder();
        sb.append(header("EmpathAI Weekly Wellness Report"));
        sb.append("User     : ").append(user.getName()).append("\n");
        sb.append("Age      : ").append(user.getAge()).append("\n");
        sb.append("User ID  : ").append(user.getUserId()).append("\n");
        sb.append("Total days tracked : ").append(records.size()).append("\n");
        sb.append(divider());

        if (records.isEmpty()) {
            sb.append("No records found. Start adding daily entries.\n");
            sb.append(footer());
            return sb.toString();
        }

        sb.append("DAILY BREAKDOWN (last 7 days)\n");
        sb.append(divider());

        int start = Math.max(0, records.size() - 7);
        int highDays = 0, moderateDays = 0, lowDays = 0;
        int totalScore = 0;

        for (int i = start; i < records.size(); i++) {
            DailyRecord r = records.get(i);
            String risk = r.getRiskLevel();
            int score = r.getTotalStressScore();
            totalScore += score;

            switch (risk) {
                case "HIGH":     highDays++; break;
                case "MODERATE": moderateDays++; break;
                default:         lowDays++; break;
            }

            sb.append(String.format("%-12s  %s  %-10s  (%d signals)\n",
                    r.getDate(),
                    scoreBar(score),
                    "[" + risk + "]",
                    r.getSignalCount()
            ));

            for (BehaviourSignal signal : r.getSignals()) {
                sb.append("             └─ ").append(signal).append("\n");
            }
        }

        int days = records.size() - start;
        double avgScore = (double) totalScore / days;

        sb.append(divider());

        sb.append("RISK SUMMARY\n");
        sb.append(String.format("  High risk days     : %d\n", highDays));
        sb.append(String.format("  Moderate risk days : %d\n", moderateDays));
        sb.append(String.format("  Low risk days      : %d\n", lowDays));
        sb.append(String.format("  Average score      : %.1f / 12\n", avgScore));
        sb.append(divider());

        sb.append("ANALYSIS & RECOMMENDATION\n");
        sb.append(analyzer.analyzeWeek(user));

        sb.append(footer());
        return sb.toString();
    }

    // ================= DAILY REPORT =================
    @Override
    public String generateReport(User user, String specificDate) {
        if (user == null)
            throw new IllegalArgumentException("User cannot be null");

        if (specificDate == null || specificDate.trim().isEmpty())
            throw new IllegalArgumentException("Date cannot be empty");

        DailyRecord record = user.getRecordByDate(specificDate);

        if (record == null) {
            return "No record found for " + user.getName() + " on " + specificDate + ".\n";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(header("EmpathAI Daily Report — " + specificDate));
        sb.append("User : ").append(user.getName()).append("\n");
        sb.append(divider());

        sb.append(record).append("\n");

        sb.append(divider());
        sb.append("DAY ANALYSIS\n");
        sb.append(analyzer.analyzeDay(record));

        sb.append(footer());
        return sb.toString();
    }

    // ================= HELPER METHODS =================

    private String header(String title) {
        return "\n====================================\n"
             + title + "\n"
             + "====================================\n";
    }

    private String divider() {
        return "------------------------------------\n";
    }

    private String footer() {
        return "====================================\n";
    }

    private String scoreBar(int score) {
        StringBuilder bar = new StringBuilder();
        int max = 12;

        for (int i = 0; i < score; i++) {
            bar.append("█");
        }
        for (int i = score; i < max; i++) {
            bar.append("-");
        }

        return bar.toString();
    }
}