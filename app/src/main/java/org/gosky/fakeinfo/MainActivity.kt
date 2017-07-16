package org.gosky.fakeinfo

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.provider.CallLog
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.sch.fakecontacts.model.generator.ContactGenerator
import com.sch.fakecontacts.model.generator.GenerationOptions
import com.sch.fakecontacts.ui.dialogs.ProgressDialogController
import com.tbruyelle.rxpermissions.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    val callType = arrayOf(CallLog.Calls.INCOMING_TYPE,
            CallLog.Calls.OUTGOING_TYPE, CallLog.Calls.MISSED_TYPE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_chuowo.setOnClickListener {
            RxPermissions.getInstance(this)
                    .request(Manifest.permission.WRITE_CONTACTS, Manifest.permission.WRITE_CALL_LOG)
                    .subscribe {
                        generateContacts()
                    }
        }
    }


    fun generateContacts() {
        val builder = GenerationOptions.Builder()
                .setContactCount(10)
                .setOverwriteExisting(true)
        builder.withEmails()
        builder.withPhones()
        builder.withAddresses()
        builder.withAvatars()
        builder.withEvents()
        GenerateContactsTask(this, builder.build()).execute()
    }

    fun generateCallLogs() {
        val cv = ContentValues()
        cv.put(CallLog.Calls.NUMBER, getRandomTelNumber())
        cv.put(CallLog.Calls.DURATION, Random().nextInt(60))
        cv.put(CallLog.Calls.NEW, 1)
        cv.put(CallLog.Calls.DATE, System.currentTimeMillis())
        cv.put(CallLog.Calls.TYPE, callType[Random().nextInt(callType.size)])
        cv.put(CallLog.Calls.CACHED_NAME, "")
        cv.put(CallLog.Calls.CACHED_NUMBER_TYPE, 0)
        cv.put(CallLog.Calls.CACHED_NUMBER_LABEL, "")
        contentResolver.insert(CallLog.Calls.CONTENT_URI, cv)
    }

    private inner class GenerateContactsTask constructor(activity: FragmentActivity, private val options: GenerationOptions) : AsyncTask<Void, Void, Void>() {
        private val context: Context
        private val progressDialogController: ProgressDialogController

        init {
            this.context = activity
            progressDialogController = ProgressDialogController(activity, activity.supportFragmentManager)
        }

        override fun onPreExecute() {
            super.onPreExecute()
            progressDialogController.show(com.sch.fakecontacts.R.string.message_please_wait)
        }

        override fun doInBackground(vararg params: Void): Void? {
            ContactGenerator(context).generate(options)
            for (i in 1..10){
                generateCallLogs()
            }

            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            progressDialogController.dismiss()
            Toast.makeText(context, com.sch.fakecontacts.R.string.message_done, Toast.LENGTH_SHORT).show()
        }
    }
}
