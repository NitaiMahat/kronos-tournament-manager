package edu.augustana.csc305.project.model.domain;

/**
 * Defines the different access levels for users within the application, matching Kronos API roles.
 * This class creation was recommended by Gemini 2.5 Pro to make function of user class levels easier by using an enum.
 */
public enum UserRole {
    /**
     * Has full control over the entire application, including managing
     * tournaments, users, and system settings.
     */
    ADMIN,

    /**
     * Can manage all aspects of a specific tournament, such as adding teams,
     * generating brackets, and recording match scores.
     */
    TOURNAMENT_ORGANIZER,

    /**
     * Can be assigned to matches to verify and report scores.
     */
    REFEREE,

    USER,

    /**
     * Represents a publicly accessible role with limited permissions (API use).
     */
    ANYONE,
}