package ir.elevin.azhand.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ir.elevin.azhand.Card
import ir.elevin.azhand.R
import kotlinx.android.synthetic.main.card_row.view.*

class CardAdapter(private val activity: FragmentActivity, private val items: List<Card>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return CardHolder(LayoutInflater.from(activity).inflate(R.layout.card_row, p0, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @SuppressLint("SetTextI18n", "RestrictedApi", "CheckResult")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = items[position]

        Picasso.get().load(data.image).into((holder as CardHolder).imageView)
    }
}

private class CardHolder(view: View) : RecyclerView.ViewHolder(view) {
    val imageView = view.iv!!
}