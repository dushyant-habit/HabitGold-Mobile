package com.habit.gold.payments.juspay

import android.content.Intent
import java.lang.ref.WeakReference

object JuspayActivityForwarder {
    private var weakHelper: WeakReference<JuspayCheckoutHelper>? = null

    fun attach(helper: JuspayCheckoutHelper) {
        weakHelper = WeakReference(helper)
    }

    fun detach(helper: JuspayCheckoutHelper) {
        if (weakHelper?.get() === helper) {
            weakHelper = null
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        weakHelper?.get()?.onActivityResult(requestCode, resultCode, data)
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        weakHelper?.get()?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
