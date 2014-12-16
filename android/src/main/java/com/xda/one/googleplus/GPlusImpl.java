package com.xda.one.googleplus;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.xda.one.auth.XDAAuthenticatorActivity;

public class GPlusImpl extends XDAAuthenticatorActivity implements
        OnConnectionFailedListener, OnIntentResult,
        GoogleApiClient.ConnectionCallbacks {
    public static final int REQUEST_CODE_RESOLVE_ERR = 9000;
    static GPlusImpl googlePlus;
    private static int counter = 0;
    Activity mActivity;
    GPLUS_Callback mInterface;
    private ProgressDialog mConnectionProgressDialog;
    private ConnectionResult mConnectionResult;
    private GoogleApiClient mGoogleApiClient;

    public GPlusImpl(Context con, GPLUS_Callback mInterface) {
        mActivity = (Activity) con;
        this.mInterface = mInterface;
        mUpdateResult = this;

        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_PROFILE).build();
    }

    public void initGPlusImpl() {
        // Progress bar to be displayed if the connection failure is not
        // resolved.
        mConnectionProgressDialog = new ProgressDialog(mActivity);
        mConnectionProgressDialog.setCancelable(true);
        mConnectionProgressDialog.setMessage("Signing in...");
        if (checkGooglePlayServicesAvailability())
            signInWithGooglePlus();
    }

    public void disconnectGooglePlus() {
        Log.e("disconnected", "1");
        mGoogleApiClient.disconnect();

        mInterface.ondisconn(true);
    }

    public void GPlusLogout() {
        // Prior to disconnecting, run clearDefaultAccount().
        Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
        Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
                .setResultCallback(new ResultCallback<Status>() {

                    public void onResult(Status result) {
                        if (mGoogleApiClient.isConnected()) {
                            Plus.AccountApi
                                    .clearDefaultAccount(mGoogleApiClient);
                            mGoogleApiClient.disconnect();
//                            mGoogleApiClient.connect();
                        }
                    }

                });

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

        Log.e("connection failure", "1");
        mGoogleApiClient.disconnect();
        if (mConnectionProgressDialog.isShowing() && counter < 3) {
            // The user clicked the sign-in button already. Start to resolve
            // connection errors. Wait until onConnected() to dismiss the
            // connection dialog.
            if (result.hasResolution()) {
                try {
                    counter++;
                    result.startResolutionForResult(mActivity,
                            REQUEST_CODE_RESOLVE_ERR);
                } catch (SendIntentException e) {
                    mGoogleApiClient.disconnect();
                    mGoogleApiClient.connect();
                }
            }
        } else if (counter > 2) {
            mGoogleApiClient.disconnect();
            mConnectionResult = result;
            if (mConnectionProgressDialog.isShowing())
            mInterface.onFailure(true);
            counter = 0;
        }
        // Save the result and resolve the connection failure upon a user
        // click.
        mConnectionResult = result;
        mConnectionProgressDialog.dismiss();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.e("on connected", "1");
        if (mConnectionProgressDialog.isShowing())
            mConnectionProgressDialog.dismiss();
//        GPlusLogout();
        Plus.PeopleApi.loadVisible(mGoogleApiClient, null).setResultCallback((XDAAuthenticatorActivity) mActivity);
        mInterface.onLogin(mGoogleApiClient);
    }

    public boolean checkGooglePlayServicesAvailability() {
        Log.e("check play services", "1");
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(mActivity);
        if (resultCode != ConnectionResult.SUCCESS) {
            GooglePlayServicesUtil.getErrorDialog(resultCode, mActivity, 0).show();
            return false;
        }

        Log.d("GooglePlayServicesUtil Check", "Result is: " + resultCode);
        return true;
    }

    public void signInWithGooglePlus() {
        if (!(mGoogleApiClient.isConnected())) {
            if (mConnectionResult == null) {
                mConnectionProgressDialog.show();
                mGoogleApiClient.connect();
                Log.e("calling connect", "1");
            } else {
                try {
                    mConnectionResult.startResolutionForResult(mActivity,
                            REQUEST_CODE_RESOLVE_ERR);
                } catch (SendIntentException e) {
                    // Try connecting again.
                    mConnectionResult = null;
                    Log.e("re connecting", "1");
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    @Override
    public void updateUI(int requestCode, int resultCode, Intent bundle) {
        mConnectionResult = null;
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
    }
}