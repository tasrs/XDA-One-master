package com.xda.one.googleplus;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;
import com.xda.one.auth.XDAAuthenticatorActivity;

import java.io.IOException;

public class GPlusLoginClass implements GPLUS_Callback {

    public Person mPerson;
    public PlusClient plusClient;
    public Fragment fragment;
    private XDAAuthenticatorActivity context;
    private GPlusImpl gplusImpl;
    private String regIdPushNotif = "";

    private ITokenEventCallback mITokenEventCallback;

    public GPlusLoginClass(XDAAuthenticatorActivity con, ITokenEventCallback iTokenEventCallback) {
        this.context = con;
        gplusImpl = new GPlusImpl(context, this);
        mITokenEventCallback = iTokenEventCallback;
    }

    public String getRegIdPushNotif() {
        return regIdPushNotif;
    }

    public void setRegIdPushNotif(String regIdPushNotif) {
        this.regIdPushNotif = regIdPushNotif;
    }

    public void loginGPlus() {
        gplusImpl.initGPlusImpl();
    }

    public void logOutGPlus() {
        gplusImpl.GPlusLogout();
    }

    private void getToken(final GoogleApiClient mGoogleApiClient) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String mScopes = "oauth2:https://www.googleapis.com/auth/plus.login https://www.googleapis.com/auth/plus.profile.emails.read";
                Bundle mAppActivities = new Bundle();
                mAppActivities.putString(GoogleAuthUtil.KEY_REQUEST_VISIBLE_ACTIVITIES, "");
                try {
                    String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
                    final String cod = GoogleAuthUtil.getToken(context, email, mScopes);
                    final String code = GoogleAuthUtil.getToken(
                            context,                                     // Context context
                            Plus.AccountApi.getAccountName(mGoogleApiClient),  // String accountName
                            mScopes,                                            // String scope
                            mAppActivities                                      // Bundle bundle
                    );
                    Log.e("Token", "code    " + code);
                    if (code != null && !code.equals("")) {
                        mITokenEventCallback.onTokenResult(code);
                    }
                } catch (UserRecoverableAuthException userAuthEx) {
                    // Start the user recoverable action using the intent returned by
                    // getIntent()
                    context.startActivityForResult(
                            userAuthEx.getIntent(),
                            1000);
                    return;
                } catch (IOException transientEx) {
                    // network or server error, the call is expected to succeed if you try again later.
                    // Don't attempt to call again immediately - the request is likely to
                    // fail, you'll hit quotas or back-off.
                    return;
                } catch (GoogleAuthException authEx) {
                    // Failure. The call is not expected to ever succeed so it should not be
                    // retried.
                    return;
                }
            }
        }).start();
    }

    @Override
    public void onLogin(GoogleApiClient mGoogleApiClient) {
        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            getToken(mGoogleApiClient);
            Person currentPerson = Plus.PeopleApi
                    .getCurrentPerson(mGoogleApiClient);
            String personName = currentPerson.getDisplayName();
            String personPhoto = currentPerson.getImage().getUrl();
            String personGooglePlusProfile = currentPerson.getUrl();
            String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
            Log.e("data", "name" + currentPerson + "\n" + personName + "\n"
                    + personPhoto + "\n" + email);
        }
    }

    public void onAuthFailiure() {
    }

    @Override
    public void onUserData(Person person, PlusClient mPlusClient) {
    }

    @Override
    public void onPost(boolean postStatus) {
    }

    @Override
    public void onFriendRequest(boolean shareAppStatus) {
    }

    @Override
    public void ondisconn(boolean status) {
    }

    @Override
    public void onFailure(boolean status) {
    }

    @Override
    public void onLogout(boolean status) {
    }

}
