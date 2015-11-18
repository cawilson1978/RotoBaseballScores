package com.secondavestudios.rotobaseballscores;

import android.app.ActionBar;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.secondavestudios.rotobaseballscores.adapter.PlayerRankingAdapter;
import com.secondavestudios.rotobaseballscores.db.DatabaseHelper;
import com.secondavestudios.rotobaseballscores.db.PlayerRepository;
import com.secondavestudios.rotobaseballscores.db.TeamRepository;
import com.secondavestudios.rotobaseballscores.model.Player;
import com.secondavestudios.rotobaseballscores.model.Team;
import com.secondavestudios.rotobaseballscores.util.LayoutUtil;
import com.secondavestudios.rotobaseballscores.util.StatisticsUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PlayerRankingActivity extends OrmLiteBaseActivity<DatabaseHelper> {

    private PlayerRepository playerRepository;
    private TeamRepository teamRepository;

    private SwipeRefreshLayout swipeRefreshLayout;

    private List<Player> players;

    private View headerView;

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        playerRepository = PlayerRepository.getInstance(getHelper());
        teamRepository = TeamRepository.getInstance(getHelper());

        ActionBar ab = getActionBar();
        ab.setDisplayShowHomeEnabled(true);
        ab.setIcon(R.drawable.freds);
        ab.setTitle("Player Ranking");

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.teamSwipeContainer);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new updateCurrentPlayers().execute();
            }
        });

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        players = getAllUnusedPlayersList();

//        players = getPlayerList();

//        getHelper().changeCruzStats();
    }

    @Override
    protected void onResume(){
        super.onResume();
        retrieveCurrentPlayers();
        Collections.sort(players);
        Collections.sort(players);
        refreshPlayerStatsAndLayout();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_team, menu);
//
//        final MenuItem editTeams = menu.findItem(R.id.action_edit_players);
//
//        editTeams.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                if (editTeams.getTitle().equals("Edit")) {
//                    editTeams.setTitle("Done");
//                } else {
//                    editTeams.setTitle("Edit");
//                }
//                return false;
//            }
//        });
//
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    private void retrieveCurrentPlayers(){
        if (players != null) {
            for (Player player : players) {
                retrievePlayer(player.getRemoteId());
            }
        }
    }

    private List<Player> getPlayerList(){
        players = new ArrayList<Player>();

        for (Team team : teamRepository.getAllTeams()) {
            for (Player player : team.getPlayers()) {
                players.add(player);
            }
        }

        return players;
    }

    private List<Player> getAllUnusedPlayersList(){
        players = new ArrayList<Player>();
        List<Player> tempPlayers = playerRepository.getAllPlayers();

        for (Player player : tempPlayers) {
            if (player.getTeam() == null) {
                players.add(player);
            }
        }

        return players;
    }

    public void retrievePlayer(String remoteId){
        try {
            StatisticsUtil statisticsUtil = new StatisticsUtil();
            final JsonArray athletes = statisticsUtil.getAthletesJsonArray(statisticsUtil.getPlayerUrl(remoteId));
            for (final JsonElement athlete : athletes) {
                updateRetrievedPlayer(athlete);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateRetrievedPlayer(JsonElement athlete){
        StatisticsUtil statisticsUtil = new StatisticsUtil();
        if (athlete.getAsJsonObject().get("stats") != null) {

            Player player = playerRepository.findPlayerByRemoteId(athlete.getAsJsonObject().get("id").toString());

            if (player == null){
                player = new Player();
            }

            player = statisticsUtil.parseJsonIntoPlayer(athlete, player);

            playerRepository.createOrUpdatePlayer(player);
        }
    }
    
    public void refreshPlayerStatsAndLayout(){
        PlayerRankingAdapter playerRankingAdapter = new PlayerRankingAdapter(this, players);
        ListView playerRankingView = (ListView)findViewById(R.id.teamPlayersListView);
        if (playerRankingView.getHeaderViewsCount() > 0) {
            playerRankingView.removeHeaderView(this.headerView);
        }

        this.headerView = LayoutUtil.getPlayerListHeader(PlayerRankingActivity.this, false);
        playerRankingView.addHeaderView(this.headerView);

        playerRankingView.setAdapter(playerRankingAdapter);
        playerRankingView.refreshDrawableState();
    }


    private class updateCurrentPlayers extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            retrieveCurrentPlayers();
            return null;
        }

        protected void onPostExecute(String results) {
            if (results == null) {
                Collections.sort(players);
                refreshPlayerStatsAndLayout();
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }
}
