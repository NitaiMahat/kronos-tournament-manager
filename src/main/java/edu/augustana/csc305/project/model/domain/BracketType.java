package edu.augustana.csc305.project.model.domain;

/**
 * Defines the possible formats for a tournament bracket.
 * JavaDoc by Gemini 2.5 Pro
 */
public enum BracketType  {
    /** A single-elimination tournament format. */
    SINGLE_ELIMINATION,
    /** A round-robin tournament format where every team plays every other team. */
    ROUND_ROBIN,

    SINGLE_ELIMINATION_SEEDED,

    OTHER
}