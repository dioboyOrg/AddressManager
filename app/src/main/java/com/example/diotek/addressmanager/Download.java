package com.example.diotek.addressmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by diotek on 2015-07-28.
 */
public class Download implements BaseVariables {

    private static final String TAG = "Download";

    private GoogleApiClient mGoogleApiClient;
    private Context mContext;
    private File mDatabaseFile;
    private TextView mCloudTextView;
    private int mDownload;

    public Download(Context context, GoogleApiClient googleApiClient, File databaseFile, TextView cloudTextView, int download) {
        mContext = context;
        mGoogleApiClient = googleApiClient;
        mDatabaseFile = databaseFile;
        mCloudTextView = cloudTextView;
        mDownload = download;

        if(mDownload == DOWNLOAD) {
            mCloudTextView.setText(DOWNLOAD_ING_MESSAGE);
        } else {
            mCloudTextView.setText(SYNCHRONIZATION_ING_MESSAGE);
        }

        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, CREATE_FILE_NAME))
                .build();
        Drive.DriveApi.query(mGoogleApiClient, query)
                .setResultCallback(downloadMetadataCallback);
    }

    final private ResultCallback<DriveApi.MetadataBufferResult> downloadMetadataCallback = new
            ResultCallback<DriveApi.MetadataBufferResult>() {
                @Override
                public void onResult(DriveApi.MetadataBufferResult result) {
                    if (!result.getStatus().isSuccess()) {
                        if(mDownload == DOWNLOAD) {
                            mCloudTextView.setText(DOWNLOAD_FAIL_MESSAGE);
                        } else {
                            mCloudTextView.setText(SYNCHRONIZATION_FAIL_MESSAGE);
                        }
                        return;
                    }
                    Metadata metadata = getMetaData(result.getMetadataBuffer());
                    if(metadata == null){
                        if(mDownload == DOWNLOAD) {
                            mCloudTextView.setText(DOWNLOAD_FAIL_MESSAGE);
                        } else {
                            mCloudTextView.setText(SYNCHRONIZATION_FAIL_MESSAGE);
                        }
                        return;
                    }
                    Drive.DriveApi.fetchDriveId(mGoogleApiClient, metadata.getDriveId().getResourceId())
                            .setResultCallback(downloadIdCallback);
                }
            };

    final private ResultCallback<DriveApi.DriveIdResult> downloadIdCallback = new ResultCallback<DriveApi.DriveIdResult>() {
        @Override
        public void onResult(DriveApi.DriveIdResult result) {
            DriveFile file = Drive.DriveApi.getFile(mGoogleApiClient, result.getDriveId());
            new EditContentsAsyncTask(mContext).execute(file);
        }
    };

    public class EditContentsAsyncTask extends ApiClientAsyncTask<DriveFile, Void, Boolean> {

        public EditContentsAsyncTask(Context context) {
            super(context);
        }

        @Override
        protected Boolean doInBackgroundConnected(DriveFile... args) {
            DriveFile file = args[0];
            DriveApi.DriveContentsResult driveContentsResult =
                    file.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null).await();
            if (!driveContentsResult.getStatus().isSuccess()) {
                if(mDownload == DOWNLOAD) {
                    mCloudTextView.setText(DOWNLOAD_FAIL_MESSAGE);
                } else {
                    mCloudTextView.setText(SYNCHRONIZATION_FAIL_MESSAGE);
                }
                return null;
            }

            DriveContents driveContents = driveContentsResult.getDriveContents();

            InputStream inputStream = null;
            FileOutputStream outputStream = null;
            byte[] memoData  = null;

            try {
                inputStream = driveContents.getInputStream();
                outputStream = new FileOutputStream(mDatabaseFile);
                memoData = new byte[inputStream.available()];

                while (inputStream.read(memoData) != -1) {
                    outputStream.write(memoData);
                }
                return true;

            } catch (IOException e) {
                Log.e(TAG, "IOException while reading from the stream", e);
                return false;
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                if(mDownload == DOWNLOAD) {
                    mCloudTextView.setText(DOWNLOAD_FAIL_MESSAGE);
                } else {
                    mCloudTextView.setText(SYNCHRONIZATION_FAIL_MESSAGE);
                }
                return;
            }
            if(mDownload == DOWNLOAD) {
                mCloudTextView.setText(DOWNLOAD_SUCCESS_MESSAGE);
            } else {
                mCloudTextView.setText(SYNCHRONIZATION_SUCCESS_MESSAGE);
            }
        }
    }

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
