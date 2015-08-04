package com.example.diotek.addressmanager;

import android.content.Context;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.io.File;

/**
 * Created by diotek on 2015-07-22.
 */
public class DeleteFilesInDrive implements BaseVariables{
        private GoogleApiClient mGoogleApiClient;
    private TextView mCloudTextView;

    public DeleteFilesInDrive(GoogleApiClient googleApiClient, TextView cloudTextView) {
        mGoogleApiClient = googleApiClient;
        mCloudTextView = cloudTextView;

        mCloudTextView.setText(DELETE_ING_MESSAGE);

        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, CREATE_FILE_NAME))
                .build();
        Drive.DriveApi.query(mGoogleApiClient, query)
                .setResultCallback(deleteMetadataCallback);
    }

    final private ResultCallback<DriveApi.MetadataBufferResult> deleteMetadataCallback = new
            ResultCallback<DriveApi.MetadataBufferResult>() {
                @Override
                public void onResult(DriveApi.MetadataBufferResult result) {
                    if (!result.getStatus().isSuccess()) {
                        mCloudTextView.setText("Problem while retrieving results");
                        return;
                    }
                    Metadata metadata = getMetaData(result.getMetadataBuffer());
                    if(metadata != null) {
                        Drive.DriveApi.getFile(mGoogleApiClient, metadata.getDriveId()).delete(mGoogleApiClient).setResultCallback(deleteResultCallback);
                    }
                }
            };
    final private ResultCallback<Status> deleteResultCallback = new
            ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    if (!status.isSuccess()) {
                        mCloudTextView.setText(DELETE_FAIL_MESSAGE);
                        return;
                    }
                    mCloudTextView.setText(DELETE_SUCCESS_MESSAGE);
                }
            };
    public Metadata getMetaData(MetadataBuffer metadataBuffer){
        Metadata metadata = null;
        for(int i = 0; i< metadataBuffer.getCount(); i++){
            metadata = metadataBuffer.get(i);
            if(metadata.getTitle().equals(CREATE_FILE_NAME)){
                break;
            }
        }
        return metadata;
    }
}