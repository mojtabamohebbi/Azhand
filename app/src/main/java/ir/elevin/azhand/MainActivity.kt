package ir.elevin.azhand

import android.os.Bundle
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import android.content.Intent
import com.google.android.material.appbar.AppBarLayout
import android.view.*
import kotlinx.android.synthetic.main.content_main.pager
import kotlin.collections.ArrayList
import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.transition.TransitionInflater
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.livedata.liveDataObject
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import ir.elevin.azhand.adapters.SupporterAdapter
import ir.elevin.azhand.database.DatabaseHandler
import ir.elevin.azhand.fragments.*
import kotlinx.android.synthetic.main.content_main.rootView
import kotlinx.android.synthetic.main.dialog_confirm.*
import kotlinx.android.synthetic.main.dialog_sort_price.*
import kotlinx.android.synthetic.main.dialog_support.*
import kotlinx.android.synthetic.main.dialog_update.*
import libs.mjn.prettydialog.PrettyDialog


class MainActivity : CustomActivity(), NavigationView.OnNavigationItemSelectedListener{

    private var appBarExpanded : Boolean = true
    private val flowerAndPotFragment = ApartmentFlowerFragment()
    private val singleFlowerFragment = SingleFlowerFragment()
    private val bunchOfFlowerFragment = BunchOfFlowerFragment()
    private val cactusFragment = CactusFragment()
    private val boxFlowerFragment = BoxFlowerFragment()
    private val subscriptionFragment = SubscriptionFragment()
    private val cardPostalFragment = CardPostalFragment()
    private val potFragment = PotFragment()

    private lateinit var pagerAdapter: TabsPagerAdapter
    private val tabs = ArrayList<TabModel>()

    var headerLayoutHeight = 0
    var transactionFormLayoutHeight = 0
    var toolbarHeight = 0

    var isOpenTransactionForm = false
    var db: DatabaseHandler? = null

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = DatabaseHandler(this)
        account = db!!.getAccount()

        val fade = TransitionInflater.from(this).inflateTransition(R.transition.activity_slide)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fade.excludeTarget(android.R.id.statusBarBackground, true)
            fade.excludeTarget(android.R.id.navigationBarBackground, true)
            window.allowReturnTransitionOverlap = true
            window.allowEnterTransitionOverlap = true
            window.enterTransition = fade
            window.returnTransition = fade
            window.reenterTransition = fade
            window.exitTransition = fade
        }

        setSupportActionBar(toolbar)

        title = ""

        appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            //  Vertical offset == 0 indicates appBar is fully expanded.
            if (Math.abs(verticalOffset) > 200) {
                appBarExpanded = false
                invalidateOptionsMenu()
            } else {
                appBarExpanded = true
                invalidateOptionsMenu()
            }
        })

        collapseActionView.apply {
            setCollapsedTitleTypeface(getFont(5))
            setExpandedTitleTypeface(getFont(5))
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = false
        toggle.setHomeAsUpIndicator(R.drawable.ic_menu_24dp)
        toggle.syncState()

        toggle.setToolbarNavigationClickListener {
            if (drawer_layout.isDrawerVisible(GravityCompat.START)){
                drawer_layout.closeDrawer(GravityCompat.START)
            }else {
                drawer_layout.openDrawer(GravityCompat.START)
            }
        }

        nav_view.setNavigationItemSelectedListener(this)
        nav_view.menu.getItem(3).isVisible = account.id > 0

        tabs.add(TabModel("کارت پستال", cardPostalFragment))
        tabs.add(TabModel("گلدان", potFragment))
        tabs.add(TabModel("کاکتوس", cactusFragment))
        tabs.add(TabModel("اشتراک", subscriptionFragment))
        tabs.add(TabModel("جعبه گل", boxFlowerFragment))
        tabs.add(TabModel("شاخه گل", singleFlowerFragment))
        tabs.add(TabModel("دسته گل", bunchOfFlowerFragment))
        tabs.add(TabModel("آپارتمانی", flowerAndPotFragment))

        pagerAdapter = TabsPagerAdapter(supportFragmentManager, tabs)
        pager.adapter = pagerAdapter
        pager.offscreenPageLimit = 8
        tabLayout.setupWithViewPager(pager)

        val tab = tabLayout.getTabAt(7)
        tab!!.select()

        pager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                tabSelectedIndex = position
                when (position) {
                    0 -> {
                        changeTabBar(R.color.colorGardeningTabBar, R.color.colorGardeningBackground, R.color.colorGardeningStatusBar, R.drawable.card_postal)
                    }
                    1 -> {
                        changeTabBar(R.color.colorTreeTabBar, R.color.colorTreeBackground, R.color.colorTreeStatusBar, R.drawable.pot)
                    }
                    2 -> {
                        changeTabBar(R.color.colorPotTabBar, R.color.colorPotBackground, R.color.colorPotStatusBar, R.drawable.cactus)
                    }
                    3 -> {
                        changeTabBar(R.color.colorCactusTabBar, R.color.colorCactusBackground, R.color.colorCactusStatusBar, R.drawable.subscription)
                    }
                    4 -> {
                        changeTabBar(R.color.colorSingleFlowerTabBar, R.color.colorSingleFlowerBackground, R.color.colorSingleFlowerStatusBar, R.drawable.flowerbox)
                    }
                    5 -> {
                        changeTabBar(R.color.colorBunchFlowersTabBar, R.color.colorBunchFlowersBackground, R.color.colorBunchFlowersStatusBar, R.drawable.single_flower)
                    }
                    6 -> {
                        changeTabBar(R.color.colorFlowerAndPotTabBar, R.color.colorFlowerAndPotBackground, R.color.colorFlowerAndPotStatusBar, R.drawable.bunch_of_flowers)
                    }
                    7 -> {
                        changeTabBar(R.color.colorFlowerAndPotTabBar, R.color.colorFlowerAndPotBackground, R.color.colorFlowerAndPotStatusBar, R.drawable.flower_and_pot)
                    }
                }
                YoYo.with(Techniques.Landing).duration(200).playOn(headerIv)
            }

        })

        rootView.heightOfView {
            rootViewlHeight = it.toFloat()
        }

        collapseActionView.heightOfView {
            headerLayoutHeight = it
        }

        toolbar.heightOfView{
            toolbarHeight = it
        }

        insertAccounts()
        checkVersions()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if (item!!.itemId == R.id.action_sort){
            val d = Dialog(this)
            d.requestWindowFeature(Window.FEATURE_NO_TITLE)
            d.setContentView(R.layout.dialog_sort_price)
            d.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            d.mostVisitedButton.setOnClickListener {
                filterType = 0
                getDataForChangeSort()
                d.dismiss()
            }

            d.expensiveButton.setOnClickListener {
                filterType = 1
                getDataForChangeSort()
                d.dismiss()
            }

            d.cheapButton.setOnClickListener {
                filterType = 2
                getDataForChangeSort()
                d.dismiss()
            }

            d.show()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun getDataForChangeSort(){
        when (tabSelectedIndex){
            0 -> {flowerAndPotFragment.getData(true)}
            1 -> {bunchOfFlowerFragment.getData(true)}
            2 -> {singleFlowerFragment.getData(true)}
            3 -> {boxFlowerFragment.getData(true)}
            4 -> {subscriptionFragment.getData(true)}
            5 -> {cactusFragment.getData(true)}
            6 -> {potFragment.getData(true)}
            7 -> {cardPostalFragment.getData(true)}
        }
    }

    fun changeTabBar(colorTabBar: Int, colorBackground: Int, colorStatusBar: Int, image: Int){
//        tabColor = colorTabBar
//        backgroundColor = colorBackground
//        Log.d("efwfwdqwdwf", ""+ backgroundColor)
        headerIv.setImageResource(image)
//        rootView.setBackgroundColor(AppCompatResources.getColorStateList(this, colorBackground).defaultColor)
//        tabLayout.setBackgroundColor(AppCompatResources.getColorStateList(this, colorTabBar).defaultColor)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            window.statusBarColor = AppCompatResources.getColorStateList(this, colorStatusBar).defaultColor
//        }
    }

    private fun insertAccounts(){
//        val db = DatabaseHandler(this)
//        for (i in 0..5){
//            db.insertAccounts(i, "عنوان حساب $i", "توضیحات حساب را در این قسمت مشاهده می کنید.", "http://andreirobu.com/wp-content/uploads/2016/01/intypewetrust-031.png")
//            db.insertAccounts(i, "عنوان حساب $i", "توضیحات حساب را در این قسمت مشاهده می کنید.", "https://mir-s3-cdn-cf.behance.net/project_modules/disp/8dc7bb18709591.562cdfa74d2b8.jpg")
//            db.insertAccounts(i, "عنوان حساب $i", "توضیحات حساب را در این قسمت مشاهده می کنید.", "https://cdn.dribbble.com/users/874922/screenshots/5135135/untitled-2.jpg")
//        }
//        db.close()
    }

    class TabsPagerAdapter(fragmentManager: androidx.fragment.app.FragmentManager, val tabs: ArrayList<TabModel>) :
            androidx.fragment.app.FragmentStatePagerAdapter(fragmentManager) {

        // 2
        override fun getItem(position: Int): androidx.fragment.app.Fragment {
            return when (position) {
                7 -> tabs[7].fragment
                6 -> tabs[6].fragment
                5 -> tabs[5].fragment
                4 -> tabs[4].fragment
                3 -> tabs[3].fragment
                2 -> tabs[2].fragment
                1 -> tabs[1].fragment
                0 -> tabs[0].fragment
                else -> tabs[6].fragment
            }
        }

        // 3
        override fun getCount(): Int {
            return tabs.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return tabs[position].title
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun getSupporterList(d: Dialog){
        d.progressBar.visibility = View.VISIBLE
        d.supportersRecyclerView.visibility = View.GONE
        val params: List<Pair<String, Any?>> = listOf("func" to "get_supporters")
        webserviceUrl
                .httpPost(params)
                .liveDataObject(Supporter.ListDeserializer())
                .observeForever { it ->
                    Log.d("gwegewg", it.toString())
                    it?.success {
                        d.progressBar.visibility = View.GONE
                        d.supportersRecyclerView.visibility = View.VISIBLE
                        if (it.isNotEmpty()){
                            val array = ArrayList<Supporter>()
                            d.supportersRecyclerView.adapter = SupporterAdapter(this, it.toCollection(array))
                        }
                    }
                    it?.failure {
                        Log.d("errorFound", it.message)
                        val dd = PrettyDialog(this)
                        dd.setTitle(getString(R.string.error))
                                .setIcon(R.drawable.error)
                                .setMessage(getString(R.string.network_error))
                                .addButton(getString(R.string.try_again), R.color.colorWhite, R.color.colorRed) {
                                    d.dismiss()
                                    getSupporterList(d)
                                }
                                .show()
                    }
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 21){
            nav_view.menu.getItem(3).isVisible = true
            startActivity(Intent(this, CartActivity::class.java))
        }else if (resultCode == 22){
            nav_view.menu.getItem(3).isVisible = true
            startActivity(Intent(this, OrdersActivity::class.java))
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_support -> {
                val d = Dialog(this)
                d.requestWindowFeature(Window.FEATURE_NO_TITLE)
                d.setContentView(R.layout.dialog_support)
                d.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                d.window?.decorView!!.layoutDirection = View.LAYOUT_DIRECTION_RTL

                d.supportersRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                getSupporterList(d)

                d.show()
            }
            R.id.nav_cart -> {
                account = db!!.getAccount()
                if (account.id != 0){
                    startActivity(Intent(this, CartActivity::class.java))
                }else{
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.putExtra("result", 21)
                    startActivityForResult(intent, 2)
                }
            }
            R.id.nav_orders -> {
                account = db!!.getAccount()
                if (account.id != 0){
                    startActivity(Intent(this, OrdersActivity::class.java))
                }else{
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.putExtra("result", 22)
                    startActivityForResult(intent, 2)
                }
            }
            R.id.nav_about -> {
                startActivity(Intent(this, AboutUsActivity::class.java))
            }
            R.id.nav_contact_us -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_common_q -> {
                startActivity(Intent(this, CommonQuestionsActivity::class.java))
            }
            R.id.nav_logout -> {
                val d = Dialog(this)
                d.requestWindowFeature(Window.FEATURE_NO_TITLE)
                d.setContentView(R.layout.dialog_confirm)
                d.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                d.yesButton.setOnClickListener {
                    db!!.deleteAccount()
                    nav_view.menu.getItem(3).isVisible = false
                    d.dismiss()
                }

                d.noButton.setOnClickListener {
                    d.dismiss()
                }

                d.show()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun checkVersions(){
        webserviceUrl.httpPost(listOf("func" to "check_versions", "version" to "1", "osType" to 2))
                .liveDataObject(Version.Deserializer())
                .observeForever { res ->
                    res.success {it ->
                        Log.d("update", "available")
                        val d = Dialog(this)
                        d.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        d.setContentView(R.layout.dialog_update)
                        d.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                        d.versionNameTv.text = "ورژن جدید: ${it.versionName}"
                        d.changesTv.text = "تغییرات: ${it.changes}"
                        d.isForceTv.text = if(it.isForce == 1) "این بروزرسانی الزامیست" else "این بروزرسانی اختیاریست"

                        if(it.isForce == 1){
                            d.setCancelable(false)
                            d.setCanceledOnTouchOutside(false)
                            d.isForceTv.setTextColor(AppCompatResources.getColorStateList(this, R.color.colorAccentDark).defaultColor)
                        }else{
                            d.isForceTv.setTextColor(AppCompatResources.getColorStateList(this, R.color.colorBlue).defaultColor)
                        }

                        d.updateButton.setOnClickListener {it2 ->
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.updateUrl))
                            startActivity(intent)
                        }

                        d.show()
                    }
                    res.failure {
                        Log.d("update", "not available")
                    }
                }
    }
}
