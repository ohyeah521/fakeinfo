package org.gosky.fakeinfo

import android.Manifest
import android.annotation.TargetApi
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.CallLog
import android.provider.Telephony
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.sch.fakecontacts.model.generator.ContactGenerator
import com.sch.fakecontacts.model.generator.GenerationOptions
import com.sch.fakecontacts.ui.dialogs.ProgressDialogController
import com.tbruyelle.rxpermissions.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*
import java.text.ParseException
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
        if (Build.VERSION.SDK_INT >= 20) {
            if (packageName != getSystemDefaultSms()) {
                setDefaultSms(packageName)
            }
        }

    }


    fun generateContacts() {
        val builder = GenerationOptions.Builder()
                .setContactCount(et_contact_count.text.toString().toInt())
                .setOverwriteExisting(true)
//        builder.withEmails()
        builder.withPhones()
//        builder.withAddresses()
//        builder.withAvatars()
//        builder.withEvents()
        GenerateContactsTask(this, builder.build()).execute()
    }

    fun generateCallLogs() {
        val cv = ContentValues()
        cv.put(CallLog.Calls.NUMBER, getRandomContact())
        cv.put(CallLog.Calls.DURATION, Random().nextInt(60))
        cv.put(CallLog.Calls.NEW, 1)
        cv.put(CallLog.Calls.DATE, System.currentTimeMillis() - Random().nextInt(1000 * 1000))
        cv.put(CallLog.Calls.TYPE, callType[Random().nextInt(callType.size)])
        cv.put(CallLog.Calls.CACHED_NAME, "")
        cv.put(CallLog.Calls.CACHED_NUMBER_TYPE, 0)
        cv.put(CallLog.Calls.CACHED_NUMBER_LABEL, "")
        contentResolver.insert(CallLog.Calls.CONTENT_URI, cv)
    }


    fun generateSms() {

        if (Build.VERSION.SDK_INT >= 20) {
            if (packageName != getSystemDefaultSms()) {
                setDefaultSms(packageName)
            }
        }
        val values = ContentValues()

        try {
            values.put("date", System.currentTimeMillis() - Random().nextInt(1000 * 1000))
            values.put("address", getRandomContact())
            values.put("body", GeneratorSmsContent.random())
            values.put("type", "2")
            values.put("read", "1")//"1"means has read ,1表示已读

            contentResolver.insert(Uri.parse("content://sms/inbox"), values)
//            Toast.makeText(this@MainActivity, "短信插入成功，部分手机的收件箱有延迟，请等候", Toast.LENGTH_SHORT).show()

        } catch (e: ParseException) {
            Toast.makeText(this@MainActivity, "时间输入异常，请重新尝试", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getRandomContact(): String? {
        if (Random().nextInt(100) > 50) {
            val list = ContactUtils.getAllContacts(this).entries.map { it.key }
            if (list.isNotEmpty())
                return list[Random().nextInt(list.size)]
        }
        return getRandomTelNumber()
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun getSystemDefaultSms(): String {
        return Telephony.Sms.getDefaultSmsPackage(this)
    }

    fun setDefaultSms(packageName: String) {
        val intent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, packageName)
        startActivity(intent)
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
            for (i in 1..et_call_logs_count.text.toString().toInt()) {
                generateCallLogs()
            }
            for (i in 1..et_sms_count.text.toString().toInt()) {
                generateSms()
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
