package com.example.healthtracker;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HeartRecyclerViewAdapter extends RecyclerView.Adapter<HeartRecyclerViewAdapter.ViewHolder>
{
    private static final String TAG = "HeartListFragment";
    private List<Pair<String, String>> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    HeartRecyclerViewAdapter(Context context, List<Pair<String, String>> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_heart_items, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Pair<String, String> dataPair = mData.get(position);
        String date = dataPair.first;
        String dd = dataPair.second;
        Log.w(TAG, "dd "+dd);
        String val = dd.replaceAll("[{}\" ]", "");
        Log.w(TAG, "dd "+val);
        Map<String, String> myMap = new HashMap<String, String>();
        String[] pairs = val.split(",");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            myMap.put(keyValue[0], String.valueOf(keyValue[1]));
        }
        Log.e(TAG, "dd "+myMap);
        holder.myTextView.setText(date);
        holder.heartVal.setText(myMap.get("rate"));
        holder.systVal.setText(myMap.get("syst"));
        holder.diastVal.setText(myMap.get("diast"));
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        TextView heartVal;
        TextView systVal;
        TextView diastVal;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.heartDate);
            heartVal = itemView.findViewById(R.id.rateVal);
            systVal = itemView.findViewById(R.id.systVal);
            diastVal = itemView.findViewById(R.id.diastVal);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id).toString();
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
