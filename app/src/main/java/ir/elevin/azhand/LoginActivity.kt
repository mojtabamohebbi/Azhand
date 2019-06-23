package ir.elevin.azhand

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.livedata.liveDataObject
import com.github.kittinunf.fuel.livedata.liveDataResponse
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import ir.elevin.azhand.database.DatabaseHandler
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import libs.mjn.prettydialog.PrettyDialog
import org.jetbrains.anko.doAsync
import java.util.*



@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class LoginActivity : CustomActivity() {

    enum class LoginStates{
        PHONE,
        CODE
    }

    var resultCode: Int = 0

    var code = 0
    var phone = ""
    var state = LoginStates.PHONE
    var seconds = 60

    var progressDialog: Dialog? = null

    var db: DatabaseHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        resultCode = intent.extras.getInt("result")
        db = DatabaseHandler(this)

        codeTelEt.requestFocusFromTouch()
        codeTelEt.showKeyboard(this)

        sendAccButton.setOnClickListener {
            sendSmsOrCheckCode()
        }

        sendAgainButton.setOnClickListener {
            sendSms()
            codeTelEt.setText("")
            runTimer()
        }
    }

    @SuppressLint("SetTextI18n")
    fun sendSmsOrCheckCode(){
        if (state == LoginStates.PHONE){
            Log.d("codegenerated", "$code")
            phone = codeTelEt.text.toString()
            if (!phone.isBlank() && phone.length != 11){
                YoYo.with(Techniques.Shake).playOn(codeTelEt)
            }else{
                //ready to send sms
                sendSms()
                codeTelEt.setText("")
                desTv.text = "کد تایید برای شماره $phone پیامک می شود. لطفا کد را در فیلد زیر وارد نمایید."
                sendAccButton.text = "تایید کد"
                codeTelEt.hint = "کد"
                state = LoginStates.CODE
                sendAgainButton.visibility = View.VISIBLE
                sendAgainButton.isEnabled = false
                runTimer()
            }
        }else{
            val codeTyped = codeTelEt.text.toString()
            if (codeTyped == ""+code){
                //login done
                codeTelEt.hideKeyboard(this)
                checkPhone()
            }else{
                //code was wrong
                YoYo.with(Techniques.Shake).playOn(codeTelEt)
            }
        }

    }

    override fun onPause() {
        super.onPause()
        progressDialog?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        progressDialog?.dismiss()
    }

    override fun onStop() {
        super.onStop()
        progressDialog?.dismiss()
    }

    private fun runTimer(){
        val timer = Timer()
        timer.schedule(object: TimerTask(){
            @SuppressLint("SetTextI18n")
            override fun run() {
                if (seconds > 0){
                    seconds -= 1
                    val uiScope = CoroutineScope(Dispatchers.IO)
                    uiScope.launch {
                        doAsync {
                            sendAgainButton.text = "ارسال دوباره ($seconds)"
                        }
                    }
                }else{
                    timer.purge()
                    timer.cancel()
                    seconds = 60
                    val uiScope = CoroutineScope(Dispatchers.IO)
                    uiScope.launch {
                        doAsync {
                            sendAgainButton.text = "ارسال دوباره"
                            sendAgainButton.isEnabled = true
                        }
                    }
                }
            }
        }, 1000, 1000)
    }

    private fun sendSms(){
        code = (1000..9999).shuffled().first()
        val params: List<Pair<String, Any?>> = listOf("receptor" to phone, "token" to "$code", "template" to "gr00p")
        "https://api.kavenegar.com/v1/4362695A3879324943585372326F7A493574483068413D3D/verify/lookup.json".httpPost(params).liveDataResponse().observeForever {
            Log.d("response", it.toString())
        }
    }
//
    lateinit var main: MainActivity
    private fun checkPhone(){
        progressDialog?.dismiss()
        progressDialog = progressDialog(this)
        val params: List<Pair<String, Any?>> = listOf("func" to "check_user", "phone" to phone)
        webserviceUrl.httpPost(params).liveDataObject(Account.Deserializer()).observeForever { it ->
            Log.d("response-login", it.toString())
            it?.success {
                db!!.deleteAccount()
                db!!.insertAccounts(it.id , phone)
                account = db!!.getAccount()
                Toast.makeText(this, "خوش آمدید!", Toast.LENGTH_LONG).show()
                progressDialog?.dismiss()
                setResult(resultCode)
                finish()
            }
            it?.failure {
                progressDialog?.dismiss()
                val d = PrettyDialog(this)
                d.setTitle(getString(R.string.error))
                        .setIcon(R.drawable.error)
                        .setMessage(getString(R.string.network_error))
                        .addButton(getString(R.string.try_again), R.color.colorWhite, R.color.colorRed) {
                            d.dismiss()
                            checkPhone()
                        }
                        .show()
            }
        }
    }

}




















