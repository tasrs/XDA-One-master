package com.xda.one.googleplus;

import android.content.Intent;

public interface OnIntentResult {

	void updateUI(int requestCode, int resultCode, Intent bundle);
}
