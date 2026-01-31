package edu.augustana.csc305.project.model.api;

import edu.augustana.csc305.project.model.domain.BracketType;
import java.util.List;

/**
 * Data Transfer Object for reading Bracket structure and details from the API.
 * It includes the list of {@link RoundDTO}s that make up the bracket.
 *
 * @author Java doc by Gemini 2.5 Flash
 */
public class BracketDTO {
    private int bracketId;
    private String name;
    private BracketType type;
    private List<RoundDTO> rounds;

    public int getBracketId() { return bracketId; }
    public String getName() { return name; }
    public BracketType getType() { return type; }
    public List<RoundDTO> getRounds() { return rounds; }
}