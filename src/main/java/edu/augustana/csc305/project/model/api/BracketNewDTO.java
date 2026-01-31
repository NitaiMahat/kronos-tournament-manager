package edu.augustana.csc305.project.model.api;

import edu.augustana.csc305.project.model.domain.BracketType;

/**
 * Data Transfer Object used to request the creation of a new bracket.
 * Contains the necessary parameters for the server to generate rounds and matches.
 *
 * @author Java doc by Gemini 2.5 Flash
 */
public class BracketNewDTO {
    private final String name;
    private final BracketType type;
    private final int tournamentId;
    private final Integer sourceBracketId;

    /**
     * Constructs a DTO for creating a new bracket.
     *
     * @param name The name of the new bracket.
     * @param type The {@link BracketType} (e.g., SINGLE_ELIMINATION).
     * @param tournamentId The ID of the tournament the bracket belongs to.
     * @param sourceBracketId Optional ID of an existing bracket to use for seeding.
     */
    public BracketNewDTO(String name, BracketType type, int tournamentId, Integer sourceBracketId) {
        this.name = name;
        this.type = type;
        this.tournamentId = tournamentId;
        this.sourceBracketId = sourceBracketId;
    }

    public String getName() {
        return name;
    }

    public BracketType getType() {
        return type;
    }

    public int getTournamentId() {
        return tournamentId;
    }

    public Integer getSourceBracketId() {
        return sourceBracketId;
    }
}