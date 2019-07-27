package ir.elevin.azhand

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import java.util.*
import android.content.Context
import android.graphics.Typeface
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import android.view.View.MeasureSpec.UNSPECIFIED
import android.view.View.MeasureSpec.makeMeasureSpec
import android.view.View.MeasureSpec.AT_MOST
import android.app.Dialog
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.multidex.MultiDex
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.livedata.liveDataObject
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import saman.zamani.persiandate.PersianDate
import java.text.DecimalFormat
import com.squareup.picasso.Picasso
import com.squareup.picasso.OkHttp3Downloader
import ir.mjmim.woocommercehelper.enums.RequestMethod
import ir.mjmim.woocommercehelper.enums.SigningMethod
import ir.mjmim.woocommercehelper.helpers.OAuthSigner
import ir.mjmim.woocommercehelper.main.WooBuilder
import libs.mjn.prettydialog.PrettyDialog


val webserviceUrl = "http://florals.ir/goli/api.php"
var account = Account()
var token = ""
var rootViewlHeight = 0f
var filterType = 0 //0=most visited, 1=highest price, 2= lowest price
var tabColor = R.color.colorFlowerAndPotTabBar
var backgroundColor = R.color.colorFlowerAndPotBackground
var tabSelectedIndex = 0
var isErrorShowing = false

val wooBuilder = WooBuilder().apply {
    isHttps = false
    baseUrl = "florals.ir/wp-json/wc/v3"
    signing_method = SigningMethod.HMACSHA1
    wc_key = "ck_b89b3e5cd871e50755f2d021967aa903cf2839cc"
    wc_secret = "cs_3c6fd6cfd5399a358f6ae285848829c7cc2cae88"
}

lateinit var sp: SharedPreferences
lateinit var editor: SharedPreferences.Editor

class AppController : Application() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreate() {
        super.onCreate()

        sp = getSharedPreferences("floral", Context.MODE_PRIVATE)
        editor = sp.edit()

//        val builder = Picasso.Builder(this)
//        builder.downloader(OkHttp3Downloader(this, Integer.MAX_VALUE.toLong()))
//        val built = builder.build()
//        built.setIndicatorsEnabled(true)
//        built.isLoggingEnabled = true
//        Picasso.setSingletonInstance(built)

        val languageToLoad = "fa-IR"
        val locale = Locale(languageToLoad)
        resources.configuration.setLocale(locale)
        resources.configuration.setLayoutDirection(locale)

        MultiDex.install(this)

        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath("font/iransans_medium.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        )
    }

}

fun checkCustomerAccount(activity: Activity){
    val userId = sp.getInt("id", 0)
    Log.d("userid", ""+userId)
    Log.d("token", ""+sp.getString("token", "null"))
    if (userId != 0){
        getCustomerDetail(activity, userId, 0)
    }
}

//resultCode == 0 means not showing dialog
fun getCustomerDetail(context: Activity, id: Int, resultCode: Int){

    lateinit var progressDialog: Dialog

    progressDialog = progressDialog(context)
    progressDialog.setCancelable(false)
    progressDialog.setCanceledOnTouchOutside(false)

    if (resultCode == 0){
        progressDialog.window?.setBackgroundDrawable(ColorDrawable(context.resources.getColor(R.color.colorEEEEEE)))
    }else{
        progressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
//    if (resultCode != 0){
//        progressDialog = progressDialog(context)
//    }

    val params = HashMap<String, String>().apply {}

    val wooBuilderCustomer = WooBuilder().apply {
        isHttps = false
        baseUrl = "florals.ir/wc-api/v3"
        signing_method = SigningMethod.HMACSHA1
        wc_key = "ck_b89b3e5cd871e50755f2d021967aa903cf2839cc"
        wc_secret = "cs_3c6fd6cfd5399a358f6ae285848829c7cc2cae88"
    }

    val resultLink: String? = OAuthSigner(wooBuilderCustomer)
            .getSignature(RequestMethod.GET, "/customers/$id", params)
    Log.d("gwegewg", resultLink+"--")

    resultLink!!
            .httpGet()
            .liveDataObject(GetUser.Deserializer())
            .observeForever { it ->
                Log.d("gwegewwefefg", it.toString())
                it?.success {
                    account = it.customer
                    Log.d("accountttt", account.billing_address.address_1)
                    editor.putInt("id", id).commit()

                    val handler = Handler()
                    handler.postDelayed({
                        progressDialog.dismiss()
                        if (resultCode != 0){
                            Toast.makeText(context, "به فلورال خوش آمدید", Toast.LENGTH_LONG).show()
                            context.setResult(resultCode)
                            context.finish()
                        }
                    }, 2000)

                }
                it?.failure {
//                    if (resultCode != 0){
                        progressDialog.dismiss()
                        val d = PrettyDialog(context)
                        d.setTitle(context.getString(R.string.error))
                                .setIcon(R.drawable.error)
                                .setMessage(context.getString(R.string.network_error))
                                .addButton(context.getString(R.string.try_again), R.color.colorWhite, R.color.colorRed) {
                                    d.dismiss()
                                    getCustomerDetail(context, id, resultCode)
                                }
                                .show()
//                    }
                }
            }
}

fun parsDate(date: String): String{
    val persianDate = PersianDate()
    val splited = date.split("-")
    val jalali = persianDate.toJalali(splited[0].toInt(), splited[1].toInt(), splited[2].toInt())
    return "$jalali[0]/$jalali[1]/$jalali[2]"
}

fun parsDateAndTime(dateAndTime: String): Pair<String, String>{
    val persianDate = PersianDate()
    val splited = dateAndTime.split(" ")
    val dateSplited = splited[0].split("-")
    val timeSplited = splited[1]
    val jalali = persianDate.toJalali(dateSplited[0].toInt(), dateSplited[1].toInt(), dateSplited[2].toInt())
    return Pair(""+jalali[0]+"/"+jalali[1]+"/"+jalali[2], timeSplited)
}

fun parsDateAndTimeWooo(dateAndTime: String): Pair<String, String>{
    Log.d("gegewgweg", dateAndTime)
    val persianDate = PersianDate()
    val splited = dateAndTime.split("T")
    val dateSplited = splited[0].split("-")
    val timeSplited = splited[1]
    val jalali = persianDate.toJalali(dateSplited[0].toInt(), dateSplited[1].toInt(), dateSplited[2].toInt())
    return Pair(""+jalali[0]+"/"+jalali[1]+"/"+jalali[2], timeSplited)
}

@NonNull
fun decimalFormatCommafy(number: String): String {

    var inputNum = number
    if(inputNum == "") {
        inputNum = "0"
    }

    val splittedNum = inputNum.split("\\.").toTypedArray()
    var decimalNum = ""
    if(splittedNum.size == 2)
    {
        inputNum = splittedNum[0]
        decimalNum="."+splittedNum[1]
    }

    val inputDouble: Double = inputNum.toDouble()
    val myFormatter = DecimalFormat("###,###")
    val output = myFormatter.format(inputDouble)

    var final = "$output$decimalNum"
    if (final.length > 3){
        final = final.substring(0, final.length-4)
    }

    return if (number.length >= 9){
        "$final میلیون تومان"
    }else{
        "$final هزارتومان"
    }

}

fun AppCompatEditText.hideKeyboard(context: Context){
    val imm = context.getSystemService(
            Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}

fun AppCompatEditText.showKeyboard(context: Context){
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun AppCompatAutoCompleteTextView.hideKeyboard(context: Context){
    val imm = context.getSystemService(
            Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}

fun AppCompatAutoCompleteTextView.showKeyboard(context: Context){
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun View.getViewHeight(): Int {
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = wm.defaultDisplay

    val deviceWidth: Int

    val size = Point()
    display.getSize(size)
    deviceWidth = size.x

    val widthMeasureSpec = makeMeasureSpec(deviceWidth, AT_MOST)
    val heightMeasureSpec = makeMeasureSpec(0, UNSPECIFIED)
    measure(widthMeasureSpec, heightMeasureSpec)
    return measuredHeight //        view.getMeasuredWidth();
}

fun <T : View> T.widthOfView(function: (Int) -> Unit) {
    if (width == 0)
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                function(width)
            }
        })
    else function(width)
}

fun RecyclerView.onScrollToEnd(linearLayoutManager: LinearLayoutManager, onScrollNearEnd: (Unit) -> Unit)
        = addOnScrollListener(object : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (linearLayoutManager.childCount + linearLayoutManager.findFirstVisibleItemPosition()
                >= linearLayoutManager.itemCount - 5) {  //if near fifth item from end
            onScrollNearEnd(Unit)
        }
    }
})

fun Drawable.setIconColor(color: Int) {
    mutate()
    setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
}

fun <T : View> T.heightOfView(function: (Int) -> Unit) {
    if (height == 0)
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                function(height)
            }
        })
    else function(height)
}

fun progressDialog(context: Context): Dialog{
    val d = Dialog(context)
    d.requestWindowFeature(Window.FEATURE_NO_TITLE)
    d.setContentView(R.layout.dialog_progress)
    d.setCanceledOnTouchOutside(false)
    d.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    d.show()
    return d
}

fun Context.getFont(level: Int): Typeface{
    return when (level){
        1 -> Typeface.createFromAsset(assets, "font/iransans_ultralight.ttf")
        2 -> Typeface.createFromAsset(assets, "font/iransans_light.ttf")
        3 -> Typeface.createFromAsset(assets, "font/iransans_regular.ttf")
        4 -> Typeface.createFromAsset(assets, "font/iransans_medium.ttf")
        5 -> Typeface.createFromAsset(assets, "font/iransans_bold.ttf")
        else -> Typeface.createFromAsset(assets, "font/iransans.ttf")
    }
}