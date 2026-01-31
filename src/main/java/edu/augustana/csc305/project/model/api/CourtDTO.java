package edu.augustana.csc305.project.model.api;

/**
 * Data Transfer Object for reading Court data from the API.
 *
 * @author Java doc by Gemini 2.5 Flash
 */
public class CourtDTO {
    private int courtId;
    private String name;
    private int tournamentId;

    public int getCourtId() {
        return courtId;
    }

    public String getName() {
        return name;
    }

    public int getTournamentId() {
        return tournamentId;
    }
}