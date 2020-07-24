package com.aueb.podshare.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.aueb.podshare.R;
import com.aueb.podshare.classes.Episode;

import java.util.ArrayList;

public class EpisodeAdapter extends BaseAdapter implements Filterable {
    private ArrayList<String> mStringList;
    private ArrayList<String> mStringFilterList;
    private LayoutInflater mInflater;
    private ValueFilter valueFilter;
    private ArrayList<Episode> episodes;

    public EpisodeAdapter(ArrayList<String> mStringList, ArrayList<Episode> episodes, Context context) {
        this.mStringList = mStringList;
        this.mStringFilterList = mStringList;
        this.episodes = episodes;
        mInflater = LayoutInflater.from(context);
        getFilter();
    }

    //How many items are in the data set represented by this Adapter.
    @Override
    public int getCount() {
        return mStringList.size();
    }

    //Get the data item associated with the specified position in the data set.
    @Override
    public Object getItem(int position) {
        return mStringList.get(position);
    }

    //Get the row id associated with the specified position in the list.
    @Override
    public long getItemId(int position) {
        return position;
    }

    //Get a View that displays the data at the specified position in the data set.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.episode_item_fragment, null);
            viewHolder = new Holder();
            viewHolder.title = convertView.findViewById(R.id.episode_name_txt);
            viewHolder.publishDate = convertView.findViewById(R.id.episode_publish_date);
            viewHolder.description = convertView.findViewById(R.id.episode_description);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (Holder) convertView.getTag();
        }
        viewHolder.title.setText(mStringList.get(position));
        for (int i = 0; i < episodes.size(); i++) {
            if (episodes.get(i).get_name().equals(mStringList.get(position))) {
                viewHolder.publishDate.setText(String.valueOf(episodes.get(i).get_pubDate()));
                viewHolder.description.setText(episodes.get(i).get_description());
            }
        }
        return convertView;
    }

    public ArrayList<String> print() {
        return mStringList;
    }

    //Returns a filter that can be used to constrain data with a filtering pattern.
    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private static class Holder {
        TextView title;
        TextView publishDate;
        TextView description;
    }

    private class ValueFilter extends Filter {

        //Invoked in a worker thread to filter the data according to the constraint.
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<String> filterList = new ArrayList<>();
                for (int i = 0; i < mStringFilterList.size(); i++) {
                    if (mStringFilterList.get(i).toLowerCase().contains(constraint)) {
                        Log.d("ADD to new list", mStringFilterList.get(i));
                        filterList.add(mStringFilterList.get(i));
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                Log.d("ADD to new list", "all values");
                results.count = mStringFilterList.size();
                results.values = mStringFilterList;
            }
            return results;
        }

        //Invoked in the UI thread to publish the filtering results in the user interface.
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            mStringList = (ArrayList<String>) results.values;
            notifyDataSetChanged();
        }
    }
}
