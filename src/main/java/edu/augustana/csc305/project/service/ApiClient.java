package edu.augustana.csc305.project.service;

import edu.augustana.csc305.project.config.ApplicationConfig;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton class for setting up and providing the Retrofit API client.
 *
 * <p>This class handles the configuration of OkHttp, including adding a dynamic
 * JWT Bearer token interceptor for authenticated requests, ensuring the token
 * is automatically attached to requests after a successful login.</p>
 *
 * @author Java doc by Gemini 2.5 Flash
 */
public class ApiClient {
    private static final String BASE_URL = ApplicationConfig.getApiBaseUrl();

    private static ApiClient instance;
    private final KronosApi kronosApi;
    private String jwtToken = null;

    private ApiClient() {
        Interceptor authInterceptor = chain -> {
            Request originalRequest = chain.request();
            Request.Builder builder = originalRequest.newBuilder();

            if (jwtToken != null) {
                builder.header("Authorization", "Bearer " + jwtToken);
            }

            return chain.proceed(builder.build());
        };

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.kronosApi = retrofit.create(KronosApi.class);
    }

    /**
     * Provides the singleton instance of the ApiClient.
     *
     * @return The ApiClient instance.
     */
    public static synchronized ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    /**
     * Returns the Retrofit service interface for making API calls.
     *
     * @return The KronosApi interface implementation.
     */
    public KronosApi getKronosApi() {
        return kronosApi;
    }

    /**
     * Stores the JWT token received after a successful login.
     * This token will be automatically added to subsequent requests.
     *
     * @param token The JWT string to store.
     */
    public void setToken(String token) {
        this.jwtToken = token;
    }

    /**
     * Clears the stored JWT token (used for logout).
     */
    public void clearToken() {
        this.jwtToken = null;
    }

    /**
     * A simple check to see if a token is present, indicating authentication status.
     *
     * @return True if a token is stored, false otherwise.
     */
    public boolean isAuthenticated() {
        return this.jwtToken != null;
    }
}