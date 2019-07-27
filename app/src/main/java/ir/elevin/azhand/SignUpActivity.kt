package ir.elevin.azhand

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.jackson.mapper
import com.github.kittinunf.fuel.livedata.liveDataObject
import com.github.kittinunf.fuel.livedata.liveDataResponse
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import com.google.gson.Gson
import com.kotlinpermissions.notNull
import ir.mjmim.woocommercehelper.enums.RequestMethod
import ir.mjmim.woocommercehelper.enums.SigningMethod
import ir.mjmim.woocommercehelper.helpers.OAuthSigner
import ir.mjmim.woocommercehelper.main.WooBuilder
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.dialog_reset_password.*
import kotlinx.android.synthetic.main.recycler_fragment.*
import libs.mjn.prettydialog.PrettyDialog
import java.util.*

class SignUpActivity : CustomActivity() {


    var isLoginViewShowing = true
    var resultCode: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        resultCode = try {
            intent.extras.getInt("result")
        }catch (e: Exception){
            0
        }

        signUpButton.setOnClickListener {
            if (isLoginViewShowing){
                isLoginViewShowing = false
                signUpLayout.visibility = View.VISIBLE
                loginLayout.visibility = View.GONE
            }else{
                signUp()
            }
        }

        loginButton.setOnClickListener {
            if (!isLoginViewShowing){
                isLoginViewShowing = true
                signUpLayout.visibility = View.GONE
                loginLayout.visibility = View.VISIBLE
            }else{
                login()
            }
        }

        resetPasswordButton.setOnClickListener {
            val d = Dialog(this)
            d.requestWindowFeature(Window.FEATURE_NO_TITLE)
            d.setContentView(R.layout.dialog_reset_password)
            d.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            d.window?.decorView!!.layoutDirection = View.LAYOUT_DIRECTION_RTL

            d.getResetLink.setOnClickListener {

                val username = d.resetUsernameEt.text.toString()
                if (username.isEmpty()){
                    YoYo.with(Techniques.Shake).playOn(d.resetUsernameEt)
                }else{
                    val progressBar = progressDialog(this)
                    val params = HashMap<String, String>().apply {
                        put("username", d.resetUsernameEt.text.toString())
                    }

                    val wooBuilder = WooBuilder().apply {
                        isHttps = false
                        baseUrl = "florals.ir/wp-json/wp/v2"
                        signing_method = SigningMethod.HMACSHA1
                        wc_key = "ck_b89b3e5cd871e50755f2d021967aa903cf2839cc"
                        wc_secret = "cs_3c6fd6cfd5399a358f6ae285848829c7cc2cae88"
                    }

                    val resultLink: String? = OAuthSigner(wooBuilder)
                            .getSignature(RequestMethod.POST, "/m_users/password", params)
                    Log.d("gwegewg33", resultLink+"--")

                    resultLink!!.httpPost().liveDataResponse().observeForever {
                        Log.d("WEGweg", it.first.data.toString(Charsets.UTF_8)+" --- "+it.toString())
                        d.dismiss()
                        progressBar.dismiss()
                        Toast.makeText(this, "لینک بازیابی به ایمیل شما ارسال شد", Toast.LENGTH_LONG).show()

                    }
                }

            }

            d.show()
        }

        backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun signUp(){
        val name : String = nameEt.text.toString()
        val family : String = familyEt.text.toString()
        var email : String = emailEt.text.toString()
        val address : String = addressEt.text.toString()
        var postalCode : String = postalCodeEt.text.toString()
        val phoneNumber : String = phoneEt.text.toString()
        val username : String = usernameSignUpEt.text.toString()
        val password : String = passwordSignUpEt.text.toString()

        when {
            name.isEmpty() -> YoYo.with(Techniques.Shake).playOn(nameEt)
            family.isEmpty() -> YoYo.with(Techniques.Shake).playOn(familyEt)
            address.isEmpty() -> YoYo.with(Techniques.Shake).playOn(addressEt)
            phoneNumber.isEmpty() -> YoYo.with(Techniques.Shake).playOn(phoneEt)
            username.isEmpty() -> YoYo.with(Techniques.Shake).playOn(usernameSignUpEt)
            password.isEmpty() -> YoYo.with(Techniques.Shake).playOn(passwordSignUpEt)
            else -> {
                if (email.isEmpty()){
                    email = "${UUID(100000, 1000000)}@florals.ir"
                }
                if (postalCode.isEmpty()){
                    postalCode = "1234567890"
                }

                val data = MakeUser(customer(0, email, name, family, username, password, billing_address(name, family, "", address, "", "مشهد", "خراسان رضوی", postalCode, "ایران", email, phoneNumber), shipping_address(name, family, "", address, "", "مشهد", "خراسان رضوی", postalCode, "ایران")))

                val gson = Gson()
                val jsonString = gson.toJson(data)

                val progressDialog = progressDialog(this)

                val params = HashMap<String, String>().apply {}

                val wooBuilderCustomer = WooBuilder().apply {
                    isHttps = false
                    baseUrl = "florals.ir/wc-api/v3"
                    signing_method = SigningMethod.HMACSHA1
                    wc_key = "ck_b89b3e5cd871e50755f2d021967aa903cf2839cc"
                    wc_secret = "cs_3c6fd6cfd5399a358f6ae285848829c7cc2cae88"
                }

                val resultLink: String? = OAuthSigner(wooBuilderCustomer)
                        .getSignature(RequestMethod.POST, "/customers", params)

                resultLink!!.httpPost().body(jsonString, Charsets.UTF_8).liveDataResponse().observeForever {
                    Log.d("egwegewg", it.toString())
                    if (it.first.statusCode == 201){
                        progressDialog.dismiss()
                        usernameEt.setText(data.customer.username)
                        passwordEt.setText(data.customer.password)
                        signUpLayout.visibility = View.GONE
                        loginLayout.visibility = View.VISIBLE
                        loginButton.performClick()
                        isLoginViewShowing = true
                        Toast.makeText(this, "ثبت نام با موفقیت انجام شد", Toast.LENGTH_LONG).show()
                    }else{
                        progressDialog.dismiss()
                        val dd = PrettyDialog(this)
                        dd.setTitle("بررسی اطلاعات")
                                .setIcon(R.drawable.error)
                                .setMessage("با این نام کاربری یا ایمیل قبلا ثبت نام انجام شده است.")
                                .addButton("بستن", R.color.colorWhite, R.color.colorFlowerAndPotTabBar) {
                                    dd.dismiss()
                                }
                                .show()
                    }
                }
            }
        }
    }

    var loginToken = ""
    var loginUserId = 0

    private fun login(){

        val username : String = usernameEt.text.toString()
        val password : String = passwordEt.text.toString()

        when {
            username.isEmpty() -> YoYo.with(Techniques.Shake).playOn(usernameEt)
            password.isEmpty() -> YoYo.with(Techniques.Shake).playOn(passwordEt)
            else -> {
                val params = HashMap<String, String>().apply {
                    put("username", username)
                    put("password", password)
                }

                val wooBuilderCustomer = WooBuilder().apply {
                    isHttps = false
                    baseUrl = "florals.ir/wp-json/jwt-auth/v1"
                    signing_method = SigningMethod.HMACSHA1
                    wc_key = "ck_b89b3e5cd871e50755f2d021967aa903cf2839cc"
                    wc_secret = "cs_3c6fd6cfd5399a358f6ae285848829c7cc2cae88"
                }

                val resultLink: String? = OAuthSigner(wooBuilderCustomer)
                        .getSignature(RequestMethod.POST, "/token", params)

                val progressDialog = progressDialog(this)

                resultLink!!.httpPost().liveDataObject(LoginData.Deserializer()).observeForever {
                    Log.d("gwegewg", it.toString())
                    it?.success {
                        progressDialog.dismiss()
                        it.notNull {
                            loginToken = it.token
                            Log.d("token", loginToken)
                            loginUserId = it.user_id
                            editor.putString("token", loginToken).commit()
                            getCustomerDetail(this, loginUserId, resultCode)
                        }
                    }
                    it?.failure {
                        Log.d("errorFound", it.message)
                        progressDialog.dismiss()
                        val d = PrettyDialog(this)
                        d.setTitle("بررسی اطلاعات")
                                .setIcon(R.drawable.error)
                                .setMessage("لطفا اطلاعات وارد شده را بررسی نمایید و سپس دوباره تلاش کنید")
                                .addButton("بستن", R.color.colorWhite, R.color.colorFlowerAndPotTabBar) {
                                    d.dismiss()
                                }
                                .show()
                    }
                }
            }
        }
    }

}
