package id.muhammadfaisal.penantianqrscanner.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import id.muhammadfaisal.penantianqrscanner.R
import id.muhammadfaisal.penantianqrscanner.databinding.ItemResultBinding
import id.muhammadfaisal.penantianqrscanner.room.entity.SpreadSheetEntity
import java.text.SimpleDateFormat
import java.util.*

class ResultAdapter(var context : Context, var lists : List<SpreadSheetEntity>, var isForPdf : Boolean) : RecyclerView.Adapter<ResultAdapter.ViewHolder>() {
    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        private var binding = ItemResultBinding.bind(this.itemView)

        fun bind(
            context: Context,
            spreadSheetEntity: SpreadSheetEntity,
            position: Int,
            isForPdf: Boolean
        ) {

            val date = Date(spreadSheetEntity.time!!)
            var sdf = SimpleDateFormat("HH:mm dd/MM/YYYY")

            this.binding.textResult.text = spreadSheetEntity.value
            this.binding.textTime.text = sdf.format(date).toString()
            this.binding.textNo.text = (position + 1).toString()

            if (isForPdf){
                setTextColorBlack(this.binding.textResult, this.binding.textTime, this.binding.textNo)
            }
        }

        fun setTextColorBlack(vararg text: TextView) {
            for (i in text){
                i.setTextColor(Color.BLACK)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return  ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_result, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(context, lists[position], position, isForPdf)
    }

    override fun getItemCount(): Int {
        return this.lists.size
    }
}