package edu.augustana.csc305.project.model.api;

/**
 * Data Transfer Object for linking an existing {@link edu.augustana.csc305.project.model.domain.User}
 * (who must be a Referee) to a Tournament.
 *
 * @author Java doc by Gemini 2.5 Flash
 */
public class RefereeNewDTO {
    private final int userId;

    /**
     * Constructs a DTO to link a referee to a tournament.
     *
     * @param userId The ID of the user to be added as a referee.
     */
    public RefereeNewDTO(int userId) {
        this.userId = userId;
    }
}