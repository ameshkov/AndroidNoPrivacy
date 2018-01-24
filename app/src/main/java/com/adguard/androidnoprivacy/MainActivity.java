package com.adguard.androidnoprivacy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        StringBuilder sb = new StringBuilder();
        sb.append("<h4>Your Accounts</strong></h4>");
        sb.append("<p>");
        String emails = TextUtils.join(", ", ExtractingService.getGoogleAccounts(getApplicationContext()));
        sb.append(TextUtils.htmlEncode(emails));
        sb.append("</p>");
        sb.append("<h4>Your IMEI</strong></h4>");
        sb.append("<p>");
        sb.append(TextUtils.htmlEncode(ExtractingService.getImei(getApplicationContext())));
        sb.append("</p>");
        sb.append("<h4>Your Phone Number</strong></h4>");
        sb.append("<p>");
        sb.append(TextUtils.htmlEncode(ExtractingService.getPhoneNumber(getApplicationContext())));
        sb.append("</p>");

        // Contacts
        String[] contacts = ExtractingService.getContacts(getApplicationContext());
        sb.append("<h4>Your Contacts (showing first 3 of ").append(contacts.length).append(")</h4>");
        sb.append("<ul>");
        for (int i = 0; i < 3 && i < contacts.length; i++) {
            sb.append("<li>");
            sb.append(TextUtils.htmlEncode(contacts[i]));
            sb.append("</li>");
        }
        sb.append("</ul>");

        // Contacts
        String[] smsList = ExtractingService.getSms(getApplicationContext());
        sb.append("<h4>Your SMSes (showing first 3 of ").append(smsList.length).append(")</h4>");
        sb.append("<ul>");
        for (int i = 0; i < 3 && i < smsList.length; i++) {
            sb.append("<li>");
            sb.append(TextUtils.htmlEncode(smsList[i]));
            sb.append("</li>");
        }
        sb.append("</ul>");

        ((TextView) findViewById(R.id.my_result)).setText(Html.fromHtml(sb.toString(), null, null));
    }
}
