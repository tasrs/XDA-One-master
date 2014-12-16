package com.xda.one.googleplus;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;

public interface GPLUS_Callback
{
	public void onLogin(GoogleApiClient mPlusClient);

	public void ondisconn(boolean status);

	public void onFailure(boolean status);

	public void onAuthFailiure();

	public void onUserData(Person person, PlusClient mPlusClient);

	public void onPost(boolean postStatus);

	public void onFriendRequest(boolean shareAppStatus);

	public void onLogout(boolean status);
}
