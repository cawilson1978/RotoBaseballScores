package com.secondavestudios.rotobaseballscores;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.secondavestudios.rotobaseballscores.adapter.PlayerAdapter;
import com.secondavestudios.rotobaseballscores.adapter.PlayerListAdapter;
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


public class TeamActivity extends OrmLiteBaseActivity<DatabaseHelper> {

    private PlayerRepository playerRepository;
    private TeamRepository teamRepository;

    private SwipeRefreshLayout swipeRefreshLayout;

    private Team team;
    private List<Player> players;
    private List<Player> playersToAdd;
    private View footerView;
    private boolean editMode;

    private View headerView;

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        playerRepository = PlayerRepository.getInstance(getHelper());
        teamRepository = TeamRepository.getInstance(getHelper());

        this.setTeam(teamRepository.findTeamById(this.getIntent().getExtras().getInt("teamId")));

        ActionBar ab = getActionBar();
        ab.setDisplayShowHomeEnabled(true);
        ab.setIcon(R.drawable.freds);
        ab.setTitle("Team " + team.getName());

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

        final AutoCompleteTextView addPlayerAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.addPlayerAutoCompleteTextView);
        addPlayerAutoCompleteTextView.setHint("Add Player");
        addPlayerAutoCompleteTextView.setDropDownWidth(1000);
        populatePlayerListToAdd();
        PlayerAdapter adapter = new PlayerAdapter(this, playersToAdd);
        addPlayerAutoCompleteTextView.setAdapter(adapter);

//        getHelper().changeCruzStats();
    }

    @Override
    protected void onResume(){
        super.onResume();
        this.setTeam(teamRepository.findTeamById(this.getIntent().getExtras().getInt("teamId")));
        this.setPlayers(team.getPlayers());
        refreshPlayerStatsAndLayout();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_team, menu);

        final MenuItem editTeams = menu.findItem(R.id.action_edit_players);

        editTeams.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (editTeams.getTitle().equals("Edit Players")) {
                    editTeams.setTitle("Done");
                    editTeams.setIcon(null);
                } else {
                    editTeams.setTitle("Edit Players");
                    editTeams.setIcon(R.drawable.ic_action_edit);
                    EditText addPlayerAutoCompleteTextView = (EditText) findViewById(R.id.addPlayerAutoCompleteTextView);
                    addPlayerAutoCompleteTextView.setText("");
                    ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(addPlayerAutoCompleteTextView.getWindowToken(),0);
                }
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

         if (id == R.id.action_edit_players) {
            editMode = !editMode;

            View addPlayerView = findViewById(R.id.add_player);

            if (editMode){
                addPlayerView.setVisibility(View.VISIBLE);
            } else {
                addPlayerView.setVisibility(View.GONE);
            }
             headerView.findViewById(R.id.delete).setVisibility(editMode ? View.VISIBLE : View.GONE);
             if (footerView != null) {
                 footerView.findViewById(R.id.delete).setVisibility(editMode ? View.VISIBLE : View.GONE);
             }
             ListView playerListView = (ListView) findViewById(R.id.teamPlayersListView);
             PlayerListAdapter playerListAdapter = getTeamPlayersListAdapter();
             playerListAdapter.notifyDataSetChanged();

             playerListView.refreshDrawableState();
        }

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    private void retrieveCurrentPlayers(){
        for (Player player : getPlayers()){
            retrievePlayer(player.getRemoteId());
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
        setPlayers(playerRepository.findPlayersByTeam(team));
        Collections.sort(players);
        PlayerListAdapter playerListAdapter = new PlayerListAdapter(this, players);
        ListView playerListView = (ListView)findViewById(R.id.teamPlayersListView);
        if (playerListView.getHeaderViewsCount() > 0) {
            playerListView.removeHeaderView(this.headerView);
        }

        this.headerView = LayoutUtil.getPlayerListHeader(TeamActivity.this, editMode);
        playerListView.addHeaderView(this.headerView);

        if (players != null && !players.isEmpty()) {
            if (playerListView.getFooterViewsCount() == 0) {
                footerView = LayoutUtil.getPlayerListFooter(TeamActivity.this, team, editMode);
                playerListView.addFooterView(footerView);
            } else {
                playerListView.removeFooterView(footerView);
                footerView = LayoutUtil.getPlayerListFooter(TeamActivity.this, team, editMode);
                playerListView.addFooterView(footerView);
            }
        }

        playerListView.setAdapter(playerListAdapter);
        playerListView.refreshDrawableState();
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

    private void populatePlayerListToAdd(){
        List<Player> playerList = playerRepository.getAllPlayers();
        playersToAdd = new ArrayList<Player>();

        for (Player player : playerList) {
            if (player.getTeam() == null){
                playersToAdd.add(player);
            }
        }
        String[] playerDisplayNames = new String[playersToAdd.size()];
        for (int i = 0; i < playersToAdd.size(); i++){
            playerDisplayNames[i] = playersToAdd.get(i).getPlayerDisplay();
        }
    }

    public void addPlayer(String playerDisplayName){
        Player player = null;
        for (Player playerTemp : playersToAdd){
            if (playerDisplayName.equals(playerTemp.getPlayerDisplay())){
                player = playerTemp;
            }
        }

        if (player == null){
            new AlertDialog.Builder(TeamActivity.this).setMessage("Please select an existing player")
                    .setPositiveButton("Ok", null)
                    .show();
            return;
        }

        this.getTeam().getPlayers().add(player);
        player.setTeam(this.getTeam());

        teamRepository.createOrUpdateTeam(this.getTeam());
        playerRepository.createOrUpdatePlayer(player);

        EditText addPlayerAutoCompleteTextView = (EditText) findViewById(R.id.addPlayerAutoCompleteTextView);
        addPlayerAutoCompleteTextView.setText("");
        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(addPlayerAutoCompleteTextView.getWindowToken(),0);

        ListView playerListView = (ListView) findViewById(R.id.teamPlayersListView);
        PlayerListAdapter playerListAdapter = getTeamPlayersListAdapter();
        playerListAdapter.add(player);

        if (playerListView.getFooterViewsCount() == 0) {
            footerView = LayoutUtil.getPlayerListFooter(TeamActivity.this, team, editMode);
            playerListView.addFooterView(footerView);
        } else {
            LayoutUtil.getPlayerListFooter(TeamActivity.this, team, editMode, footerView);
        }

        Collections.sort(players);

        playerListAdapter.notifyDataSetChanged();
        playerListView.refreshDrawableState();

        playersToAdd.remove(player);
    }

    public void removePlayer(Player player){

        team.getPlayers().remove(player);
        teamRepository.createOrUpdateTeam(team);

        player.setTeam(null);
        playerRepository.createOrUpdatePlayer(player);

        playersToAdd.add(player);

        ListView playerListView = (ListView) findViewById(R.id.teamPlayersListView);
        PlayerListAdapter playerListAdapter = getTeamPlayersListAdapter();
        playerListAdapter.remove(player);

        if (playerListView.getFooterViewsCount() > 0 && team.getPlayers() != null && team.getPlayers().size() == 0) {
            playerListView.removeFooterView(footerView);
        } else {
            LayoutUtil.getPlayerListFooter(TeamActivity.this, team, editMode, footerView);
        }

        playerListAdapter.notifyDataSetChanged();
        playerListView.refreshDrawableState();

        EditText addPlayerAutoCompleteTextView = (EditText) findViewById(R.id.addPlayerAutoCompleteTextView);
        addPlayerAutoCompleteTextView.setText("");
        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(addPlayerAutoCompleteTextView.getWindowToken(),0);

    }

    private PlayerListAdapter getTeamPlayersListAdapter(){
        ListView playerListView = (ListView) findViewById(R.id.teamPlayersListView);
        HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) playerListView.getAdapter();
        return (PlayerListAdapter)headerViewListAdapter.getWrappedAdapter();
    }

}
