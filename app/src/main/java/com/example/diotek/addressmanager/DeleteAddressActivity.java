package com.example.diotek.addressmanager;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by diotek on 2015-07-22.
 */
public class DeleteAddressActivity extends Activity implements BaseVariables {

    public DBManage mDbManager = null;

    ArrayList<Item> mDeleteNameList = null;
    ArrayList<Long> mCheckedListIndex = null;
    boolean[] mCheckedListPosition = null;
    DeleteAdapter mDeleteAdapter = null;

    ListView mDeleteListView = null;
    Button mDeleteBtn = null;
    Button mBackBtn = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deleteaddress);

        mDbManager = DBManage.getInstance(this);

        mDeleteListView = (ListView)findViewById(R.id.deleteAddressListView);
        mDeleteBtn = (Button)findViewById(R.id.deleteButton);
        mBackBtn = (Button)findViewById(R.id.backButton);

        mDeleteNameList = new ArrayList<Item>();
        mCheckedListIndex= new ArrayList<Long>();

        mDeleteAdapter = new DeleteAdapter(this, R.layout.deletelist, mDeleteNameList, mDeleteCheckBoxCheckedListener);
        mDeleteListView.setAdapter(mDeleteAdapter);
        mDeleteListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        mDeleteBtn.setOnClickListener(mDeleteClickListener);
        mBackBtn.setOnClickListener(mBackClickListener);

        setDeleteList();
    }

    private void setDeleteList(){
        Cursor c = mDbManager.query(COLUMNS, null, null, null, null, null);
        mDeleteNameList.clear();
        if(c != null) {
            while (c.moveToNext()) {
                long index = c.getLong(c.getColumnIndex(DB_INDEX));
                String name = c.getString(c.getColumnIndex(DB_NAME));
                mDeleteNameList.add(new Item(index, name));
            }
            mDeleteAdapter.notifyDataSetChanged();
            Collections.sort(mDeleteNameList, new AddressNameCompare());
            setCheckBox();

            c.close();
        }
    }

  private void setCheckBox(){
        mCheckedListPosition= new boolean[mDeleteAdapter.getCount()];

        for (int i = 0; i < mCheckedListPosition.length; i++) {
            mCheckedListPosition[i] = false;
        }
    }

    private CompoundButton.OnCheckedChangeListener mDeleteCheckBoxCheckedListener = new CompoundButton.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int position = (int)buttonView.getTag();
            long deleteIndex = mDeleteAdapter.getItem(position).getmIndex();

            if(isChecked){
                mCheckedListIndex.add(deleteIndex);
                mCheckedListPosition[position] = true;
            }
            else{
                try {
                    mCheckedListIndex.remove(deleteIndex);
                    mCheckedListPosition[position] = false;
                }catch (IndexOutOfBoundsException e){
                    e.printStackTrace();
                }
            }
            Collections.sort(mCheckedListIndex);
        }
    };

    private View.OnClickListener mDeleteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mCheckedListIndex.size() != 0) {
                for (int i = 0; i < mCheckedListIndex.size(); i++) {
                    long deleteIndex = mCheckedListIndex.get(i);
                    mDbManager.delete( DB_INDEX + "=" + deleteIndex, null);
                }
               for(int i = mCheckedListPosition.length-1 ; i >=0 ; i--){
                    if(mCheckedListPosition[i]){
                        mCheckedListPosition[i] = false;
                        mDeleteNameList.remove(i);
                    }
                }
                mDeleteAdapter.notifyDataSetChanged();
            }
            mCheckedListIndex.clear();
        }
    };

    private View.OnClickListener mBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setResult(DELETE_RESULT_OK);
            finish();
        }
    };
}
