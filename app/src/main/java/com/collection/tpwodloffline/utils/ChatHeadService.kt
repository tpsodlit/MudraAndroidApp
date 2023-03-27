package com.collection.tpwodloffline.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.IBinder
import android.os.Parcelable
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.collection.tpwodloffline.R
import com.collection.tpwodloffline.activity.ConsumerNavigation
import com.collection.tpwodloffline.activity.MainActivity
import com.collection.tpwodloffline.utils.SharedPreferenceClass

import jp.co.recruit_lifestyle.android.floatingview.FloatingViewListener
import jp.co.recruit_lifestyle.android.floatingview.FloatingViewManager
import java.util.ArrayList
import java.util.HashMap
import com.google.gson.reflect.TypeToken

import com.google.gson.Gson
import java.lang.reflect.Type


class ChatHeadService : Service(), FloatingViewListener {
    /**
     * FloatingViewManager
     */
    private var mFloatingViewManager: FloatingViewManager? = null
    private var sharedPreferenceClass: SharedPreferenceClass? = null

    /**
     * {@inheritDoc}
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        sharedPreferenceClass = SharedPreferenceClass(this@ChatHeadService)
        if (mFloatingViewManager != null) {
            return START_STICKY
        }
        val metrics = DisplayMetrics()
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(metrics)
        val inflater = LayoutInflater.from(this)
        val iconView = inflater.inflate(R.layout.widget_chathead, null, false) as ImageView
        iconView.setOnClickListener {

        }
        mFloatingViewManager = FloatingViewManager(this, this@ChatHeadService)
        mFloatingViewManager?.setFixedTrashIconImage(R.drawable.ic_trash_fixed)
        mFloatingViewManager?.setActionTrashIconImage(R.drawable.ic_trash_action)
        mFloatingViewManager?.setSafeInsetRect(
            intent.getParcelableExtra<Parcelable>(
                EXTRA_CUTOUT_SAFE_AREA
            ) as Rect?
        )
        val options: FloatingViewManager.Options = FloatingViewManager.Options()
        options.overMargin = (16 * metrics.density).toInt()
        mFloatingViewManager?.addViewToWindow(iconView, options)

        iconView.setOnTouchListener { v, event ->
            val floatfrom: String? = sharedPreferenceClass?.getValue_string("floatfrom")
            var position: Int? = sharedPreferenceClass?.getValue_int("position")
            var distance: String? = sharedPreferenceClass?.getValue_string("distance")
            var data_list: String? = sharedPreferenceClass?.getValue_string("data_list")

            // Toast.makeText(this@ChatHeadService, "onTouchEvent", Toast.LENGTH_SHORT).show()
            if (mFloatingViewManager != null) {
                mFloatingViewManager!!.removeAllViewToWindow()
                mFloatingViewManager = null
            }
            //Toast.makeText(this@ChatHeadService, "on Click Event", Toast.LENGTH_SHORT).show()
            if (floatfrom.equals("adapter")) {
                sharedPreferenceClass?.setValue_string("from_details", "float2")

                startActivity(
                    Intent(this@ChatHeadService, ConsumerNavigation::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra("dtname", "dtname")
                        .putExtra("from", "home")
                )
            } else if (floatfrom.equals("search")) {


                startActivity(
                    Intent(this@ChatHeadService, ConsumerNavigation::class.java)
                        .putExtra("dtname", sharedPreferenceClass?.getValue_string("dtname"))
                        .putExtra("from", "search")
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )


            }
            true
        }

       /* iconView.setOnClickListener {

            if (mFloatingViewManager != null) {
                mFloatingViewManager!!.removeAllViewToWindow()
                mFloatingViewManager = null
            }
            //Toast.makeText(this@ChatHeadService, "on Click Event", Toast.LENGTH_SHORT).show()
            startActivity(
                Intent(
                    this@ChatHeadService,
                    MainActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )

        }*/

        // 常駐起動
        // startForeground(NOTIFICATION_ID, createNotification(this))
        return START_REDELIVER_INTENT
    }

    /**
     * {@inheritDoc}
     */
    override fun onDestroy() {
        destroy()
        super.onDestroy()
    }



    /**
     * {@inheritDoc}
     */
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    /**
     * {@inheritDoc}
     */
    override fun onFinishFloatingView() {
        stopSelf()
        // Log.d(TAG, getString(R.string.finish_deleted))
    }


    override fun onTouchFinished(isFinishing: Boolean, x: Int, y: Int) {
        if (isFinishing) {
            // Log.d(TAG, getString(R.string.deleted_soon))
        } else {
            //  Log.d(TAG, getString(R.string.touch_finished_position, x, y))
        }
    }

    /**
     * Viewを破棄します。
     */
    private fun destroy() {
        if (mFloatingViewManager != null) {
            mFloatingViewManager!!.removeAllViewToWindow()
            mFloatingViewManager = null
        }
    }

    fun closeFloat(){
        if (mFloatingViewManager != null) {
            mFloatingViewManager!!.removeAllViewToWindow()
            mFloatingViewManager = null
        }
    }
    companion object {
        /**
         * デバッグログ用のタグ
         */
        private const val TAG = "ChatHeadService"

        /**
         * Intent key (Cutout safe area)
         */
        const val EXTRA_CUTOUT_SAFE_AREA = "cutout_safe_area"

        /**
         * 通知ID
         */
        private const val NOTIFICATION_ID = 9083150

        /**
         * 通知を表示します。
         * クリック時のアクションはありません。
         */

    }
}