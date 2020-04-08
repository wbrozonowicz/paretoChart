package sample;

import javafx.beans.property.SimpleStringProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TableItem {
    private final SimpleStringProperty name;
    private double value;
    private double percent;
    private double percentCumulativ;
    private double percentRounded;
    private double percentCumulativRounded;

    public TableItem(String name, double value, double percent, double percentCumulativ) {
        this.name = new SimpleStringProperty(name);
        this.value = value;
        this.percent = percent;
        this.percentCumulativ = percentCumulativ;
    }


    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
        this.percentRounded = round(percent, 2);
    }


    public double getPercentCumulativ() {
        return percentCumulativ;
    }

    public void setPercentCumulativ(double percentCumulativ) {
        this.percentCumulativ = percentCumulativ;
        this.percentCumulativRounded = round(percentCumulativ, 2);
    }


    public double getPercentRounded() {
        return percentRounded;
    }

    public double getPercentCumulativRounded() {
        return percentCumulativRounded;
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
