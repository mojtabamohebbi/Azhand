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
import android.os.Build
import android.transition.TransitionInflater
import androidx.viewpager.widget.ViewPager
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import ir.elevin.azhand.database.DatabaseHandler
import ir.elevin.azhand.fragments.*
import kotlinx.android.synthetic.main.content_main.rootView
import kotlinx.android.synthetic.main.dialog_sort_price.*


class MainActivity : CustomActivity(), NavigationView.OnNavigationItemSelectedListener{

    private var appBarExpanded : Boolean = true
    private val flowerAndPotFragment = ApartmentFlowerFragment()
    private val singleFlowerFragment = SingleFlowerFragment()
    private val bunchOfFlowerFragment = BunchOfFlowerFragment()
    private val cactusFragment = CactusFragment()
    private val treeFragment = TreeFragment()
    private val gardeningFragment = GardeningFragment()
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

        tabs.add(TabModel("ابزار و یراق", gardeningFragment))
        tabs.add(TabModel("گلدان", potFragment))
        tabs.add(TabModel("کاکتوس", cactusFragment))
        tabs.add(TabModel("جعبه گل", treeFragment))
        tabs.add(TabModel("شاخه گل", singleFlowerFragment))
        tabs.add(TabModel("دسته گل", bunchOfFlowerFragment))
        tabs.add(TabModel("آپارتمانی", flowerAndPotFragment))

        pagerAdapter = TabsPagerAdapter(supportFragmentManager, tabs)
        pager.adapter = pagerAdapter
        pager.offscreenPageLimit = 7
        tabLayout.setupWithViewPager(pager)

        val tab = tabLayout.getTabAt(6)
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
                        changeTabBar(R.color.colorGardeningTabBar, R.color.colorGardeningBackground, R.color.colorGardeningStatusBar, R.drawable.gardening)
                    }
                    1 -> {
                        changeTabBar(R.color.colorTreeTabBar, R.color.colorTreeBackground, R.color.colorTreeStatusBar, R.drawable.pot)
                    }
                    2 -> {
                        changeTabBar(R.color.colorPotTabBar, R.color.colorPotBackground, R.color.colorPotStatusBar, R.drawable.cactus)
                    }
                    3 -> {
                        changeTabBar(R.color.colorCactusTabBar, R.color.colorCactusBackground, R.color.colorCactusStatusBar, R.drawable.flowerbox)
                    }
                    4 -> {
                        changeTabBar(R.color.colorSingleFlowerTabBar, R.color.colorSingleFlowerBackground, R.color.colorSingleFlowerStatusBar, R.drawable.single_flower)
                    }
                    5 -> {
                        changeTabBar(R.color.colorBunchFlowersTabBar, R.color.colorBunchFlowersBackground, R.color.colorBunchFlowersStatusBar, R.drawable.bunch_of_flowers)
                    }
                    6 -> {
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
            1 -> {}
            2 -> {}
            3 -> {}
            4 -> {}
            5 -> {}
            6 -> {}
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_account -> {
                account = db!!.getAccount()
                if (account.id != 0){
                    startActivity(Intent(this, AccountActivity::class.java))
                }else{
                    startActivity(Intent(this, LoginActivity::class.java))
                }
            }
            R.id.nav_cart -> {
                account = db!!.getAccount()
                if (account.id != 0){
                    startActivity(Intent(this, CartActivity::class.java))
                }else{
                    startActivity(Intent(this, LoginActivity::class.java))
                }
            }
            R.id.nav_receipts -> {
                account = db!!.getAccount()
                if (account.id != 0){
                    startActivity(Intent(this, AccountActivity::class.java))
                }else{
                    startActivity(Intent(this, LoginActivity::class.java))
                }
            }
            R.id.nav_about -> {

            }
            R.id.nav_contact_us -> {

            }
            R.id.nav_share -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
