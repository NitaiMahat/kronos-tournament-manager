package edu.augustana.csc305.project.model.api;

import java.util.List;

/**
 * Data Transfer Object for reading Round data from the API.
 * Contains a list of {@link MatchDTO}s belonging to that round.
 *
 * @author Java doc by Gemini 2.5 Flash
 */
public class RoundDTO {
    private int roundId;
    private List<MatchDTO> matches;

    public int getRoundId() { return roundId; }
    public List<MatchDTO> getMatches() { return matches; }
}