package com.secondavestudios.rotobaseballscores.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.secondavestudios.rotobaseballscores.model.Player;
import com.secondavestudios.rotobaseballscores.model.Team;

import java.sql.SQLException;
import java.util.List;

public class PlayerRepository {

    private RuntimeExceptionDao<Player, Integer> playerDao;
    private static PlayerRepository playerRepository;

    private PlayerRepository(DatabaseHelper databaseHelper)
    {
        playerDao = databaseHelper.getPlayerRuntimeExceptionDao();
    }

    public static PlayerRepository getInstance(DatabaseHelper databaseHelper){
        if (playerRepository == null){
            playerRepository = new PlayerRepository(databaseHelper);
        }

        return playerRepository;
    }

    public List<Player> getAllPlayers() {
        return playerDao.queryForAll();
    }

    public Dao.CreateOrUpdateStatus createOrUpdatePlayer(Player player){
        return playerDao.createOrUpdate(player);
    }

    public Player findPlayerByRemoteId(String remoteId) {
        List<Player> player = playerDao.queryForEq("remoteId", remoteId);
        if (player != null && player.size() == 1){
            return player.get(0);
        }
        return null;
    }

    public List<Player> findPlayersByTeam(Team team){
        List<Player> players = (List<Player>) playerDao.queryForEq("team_id", team.getId());
        if (players != null){
            return players;
        }
        return null;
    }

    public Player getFirstPlayer(){
        Player player;
        try {
            QueryBuilder queryBuilder = playerDao.queryBuilder();
            PreparedQuery<Player> playerPreparedQuery = queryBuilder.prepare();
            player = playerDao.queryForFirst(playerPreparedQuery);
        } catch (SQLException se) {
            se.printStackTrace();
            return null;
        }
        return player;
    }

}
