
public class SleepSignal extends BehaviourSignal {
    private double hoursSlept;

    public SleepSignal(String date, double hoursSlept) {
        super("SLEEP", date);
        this.hoursSlept = hoursSlept;
    }

    public double getHoursSlept() { return hoursSlept; }

    @Override
    public int getStressContribution() {
        if (hoursSlept < 4) return 4;
        if (hoursSlept < 6) return 2;
        if (hoursSlept > 9) return 1;
        return 0;
    }

    @Override
    public String getSummary() {
        return "Slept " + hoursSlept + " hours";
    }
}
