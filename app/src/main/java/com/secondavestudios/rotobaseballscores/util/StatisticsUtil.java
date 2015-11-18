package com.secondavestudios.rotobaseballscores.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.secondavestudios.rotobaseballscores.model.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Calendar;

public class StatisticsUtil {

    private static final String GET_PLAYERS_URL_PRE = "http://api-app.espn.com/v1/sports/baseball/mlb/athletes/";
    private static final String GET_PLAYERS_URL_POST = "dates/" + Calendar.getInstance().get(Calendar.YEAR) + "?enable=stats";

    private static String readUrl(final String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            final URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            final StringBuffer buffer = new StringBuffer();
            int read;
            final char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1) {
                buffer.append(chars, 0, read);
            }
            return buffer.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    public JsonArray getAthletesJsonArray(String url)
            throws Exception {
        final String json = readUrl(url);
        final JsonArray sports = getSportsJsonArray(json);
        final JsonElement league = sports.get(0);
        return league.getAsJsonObject().get("leagues").getAsJsonArray().get(0).getAsJsonObject().get("athletes").getAsJsonArray();
    }

    private JsonArray getSportsJsonArray(final String json) {
        final JsonArray sports = new JsonParser().parse(json).getAsJsonObject().get("sports").getAsJsonArray();
        return sports;
    }

    public String getAllPlayersUrl(final int offset) {
        return GET_PLAYERS_URL_PRE + GET_PLAYERS_URL_POST + "&offset=" + offset;
    }

    public String getPlayerUrl(String remoteId){
        return GET_PLAYERS_URL_PRE + remoteId + "/" + GET_PLAYERS_URL_POST;
    }

    public Player parseJsonIntoPlayer(JsonElement athlete, Player player){
        player.setName(athlete.getAsJsonObject().get("fullName").toString().substring(1, athlete.getAsJsonObject().get("fullName").toString().length() - 1));
        player.setLastName(athlete.getAsJsonObject().get("lastName").toString().substring(1, athlete.getAsJsonObject().get("lastName").toString().length() - 1));
        player.setRemoteId(athlete.getAsJsonObject().get("id").toString());
        player.setLocation(athlete.getAsJsonObject().get("team").getAsJsonObject().get("location").toString().substring(1, athlete.getAsJsonObject().get("team").getAsJsonObject().get("location").toString().length() - 1));
        player.setRealTeam(athlete.getAsJsonObject().get("team").getAsJsonObject().get("name").toString().substring(1, athlete.getAsJsonObject().get("team").getAsJsonObject().get("name").toString().toString().length() - 1));

        JsonObject battingStats = athlete.getAsJsonObject().get("stats").getAsJsonObject().get("batting").getAsJsonObject();

        player.setHits(battingStats.get("hits").getAsInt());
        player.setHr(battingStats.get("homeRuns").getAsInt());
        player.setRbi(battingStats.get("RBIs").getAsInt());
        player.setAvg((int) (battingStats.get("average").getAsFloat() * 1000));

        return player;
    }
}
