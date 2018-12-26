package com.vincent.wear.audio.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vincent.wear.audio.R;
import com.vincent.wear.audio.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * 小区列表适配器
 */

public class RecordListAdapter extends RecyclerView.Adapter<RecordListAdapter.ViewHolder> implements View.OnClickListener {
    private OnItemClickListener mOnItemClickListener = null;
    private boolean mIsScrolling;
    private List<File> mFiles = new ArrayList<>();

    public RecordListAdapter(List<File> ifs) {
        this.mFiles = ifs;
    }

    public boolean isScrolling() {
        return mIsScrolling;
    }

    public void setScrolling(boolean mIsScrolling) {
        this.mIsScrolling = mIsScrolling;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record_list, parent, false);
        view.setOnClickListener(this);
        ViewHolder vh = new ViewHolder(view);
        return vh;

    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.itemView.setTag(position);
        final File file = mFiles.get(position);
        viewHolder.nameView.setText(file.getName().replace(".wav", ""));
        viewHolder.sizeView.setText(Util.formatFileSize(file.length()));


    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public static interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameView;
        TextView sizeView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.name);
            sizeView = itemView.findViewById(R.id.size);
        }
    }
}


