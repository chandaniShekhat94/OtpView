package com.otpviewcontrol

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.customotpview.CountDownTextView
import com.customotpview.OTPListener
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import com.otpviewcontrol.utility.isValidEmail
import com.otpviewcontrol.utility.isValidPhoneNumber
import kotlinx.android.synthetic.main.activity_otp.*
import kotlinx.android.synthetic.main.layout_toolbar.*

class OtpActivity : AppCompatActivity() {

    companion object {
        const val INPUT = "input"
        const val SHAPE = "shape"
    }

    var stringExtra: String? = null
    var selectedOption: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        if (intent != null && intent.hasExtra(INPUT)) {
            stringExtra = intent.getStringExtra(INPUT)
        }

        if (intent != null && intent.hasExtra(SHAPE)) {
            selectedOption = intent.getStringExtra(SHAPE)
        }

        ivBack.visibility = View.GONE

        tvTitle.text = getString(R.string.str_verify)
        ivBack.setOnClickListener {
            finish()
        }

        if (stringExtra!!.isValidEmail()) {
            otpView?.setTitle(getString(R.string.str_msg_1))
        } else if (stringExtra!!.isValidPhoneNumber()) {
            otpView?.setTitle(getString(R.string.str_msg_2))
        }
        otpView?.setNumber(stringExtra!!)
        otpView?.requestFocusOTP()

        when (selectedOption) {
            getString(R.string.line) -> {
                otpView?.changeBar(true)
                otpView?.changeOtpBackground(ResourcesCompat.getColor(resources, R.color.ov_transparent, null))
            }
            getString(R.string.square) -> {
                otpView?.changeOtpBackground(R.drawable.rounded_white_bg_square)
            }
            getString(R.string.circle) -> {
                otpView?.changeOtpBackground(R.drawable.rounded_white_bg_circle)
            }
        }

        otpView?.otpListener = object : OTPListener {
            override fun onInteractionListener() {

            }

            override fun onOTPComplete(otp: String) {
                Snackbar.make(
                    otpView, getString(R.string.str_otp_is, otp),
                    LENGTH_LONG
                ).show()
            }

            override fun onVerifyClick(otp: String?) {
                Snackbar.make(
                    otpView, getString(R.string.str_verify_click),
                    LENGTH_LONG
                ).show()
            }

        }

        otpView?.timerCallBack = object : CountDownTextView.Callbacks {
            override fun onTimeFinish() {
                Snackbar.make(
                    otpView, getString(R.string.str_timer_end),
                    LENGTH_LONG
                ).show()
            }
        }

    }
}
