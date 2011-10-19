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

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.RemoteViews;

public class MyPhoneNumberWidget extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(new Intent(context, UpdateService.class));
    }

    public static class UpdateService extends Service {
        @Override
        public void onStart(Intent intent, int startId) {
            RemoteViews updateViews = new RemoteViews(getPackageName(), R.layout.main);

            TelephonyManager mTelephonyManager = (TelephonyManager)this
                    .getSystemService(Context.TELEPHONY_SERVICE);

            String rawNumber = mTelephonyManager.getLine1Number();
            String formattedNumber = null;

            if (!TextUtils.isEmpty(rawNumber)) {
                formattedNumber = PhoneNumberUtils.formatNumber(rawNumber);
            } else {
                formattedNumber = getResources().getString(R.string.unknown);
            }

            updateViews.setTextViewText(R.id.widget_textview, getResources().getString(
                    R.string.application_name)
                    + "：" + formattedNumber);

            ComponentName thisWidget = new ComponentName(this, MyPhoneNumberWidget.class);

            AppWidgetManager manager = AppWidgetManager.getInstance(this);
            manager.updateAppWidget(thisWidget, updateViews);
        }

        @Override
        public IBinder onBind(Intent intent) {
            // TODO Auto-generated method stub
            return null;
        }
    }

}
