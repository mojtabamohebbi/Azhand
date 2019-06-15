package ir.elevin.azhand
import android.os.Build
import android.os.Bundle
import androidx.appcompat.content.res.AppCompatResources
import hari.bounceview.BounceView
import kotlinx.android.synthetic.main.activity_upgrade.*

class UpgradeActivity : CustomActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upgrade)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = AppCompatResources.getColorStateList(this, R.color.colorPrimaryLight).defaultColor
            window.navigationBarColor = AppCompatResources.getColorStateList(this, R.color.colorPrimaryLight).defaultColor
        }

        headerTitleTv.typeface = getFont(5)

        BounceView.addAnimTo(payButton)

        firstPlanCardView.setOnClickListener {
            firstPlanCardView.setCardBackgroundColor(AppCompatResources.getColorStateList(this, R.color.colorPrimary))
            secondPlanCardView.setCardBackgroundColor(AppCompatResources.getColorStateList(this, R.color.colorWhite))
            thirdPlanCardView.setCardBackgroundColor(AppCompatResources.getColorStateList(this, R.color.colorWhite))
            fourthPlanCardView.setCardBackgroundColor(AppCompatResources.getColorStateList(this, R.color.colorWhite))

//            firstTv.setTextColor(AppCompatResources.getColorStateList(this, R.color.colorRed))
//            secondTv.setTextColor(AppCompatResources.getColorStateList(this, R.color.colorWhite))
//            thirdTv.setTextColor(AppCompatResources.getColorStateList(this, R.color.colorWhite))
//            fourthTv.setTextColor(AppCompatResources.getColorStateList(this, R.color.colorWhite))
        }

        secondPlanCardView.setOnClickListener {
            firstPlanCardView.setCardBackgroundColor(AppCompatResources.getColorStateList(this, R.color.colorWhite))
            secondPlanCardView.setCardBackgroundColor(AppCompatResources.getColorStateList(this, R.color.colorPrimary))
            thirdPlanCardView.setCardBackgroundColor(AppCompatResources.getColorStateList(this, R.color.colorWhite))
            fourthPlanCardView.setCardBackgroundColor(AppCompatResources.getColorStateList(this, R.color.colorWhite))

//            firstTv.setTextColor(AppCompatResources.getColorStateList(this, R.color.colorWhite))
//            secondTv.setTextColor(AppCompatResources.getColorStateList(this, R.color.colorRed))
//            thirdTv.setTextColor(AppCompatResources.getColorStateList(this, R.color.colorWhite))
//            fourthTv.setTextColor(AppCompatResources.getColorStateList(this, R.color.colorWhite))
        }

        thirdPlanCardView.setOnClickListener {
            firstPlanCardView.setCardBackgroundColor(AppCompatResources.getColorStateList(this, R.color.colorWhite))
            secondPlanCardView.setCardBackgroundColor(AppCompatResources.getColorStateList(this, R.color.colorWhite))
            thirdPlanCardView.setCardBackgroundColor(AppCompatResources.getColorStateList(this, R.color.colorPrimary))
            fourthPlanCardView.setCardBackgroundColor(AppCompatResources.getColorStateList(this, R.color.colorWhite))

//            firstTv.setTextColor(AppCompatResources.getColorStateList(this, R.color.colorWhite))
//            secondTv.setTextColor(AppCompatResources.getColorStateList(this, R.color.colorWhite))
//            thirdTv.setTextColor(AppCompatResources.getColorStateList(this, R.color.colorRed))
//            fourthTv.setTextColor(AppCompatResources.getColorStateList(this, R.color.colorWhite))
        }

        fourthPlanCardView.setOnClickListener {
            firstPlanCardView.setCardBackgroundColor(AppCompatResources.getColorStateList(this, R.color.colorWhite))
            secondPlanCardView.setCardBackgroundColor(AppCompatResources.getColorStateList(this, R.color.colorWhite))
            thirdPlanCardView.setCardBackgroundColor(AppCompatResources.getColorStateList(this, R.color.colorWhite))
            fourthPlanCardView.setCardBackgroundColor(AppCompatResources.getColorStateList(this, R.color.colorPrimary))

//            firstTv.setTextColor(AppCompatResources.getColorStateList(this, R.color.colorWhite))
//            secondTv.setTextColor(AppCompatResources.getColorStateList(this, R.color.colorWhite))
//            thirdTv.setTextColor(AppCompatResources.getColorStateList(this, R.color.colorWhite))
//            fourthTv.setTextColor(AppCompatResources.getColorStateList(this, R.color.colorRed))
        }

        backButton.setOnClickListener {
            onBackPressed()
        }

    }
}
