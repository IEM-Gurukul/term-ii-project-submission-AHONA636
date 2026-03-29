
public class StressEntry extends BehaviourSignal {
    private int selfRating; 

    public StressEntry(String date, int selfRating) {
        super("STRESS", date);
        if (selfRating < 1 || selfRating > 10)
            throw new IllegalArgumentException("Stress rating must be 1-10");
        this.selfRating = selfRating;
    }

    public int getSelfRating() { return selfRating; }

    @Override
    public int getStressContribution() {
        if (selfRating >= 8) return 4;
        if (selfRating >= 6) return 2;
        if (selfRating >= 4) return 1;
        return 0;
    }

    @Override
    public String getSummary() {
        return "Self-reported stress: " + selfRating + "/10";
    }
}
