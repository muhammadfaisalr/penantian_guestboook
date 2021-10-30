package id.muhammadfaisal.penantianqrscanner.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import id.muhammadfaisal.penantianqrscanner.R
import id.muhammadfaisal.penantianqrscanner.databinding.ItemLanguageBinding
import id.muhammadfaisal.penantianqrscanner.model.Language

class SpinnerAdapter(val context : Context, var lang : List<Language>) : BaseAdapter() {

    private val inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return lang.size
    }

    override fun getItem(position: Int): Any {
        return lang[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view : View
        val holder : ItemHolder

        if (convertView == null){
            view = inflater.inflate(R.layout.item_language, parent, false)
            holder = ItemHolder(view)
            view.tag = holder
        }else{
            view = convertView
            holder = view.tag as ItemHolder
        }

        holder.text.apply {
            text = lang[position].name
        }

        holder.image.apply {
            setImageResource(lang[position].image!!)
        }

        return view
    }

    class ItemHolder(view: View) {
        var text : TextView
        var image : ImageView
        var binding : ItemLanguageBinding = ItemLanguageBinding.bind(view)

        init {
            this.text = this.binding.text
            this.image = this.binding.imageCountry
        }
    }
}