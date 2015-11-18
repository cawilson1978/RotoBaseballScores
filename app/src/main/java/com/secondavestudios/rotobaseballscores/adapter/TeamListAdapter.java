package com.secondavestudios.rotobaseballscores.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.secondavestudios.rotobaseballscores.R;
import com.secondavestudios.rotobaseballscores.TeamActivity;
import com.secondavestudios.rotobaseballscores.TeamListActivity;
import com.secondavestudios.rotobaseballscores.model.Team;

import java.util.List;

public class TeamListAdapter extends BaseAdapter {
    private TeamListActivity teamListActivity;
    List<Team> teams;

    public TeamListAdapter(TeamListActivity teamListActivity, List<Team> teams){
        this.teamListActivity = teamListActivity;
        this.teams = teams;
    }

    public void add(Team team){
        teams.add(team);
    }

    public void remove(Team team){
        teams.remove(team);
    }

    @Override
    public int getCount() {
        return teams.size();
    }

    @Override
    public Object getItem(int position) {
        return teams.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final int backgroundColor;
        if ( (position & 1) == 0 ) {
            backgroundColor = Color.WHITE;
        }
        else {
            backgroundColor = Color.LTGRAY;
        }

        if(convertView==null)
        {
            LayoutInflater inflater = (LayoutInflater) teamListActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.team_list_item, parent,false);
        }

        convertView.setBackgroundColor(backgroundColor);

        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView hits = (TextView) convertView.findViewById(R.id.hits);
        TextView rbi = (TextView) convertView.findViewById(R.id.rbi);
        TextView hr = (TextView) convertView.findViewById(R.id.hr);
        TextView score = (TextView) convertView.findViewById(R.id.score);
        final Team team = teamListActivity.getTeams().get(position);
        if (team.getPlayers() != null) {
            team.calculateStats();
        }

        ImageView delete = (ImageView) convertView.findViewById(R.id.delete);

        if (teamListActivity.isEditMode()){
            delete.setVisibility(View.VISIBLE);
        } else {
            delete.setVisibility(View.GONE);
        }

        if (backgroundColor != Color.WHITE){
            delete.setImageResource(R.drawable.delete_ltgray);
        }
        delete.setBackgroundColor(backgroundColor);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (team.getPlayers() != null && team.getPlayers().size() > 0) {
                    new AlertDialog.Builder(teamListActivity)
                            .setMessage("Are you sure you want to remove " + team.getName())
                            .setTitle("Remove Team")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    teamListActivity.deleteTeam(team);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                } else {
                    teamListActivity.deleteTeam(team);
                }
            }
        });

        name.setText(team.getName());
        hits.setText(team.getHits() + "");
        rbi.setText(team.getRbi() + "");
        hr.setText(team.getHr() + "");
        score.setText(team.getScore() + "");

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(teamListActivity, TeamActivity.class);
                intent.putExtra("teamId", team.getId());
                teamListActivity.startActivity(intent);
            }
        });

        convertView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setBackgroundColor(Color.BLUE);
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    v.setBackgroundColor(backgroundColor);
                }
                return false;
            }
        });

        return convertView;
    }
}
