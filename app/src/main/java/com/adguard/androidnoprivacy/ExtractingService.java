package com.adguard.androidnoprivacy;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * The service that extracts all the dangerous private information
 */
public final class ExtractingService {

    private static final String CANNOT_EXTRACT = "Cannot extract";

    static String getPhoneNumber(Context context) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            return CANNOT_EXTRACT;
        }

        try {
            SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            if (subscriptionManager == null) {
                return CANNOT_EXTRACT;
            }

            List<SubscriptionInfo> list = subscriptionManager.getActiveSubscriptionInfoList();
            if (list != null) {
                List<String> results = new ArrayList<>();
                for (SubscriptionInfo subscriptionInfo : list) {
                    results.add(subscriptionInfo.getNumber());
                }

                return TextUtils.join(", ", results);
            }


            return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
        } catch (SecurityException ex) {
            return CANNOT_EXTRACT;
        }
    }

    static String getImei(Context context) {
        try {
            return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        } catch (SecurityException ex) {
            return CANNOT_EXTRACT;
        }
    }

    static List<String> getGoogleAccounts(Context context) {
        List<String> mGmail = new ArrayList<>();
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType("com.google");
        for (Account account : accounts) {
            String name = account.name;
            if (!(TextUtils.isEmpty(name) || mGmail.contains(name))) {
                mGmail.add(name);
            }
        }

        return mGmail;
    }

    static String[] getContacts(Context context) {
        Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor == null) {
            return new String[]{};
        }
        try {
            ArrayList<String> list = new ArrayList<>();
            while (cursor.moveToNext()) {
                list.add(cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME)));
            }
            return list.toArray(new String[]{});
        } finally {
            cursor.close();
        }
    }

    public static String[] getSms(Context context) {
        Cursor cursor = context.getContentResolver().query(Telephony.Sms.CONTENT_URI, null, null, null, Telephony.Sms.DEFAULT_SORT_ORDER);
        if (cursor == null) {
            return new String[]{};
        }

        try {
            ArrayList<String> list = new ArrayList<>();
            while (cursor.moveToNext()) {
                String address = cursor.getString(cursor.getColumnIndex(Telephony.Sms.ADDRESS));
                String body = cursor.getString(cursor.getColumnIndex(Telephony.Sms.BODY));
                list.add(address + "\n " + body);
            }
            return list.toArray(new String[]{});
        } finally {
            cursor.close();
        }
    }
}
