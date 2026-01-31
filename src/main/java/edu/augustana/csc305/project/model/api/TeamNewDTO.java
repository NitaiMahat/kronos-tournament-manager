package edu.augustana.csc305.project.model.api;

/**
 * Data Transfer Object for creating a new Team via the API.
 *
 * @author Java doc by Gemini 2.5 Flash
 */
public class TeamNewDTO {
    private final String name;

    /**
     * Constructs a DTO for creating a new team.
     *
     * @param name The name of the new team.
     */
    public TeamNewDTO(String name) {
        this.name = name;
    }
}