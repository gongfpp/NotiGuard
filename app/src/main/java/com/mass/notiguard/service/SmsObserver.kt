package com.mass.notiguard.service

import android.content.ContentResolver
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.provider.Settings
import com.mass.notiguard.NotiGuardApplication
import com.mass.notiguard.domain.AlertEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** 监听短信收件箱变化（需 READ_SMS 权限） */
class SmsObserver(handler: Handler, private val context: Context) : ContentObserver(handler) {

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        super.onChange(selfChange, uri)
        uri ?: return
        runCatching {
            val resolver: ContentResolver = context.contentResolver
            resolver.query(uri, arrayOf("address", "body"), null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val address =
                        cursor.getString(cursor.getColumnIndexOrThrow("address")).orEmpty()
                    val body = cursor.getString(cursor.getColumnIndexOrThrow("body")).orEmpty()
                    val engine = (context.applicationContext as NotiGuardApplication).alertEngine
                    CoroutineScope(Dispatchers.IO).launch {
                        engine.process("sms", "短信 · $address", address, body)
                    }
                }
            }
        }
    }
}
