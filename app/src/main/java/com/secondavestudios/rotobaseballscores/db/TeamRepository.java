package com.secondavestudios.rotobaseballscores.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.secondavestudios.rotobaseballscores.model.Team;

import java.util.List;

public class TeamRepository {

    private DatabaseHelper databaseHelper;
    private RuntimeExceptionDao<Team, Integer> teamDao;
    private static TeamRepository teamRepository;

    private TeamRepository(DatabaseHelper databaseHelper)
    {
        teamDao = databaseHelper.getTeamRuntimeExceptionDao();
    }

    public static TeamRepository getInstance(DatabaseHelper databaseHelper){
        if (teamRepository == null){
            teamRepository = new TeamRepository(databaseHelper);
        }

        return teamRepository;
    }

    public int deleteTeam(Team team){
        return teamDao.delete(team);
    }

    public Team findTeamById(int teamId){
        return (Team)teamDao.queryForId(teamId);
    }

    public Dao.CreateOrUpdateStatus createOrUpdateTeam(Team team){
        return teamDao.createOrUpdate(team);
    }

    public int createTeam(Team team){
        teamDao.assignEmptyForeignCollection(team, "players");
        return teamDao.create(team);
    }

    public List<Team> getAllTeams() {
        return teamDao.queryForAll();
    }

}
