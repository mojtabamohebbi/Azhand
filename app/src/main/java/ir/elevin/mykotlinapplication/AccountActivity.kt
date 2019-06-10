package ir.elevin.mykotlinapplication

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.livedata.liveDataObject
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import com.yalantis.ucrop.UCrop
//import com.yalantis.ucrop.UCrop
import hari.bounceview.BounceView
import kotlinx.android.synthetic.main.activity_account.*
import libs.mjn.prettydialog.PrettyDialog
import java.io.File

class AccountActivity : CustomActivity() {

    lateinit var progressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        backButton.setOnClickListener {
            onBackPressed()
        }

        val citiesAdapter = ArrayAdapter(this,
                R.layout.array_list_item, resources.getStringArray(R.array.cities))
        cityEt.setAdapter(citiesAdapter)
        BounceView.addAnimTo(registerDataButton)
        BounceView.addAnimTo(avatarIv)
        progressDialog = progressDialog(this)

        creditTv.setOnClickListener {
            startActivity(Intent(this, UpgradeActivity::class.java))
        }

        registerDataButton.setOnClickListener {
//            sendData()
        }

        avatarIv.setOnClickListener {
            pickImage()
        }

//        getData()
    }

    private fun pickImage(){
        ImagePicker.with(this)
                .compress(300)
                .maxResultSize(1024, 1024)
                .start()
    }

    lateinit var finalPickImageUrl: Uri
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == ImagePicker.REQUEST_CODE) {
            //Image Uri will not be null for RESULT_OK

            val fileUri = data?.data
            finalPickImageUrl = fileUri!!
            UCrop.of(fileUri, finalPickImageUrl)
                    .start(this)

            //You can get File object from intent
            val file: File? = ImagePicker.getFile(data)
            //You can also get File Path from intent
            val filePath: String? = ImagePicker.getFilePath(data)

        }
        else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val fileUri = UCrop.getOutput(data!!)
            avatarIv.setImageURI(fileUri)
        }
    }

//
//    @SuppressLint("SetTextI18n")
//    private fun getData(){
//        Log.d("gwegwe", "${account.id}")
//        val params = listOf("func" to "get_account_by_id", "uid" to "${account.id}")
//        webserviceUrl.httpPost(params).liveDataObject(Demand.Deserializer()).observeForever {
//            Log.d("response", it.toString())
//            it.success { it ->
//                progressDialog.dismiss()
//                nameEt.setText(it.name)
//                expertEt.setText(it.expertise)
//                aboutEt.setText(it.about)
//                if (it.workExperience != "0"){
//                    yearsOfExperienceEt.setText(it.workExperience)
//                }
//                educationEt.setText(it.education)
//                cityEt.setText(it.city)
//                if (it.dateCredit > 0){
//                    creditTv.text = "اعتبار: "+it.dateCredit+" روز - "+it.chatCredit+" گفتگو"
//                }else{
//                    creditTv.text = "برای خرید اعتبار کلیک کنید"
//                }
//            }
//            it.failure {
//                progressDialog.dismiss()
//                val d = PrettyDialog(this)
//                d.setTitle(getString(R.string.error))
//                        .setIcon(R.drawable.error)
//                        .setMessage(getString(R.string.network_error))
//                        .addButton(getString(R.string.try_again), R.color.colorWhite, R.color.colorRed) {
//                            d.dismiss()
//                            getData()
//                        }
//                        .show()
//            }
//        }
//    }
//
//    private fun sendData(){
//        val name = nameEt.text.toString()
//        val expertise = expertEt.text.toString()
//        val about = aboutEt.text.toString()
//        var experience = yearsOfExperienceEt.text.toString()
//        val education = educationEt.text.toString()
//        val city = cityEt.text.toString()
//
//        if(experience.isEmpty()){
//            experience = "0"
//        }
//
//        if (name.length > 100 || name.length < 6){
//            YoYo.with(Techniques.Shake).playOn(nameEt)
//            Toast.makeText(this, "۶ تا ۱۰۰ کاراکتر", Toast.LENGTH_SHORT).show()
//        }else if (expertise.length > 100 || expertise.length < 6){
//            YoYo.with(Techniques.Shake).playOn(nameEt)
//            Toast.makeText(this, "۶ تا ۱۰۰ کاراکتر", Toast.LENGTH_SHORT).show()
//        }else if (about.length > 1000 || about.length < 25){
//            YoYo.with(Techniques.Shake).playOn(nameEt)
//            Toast.makeText(this, "۲۵ تا ۱۰۰۰ کاراکتر", Toast.LENGTH_SHORT).show()
//        }else if (education.length > 100 || education.length < 6){
//            YoYo.with(Techniques.Shake).playOn(nameEt)
//            Toast.makeText(this, "۶ تا ۱۰۰ کاراکتر", Toast.LENGTH_SHORT).show()
//        }else if (city.length > 30 || city.length < 2){
//            YoYo.with(Techniques.Shake).playOn(nameEt)
//            Toast.makeText(this, "۲ تا ۳۰ کاراکتر", Toast.LENGTH_SHORT).show()
//        }else{
//
//        }
//    }
}

























