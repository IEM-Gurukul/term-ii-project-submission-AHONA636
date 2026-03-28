
public abstract class BehaviorSignal {
    private String signalType;
    private String date;

    public BehaviorSignal(String signalType, String date) {
        this.signalType = signalType;
        this.date = date;
    }

    public String getSignalType() { return signalType; }
    public String getDate() { return date; }

    // Abstract method — every subclass MUST implement this
    public abstract int getStressContribution();

    // Abstract display — polymorphism in action
    public abstract String getSummary();

    @Override
    public String toString() {
        return "[" + signalType + " | " + date + "] " + getSummary();
    }
}