package edu.augustana.csc305.project.service;

import edu.augustana.csc305.project.model.api.*;
import edu.augustana.csc305.project.model.domain.User;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

/**
 * Retrofit interface defining the Kronos API endpoints for tournament and league management.
 *
 * <p>This interface serves as the primary contract for all backend API communication,
 * including authentication, resource management (teams, courts, referees), and bracket/match operations.</p>
 *
 * @author Java doc by Gemini 2.5 Flash
 */
public interface KronosApi {

    @POST("auth/login")
    Call<LoginResponseDTO> login(@Body LoginRequestDTO request);

    @POST("api/users")
    Call<User> createUser(@Body UserNewDTO request);

    @GET("api/users")
    Call<List<User>> getAllUsers(@Query("role") String role);

    // Added endpoint for updating users (PATCH)
    @PATCH("api/users/{id}")
    Call<Void> updateUser(@Path("id") int id, @Body UserUpdateDTO request);

    // --- League Endpoints ---

    @GET("api/leagues")
    Call<List<LeagueDTO>> getAllLeagues();

    @POST("api/leagues")
    Call<Void> createLeague(@Body LeagueNewDTO request);

    @DELETE("api/leagues/{id}")
    Call<Void> deleteLeague(@Path("id") int leagueId);

    @GET("api/leagues/{leagueId}/standings")
    Call<Map<Integer, Integer>> getLeagueStandings(@Path("leagueId") int leagueId);

    // --- Tournament Endpoints ---

    @GET("api/tournaments")
    Call<List<TournamentDTO>> getTournamentsByLeague(@Query("leagueId") int leagueId);

    @POST("api/tournaments")
    Call<Void> createTournament(@Body TournamentNewDTO request);

    @GET("api/tournaments/{id}")
    Call<TournamentDTO> getTournamentById(@Path("id") int tournamentId);

    @DELETE("api/tournaments/{id}")
    Call<Void> deleteTournament(@Path("id") int tournamentId);

    @GET("api/tournaments/{tournamentId}/standings")
    Call<Map<Integer, Integer>> getTournamentStandings(@Path("tournamentId") int tournamentId);

    @PUT("api/tournaments/{tournamentId}/standings")
    Call<Void> replaceTournamentStandings(@Path("tournamentId") int tournamentId, @Body TournamentStandingsPutDTO request);

    // --- Team Endpoints ---

    @GET("api/teams")
    Call<List<TeamDTO>> getTeams(@Query("leagueId") Integer leagueId);

    @GET("api/tournaments/{tournamentId}/teams")
    Call<List<TeamDTO>> getTeamsForTournament(@Path("tournamentId") int tournamentId);

    @POST("api/tournaments/{tournamentId}/teams")
    Call<Void> createTeamInTournament(@Path("tournamentId") int tournamentId, @Body TeamNewDTO team);

    @DELETE("api/tournaments/{tournamentId}/teams/{teamId}")
    Call<Void> deleteTeamFromTournament(@Path("tournamentId") int tournamentId, @Path("teamId") int teamId);

    // --- Court Endpoints ---

    @GET("api/tournaments/{tournamentId}/courts")
    Call<List<CourtDTO>> getCourtsForTournament(@Path("tournamentId") int tournamentId);

    @POST("api/tournaments/{tournamentId}/courts")
    Call<Void> createCourtInTournament(@Path("tournamentId") int tournamentId, @Body CourtNewDTO court);

    @DELETE("api/tournaments/{tournamentId}/courts/{courtId}")
    Call<Void> deleteCourtFromTournament(@Path("tournamentId") int tournamentId, @Path("courtId") int courtId);

    // --- Referee Endpoints ---

    @GET("api/tournaments/{tournamentId}/referees")
    Call<List<User>> getRefereesForTournament(@Path("tournamentId") int tournamentId);

    @POST("api/tournaments/{tournamentId}/referees")
    Call<Void> addRefereeToTournament(@Path("tournamentId") int tournamentId, @Body RefereeNewDTO referee);

    @DELETE("api/tournaments/{tournamentId}/referees/{userId}")
    Call<Void> removeRefereeFromTournament(@Path("tournamentId") int tournamentId, @Path("userId") int userId);

    // --- Bracket & Match Endpoints ---

    @GET("api/tournaments/{tournamentId}/brackets")
    Call<List<BracketDTO>> getBracketsForTournament(@Path("tournamentId") int tournamentId);

    @POST("api/brackets")
    Call<BracketDTO> createBracket(@Body BracketNewDTO request);

    @GET("api/matches/{matchId}")
    Call<MatchDTO> getMatchById(@Path("matchId") int matchId);

    @PATCH("api/matches/{matchId}")
    Call<Void> updateMatch(@Path("matchId") int matchId, @Body MatchUpdateDTO update);
}