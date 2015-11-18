package com.secondavestudios.rotobaseballscores.util;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.secondavestudios.rotobaseballscores.R;
import com.secondavestudios.rotobaseballscores.model.Team;

public class LayoutUtil {

    public static View getPlayerListFooter(Context context, Team team, boolean editMode){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View footer = inflater.inflate(R.layout.team_item_footer_header, null);

        footer.setBackgroundColor(Color.parseColor("#6fb6ff"));

        team.calculateStats();

        TextView name = (TextView) footer.findViewById(R.id.name);
        name.setText("Total");
        TextView hits = (TextView) footer.findViewById(R.id.hits);
        hits.setText(team.getHits() + "");
        TextView rbi = (TextView) footer.findViewById(R.id.rbi);
        rbi.setText(team.getRbi() + "");
        TextView hr = (TextView) footer.findViewById(R.id.hr);
        hr.setText(team.getHr() + "");
        TextView avg = (TextView) footer.findViewById(R.id.avg);
        avg.setText("");
        TextView score = (TextView) footer.findViewById(R.id.score);
        score.setText(team.getScore() + "");

        View delete = footer.findViewById(R.id.delete);
        if (editMode){
            delete.setVisibility(View.INVISIBLE);
        } else {
            delete.setVisibility(View.GONE);
        }

        return footer;
    }

    public static void getPlayerListFooter(Context context, Team team, boolean editMode, View footer){

        footer.setBackgroundColor(Color.parseColor("#6fb6ff"));

        team.calculateStats();

        TextView name = (TextView) footer.findViewById(R.id.name);
        name.setText("Total");
        TextView hits = (TextView) footer.findViewById(R.id.hits);
        hits.setText(team.getHits() + "");
        TextView rbi = (TextView) footer.findViewById(R.id.rbi);
        rbi.setText(team.getRbi() + "");
        TextView hr = (TextView) footer.findViewById(R.id.hr);
        hr.setText(team.getHr() + "");
        TextView avg = (TextView) footer.findViewById(R.id.avg);
        avg.setText("");
        TextView score = (TextView) footer.findViewById(R.id.score);
        score.setText(team.getScore() + "");

        View delete = footer.findViewById(R.id.delete);
        if (editMode){
            delete.setVisibility(View.INVISIBLE);
        } else {
            delete.setVisibility(View.GONE);
        }
    }

    public static View getPlayerListHeader(Context context, boolean editMode){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View header = inflater.inflate(R.layout.team_item_footer_header, null);

        header.setBackgroundColor(Color.parseColor("#6fb6ff"));

        TextView name = (TextView) header.findViewById(R.id.name);
        name.setText("Name");
        TextView hits = (TextView) header.findViewById(R.id.hits);
        hits.setText("Hits");
        TextView rbi = (TextView) header.findViewById(R.id.rbi);
        rbi.setText("RBI");
        TextView hr = (TextView) header.findViewById(R.id.hr);
        hr.setText("HR");
        TextView avg = (TextView) header.findViewById(R.id.avg);
        avg.setText("Avg");
        TextView score = (TextView) header.findViewById(R.id.score);
        score.setText("Score");

        View delete = header.findViewById(R.id.delete);
        if (editMode){
            delete.setVisibility(View.INVISIBLE);
        } else {
            delete.setVisibility(View.GONE);
        }

        return header;
    }

    public static View getTeamListHeader(Context context, boolean editMode){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View header = inflater.inflate(R.layout.team_list_item_header, null);

        header.setBackgroundColor(Color.parseColor("#6fb6ff"));

        TextView name = (TextView) header.findViewById(R.id.name);
        name.setText("Team Name");
        TextView hits = (TextView) header.findViewById(R.id.hits);
        hits.setText("Hits");
        TextView rbi = (TextView) header.findViewById(R.id.rbi);
        rbi.setText("RBI");
        TextView hr = (TextView) header.findViewById(R.id.hr);
        hr.setText("HR");
        TextView score = (TextView) header.findViewById(R.id.score);
        score.setText("Score");

        View delete = header.findViewById(R.id.delete);
        if (editMode){
            delete.setVisibility(View.INVISIBLE);
        } else {
            delete.setVisibility(View.GONE);
        }

        return header;
    }

}
