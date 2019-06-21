package ir.elevin.azhand

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
import android.graphics.Color
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import saman.zamani.persiandate.PersianDate
import java.text.DecimalFormat

val webserviceUrl = "https://gr00p.com/goli/api.php"
var account = Account()
var token = ""
var rootViewlHeight = 0f
var filterType = 0 //0=most visited, 1=highest price, 2= lowest price
var tabColor = R.color.colorFlowerAndPotTabBar
var backgroundColor = R.color.colorFlowerAndPotBackground
var tabSelectedIndex = 0
var isErrorShowing = false

class AppController : Application() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onCreate() {
        super.onCreate()

        val languageToLoad = "fa-IR"
        val locale = Locale(languageToLoad)
        resources.configuration.setLocale(locale)
        resources.configuration.setLayoutDirection(locale)

        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath("font/iransans_medium.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        )
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

    return if (number.length > 3){
        "$output$decimalNum میلیون تومان"
    }else{
        "$output$decimalNum هزارتومان"
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