package com.example.diotek.addressmanager;

        import android.content.Context;
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
        import com.google.android.gms.drive.DriveResource;
        import com.google.android.gms.drive.Metadata;
        import com.google.android.gms.drive.MetadataBuffer;
        import com.google.android.gms.drive.MetadataChangeSet;
        import com.google.android.gms.drive.query.Filters;
        import com.google.android.gms.drive.query.Query;
        import com.google.android.gms.drive.query.SearchableField;

        import java.io.File;
        import java.io.FileInputStream;
        import java.io.IOException;
        import java.io.OutputStream;
        import java.util.Date;

/**
 * Created by diotek on 2015-07-28.
 */
public class Synchronization implements BaseVariables {

    private static final String TAG = "Upload";

    private GoogleApiClient mGoogleApiClient;
    private Context mContext;
    private File mDatabaseFile;
    private TextView mCloudTextView;

    public Synchronization(Context context, GoogleApiClient googleApiClient, File databaseFile, TextView cloudTextView) {
        mContext = context;
        mGoogleApiClient = googleApiClient;
        mDatabaseFile = databaseFile;
        mCloudTextView = cloudTextView;

        mCloudTextView.setText(SYNCHRONIZATION_ING_MESSAGE);

        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, CREATE_FILE_NAME))
                .build();
        Drive.DriveApi.query(mGoogleApiClient, query)
                .setResultCallback(uploadMetadataCallback);
    }

    final private ResultCallback<DriveApi.MetadataBufferResult> uploadMetadataCallback = new
            ResultCallback<DriveApi.MetadataBufferResult>() {
                @Override
                public void onResult(DriveApi.MetadataBufferResult result) {
                    if (!result.getStatus().isSuccess()) {
                        mCloudTextView.setText("Problem while retrieving results");
                        return;
                    }
                    Metadata metadata = getMetaData(result.getMetadataBuffer());
                    if(metadata == null) {
                        Upload upload = new Upload(mContext,  mGoogleApiClient, mDatabaseFile, mCloudTextView, SYNCHRONIZATION);
                        return;
                    } else {
                        Drive.DriveApi.fetchDriveId(mGoogleApiClient,
                                metadata.getDriveId().getResourceId())
                                .setResultCallback(synchronizeCallback);
                    }
                }
            };

    final private ResultCallback<DriveApi.DriveIdResult> synchronizeCallback = new ResultCallback<DriveApi.DriveIdResult>() {
        @Override
        public void onResult(DriveApi.DriveIdResult result) {
            if (!result.getStatus().isSuccess()) {
                mCloudTextView.setText("Problem while retrieving results");
                return;
            }
            DriveFile file = Drive.DriveApi.getFile(mGoogleApiClient, result.getDriveId());
            file.getMetadata(mGoogleApiClient).setResultCallback(metadataResultResultCallback);
        }
    };
    final private ResultCallback<DriveResource.MetadataResult> metadataResultResultCallback = new ResultCallback<DriveResource.MetadataResult>() {
        @Override
        public void onResult(DriveResource.MetadataResult metadataResult) {
            if (!metadataResult.getStatus().isSuccess()) {
                mCloudTextView.setText("Problem while retrieving results");
                return;
            }
            if (mDatabaseFile != null && metadataResult.getMetadata().getModifiedByMeDate().before(new Date(mDatabaseFile.lastModified()))) {
                Upload upload = new Upload(mContext,  mGoogleApiClient, mDatabaseFile, mCloudTextView, SYNCHRONIZATION);
            } else {
                Download download = new Download(mContext, mGoogleApiClient, mDatabaseFile, mCloudTextView, SYNCHRONIZATION);
            }
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
