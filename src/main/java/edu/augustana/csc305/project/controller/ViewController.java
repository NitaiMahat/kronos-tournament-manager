package edu.augustana.csc305.project.controller;

import edu.augustana.csc305.project.model.domain.Tournament;
import edu.augustana.csc305.project.userInterface.View;

/**
 * An abstract base class for all view controllers in the application.
 *
 * <p>A {@code ViewController} acts as the intermediary between a specific {@link View}
 * (the user interface) and the application's data model (the {@link Tournament}).
 * It handles UI events, updates the model, and triggers view refreshes.
 * Concrete subclasses implement specific behavior for each type of view.</p>
 *
 * @author Java doc by Gemini 2.5 Flash
 */
public abstract class ViewController {

    /**
     * The specific {@link View} instance managed by this controller.
     * <p>The declaration of this field as {@code protected} received assistance from an AI model (Gemini 2.5 Pro).</p>
     */
    protected final View view;

    /**
     * The core {@link Tournament} model instance currently being managed.
     */
    protected final Tournament tournament;

    /**
     * The global {@link AppController} instance used for navigation and state delegation.
     */
    protected final AppController appController;

    /**
     * Constructs a new ViewController.
     *
     * <p>Initializes references to the view, model, and app controller,
     * then calls {@link #attachEvents()} (to be implemented by subclasses)
     * to set up event handling for the view.</p>
     *
     * @param view The specific {@link View} instance managed by this controller.
     * @param tournament The core {@link Tournament} model.
     * @param appController The global {@link AppController} instance.
     */
    public ViewController(View view, Tournament tournament, AppController appController) {
        this.view = view;
        this.tournament = tournament;
        this.appController = appController;
    }

    /**
     * Sets up event handlers on the controlled {@link View}'s components.
     *
     * <p>Concrete subclasses must implement this method to define how the view
     * interacts with the model and other controllers.</p>
     *
     * <p>The declaration of this method as {@code protected} abstract received assistance from an AI model
     * (Gemini 2.5 Pro).</p>
     */
    protected abstract void attachEvents();

    /**
     * Returns the {@link View} instance managed by this controller.
     *
     * @return the {@link View} associated with this controller.
     */
    public View getView() {
        return view;
    }
}