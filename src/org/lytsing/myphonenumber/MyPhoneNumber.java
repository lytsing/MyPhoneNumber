/*
 * Copyright (C) 2010 lytsing.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lytsing.myphonenumber;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.telephony.TelephonyManager;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;

import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;


/**
 * Display and Setting My Phone Number
 */
public class MyPhoneNumber extends PreferenceActivity implements OnSharedPreferenceChangeListener {
    private static final String LOG_TAG = "MyPhoneNumber";
    
    private static final String KEY_PHONE_NUMBER_STATUS_PREFERENCE = "my_phone_number_status";
    private static final String KEY_TEXTEDIT_PREFERENCE = "edittext_preference";

    private Preference mPhoneNumberStatus;
    
    private TelephonyManager mTelephonyManager;
    private Phone mPhone = null;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.my_phone_number);
        mPhoneNumberStatus = findPreference(KEY_PHONE_NUMBER_STATUS_PREFERENCE);

        mTelephonyManager = (TelephonyManager)this
                .getSystemService(Context.TELEPHONY_SERVICE);
        
        mPhone = PhoneFactory.getDefaultPhone();

        String rawNumber = mTelephonyManager.getLine1Number();
        String formattedNumber = null;

        if (!TextUtils.isEmpty(rawNumber)) {
            formattedNumber = PhoneNumberUtils.formatNumber(rawNumber);
        } else {
            formattedNumber = getResources().getString(R.string.unknown);
        }

        mPhoneNumberStatus.setSummary(formattedNumber);
                
        Log.d(LOG_TAG, " sim state: " + mTelephonyManager.getSimState());
        
        // Check SIM status
        if (mTelephonyManager.getSimState() != TelephonyManager.SIM_STATE_READY) {
            showNoSimAlertDialog();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(
                this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(KEY_TEXTEDIT_PREFERENCE)) {

            String alphaTag = mPhone.getLine1AlphaTag();
            if (alphaTag == null || "".equals(alphaTag)) {
                // No tag, set it.
                alphaTag = "Voice Line 1";
            }

            String number = sharedPreferences.getString(key, "");

            if (number.trim().length() > 0) {
                mPhoneNumberStatus.setSummary(number);
                mPhone.setLine1Number(alphaTag, number, null);
            }
        }
    }
    
    private void showNoSimAlertDialog() {
        Dialog dialog = new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(R.string.no_sim_error_message)
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            })
            .create();
        
        dialog.show();
    }
}

