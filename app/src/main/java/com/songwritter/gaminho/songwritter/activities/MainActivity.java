package com.songwritter.gaminho.songwritter.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.songwritter.gaminho.songwritter.R;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    protected ListView mLvProjects;
    protected TextView mTVUpdateStatus;
    protected List<String> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mList = new ArrayList<>();
        mList.add("Project 1");
        mList.add("Project 2");
        mList.add("Project 3");
        fillViews();
    }

    public void save(View view){
        Toast.makeText(this,"Utils'est cool Raoul!", Toast.LENGTH_SHORT).show();
        mList.add("New Project");

        Log.i("onClick", "save");
        ((ProjectAdapter)mLvProjects.getAdapter()).setListItem(mList);
    }

    public void newTxt(View view){

        Log.i("onClick", "newTxt");
        startActivity(new Intent(getApplication(), SongActivity.class));
    }

    public boolean needUpdate(){
        return true;
    }

    public void fillViews(){
        mLvProjects = (ListView) findViewById(R.id.lv);
        mLvProjects.setAdapter(new ProjectAdapter(this, mList));

        mTVUpdateStatus = (TextView) findViewById(R.id.updatestatus);
        if(needUpdate()){
            mTVUpdateStatus.setBackgroundColor(getColor(R.color.red500));
            mTVUpdateStatus.setText(getText(R.string.needUpdate));
        }
        else{
            mTVUpdateStatus.setBackgroundColor(getColor(R.color.green500));
            mTVUpdateStatus.setText(getText(R.string.uptodate));
        }
    }


    private class ProjectAdapter extends BaseAdapter {

        private Context mContext;
        private List<String> mItems;

        ProjectAdapter(Context context, List<String> mProjects){
            this.mContext = context;
            this.mItems = mProjects;
        }


        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int i) {
            return mItems.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.adapter_projects, viewGroup, false);
            TextView title = (TextView) row.findViewById(R.id.tv1);
            TextView  detail = (TextView) row.findViewById(R.id.tv2);
            title.setText(mItems.get(i));
            detail.setText("Description " + mItems.get(i));

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(), mItems.get(i), Toast.LENGTH_SHORT).show();
                }
            });

            return row;
        }

        public void setListItem(List<String> neoList){
            mList = neoList;
            notifyDataSetChanged();
        }
    }
}
