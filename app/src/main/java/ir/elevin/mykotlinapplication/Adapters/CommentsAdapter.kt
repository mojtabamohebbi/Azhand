package ir.elevin.mykotlinapplication.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import ir.elevin.mykotlinapplication.*
import kotlinx.android.synthetic.main.comment_row.view.*

class CommentsAdapter(private val activity: FragmentActivity, private val items: List<Comment>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return CommentViewHolder(LayoutInflater.from(activity).inflate(R.layout.comment_row, p0, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @SuppressLint("SetTextI18n", "RestrictedApi")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = items[position]
        (holder as CommentViewHolder).nameTv.text = data.name
        holder.commentTv.text = data.comment
        val date = parsDateAndTime(data.dateCreate)
        holder.dateCreate.text = "${date.first} - ${date.second}"
        holder.ratingBar.rating = data.rate
    }
}

private class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val nameTv = view.nameTv!!
    val dateCreate = view.dateCreateTv!!
    val commentTv = view.commentTv!!
    val ratingBar = view.ratingBar!!
}