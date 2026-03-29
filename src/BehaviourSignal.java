
public abstract class BehaviourSignal {
    private String signalType;
    private String date;

    public BehaviourSignal(String signalType, String date) {
        this.signalType = signalType;
        this.date = date;
    }

    public String getSignalType() { return signalType; }
    public String getDate() { return date; }
    public abstract int getStressContribution();
    public abstract String getSummary();

    @Override
    public String toString() {
        return "[" + signalType + " | " + date + "] " + getSummary();
    }
}