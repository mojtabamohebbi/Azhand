package ir.elevin.azhand

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import kotlinx.android.synthetic.main.activity_about_us.*
import android.view.ViewGroup
import android.animation.ValueAnimator



class AboutUsActivity : CustomActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

        backButton.setOnClickListener {
            onBackPressed()
        }

        view1.postDelayed({
            view1.visibility = View.VISIBLE
            YoYo.with(Techniques.FadeInUp).duration(300).playOn(view1)

            view2.postDelayed({
                view2.visibility = View.VISIBLE
                YoYo.with(Techniques.FadeInUp).duration(300).playOn(view2)

                view3.postDelayed({
                    view3.visibility = View.VISIBLE
                    YoYo.with(Techniques.FadeInUp).duration(300).playOn(view3)

                    view4.postDelayed({
                        view4.visibility = View.VISIBLE
                        YoYo.with(Techniques.FadeInUp).duration(300).playOn(view4)

                        view5.postDelayed({
                            view5.visibility = View.VISIBLE
                            YoYo.with(Techniques.FadeInUp).duration(300).playOn(view5)

                            view6_expand.postDelayed({

                                val anim = ValueAnimator.ofInt(0, 400)
                                anim.addUpdateListener { valueAnimator ->
                                    val value = valueAnimator.animatedValue as Int
                                    val layoutParams = view6_expand.layoutParams
                                    layoutParams.width = value
                                    view6_expand.layoutParams = layoutParams
                                }
                                anim.duration = 1500
                                anim.start()

                                viewTeamName.visibility = View.VISIBLE
                                YoYo.with(Techniques.ZoomIn).duration(300).playOn(viewTeamName)

                                view7.postDelayed({

                                    view7.visibility = View.VISIBLE
                                    YoYo.with(Techniques.ZoomInUp).duration(800).playOn(view7)

                                    view8.postDelayed({

                                        view8.visibility = View.VISIBLE
                                        YoYo.with(Techniques.ZoomInLeft).duration(1300).playOn(view8)

                                        view9.visibility = View.VISIBLE
                                        YoYo.with(Techniques.ZoomInRight).duration(1000).playOn(view9)

                                        view10_expand.postDelayed({

                                            val anim2 = ValueAnimator.ofInt(0, 400)
                                            anim2.addUpdateListener { valueAnimator ->
                                                val value = valueAnimator.animatedValue as Int
                                                val layoutParams = view10_expand.layoutParams
                                                layoutParams.width = value
                                                view10_expand.layoutParams = layoutParams
                                            }
                                            anim2.duration = 1500
                                            anim2.start()

                                            view11.postDelayed({
                                                view11.visibility = View.VISIBLE
                                                YoYo.with(Techniques.FadeInRight).duration(800).playOn(view11)

                                                view12.postDelayed({
                                                    view12.visibility = View.VISIBLE
                                                    YoYo.with(Techniques.FadeInLeft).duration(900).playOn(view12)

                                                    view13.visibility = View.VISIBLE
                                                    YoYo.with(Techniques.FadeInUp).duration(3000).playOn(view13)

                                                }, 200)

                                            }, 200)

                                        }, 280)

                                    }, 260)

                                }, 240)

                            }, 220)

                        }, 200)

                    }, 180)

                }, 160)

            }, 140)

        }, 120)




    }
}
