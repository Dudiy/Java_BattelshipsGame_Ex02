package javaFXUI.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ShipsStateDataModel {
    private final SimpleStringProperty shipType = new SimpleStringProperty();
    private final SimpleIntegerProperty initialAmount = new SimpleIntegerProperty();
    ;
    private final IntegerProperty shipsRemaining = new SimpleIntegerProperty();

    public ShipsStateDataModel(String shipType, int initialAmount) {
        this.shipType.setValue(shipType);
        this.initialAmount.setValue(initialAmount);
        this.shipsRemaining.setValue(initialAmount);
    }

    public IntegerProperty shipsRemainingProperty() {
        return shipsRemaining;
    }

    public void setShipsRemaining(int shipsRemaining) {
        this.shipsRemaining.set(shipsRemaining);
    }

    public String getShipType() {
        return shipType.get();
    }

    public void setShipType(String shipType) {
        this.shipType.set(shipType);
    }

    public int getInitialAmount() {
        return initialAmount.get();
    }

    public void setInitialAmount(int initialAmount) {
        this.initialAmount.set(initialAmount);
    }

    public int getShipsRemaining() {
        return shipsRemaining.get();
    }
}
