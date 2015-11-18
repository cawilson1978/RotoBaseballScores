package com.secondavestudios.rotobaseballscores.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.secondavestudios.rotobaseballscores.PlayerRankingActivity;
import com.secondavestudios.rotobaseballscores.R;
import com.secondavestudios.rotobaseballscores.model.Player;

import java.util.List;

public class PlayerRankingAdapter extends BaseAdapter {
    private PlayerRankingActivity playerRankingActivity;
    List<Player> players;

    public PlayerRankingAdapter(PlayerRankingActivity playerRankingActivity, List<Player> players){
        this.playerRankingActivity = playerRankingActivity;
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
            LayoutInflater inflater = (LayoutInflater) playerRankingActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        name.setText(player.getName());
        hits.setText(player.getHits() + "");
        rbi.setText(player.getRbi() + "");
        hr.setText(player.getHr() + "");
        avg.setText(player.getAvg() + "");
        score.setText(player.getScore() + "");

        return convertView;

    }
}
