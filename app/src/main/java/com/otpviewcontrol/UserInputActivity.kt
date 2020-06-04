package com.otpviewcontrol

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import com.otpviewcontrol.utility.isValidEmail
import com.otpviewcontrol.utility.isValidPhoneNumber
import kotlinx.android.synthetic.main.activity_user_input.*
import kotlinx.android.synthetic.main.layout_toolbar.*

class UserInputActivity : AppCompatActivity() {

    var selectedOption: String = ""

    companion object {
        const val INPUT = "input"
        const val SHAPE = "shape"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_input)

        ivBack.visibility = View.GONE
        tvTitle.text = getString(R.string.str_user_details)

        tvVerify.setOnClickListener {
            if (etUserInput.text.toString().trim().isEmpty()) {
                Toast.makeText(this, getString(R.string.str_enter_input), Toast.LENGTH_LONG)
                    .show()
            } else if (!etUserInput.text.toString().trim().isValidEmail()
                && (!etUserInput.text.toString().trim().isValidPhoneNumber())
            ) {
                Snackbar.make(
                    etUserInput,
                    getString(R.string.str_enter_valid_input), LENGTH_LONG
                ).show()
            } else {
                val intent = Intent(this, OtpActivity::class.java)
                intent.putExtra(INPUT, etUserInput.text.toString().trim())

                val checkedRadioButtonId = rgShape.checkedRadioButtonId
                val radioButton = findViewById<RadioButton>(checkedRadioButtonId)
                if (radioButton != null) {
                    selectedOption = radioButton.text.toString()
                }

                intent.putExtra(SHAPE, selectedOption)
                startActivity(intent)
            }
        }
    }

}
