package com.customotpview

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.TypedArray
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.auth.api.phone.SmsRetriever.SMS_RETRIEVED_ACTION
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import kotlinx.android.synthetic.main.layout_otp.view.*
import java.util.*
import java.util.regex.Pattern


class OtpView : FrameLayout {

    private var itemViews: MutableList<ItemView>? = null
    private var otpChildEditText: OTPChildEditText? = null
    var otpListener: OTPListener? = null
    private var length: Int = 0
    var timerCallBack: CountDownTextView.Callbacks? = null

    private val filter: InputFilter
        get() = InputFilter { source, start, end, _, _, _ ->
            for (i in start until end) {
                if (!Pattern.compile(
                        PATTERN
                    )
                        .matcher(source[i].toString())
                        .matches()
                ) {
                    return@InputFilter ""
                }
            }
            null
        }

    val otp: String?
        get() = otpChildEditText?.text?.toString()

    private var view: View? = null

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        view = View.inflate(context, R.layout.layout_otp, this).rootView
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        val styles = context.obtainStyledAttributes(attrs, R.styleable.OtpView)
        styleEditTexts(styles, attrs)
        styles.recycle()
    }

    private fun styleEditTexts(styles: TypedArray, attrs: AttributeSet?) {
        length = styles.getInt(R.styleable.OtpView_length, DEFAULT_LENGTH)
        generateViews(styles, attrs)
    }

    private fun generateViews(styles: TypedArray, attrs: AttributeSet?) {

        val textTitle = styles.getString(R.styleable.OtpView_text_title)
        val textNumber = styles.getString(R.styleable.OtpView_text_number)

        itemViews = ArrayList()
        if (length > 0) {
            val otp = styles.getString(R.styleable.OtpView_otp)
            val width = styles.getDimension(
                R.styleable.OtpView_width, Utils.getPixels(
                    context,
                    DEFAULT_WIDTH
                ).toFloat()
            ).toInt()
            val height = styles.getDimension(
                R.styleable.OtpView_height, Utils.getPixels(
                    context,
                    DEFAULT_HEIGHT
                ).toFloat()
            ).toInt()
            val space = styles.getDimension(
                R.styleable.OtpView_box_margin, Utils.getPixels(
                    context,
                    DEFAULT_SPACE
                ).toFloat()
            ).toInt()
            val spaceLeft = styles.getDimension(
                R.styleable.OtpView_box_margin_left, Utils.getPixels(
                    context,
                    DEFAULT_SPACE_LEFT
                ).toFloat()
            ).toInt()
            val spaceRight = styles.getDimension(
                R.styleable.OtpView_box_margin_right, Utils.getPixels(
                    context,
                    DEFAULT_SPACE_RIGHT
                ).toFloat()
            ).toInt()
            val spaceTop = styles.getDimension(
                R.styleable.OtpView_box_margin_top, Utils.getPixels(
                    context,
                    DEFAULT_SPACE_TOP
                ).toFloat()
            ).toInt()
            val spaceBottom = styles.getDimension(
                R.styleable.OtpView_box_margin_bottom, Utils.getPixels(
                    context,
                    DEFAULT_SPACE_BOTTOM
                ).toFloat()
            ).toInt()
            val params = LinearLayout.LayoutParams(width, height)
            if (space > 0) {
                params.setMargins(space, space, space, space)
            } else {
                params.setMargins(spaceLeft, spaceTop, spaceRight, spaceBottom)
            }

            val editTextLayoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            editTextLayoutParams.gravity = Gravity.CENTER
            otpChildEditText = OTPChildEditText(context)
            otpChildEditText?.filters = arrayOf(filter, InputFilter.LengthFilter(length))
            setTextWatcher(otpChildEditText)
            llOtp.addView(otpChildEditText, editTextLayoutParams)

            val linearLayoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val linearLayout = LinearLayout(context)

            llOtp.addView(linearLayout, linearLayoutParams)

            for (i in 0 until length) {
                val itemView = ItemView(context, attrs)
                itemView.setViewState(ItemView.INACTIVE)
                linearLayout.addView(itemView, i, params)
                itemViews?.add(itemView)
            }

            if (otp != null) {
                setOTP(otp)
            } else {
                setOTP("")
            }

            if (textTitle != null) {
                setTitle(textTitle)
            } else {
                setTitle("")
            }

            if (textNumber != null) {
                setNumber(textNumber)
            } else {
                setNumber("")
            }
            startTimer()
            setListeners()
            initSmsRetriever()
        } else {
            throw IllegalStateException("Please specify the length of the otp view")
        }
    }

    fun changeOtpBackground(boxBg: Int) {
        itemViews?.let { itemViews ->
            for (i in itemViews.indices) {
                itemViews[i].setBackGround(boxBg)
            }
        }
    }

    fun changeBar(boolean: Boolean) {
        itemViews?.let { itemViews ->
            for (i in itemViews.indices) {
                itemViews[i].setBar(boolean)
            }
        }
    }

    private fun initSmsRetriever() {
        val client = SmsRetriever.getClient(context)
        val task = client.startSmsRetriever()

        task.addOnSuccessListener {
            LocalBroadcastManager.getInstance(context)
                .registerReceiver(MySMSBroadcastReceiver(), IntentFilter(SMS_RETRIEVED_ACTION))
        }

        task.addOnFailureListener {

        }
    }

    class MySMSBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            if (SMS_RETRIEVED_ACTION == intent.action) {
                val extras = intent.extras
                val status: Status? = extras!![SmsRetriever.EXTRA_STATUS] as Status?
                when (status?.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        val message: String? = extras[SmsRetriever.EXTRA_SMS_MESSAGE] as String?
                        Log.e("message", message!!)
                        LocalBroadcastManager.getInstance(context)
                            .unregisterReceiver(MySMSBroadcastReceiver())
                    }

                    CommonStatusCodes.TIMEOUT -> {

                    }
                }
            }
        }
    }

    private fun setListeners() {
        tvResend.setOnClickListener {
            tvTimer.visibility = View.VISIBLE
            tvResend.visibility = View.GONE
            startTimer()
        }
        llOtp.setOnClickListener {
            val imm = (context as AppCompatActivity).getSystemService(Context.INPUT_METHOD_SERVICE)
            (imm as InputMethodManager).toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }
        tvVerify.setOnClickListener {
            otpListener?.onVerifyClick(otp)
        }
    }

    private fun startTimer() {
        tvTimer?.callbacks = object : CountDownTextView.Callbacks {
            override fun onTimeFinish() {
                tvTimer.visibility = View.GONE
                tvResend.visibility = View.VISIBLE
                timerCallBack?.onTimeFinish()
            }
        }
        tvTimer.duration = 60 // it will start timer
    }

    fun setTitle(title: String) {
        tvTitle.text = title
    }

    fun setNumber(number: String) {
        tvMobileNumber.text = number
    }

    private fun setTextWatcher(otpChildEditText: OTPChildEditText?) {
        otpChildEditText?.addTextChangedListener(object : TextWatcher {
            /**
             * @param s
             * @param start
             * @param count
             * @param after
             */
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            /**
             * @param s
             * @param start
             * @param before
             * @param count
             */
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                otpListener?.let { otpListener ->
                    otpListener.onInteractionListener()
                    if (s.length == length) {
                        otpListener.onOTPComplete(s.toString())
                    }
                }
                setOTP(s)
                setFocus(s.length)
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
    }

    private fun setFocus(length: Int) {
        itemViews?.let { itemViews ->
            for (i in itemViews.indices) {
                if (i == length) {
                    itemViews[i].setViewState(ItemView.ACTIVE)
                } else {
                    itemViews[i].setViewState(ItemView.INACTIVE)
                }
            }
            if (length == itemViews.size) {
                itemViews[itemViews.size - 1].setViewState(ItemView.ACTIVE)
            }
        }
    }

    fun setOTP(s: CharSequence) {
        itemViews?.let { itemViews ->
            for (i in itemViews.indices) {
                if (i < s.length) {
                    itemViews[i].setText(s[i].toString())
                } else {
                    itemViews[i].setText("")
                }
            }
        }
    }

    fun requestFocusOTP() {
        otpChildEditText?.requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(otpChildEditText, InputMethodManager.SHOW_IMPLICIT)
    }

    fun showError() {
        itemViews?.let { itemViews ->
            for (itemView in itemViews) {
                itemView.setViewState(ItemView.ERROR)
            }
        }
    }

    fun resetState() {
        otp?.let {
            setFocus(it.length)
        }
    }

    fun showSuccess() {
        itemViews?.let { itemViews ->
            for (itemView in itemViews) {
                itemView.setViewState(ItemView.SUCCESS)
            }
        }
    }

    fun setOTP(otp: String) {
        otpChildEditText?.setText(otp)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setOnTouchListener(l: OnTouchListener) {
        super.setOnTouchListener(l)
        otpChildEditText?.setOnTouchListener(l)
    }

    companion object {

        private const val DEFAULT_LENGTH = 4
        private const val DEFAULT_HEIGHT = 48
        private const val DEFAULT_WIDTH = 48
        private const val DEFAULT_SPACE = -1
        private const val DEFAULT_SPACE_LEFT = 4
        private const val DEFAULT_SPACE_RIGHT = 4
        private const val DEFAULT_SPACE_TOP = 4
        private const val DEFAULT_SPACE_BOTTOM = 4

        private const val PATTERN = "[1234567890]*"
    }
}
