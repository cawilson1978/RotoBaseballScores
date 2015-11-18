package com.secondavestudios.rotobaseballscores.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.List;

@DatabaseTable
public class Team implements Comparable<Team>{

    public Team(){

    }

    public Team(String teamName){
        super();
        this.setName(teamName);
    }

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(unique = true)
    private String name;

    @ForeignCollectionField
    private ForeignCollection<Player> players;

    private int hits;
    private int rbi;
    private int hr;
    private int score;
    private int avgPoints;

    public int getAvgPoints() {
        return avgPoints;
    }

    public void setAvgPoints(int avgPoints) {
        this.avgPoints = avgPoints;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public int getRbi() {
        return rbi;
    }

    public void setRbi(int rbi) {
        this.rbi = rbi;
    }

    public int getHr() {
        return hr;
    }

    public void setHr(int hr) {
        this.hr = hr;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Player> getPlayers() {
        ArrayList<Player> playerList = new ArrayList<Player>();
        for (Player player : players) {
            playerList.add(player);
        }
        return playerList;
    }

    public void setPlayers(ForeignCollection<Player> players) {
        this.players = players;
    }

    public void calculateStats(){
        this.setHits(0);
        this.setRbi(0);
        this.setHr(0);
        this.setAvgPoints(0);
        this.setScore(0);
        calculateAveragePoints();
        for (Player player : players){
            this.setHits(this.getHits() + player.getHits());
            this.setHr(this.getHr() + player.getHr());
            this.setRbi(this.getRbi() + player.getRbi());

            this.setScore(this.getHits() + 2 * this.getRbi() + 3 * this.getHr() + this.getAvgPoints());
        }
    }

    public void calculateAveragePoints(){
        for (Player player : players){
            this.setAvgPoints(this.getAvgPoints() + player.getAveragePoints());
        }
    }

    @Override
    public int compareTo(Team team) {
        this.calculateStats();
        team.calculateStats();
        if (this.getScore() == team.getScore()) {
            return 0;
        } else if (this.getScore() < team.getScore()){
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public String toString() {
        return "Team - " + name + ", Hits - " + hits + ", RBI - " + rbi + ", HR - " + hr + ", Score - " + score;
    }
}
