package com.postjma.pranks.fakecallscreator;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FakeCallsActivity extends AppCompatActivity {
    private TextView mPhone;
    private TextView mDuration;
    private TextView mDate;
    private TextView mType;
    private Context mThis;

    private String getText(TextView textView) {
        String ret = null;
        try {
            ret = textView.getText().toString();
        } catch (Exception e) {
            // ignore
        }
        return ret;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_calls);
        mThis = this;

        mPhone = (TextView) findViewById(R.id.textView2);
        mDuration = (TextView) findViewById(R.id.textView4);
        mDate = (TextView) findViewById(R.id.textView6);
        mType = (TextView) findViewById(R.id.textView8);
        Button mButton = (Button) findViewById(R.id.button);

        // Set up the user interaction to manually show or hide the system UI.
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNo = getText(mPhone);
                String durationstr = getText(mDuration);
                String datestr = getText(mDate);
                String typestr = getText(mType);
                int duration = Integer.parseInt(durationstr);
                int typeint;
                try {
                    // if it's a raw integer, let the user specifiy the low level value directly
                    typeint = Integer.parseInt(typestr);
                } catch (Exception e) {
                    // else parse the name
                    String typer = typestr.trim().toLowerCase();
                    switch (typer) {
                        case "incoming":
                            typeint = CallLog.Calls.INCOMING_TYPE;
                            break;
                        case "outgoing":
                            typeint = CallLog.Calls.OUTGOING_TYPE;
                            break;
                        case "missed":
                            typeint = CallLog.Calls.MISSED_TYPE;
                            break;
                        case "voicemail":
                            typeint = CallLog.Calls.VOICEMAIL_TYPE;
                            break;
                        case "rejected":
                            typeint = CallLog.Calls.REJECTED_TYPE;
                            break;
                        case "blocked":
                            typeint = CallLog.Calls.BLOCKED_TYPE;
                            break;
                        case "external":
                            typeint = CallLog.Calls.ANSWERED_EXTERNALLY_TYPE;
                            break;
                        default:
                            // default to missed calls
                            typeint = CallLog.Calls.MISSED_TYPE;
                            break;
                    }
                }

                ContentValues cv = new ContentValues();
                cv.put(CallLog.Calls.NUMBER, phoneNo);
                cv.put(CallLog.Calls.DURATION, duration);
                cv.put(CallLog.Calls.NEW, 1);
                try {
                    if (datestr.trim().toUpperCase().equals("NOW")) {
                        cv.put(CallLog.Calls.DATE, System.currentTimeMillis());
                    } else {
                        cv.put(CallLog.Calls.DATE, datestr);
                    }
                } catch (Exception e2) {
                    // ignore
                    cv.put(CallLog.Calls.DATE, datestr);
                }
                cv.put(CallLog.Calls.TYPE, typeint);
                cv.put(CallLog.Calls.CACHED_NAME, "");
                cv.put(CallLog.Calls.CACHED_NUMBER_TYPE, 0);
                cv.put(CallLog.Calls.CACHED_NUMBER_LABEL, "");

                try {
                    getContentResolver().insert(CallLog.Calls.CONTENT_URI, cv);
                } catch (SecurityException se) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(mThis);
                    alertDialog.setMessage("Security Exception received.");
                    alertDialog.setTitle("Error");
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    alertDialog.setCancelable(false);
                    alertDialog.create().show();
                }

                mPhone.setText("");
                mDuration.setText("");
                mDate.setText("");
                mType.setText("");
            }
        });

    }
}
