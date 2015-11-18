package com.secondavestudios.rotobaseballscores.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.secondavestudios.rotobaseballscores.R;
import com.secondavestudios.rotobaseballscores.TeamActivity;
import com.secondavestudios.rotobaseballscores.model.Player;

import java.util.List;

public class PlayerListAdapter extends BaseAdapter {
    private TeamActivity teamActivity;
    List<Player> players;

    public PlayerListAdapter(TeamActivity teamActivity, List<Player> players){
        this.teamActivity = teamActivity;
        this.players = players;
    }

    public void add(Player player){
        players.add(player);
    }

    public void remove(Player player){
        players.remove(player);
    }

    @Override
    public int getCount() {
        return players.size();
    }

    @Override
    public Object getItem(int position) {
        return players.get(position);
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
            LayoutInflater inflater = (LayoutInflater) teamActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.team_item, parent,false);
        }

        convertView.setBackgroundColor(backgroundColor);

        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView hits = (TextView) convertView.findViewById(R.id.hits);
        TextView rbi = (TextView) convertView.findViewById(R.id.rbi);
        TextView hr = (TextView) convertView.findViewById(R.id.hr);
        TextView avg = (TextView) convertView.findViewById(R.id.avg);
        TextView score = (TextView) convertView.findViewById(R.id.score);

        final Player player = players.get(position);

        ImageView delete = (ImageView) convertView.findViewById(R.id.delete);

        if (teamActivity.isEditMode()){
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
                teamActivity.removePlayer(player);
            }
        });

        name.setText(player.getName());
        hits.setText(player.getHits() + "");
        rbi.setText(player.getRbi() + "");
        hr.setText(player.getHr() + "");
        avg.setText(player.getAvg() + "");
        score.setText(player.getScore() + "");

        return convertView;

    }
}
