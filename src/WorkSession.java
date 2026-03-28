

public class WorkSession extends BehaviorSignal {
    private double hoursWorked;

    public WorkSession(String date, double hoursWorked) {
        super("WORK", date);
        this.hoursWorked = hoursWorked;
    }

    public double getHoursWorked() { return hoursWorked; }

    @Override
    public int getStressContribution() {
        if (hoursWorked > 12) return 4;
        if (hoursWorked > 8)  return 2;
        if (hoursWorked > 6)  return 1;
        return 0;
    }

    @Override
    public String getSummary() {
        return "Worked " + hoursWorked + " hours";
    }
}
