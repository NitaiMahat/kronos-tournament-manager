package edu.augustana.csc305.project.model.api;

/**
 * Data Transfer Object for creating a new League via the API.
 *
 * @author Java doc by Gemini 2.5 Flash
 */
public class LeagueNewDTO {
    private final String name;

    /**
     * Constructs a DTO for creating a new league.
     *
     * @param name The name of the new league.
     */
    public LeagueNewDTO(String name) {
        this.name = name;
    }
}