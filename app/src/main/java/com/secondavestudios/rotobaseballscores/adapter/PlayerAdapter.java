package com.secondavestudios.rotobaseballscores.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.secondavestudios.rotobaseballscores.R;
import com.secondavestudios.rotobaseballscores.TeamActivity;
import com.secondavestudios.rotobaseballscores.model.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerAdapter extends BaseAdapter implements Filterable {

    List<Player> players;
    private Context context;
    List<Player> resultList;
    private TeamActivity teamActivity;

    public PlayerAdapter(TeamActivity teamActivity, List<Player> players){
        this.teamActivity = teamActivity;
        this.players = players;
    }

    public PlayerAdapter(List<Player> players){
        this.players = players;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public Object getItem(int position) {
        return resultList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView==null)
        {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.player_item, parent,false);
        }

        ((TextView)convertView.findViewById(R.id.displayName)).setText(resultList.get(position).getPlayerDisplay());
        final String playerToAdd = resultList.get(position).getPlayerDisplay();
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teamActivity.addPlayer(playerToAdd);
            }
        });

        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    List<Player> players = findPlayers(context, constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = players;
                    filterResults.count = players.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    resultList = (List<Player>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }};
        return filter;
    }

    private List<Player> findPlayers(Context context, String playerName) {
        List<Player> results = new ArrayList<Player>();

        for (Player player : players){
            if (player.getName().toLowerCase().contains(playerName.toLowerCase())){
                results.add(player);
            }
        }

        return results;
    }
}
