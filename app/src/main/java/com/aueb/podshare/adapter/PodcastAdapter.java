package com.aueb.podshare.adapter;

import android.app.ProgressDialog;
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
import com.aueb.podshare.classes.Podcast;

import java.util.ArrayList;

public class PodcastAdapter extends BaseAdapter implements Filterable {
    private ArrayList<Podcast> podcasts;
    private ArrayList<String> mStringList;
    private ArrayList<String> mStringFilterList;
    private LayoutInflater mInflater;
    private ValueFilter valueFilter;

    public PodcastAdapter(ArrayList<Podcast> podcasts, ArrayList<String> mStringList, Context context) {
        this.podcasts = podcasts;
        this.mStringList = mStringList;
        this.mStringFilterList = mStringList;
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
        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.podcast_item_fragment,null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.podcast_name_text);
            viewHolder.noOfEpisodes = (TextView) convertView.findViewById(R.id.number_of_episodes);
            viewHolder.description = (TextView) convertView.findViewById(R.id.podcast_description);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.title.setText(mStringList.get(position).toString());
        for (int i = 0 ; i < podcasts.size(); i++) {
            if (podcasts.get(i).getName().equals(mStringList.get(position))) {
                if (podcasts.get(i).get_number_of_episodes() != 0) {
                    viewHolder.noOfEpisodes.setText(String.valueOf(podcasts.get(i).get_number_of_episodes()));
                }
                viewHolder.description.setText(podcasts.get(i).getDescription().toString());
            }
        }
        return convertView;
    }

    private static class  ViewHolder{
        TextView title;
        TextView noOfEpisodes;
        TextView description;
    }

    //Returns a filter that can be used to constrain data with a filtering pattern.
    @Override
    public Filter getFilter() {
        if(valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {

        //Invoked in a worker thread to filter the data according to the constraint.
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results=new FilterResults();
            if(constraint!=null && constraint.length()>0){
                ArrayList<String> filterList=new ArrayList<>();
                for(int i=0;i<mStringFilterList.size();i++){
                    if(mStringFilterList.get(i).toLowerCase().contains(constraint)) {
                        Log.d("ADD to new list", mStringFilterList.get(i));
                        filterList.add(mStringFilterList.get(i));
                    }
                }
                results.count=filterList.size();
                results.values=filterList;
            }else{
                Log.d("ADD to new list", "all values");
                results.count=mStringFilterList.size();
                results.values=mStringFilterList;
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
