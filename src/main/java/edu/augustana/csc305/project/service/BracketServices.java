package edu.augustana.csc305.project.service;

import edu.augustana.csc305.project.model.domain.Bracket;
import edu.augustana.csc305.project.model.domain.Match;
import edu.augustana.csc305.project.model.domain.Round;
import edu.augustana.csc305.project.model.domain.Team;

import java.util.*;

/**
 * A utility class for analyzing Bracket data.
 * <p>
 * This class provides static methods to perform calculations and derive new data
 * from existing Bracket objects, such as generating a ranked list of teams.
 * </p>
 * This class was recommended to be made by Gemini 2.5 Pro.
 */
public class BracketServices {

    /**
     * Analyzes a completed bracket and generates a map of Team IDs to their standing position.
     * * @param bracket The completed bracket to analyze.
     * @return A Map where Key = Team ID, Value = Rank (1st, 2nd, 3rd...).
     */
    public static Map<Integer, Integer> generatePointsStandings(Bracket bracket) {
        Map<Integer, Integer> winCounts = new HashMap<>();
        Map<Integer, Integer> maxRoundReached = new HashMap<>();
        List<Integer> teamIds = new ArrayList<>();

        List<Round> rounds = bracket.getRounds();
        int finalRoundIndex = rounds.size();

        for (int roundIndex = 0; roundIndex < rounds.size(); roundIndex++) {
            Round round = rounds.get(roundIndex);
            for (Match match : round.getMatches()) {
                Team team1 = match.getTeam1();
                Team team2 = match.getTeam2();

                if (team1 != null) {
                    int t1Id = team1.getTeamId();
                    winCounts.putIfAbsent(t1Id, 0);
                    maxRoundReached.put(t1Id, Math.max(maxRoundReached.getOrDefault(t1Id, 0), roundIndex + 1));
                    if (!teamIds.contains(t1Id)) teamIds.add(t1Id);
                }
                if (team2 != null) {
                    int t2Id = team2.getTeamId();
                    winCounts.putIfAbsent(t2Id, 0);
                    maxRoundReached.put(t2Id, Math.max(maxRoundReached.getOrDefault(t2Id, 0), roundIndex + 1));
                    if (!teamIds.contains(t2Id)) teamIds.add(t2Id);
                }

                if (match.isComplete() && match.getWinner() != null) {
                    winCounts.merge(match.getWinner().getTeamId(), 1, Integer::sum);
                }
            }
        }

        teamIds.sort((id1, id2) -> {
            Integer round1 = maxRoundReached.getOrDefault(id1, 0);
            Integer round2 = maxRoundReached.getOrDefault(id2, 0);
            Integer wins1 = winCounts.getOrDefault(id1, 0);
            Integer wins2 = winCounts.getOrDefault(id2, 0);

            boolean team1InFinal = round1.equals(finalRoundIndex);
            boolean team2InFinal = round2.equals(finalRoundIndex);

            if (team1InFinal && team2InFinal) {
                return wins2.compareTo(wins1);
            }

            if (team1InFinal) return -1;
            if (team2InFinal) return 1;

            int roundComparison = round2.compareTo(round1);
            if (roundComparison != 0) {
                return roundComparison;
            }

            return wins2.compareTo(wins1);
        });

        Map<Integer, Integer> teamRankings = new HashMap<>();
        int currentRank = 0;
        int previousRound = -1;
        boolean assignedFirst = false;

        for (int i = 0; i < teamIds.size(); i++) {
            Integer currentId = teamIds.get(i);
            Integer currentRound = maxRoundReached.getOrDefault(currentId, 0);

            if (i == 0) {
                currentRank = 1;
                assignedFirst = true;
            }
            else if (i == 1 && assignedFirst && currentRound.equals(finalRoundIndex)) {
                currentRank = 2;
                previousRound = currentRound;
            }
            else {
                if (!currentRound.equals(previousRound)) {
                    currentRank = i + 1;
                }
                previousRound = currentRound;
            }
            teamRankings.put(currentId, currentRank);
        }

        return teamRankings;
    }
}