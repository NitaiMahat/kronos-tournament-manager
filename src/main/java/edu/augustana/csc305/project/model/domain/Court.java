package edu.augustana.csc305.project.model.domain;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Represents a single court in the tournament management system.
 * A {@code Court} holds information about its name, availability,
 * and a unique identifier for database and UI use.
 * <p>
 * This class is implemented using JavaFX properties to support
 * seamless data binding with user interface components.
 * </p>
 * This class JavaDoc was made by Gemini 2.5 Flash.
 */
public class Court {

    private final StringProperty courtName;
    private final BooleanProperty isAvailable;
    private int courtId;

    /**
     * Constructs a new {@code Court}.
     *
     * @param courtName   The display name for the court.
     * @param isAvailable The initial availability status of the court.
     */
    public Court(String courtName, boolean isAvailable) {
        this.courtName = new SimpleStringProperty(courtName);
        this.isAvailable = new SimpleBooleanProperty(isAvailable);
    }

    /**
     * Returns the unique ID of this court.
     *
     * @return the court's ID as an {@code int}
     */
    public int getCourtId() {
        return courtId;
    }

    /**
     * Sets the unique ID of the court, typically after it has been persisted.
     *
     * @param courtId The database ID for the court.
     */
    public void setCourtId(int courtId) {
        this.courtId = courtId;
    }

    /**
     * Returns the current display name of this court.
     *
     * @return the court's name as a {@code String}
     */
    public String getCourtName() {
        return courtName.get();
    }

    /**
     * Updates the display name of this court.
     *
     * @param courtName the new name for the court
     */
    public void setCourtName(String courtName) {
        this.courtName.set(courtName);
    }

    /**
     * Returns the {@code StringProperty} representing the court's name.
     *
     * @return the {@code courtName} property
     */
    public StringProperty courtNameProperty() {
        return courtName;
    }

    /**
     * Returns whether this court is currently available.
     *
     * @return {@code true} if available, {@code false} otherwise
     */
    public boolean isAvailable() {
        return isAvailable.get();
    }

    /**
     * Updates the availability status of this court.
     *
     * @param available {@code true} if the court is available, {@code false} otherwise
     */
    public void setAvailable(boolean available) {
        this.isAvailable.set(available);
    }

    /**
     * Returns the {@code BooleanProperty} representing the court's availability.
     *
     * @return the {@code isAvailable} property
     */
    public BooleanProperty availableProperty() {
        return isAvailable;
    }

    @Override
    public String toString() {
        return getCourtName();
    }
}
