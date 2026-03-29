import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class DailyRecord {

    private String date;
    private List<BehaviourSignal> signals;
    public DailyRecord(String date) {
        if (date == null || date.trim().isEmpty())
            throw new IllegalArgumentException("Date cannot be empty");
        this.date = date;
        this.signals = new ArrayList<>();
    }

    public String getDate() {
        return date;
    }
    public List<BehaviourSignal> getSignals() {
        return Collections.unmodifiableList(signals);
    }

    public int getSignalCount() {
        return signals.size();
    }
    public void addSignal(BehaviourSignal signal) {
        if (signal == null)
            throw new IllegalArgumentException("Signal cannot be null");
        if (!signal.getDate().equals(this.date))
            throw new IllegalArgumentException(
                "Signal date (" + signal.getDate() + ") does not match record date (" + this.date + ")"
            );
        signals.add(signal);
    }

    public int getTotalStressScore() {
        int total = 0;
        for (BehaviourSignal signal : signals) {
            total += signal.getStressContribution();
        }
        return total;
    }
    public String getRiskLevel() {
        int score = getTotalStressScore();
        if (score >= 8) return "HIGH";
        if (score >= 4) return "MODERATE";
        return "LOW";
    }
    public boolean hasSignalOfType(String signalType) {
        for (BehaviourSignal signal : signals) {
            if (signal.getSignalType().equalsIgnoreCase(signalType)) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ").append(date)
          .append(" | Risk: ").append(getRiskLevel())
          .append(" | Score: ").append(getTotalStressScore())
          .append(" ===\n");

        if (signals.isEmpty()) {
            sb.append("  (no signals recorded)\n");
        } else {
            for (BehaviourSignal signal : signals) {
                sb.append("  ").append(signal).append("\n");
            }
        }
        return sb.toString();
    }
}
