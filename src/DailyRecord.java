
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class DailyRecord {

    private String date;
    private List<BehaviorSignal> signals;

    // Constructor
    public DailyRecord(String date) {
        if (date == null || date.trim().isEmpty())
            throw new IllegalArgumentException("Date cannot be empty");
        this.date = date;
        this.signals = new ArrayList<>();
    }

    // --- Getters (Encapsulation) ---

    public String getDate() {
        return date;
    }

    /**
     * Returns an unmodifiable view of the signals list.
     * Caller can read but cannot add/remove — protects internal state.
     */
    public List<BehaviorSignal> getSignals() {
        return Collections.unmodifiableList(signals);
    }

    public int getSignalCount() {
        return signals.size();
    }

    // --- Core Methods ---

    /**
     * Adds a behavioral signal to this day's record.
     * Validates that the signal's date matches this record's date.
     */
    public void addSignal(BehaviorSignal signal) {
        if (signal == null)
            throw new IllegalArgumentException("Signal cannot be null");
        if (!signal.getDate().equals(this.date))
            throw new IllegalArgumentException(
                "Signal date (" + signal.getDate() + ") does not match record date (" + this.date + ")"
            );
        signals.add(signal);
    }

    /**
     * Calculates total stress score by summing contributions from all signals.
     * This is Polymorphism in action — getStressContribution() behaves
     * differently depending on whether the signal is Sleep, Work, or Stress.
     */
    public int getTotalStressScore() {
        int total = 0;
        for (BehaviorSignal signal : signals) {
            total += signal.getStressContribution();
        }
        return total;
    }

    /**
     * Converts the numeric stress score into a human-readable risk level.
     */
    public String getRiskLevel() {
        int score = getTotalStressScore();
        if (score >= 8) return "HIGH";
        if (score >= 4) return "MODERATE";
        return "LOW";
    }

    /**
     * Checks whether a signal of a given type already exists for this day.
     * Useful to prevent duplicate sleep/work entries.
     */
    public boolean hasSignalOfType(String signalType) {
        for (BehaviorSignal signal : signals) {
            if (signal.getSignalType().equalsIgnoreCase(signalType)) return true;
        }
        return false;
    }

    // --- Display ---

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
            for (BehaviorSignal signal : signals) {
                sb.append("  ").append(signal).append("\n");
            }
        }
        return sb.toString();
    }
}
