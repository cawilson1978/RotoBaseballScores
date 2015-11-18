package com.secondavestudios.rotobaseballscores.db;

    import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.secondavestudios.rotobaseballscores.R;
import com.secondavestudios.rotobaseballscores.model.Player;
import com.secondavestudios.rotobaseballscores.model.Team;

import java.sql.SQLException;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "rotobaseballscores.db";
    private static final int DATABASE_VERSION = 1;

    private RuntimeExceptionDao<Player, Integer> playerDao = getPlayerRuntimeExceptionDao();
    private RuntimeExceptionDao<Team, Integer> teamDao = getTeamRuntimeExceptionDao();

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");

            TableUtils.createTable(connectionSource, Team.class);
            TableUtils.createTable(connectionSource, Player.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, Team.class, true);
            TableUtils.dropTable(connectionSource, Player.class, true);
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    public RuntimeExceptionDao<Player, Integer> getPlayerRuntimeExceptionDao() {
        if (playerDao == null) {
            playerDao = getRuntimeExceptionDao(Player.class);
        }
        return playerDao;
    }

    public RuntimeExceptionDao<Team, Integer> getTeamRuntimeExceptionDao() {
        if (teamDao == null) {
            teamDao = getRuntimeExceptionDao(Team.class);
        }
        return teamDao;
    }

    @Override
    public void close() {
        super.close();
        teamDao = null;
        playerDao = null;
    }

    public void changeCruzStats(){
        Player cruz = (Player) playerDao.queryForEq("lastName", "Cruz").get(0);
        cruz.setHr(0);
        playerDao.createOrUpdate(cruz);
    }
}