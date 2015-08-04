package com.example.diotek.addressmanager;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by diotek on 2015-07-20.
 */
public class InsertAddressActivity extends Activity implements BaseVariables{

    public DBManage mDbManager = null;
    Button mIndividualCancelBtn = null;
    Button mIndividualSaveBtn = null;

    EditText mInsertNameEdit = null;
    EditText mInsertPhoneNumberEdit = null;
    EditText mInsertHomeNumberEdit = null;
    EditText mInsertCompanyNumberEdit = null;
    EditText mInsertEmailEdit = null;

    ContentValues mAddRowValue = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertaddress);

        mDbManager = DBManage.getInstance(this);
        mIndividualCancelBtn = (Button)findViewById(R.id.individualCancelButton);
        mIndividualSaveBtn = (Button)findViewById(R.id.individualSaveButton);

        mInsertNameEdit = (EditText)findViewById(R.id.insertAddressNameEdit);
        mInsertPhoneNumberEdit = (EditText)findViewById(R.id.insertAddressPhoneNumberEdit);
        mInsertHomeNumberEdit = (EditText)findViewById(R.id.insertAddressHomeNumberEdit);
        mInsertCompanyNumberEdit = (EditText)findViewById(R.id.insertAddressCompanyNumberEdit);
        mInsertEmailEdit = (EditText)findViewById(R.id.insertAddressEmailEdit);

        mIndividualSaveBtn.setOnClickListener(mInsertSaveClickListener);
        mIndividualCancelBtn.setOnClickListener(mInsertCancelClickListener);
    }

    private View.OnClickListener mInsertSaveClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent = getIntent();
            Bundle bundleNameString = new Bundle();
            String insertName = mInsertNameEdit.getText().toString();

            mAddRowValue = new ContentValues();

            mAddRowValue.put(DB_NAME,insertName);
            mAddRowValue.put(DB_PHONE_NUMBER, mInsertPhoneNumberEdit.getText().toString());
            mAddRowValue.put(DB_HOME_NUMBER, mInsertHomeNumberEdit.getText().toString());
            mAddRowValue.put(DB_COMPANY_NUMBER, mInsertCompanyNumberEdit.getText().toString());
            mAddRowValue.put(DB_E_MAIL, mInsertEmailEdit.getText().toString());

            long rowId = mDbManager.insert(mAddRowValue);
            bundleNameString.putLong(INSERT_INDEX_KEY, rowId);
            bundleNameString.putString(INSERT_NAME_KEY, insertName);

            intent.putExtra(INSERT_DATA_KEY, bundleNameString);
            setResult(INSERT_RESULT_OK, intent);

            finish();
        }
    };

    private View.OnClickListener mInsertCancelClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            finish();
        }
    };

}
