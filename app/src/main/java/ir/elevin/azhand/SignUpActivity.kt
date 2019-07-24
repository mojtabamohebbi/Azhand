package ir.elevin.azhand

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.livedata.liveDataResponse
import com.google.gson.Gson
import ir.mjmim.woocommercehelper.enums.RequestMethod
import ir.mjmim.woocommercehelper.enums.SigningMethod
import ir.mjmim.woocommercehelper.helpers.OAuthSigner
import ir.mjmim.woocommercehelper.main.WooBuilder
import kotlinx.android.synthetic.main.activity_sign_up.*
import libs.mjn.prettydialog.PrettyDialog
import java.util.*

class SignUpActivity : CustomActivity() {


    var isLoginViewShowing = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

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

                val data = MakeUser(customer(email, name, family, username, password, billing_address(name, family, "", address, "", "مشهد", "خراسان رضوی", postalCode, "ایران", email, phoneNumber), shipping_address(name, family, "", address, "", "مشهد", "خراسان رضوی", postalCode, "ایران")))

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
                    if (it.first.statusCode == 201){
                        progressDialog.dismiss()
                        Toast.makeText(this, "به فلورال خوش آمدید", Toast.LENGTH_LONG).show()
                    }else{
                        progressDialog.dismiss()
                        val dd = PrettyDialog(this)
                        dd.setTitle(this.getString(R.string.error))
                                .setIcon(R.drawable.error)
                                .setMessage("لطفا اطلاعات وارد شده را بررسی نمایید و سپس دوباره تلاش کنید")
                                .addButton(this.getString(R.string.try_again), R.color.colorWhite, R.color.colorRed) {
                                    dd.dismiss()
                                    signUp()
                                }
                                .show()
                    }
                }
            }
        }
    }

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

                resultLink!!.httpPost().liveDataResponse().observeForever {
                    if (it.first.statusCode == 200){
                        progressDialog.dismiss()
                        Toast.makeText(this, "به فلورال خوش آمدید", Toast.LENGTH_LONG).show()



                    }else{
                        progressDialog.dismiss()
                        val dd = PrettyDialog(this)
                        dd.setTitle(this.getString(R.string.error))
                                .setIcon(R.drawable.error)
                                .setMessage("لطفا اطلاعات وارد شده را بررسی نمایید و سپس دوباره تلاش کنید")
                                .addButton(this.getString(R.string.try_again), R.color.colorWhite, R.color.colorRed) {
                                    dd.dismiss()
                                    signUp()
                                }
                                .show()
                    }
                }
            }
        }
    }

    fun getCustomerDetail(){

    }
}
