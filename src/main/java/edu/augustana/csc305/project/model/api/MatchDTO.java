package edu.augustana.csc305.project.model.api;

import edu.augustana.csc305.project.model.domain.User;

/**
 * Data Transfer Object for reading Match data from the API.
 * Includes nested DTOs for teams, resources (court, referee), and source matches
 * to represent the bracket structure recursively.
 *
 * @author Java doc by Gemini 2.5 Flash
 */
public class MatchDTO {
    private int matchId;
    private TeamDTO team1;
    private TeamDTO team2;
    private TeamDTO winner;
    private boolean complete;

    private CourtDTO court;
    private User referee;

    private MatchDTO sourceMatch1;
    private MatchDTO sourceMatch2;

    public int getMatchId() { return matchId; }
    public TeamDTO getTeam1() { return team1; }
    public TeamDTO getTeam2() { return team2; }
    public TeamDTO getWinner() { return winner; }
    public boolean isComplete() { return complete; }

    public CourtDTO getCourt() { return court; }
    public User getReferee() { return referee; }

    public MatchDTO getSourceMatch1() { return sourceMatch1; }
    public MatchDTO getSourceMatch2() { return sourceMatch2; }
}