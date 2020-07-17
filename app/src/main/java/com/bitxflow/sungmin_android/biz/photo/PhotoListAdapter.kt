package com.bitxflow.sungmin_android.biz.photo

import android.content.Context
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bitxflow.sungmin_android.DB.MemberDatabase
import com.bitxflow.sungmin_android.R
import com.bitxflow.sungmin_android.biz.board.Reply
import com.bitxflow.sungmin_android.util.DownloadImage
import com.bumptech.glide.Glide
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper
import com.ortiz.touchview.TouchImageView
import kotlinx.android.synthetic.main.home_list_header.*
import java.net.URL
import java.util.ArrayList

class PhotoListAdapter(
    context: Context,
    photoListItem: ArrayList<Photo>
) :
    BaseAdapter() {
    var mInflater: LayoutInflater
    var member: ArrayList<Photo>
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
//        var convertView = convertView
        val res: Int = R.layout.photo_list
        val convertView = mInflater.inflate(res, parent, false)
        val title_tx =
            convertView.findViewById<View>(R.id.photo_list_title_tx) as TextView
        val contents_tx =
            convertView.findViewById<View>(R.id.photo_list_content_tx) as TextView
        val img_iv =
            convertView.findViewById<View>(R.id.photo_list_iv) as ImageView

        val replyContent: String? = member[position].contents
        title_tx.text = member[position].title
        contents_tx.text = Html.fromHtml(replyContent)
        contents_tx.movementMethod = LinkMovementMethod.getInstance()

//        UrlImageViewHelper.setUrlDrawable(img_iv, member[position].pUrl,R.drawable.profileimage)
        Glide.with(convertView).load(member[position].pUrl).placeholder(R.drawable.loading).override(1000,600).into(img_iv)

        return convertView
    }

    init {
        mInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        member = photoListItem
        _context = context
    }
}