# OtpView
A custom control to enter a X number of digits depending on the set pin length.

<p align="center">
<img alt="OtpView" src="https://github.com/chandaniShekhat94/OtpViewControl/blob/master/otpview.gif" width = "300" height = "633"  class="center"/>
</p>

# Use Case
- Used in case of authentication(Mobile/Email verification in the application).

Download
--------

Grab via Maven:
```xml
<dependency>
  <groupId>com.customotpview</groupId>
  <artifactId>otpview</artifactId>
  <version>1.0</version>
  <type>pom</type>
</dependency>
```
or Gradle:
```groovy
implementation 'com.customotpview:otpview:1.0'
```

# How it works?

## Just add the following to your xml design to show the otpview

``` xml
.....
  <com.customotpview.OtpView
            android:id="@+id/otp_view"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textColor="@color/red"
            app:bar_active_color="@android:color/holo_green_light"
            app:bar_enabled="false"
            app:bar_error_color="@color/red"
            app:bar_height="1.5dp"
            app:bar_inactive_color="@android:color/holo_red_light"
            app:bar_margin_bottom="0dp"
            app:bar_margin_left="2dp"
            app:bar_margin_right="2dp"
            app:bar_success_color="@android:color/holo_green_light"
            app:box_margin="0dp"
            app:height="40dp"
            app:hide_otp="false"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:length="6"
            app:otp=""
            app:otp_text_size="20dp"
            app:width="40dp"
            app:otp_box_background="@drawable/rounded_white_bg"
            app:text_title="Please enter your otp here to verify your mobile number"
            app:text_number="+91 9999999999" />
.....
```

## To get a callback when the user enters the otp make use of OTPListener Interface

```
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

```

## To get a callback when the timer gets end make use of timerCallBack Interface

```
  otpView?.timerCallBack = object : CountDownTextView.Callbacks {
            override fun onTimeFinish() {
                Toast.makeText(this@OtpActivity, "Timer gets ended", Toast.LENGTH_SHORT).show()
            }
        }
```

# Theming

There are several theming options available through XML attributes which you can use to completely change the look-and-feel of this view to match the theme of your app.


# OtpView

| Property | Description |
| :---     | :---   |
| android:textColor	| sets the color of the otp text |
| app:otp	|  sets the otp in the otp view |
| app:length |  sets the no of otp box in the otp view |
| app:otp_text_size	|  sets the otp text size in the otp view |
| app:text_typeface	|  sets the otp text typeface in the otp view |
| app:hide_otp	|  sets if the otp entered is to be shown to the user |
| app:hide_otp_drawable	|  replaces the pin bullet which is shown to the user when hide_otp is enabled |
| app:height	|  sets the height of each box inside the otp view |
| app:width		|  sets the width of each box inside the otp view |
| app:box_margin	|  sets the space between each box in otp view |
| app:box_margin_left		|  sets the left space between each box in otp view |
| app:box_margin_right	|  sets the right space between each box in otp view |
| app:box_margin_top		|  sets the top space of each box in otp view |
| app:box_margin_bottom	|  sets the bottom space of each box in otp view |
| app:bar_enabled		|  shows a bar below each otp box to the user |
| app:bar_height		|  sets the bar height |
| app:bar_margin		|  sets the bar margin within each box in otp view |
| app:bar_margin_left		|  sets the bar left margin within each box in otp view |
| app:bar_margin_right	|  sets the bar right margin within each box in otp view |
| app:bar_margin_top		|  sets the bar top margin within each box in otp view |
| app:bar_margin_bottom		|  sets the bar bottom margin within each box in otp view |
| app:bar_active_color	|  sets the bar color when the cursor is on the box in otp view |
| app:bar_inactive_color	|  sets the bar color when the cursor is not on the box in otp view |
| app:bar_error_color		 |  sets the bar color for error state in otp view |
| app:bar_success_color		|  sets the bar color for success state in otp view |
| app:otp_box_background	|  sets the box background in otp view |
| app:otp_box_background_active		|  sets the box background when the cursor is on the box in otp view |
| app:otp_box_background_inactive	|  sets the box background when the cursor is not on the box in otp view |
| app:otp_box_background_error	| sets the box background for error state in otp view |
| app:otp_box_background_success	|  sets the box background for success state in otp view |
| app:text_title   |  sets title text |
| app:text_number   |  sets mobile number |

# License

```
Copyright [2020] [Chandani Shekhat]

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
  ```

