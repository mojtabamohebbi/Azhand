package ir.elevin.azhand.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import ir.elevin.azhand.Supporter
import ir.elevin.azhand.R
import kotlinx.android.synthetic.main.supporter_row.view.*
import android.net.Uri.fromParts
import android.content.Intent
import android.graphics.Color


class SupporterAdapter(private val activity: FragmentActivity, private val items: List<Supporter>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return SupporterHolder(LayoutInflater.from(activity).inflate(R.layout.supporter_row, p0, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @SuppressLint("SetTextI18n", "RestrictedApi")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = items[position]
        (holder as SupporterHolder).responsibilityTv.text = data.responsibility
        holder.nameTv.text = data.name
        if (data.sex == 1){
            holder.avatarIv.setImageResource(R.drawable.male)
        }else{
            holder.avatarIv.setImageResource(R.drawable.female)
        }
        holder.callButton.setColorFilter(Color.parseColor("#"+data.textColor), android.graphics.PorterDuff.Mode.SRC_IN)
        holder.responsibilityTv.setTextColor(Color.parseColor("#"+data.textColor))
        holder.callButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, fromParts("tel", data.phone, null))
            activity.startActivity(intent)
        }
    }
}

private class SupporterHolder(view: View) : RecyclerView.ViewHolder(view) {
    val responsibilityTv = view.responsibilityTv!!
    val callButton = view.callButton!!
    val avatarIv = view.avatarIv!!
    val nameTv = view.nameTv!!
}