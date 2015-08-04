package com.example.diotek.addressmanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by diotek on 2015-07-20.
 */
public class MainActivity extends ActionBarActivity implements BaseVariables {

    public DBManage mDbManager = null;

    EditText mSearchEdit = null;

    ListView mAddressListView = null;
    TextView mAddressTotalText = null;
    Button mInsertBtn = null;
    Button mDeleteBtn = null;
    Button mCloudBtn = null;

    ArrayList<Item> mAddressListItem = null;
    InsertAdapter mAddressListAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDbManager = DBManage.getInstance(this);

        mAddressListView = (ListView)findViewById(R.id.addressListView);
        mAddressTotalText = (TextView)findViewById(R.id.addressTotalText);
        mSearchEdit = (EditText)findViewById(R.id.searchEdit);

        mInsertBtn = (Button)findViewById(R.id.insertButton);
        mDeleteBtn = (Button)findViewById(R.id.deleteButton);
        mCloudBtn = (Button)findViewById(R.id.cloudButton);

        //어뎁터
        mAddressListItem = new ArrayList<Item>();
        mAddressListAdapter = new InsertAdapter(this, R.layout.addresslist, mAddressListItem);

        mAddressListView.setAdapter(mAddressListAdapter);

        mAddressListView.setOnItemClickListener(mListClickListener);
        mSearchEdit.addTextChangedListener(mSearchTextWatcher);
        mInsertBtn.setOnClickListener(mInsertClickListener);
        mDeleteBtn.setOnClickListener(mDeleteClickListener);
        mCloudBtn.setOnClickListener(mCloudClickListener);

        viewAddressList();
    }

    private void viewAddressList(){
        Cursor c = mDbManager.query(COLUMNS, null, null, null, null, null);
        mAddressListItem.clear();
        if(c != null) {
            while (c.moveToNext()) {
                //데이터 원본 준비
                long index = c.getLong(c.getColumnIndex(DB_INDEX));
                String name = c.getString(c.getColumnIndex(DB_NAME));
                mAddressListItem.add(new Item(index, name));
            }
            mAddressListAdapter.notifyDataSetChanged();
            Collections.sort(mAddressListItem, new AddressNameCompare());
            setAddressTotalText(mAddressListAdapter.getCount());
            c.close();
        }
    }

    private void setAddressTotalText(int preTotalCnt){
        String totalCntString = null;
        totalCntString = MY_ADDRESS + " " + preTotalCnt;
        mAddressTotalText.setText(totalCntString);
    }

    private TextWatcher mSearchTextWatcher = new TextWatcher(){

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            Cursor c = mDbManager.query(COLUMNS, null, null, null, null, null);
            mAddressListItem.clear();
            if (c != null) {
                while (c.moveToNext()) {
                    long index = c.getLong(c.getColumnIndex(DB_INDEX));
                    String name = c.getString(c.getColumnIndex(DB_NAME));
                    if (name.contains(s)) {
                        mAddressListItem.add(new Item(index, name));
                    }
                }
                mAddressListAdapter.notifyDataSetChanged();
                setAddressTotalText(mAddressListAdapter.getCount());
                c.close();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private AdapterView.OnItemClickListener mListClickListener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position ,long id){
            Intent intent = new Intent();
            Bundle bundleData = new Bundle();

            Item item = (Item)parent.getItemAtPosition(position);

            bundleData.putLong(LIST_INDEX_KEY, item.getmIndex());
            bundleData.putInt(LIST_POSITION_KEY, position);
            intent.putExtra(LIST_INDIVIDUAL_DATA_KEY,bundleData);
            intent.setClass(MainActivity.this, DetailAddressActivity.class);
            startActivityForResult(intent, DETAIL_ADDRESS_ACTIVITY);
        }
    };

    private View.OnClickListener mInsertClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, InsertAddressActivity.class);
            startActivityForResult(intent, INSERT_ADDRESS_ACTIVITY);
        }
    };

    private View.OnClickListener mDeleteClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, DeleteAddressActivity.class);
            startActivityForResult(intent, DELETE_ADDRESS_ACTIVITY);
        }
    };

    private View.OnClickListener mCloudClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getBaseContext(),CloudActivity.class);
            startActivityForResult(intent, CLOUD_ACTIVITY);
        }
    };

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent intent){
        if(requestCode == INSERT_ADDRESS_ACTIVITY){
            if(resultCode == INSERT_RESULT_OK){
                viewAddressList();
            }
        }
        else if(requestCode == DELETE_ADDRESS_ACTIVITY){
            if(resultCode == DELETE_RESULT_OK){
                viewAddressList();
            }
        }
        else if(requestCode == DETAIL_ADDRESS_ACTIVITY){
            if(resultCode == DETAIL_RESULT_OK){
               viewAddressList();
            }
        }
        else if(requestCode == CLOUD_ACTIVITY){
            if(resultCode == CLOUD_RESULT_OK){
                viewAddressList();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
