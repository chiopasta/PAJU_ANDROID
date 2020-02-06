package com.bitxflow.sungmin_android.biz.board

import android.content.Context
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.bitxflow.sungmin_android.R
import java.util.*

class ReplyAdapter(
    context: Context,
    photoListItem: ArrayList<Reply>
) :
    BaseAdapter() {
    var mInflater: LayoutInflater
    var member: ArrayList<Reply>
    var _context: Context
    override fun getCount(): Int {
        return member.size
    }

    override fun getItem(position: Int): Any {
        return member[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var convertView = convertView
        val res: Int = R.layout.reply
        convertView = mInflater.inflate(res, parent, false)
        val name_tv =
            convertView.findViewById<View>(R.id.reply_name) as TextView
        val contents_tv =
            convertView.findViewById<View>(R.id.reply_contents) as TextView
        val replyContent: String? = member[position].contents
        name_tv.text = member[position].name
        contents_tv.text = Html.fromHtml(replyContent)
        contents_tv.movementMethod = LinkMovementMethod.getInstance()
        return convertView
    }

    init {
        mInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        member = photoListItem
        _context = context
    }
}