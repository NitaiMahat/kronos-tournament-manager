package edu.augustana.csc305.project.model.api;

/**
 * Data Transfer Object for updating match results and details via the API (e.g., PATCH request).
 * Uses {@code Integer} wrappers to allow null values for optional fields.
 *
 * @author Java doc by Gemini 2.5 Flash
 */
public class MatchUpdateDTO {
    private Integer team1Id;
    private Integer team2Id;
    private Integer winnerId;
    private Integer score1;
    private Integer score2;

    /**
     * Default constructor required for Retrofit/Gson when fields are set via setters.
     */
    public MatchUpdateDTO() {}

    /**
     * Constructs a DTO specifically for updating the winner/teams.
     *
     * @param team1Id The ID of the first team, or null.
     * @param team2Id The ID of the second team, or null.
     * @param winnerId The ID of the winning team, or null.
     */
    public MatchUpdateDTO(Integer team1Id, Integer team2Id, Integer winnerId) {
        this.team1Id = team1Id;
        this.team2Id = team2Id;
        this.winnerId = winnerId;
    }

    public Integer getTeam1Id() { return team1Id; }
    public void setTeam1Id(Integer team1Id) { this.team1Id = team1Id; }

    public Integer getTeam2Id() { return team2Id; }
    public void setTeam2Id(Integer team2Id) { this.team2Id = team2Id; }

    public Integer getWinnerId() { return winnerId; }
    public void setWinnerId(Integer winnerId) { this.winnerId = winnerId; }
}