package com.vincent.wear.audio.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.wear.widget.WearableLinearLayoutManager;
import android.support.wear.widget.WearableRecyclerView;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;

import com.vincent.wear.audio.R;
import com.vincent.wear.audio.util.Util;
import com.vincent.wear.audio.adapter.RecordListAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 *
 */
public class RecordListActivity extends WearableActivity implements RecordListAdapter.OnItemClickListener {


    private static final String TAG = RecordListActivity.class.getSimpleName();
    private WearableRecyclerView mListView;
    private Disposable mDisposable = null;
    private List<File> mFileList = new ArrayList<>();
    private RecordListAdapter mAdapter = new RecordListAdapter(mFileList);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_record_list);
        mListView = findViewById(R.id.list);
        //首尾项 垂直居中
        mListView.setEdgeItemsCenteringEnabled(true);
        WearableLinearLayoutManager llm = new WearableLinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mListView.setLayoutManager(llm);
        mListView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        //循环滚动手势
//        mListView.setCircularScrollingGestureEnabled(true);
        File file = new File(Util.getStoreDirectory());
        if (file != null && file.exists()) {

            Observable.fromArray(file.listFiles()).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).subscribe(new Observer<File>() {
                @Override
                public void onSubscribe(Disposable d) {
                    mDisposable = d;
                }

                @Override
                public void onNext(File file) {
                    Log.d(TAG, "----" + file.getAbsolutePath());
                    mFileList.add(file);
                    mAdapter.notifyItemInserted(mFileList.size());
                }

                @Override
                public void onError(Throwable e) {
                    mDisposable.dispose();
                }

                @Override
                public void onComplete() {
                    mDisposable.dispose();
                }
            });

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onItemClick(View view, int position) {

        Intent share_intent = new Intent();
        share_intent.setAction(Intent.ACTION_SEND);
        share_intent.setType("text/plain");
        share_intent.putExtra(Intent.EXTRA_SUBJECT, "信息");
        share_intent = Intent.createChooser(share_intent, "分享");
        share_intent.addCategory("android.intent.category.DEFAULT");
        share_intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(mFileList.get(position)));
        startActivity(share_intent);
    }


}
