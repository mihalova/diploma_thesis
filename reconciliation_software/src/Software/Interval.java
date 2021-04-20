package Software;

public class Interval {
    private double maximum;
    private double minimum;

    public Interval(double max, double min){
        this.maximum = max;
        this.minimum = min;
    }

    public double getMaximum() {
        return maximum;
    }

    public void setMaximum(double maximum) {
        this.maximum = maximum;
    }

    public double getMinimum() {
        return minimum;
    }

    public void setMinimum(double minimum) {
        this.minimum = minimum;
    }

}
