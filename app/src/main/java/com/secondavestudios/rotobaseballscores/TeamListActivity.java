package com.secondavestudios.rotobaseballscores;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.secondavestudios.rotobaseballscores.adapter.TeamListAdapter;
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

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TeamListActivity extends OrmLiteBaseActivity<DatabaseHelper> {

    private PlayerRepository playerRepository;
    private TeamRepository teamRepository;

    private List<Team> teams;
    @InjectView(R.id.teamListSwipeContainer) SwipeRefreshLayout swipeRefreshLayout;

    private ProgressDialog barProgressDialog;
    private Handler updateBarHandler = new Handler();

    private View headerView;

    private boolean editMode;

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_team_list);

        ButterKnife.inject(this);

        ActionBar ab = getActionBar();
        ab.setDisplayShowHomeEnabled(true);
        ab.setIcon(R.drawable.freds);

        playerRepository = PlayerRepository.getInstance(getHelper());
        teamRepository = TeamRepository.getInstance(getHelper());

        if (playerRepository.getFirstPlayer() == null){
            launchUpdateAllPlayersProgressBarDialog();
        }

//        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.teamListSwipeContainer);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new updateCurrentPlayers().execute();
            }
        });

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_blue_light,
                android.R.color.holo_blue_dark);

        ImageView createTeamButton = (ImageView) findViewById(R.id.createTeam);
        createTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView teamName = (TextView)findViewById(R.id.teamName);
                createTeam(teamName.getText().toString());
            }
        });

//        getHelper().changeCruzStats();
    }

    @Override
    protected void onStart(){
        super.onStart();
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                new updateCurrentPlayers().execute();
                refreshPlayerStatsAndLayout();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_team_list, menu);

        final MenuItem editTeams = menu.findItem(R.id.action_edit_teams);
        final MenuItem playerRanking = menu.findItem(R.id.action_player_ranking);
        final MenuItem updateAllPlayers = menu.findItem(R.id.action_update_all_players);

        editTeams.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (editTeams.getTitle().equals("Edit Teams")) {
                    editTeams.setTitle("Done");
                    editTeams.setIcon(null);
                    playerRanking.setEnabled(false);
                    updateAllPlayers.setEnabled(false);
                    View addTeam = findViewById(R.id.add_team);
                    addTeam.setVisibility(View.VISIBLE);
                } else {
                    editTeams.setTitle("Edit Teams");

                    View addTeam = findViewById(R.id.add_team);
                    editTeams.setIcon(R.drawable.ic_action_edit);
                    playerRanking.setEnabled(true);
                    updateAllPlayers.setEnabled(true);
                    addTeam.setVisibility(View.GONE);
                    EditText teamNameView = (EditText) findViewById(R.id.teamName);
                    teamNameView.setText("");
                    ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(teamNameView.getWindowToken(),0);
                }
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        if (id == R.id.action_update_all_players) {
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to update all players?  It may take a couple minutes.")
                    .setTitle("Update All Players")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            launchUpdateAllPlayersProgressBarDialog();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        } else if (id == R.id.action_edit_teams) {
            editMode = !editMode;
            ListView teamListView = (ListView)findViewById(R.id.teamListView);
            TeamListAdapter teamListAdapter = getTeamListAdapter();
            headerView.findViewById(R.id.delete).setVisibility(editMode ? View.VISIBLE : View.GONE);
            teamListAdapter.notifyDataSetChanged();
            teamListView.refreshDrawableState();
        } else if (id == R.id.action_player_ranking){
            Intent intent = new Intent(this, PlayerRankingActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public List<Team> getTeams() {
        if (teams == null){
            return new ArrayList<Team>();
        }
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public void launchUpdateAllPlayersProgressBarDialog() {

        barProgressDialog = new ProgressDialog(TeamListActivity.this);
        barProgressDialog.setTitle("Retrieve Player Stats");
        barProgressDialog.setMessage("Retrieving Player Stats...");
        barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
        barProgressDialog.setProgress(0);
        barProgressDialog.setMax(4000);
        barProgressDialog.show();
        barProgressDialog.setCancelable(false);
        barProgressDialog.setProgressNumberFormat(null);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    for (int offset = 1; offset < 4000; offset = offset + 50) {
                        final int offsetValue = retrievePlayerStats(offset);
                        barProgressDialog.incrementProgressBy(50);

                        updateBarHandler.post(new Runnable() {
                            public void run() {
                                barProgressDialog.setProgress(offsetValue - 1);
                                if (offsetValue > 3950){
                                    setTeams(teamRepository.getAllTeams());
                                    TeamListAdapter teamListAdapter = new TeamListAdapter(TeamListActivity.this, getTeams());
                                    ListView teamListView = (ListView)findViewById(R.id.teamListView);
                                    teamListView.setAdapter(teamListAdapter);
                                    teamListAdapter.notifyDataSetChanged();
                                    teamListView.refreshDrawableState();                                }
                            }
                        });
                    }

                    barProgressDialog.setProgress(4000);
                    barProgressDialog.dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public int retrievePlayerStats(int offset){
        try {
            StatisticsUtil statisticsUtil = new StatisticsUtil();
            final JsonArray athletes = statisticsUtil.getAthletesJsonArray(statisticsUtil.getAllPlayersUrl(offset));
            for (final JsonElement athlete : athletes) {
                updateRetrievedPlayer(athlete);
            }
        } catch (Exception e) {
            retrievePlayerStats(offset);
            return offset;
        }

        return offset;
    }

    private void retrieveCurrentPlayers(){
        for (Team team : getTeams()){
            for (Player player : team.getPlayers()){
                try {
                    retrievePlayer(player.getRemoteId());
                } catch (Exception e) {
                    retrievePlayer(player.getRemoteId());
                }
            }
        }
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
        setTeams(teamRepository.getAllTeams());
        Collections.sort(teams);
        TeamListAdapter teamListAdapter = new TeamListAdapter(this, getTeams());
        ListView teamListView = (ListView)findViewById(R.id.teamListView);

        if (teamListView.getHeaderViewsCount() > 0) {
            teamListView.removeHeaderView(this.headerView);
        }

        this.headerView = LayoutUtil.getTeamListHeader(TeamListActivity.this, editMode);
        teamListView.addHeaderView(this.headerView);

        teamListView.setAdapter(teamListAdapter);
        teamListView.refreshDrawableState();
    }

    public void deleteTeam(Team team){
        for (Player player : team.getPlayers()){
            player.setTeam(null);
            playerRepository.createOrUpdatePlayer(player);
        }
        teamRepository.deleteTeam(team);

        ListView teamListView = (ListView) findViewById(R.id.teamListView);
        TeamListAdapter teamListAdapter = getTeamListAdapter();
        teamListAdapter.remove(team);
        teamListAdapter.notifyDataSetChanged();

        teamListView.refreshDrawableState();

        EditText teamNameView = (EditText) findViewById(R.id.teamName);
        teamNameView.setText("");
        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(teamNameView.getWindowToken(),0);
    }

    private class updateCurrentPlayers extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            retrieveCurrentPlayers();
            return null;
        }

        protected void onPostExecute(String results) {
            if (results == null) {
                refreshPlayerStatsAndLayout();
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private void createTeam(String teamName){
        if (teamName == null || teamName.isEmpty()){
            new AlertDialog.Builder(TeamListActivity.this).setMessage("Please select a team name")
                    .setPositiveButton("Ok", null)
                    .show();
            return;
        }

        for (Team team : teamRepository.getAllTeams()){
            if (team.getName().toUpperCase().equals(teamName.toUpperCase())) {
                new AlertDialog.Builder(TeamListActivity.this).setMessage("Team name already exists")
                        .setPositiveButton("Ok", null)
                        .show();
                return;
            }
        }

        Team team = new Team(teamName);
        teamRepository.createTeam(team);

        EditText teamNameView = (EditText) findViewById(R.id.teamName);
        teamNameView.setText("");
        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(teamNameView.getWindowToken(),0);

        ListView teamListView = (ListView) findViewById(R.id.teamListView);
        TeamListAdapter teamListAdapter = getTeamListAdapter();
        teamListAdapter.add(team);
        teamListAdapter.notifyDataSetChanged();

        teamListView.refreshDrawableState();
    }

    private TeamListAdapter getTeamListAdapter(){
        ListView teamListView = (ListView) findViewById(R.id.teamListView);
        HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) teamListView.getAdapter();
        return (TeamListAdapter)headerViewListAdapter.getWrappedAdapter();
    }
}
