package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        motion_layout.transitionToState(R.id.end)

        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager

        notificationManager.cancelAll()

        val downloadId = intent.getLongExtra(DOWNLOAD_ID, -1)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query()
        query.setFilterById(downloadId)

        val cursor = downloadManager.query(query)

        if (cursor.moveToFirst()) {
            val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
            val title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE))

            file_name_text.text = title
            when (status) {
                DownloadManager.STATUS_SUCCESSFUL -> {
                    status_tv.text = "Success"
                    status_tv.setTextColor(getColor(R.color.colorPrimaryDark))
                }
                else -> {
                    status_tv.text = "Fail"
                    status_tv.setTextColor(getColor(R.color.red))
                }
            }
        }

        ok_button.setOnClickListener {
            motion_layout.transitionToState(R.id.start)
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
            finish()
        }
    }

}
