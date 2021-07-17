package com.vj.butterfly.viewmodels

import android.app.Application
import android.content.res.ColorStateList
import android.content.res.Resources
import android.os.Build
import android.telephony.SmsManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.vj.butterfly.R
import java.util.*


class LoginViewModel(application: Application) : AndroidViewModel(application) {
    //var sPhoneNumber = ObservableField<String>()
    var phoneNumber = MutableLiveData<String>()
    var isButtonEnabled = MutableLiveData<Boolean>()
    var loginButtonBackground = ObservableField<ColorStateList>()
    var askSMSPermission = MutableLiveData<Boolean>()
    private lateinit var resources: Resources
    var otp : String = ""

    fun registerResources(resources: Resources) {
        this.resources = resources
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun validationLogin() {
        /*if( phoneNumber.value == decryptNumber(resources.getString(R.string.vj))){
            Toast.makeText(getApplication<Application>().applicationContext, resources.getString(R.string.not_registered), Toast.LENGTH_LONG).show()
        }*/
        when (encryptPhoneNumber()) {
            getApplication<Application>().applicationContext.resources.getString(R.string.vj),
            getApplication<Application>().applicationContext.resources.getString(R.string.vjl) -> {
                askSMSPermission.postValue(true)
            }
            else -> {
                Toast.makeText(
                    getApplication<Application>().applicationContext,
                    resources.getString(R.string.not_registered),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        /*if (decryptedNumber == getApplication<Application>().applicationContext.resources.getString(R.string.vj)) {

        } else if (decryptedNumber == getApplication<Application>().applicationContext.resources.getString(R.string.vjl)) {

        }*/
    }

    /*private fun decryptNumber(): String {
        val bytes = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Base64.getDecoder().decode(phoneNumber.value)
        } else {
            android.util.Base64.decode(phoneNumber.value, android.util.Base64.DEFAULT)
        }
        //val bytes = android.util.Base64.decode(stringToDecrypt, android.util.Base64.DEFAULT)
        return String(bytes)
    }
*/
    @RequiresApi(Build.VERSION_CODES.O)
    private fun encryptPhoneNumber(): String {
        val bytes: ByteArray = phoneNumber.value.toString().toByteArray()
        //val encodedPhoneNumber = android.util.Base64.encode(bytes, android.util.Base64.DEFAULT)

        //val bytes1 = Base64.getDecoder().decode(string)
        return Base64.getEncoder().encodeToString(bytes)
    }

    /*fun encryptPhoneNumber(): String{
        val bytes: ByteArray = phoneNumber.toString().toByteArray()
        val string = Base64.getEncoder().encodeToString(bytes)

        val bytes1 = Base64.getDecoder().decode(string)
        return String(bytes1)
    }*/

    fun setLoginButtonBackgroundTintColor(colorStateList: ColorStateList) {
        loginButtonBackground.set(colorStateList)
    }

    fun setLoginButtonEnabled(value: Boolean) {
        isButtonEnabled.postValue(value)
    }

    private fun sendSMS(msg: String?) {
        try {
            val smsManager: SmsManager = SmsManager.getDefault() //deprecated on 31
            /*val smsManager: SmsManager =
                getApplication<Application>().applicationContext.getSystemService(SmsManager::class.java)*/
            smsManager.sendTextMessage(phoneNumber.value.toString(), null, msg, null, null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun generateRandomOTPAndSendSMS(message: String) {
        val random = Random()
        otp = String.format("%04d", random.nextInt(10000))

        startSmsListener(message + otp +System.lineSeparator()+"D4dcLtQiB1F")
        //sendSMS(message + otp)
    }

    private fun startSmsListener(otpMessage : String){
        val client = SmsRetriever.getClient(getApplication<Application>().applicationContext /* context */)
        val task = client.startSmsRetriever()
        // Listen for success/failure of the start Task. If in a background thread, this
        // can be made blocking using Tasks.await(task, [timeout]);
        task.addOnSuccessListener {
            sendSMS(otpMessage)
            // Successfully started retriever, expect broadcast intent
            // ...
            //otp_txt.text = "Waiting for the OTP"
        }

        task.addOnFailureListener {
            // Failed to start retriever, inspect Exception for more details
            // ...
            //otp_txt.text = "Cannot Start SMS Retriever"
        }
    }
}