package com.example.diotek.addressmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by diotek on 2015-07-21.
 */
public class DeleteAdapter extends BaseAdapter implements BaseVariables {
    LayoutInflater mInflater = null;
    ArrayList<Item> mItems = null;
    CheckBox mDeleteCheckBox = null;
    TextView mDeleteNameText = null;
    int mLayout = 0;
    private CompoundButton.OnCheckedChangeListener mDeleteCheckBoxCheckedListener;

    public DeleteAdapter(Context context, int layout, ArrayList<Item> addressItems,  CompoundButton.OnCheckedChangeListener deleteCheckBoxCheckedListener) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mItems = addressItems;
        mLayout = layout;
        mDeleteCheckBoxCheckedListener = deleteCheckBoxCheckedListener;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Item getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(mLayout, parent, false);
        }

        mDeleteNameText = (TextView) convertView.findViewById(R.id.addressDeleteListText);
        mDeleteNameText.setText(mItems.get(position).getmName());

        mDeleteCheckBox = (CheckBox)convertView.findViewById(R.id.deleteCheckBox);
        mDeleteCheckBox.setChecked(false);
        mDeleteCheckBox.setOnCheckedChangeListener(mDeleteCheckBoxCheckedListener);
        mDeleteCheckBox.setTag(position);

        return convertView;
    }
}
