package com.secondavestudios.rotobaseballscores.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Player implements Comparable<Player> {

    public Player() {
    }

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String name;

    @DatabaseField(canBeNull = false)
    private String lastName;

    @DatabaseField(canBeNull = false)
    private int hits;

    @DatabaseField(canBeNull = false)
    private int rbi;

    @DatabaseField(canBeNull = false)
    private int hr;

    @DatabaseField(canBeNull = false)
    private int avg;

    @DatabaseField(canBeNull = true)
    private String location;

    @DatabaseField(canBeNull = true)
    private String realTeam;

    @DatabaseField(canBeNull = false)
    private String remoteId;

    @DatabaseField(canBeNull = true)
    private int score;

    @DatabaseField(foreign=true,foreignAutoRefresh=true)
    private Team team;

    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRealTeam() {
        return realTeam;
    }

    public void setRealTeam(String realTeam) {
        this.realTeam = realTeam;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return calculateScore();
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

    public int getAvg() {
        return avg;
    }

    public void setAvg(int avg) {
        this.avg = avg;
    }

    public int getAveragePoints(){
        return (this.getAvg() > 290) ? this.getAvg() - 290 : 0;
    }

    public int calculateScore(){
        return this.getHits() + this.getRbi() * 2 + this.getHr() * 3 + this.getAveragePoints();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPlayerDisplay(){
        return this.getName() + ", " + this.getLocation() + " " + this.getRealTeam();
    }

    @Override
    public int compareTo(Player player) {
        if (this.getScore() == player.getScore()) {
            return 0;
        } else if (this.getScore() < player.getScore()){
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public String toString(){
        return name + ", Hits - " + hits + ", + RBI - " + rbi + ", HR - " + hr + ", Avg - " + avg + ", Score - " + score;
    }
}
