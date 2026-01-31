package edu.augustana.csc305.project.model.api;

import java.util.Map;

/**
 * Data Transfer Object used to replace the standings for a tournament via the API (PUT request).
 * Wraps the standings map (Team ID -> Points) to match the expected JSON structure.
 *
 * @author Java doc by Gemini 2.5 Flash
 */
public class TournamentStandingsPutDTO {
    private Map<Integer, Integer> standingsMap;

    /**
     * Constructs a DTO for replacing tournament standings.
     *
     * @param standingsMap A map where the key is the Team ID and the value is the points.
     */
    public TournamentStandingsPutDTO(Map<Integer, Integer> standingsMap) {
        this.standingsMap = standingsMap;
    }

    public Map<Integer, Integer> getStandingsMap() {
        return standingsMap;
    }

    public void setStandingsMap(Map<Integer, Integer> standingsMap) {
        this.standingsMap = standingsMap;
    }
}