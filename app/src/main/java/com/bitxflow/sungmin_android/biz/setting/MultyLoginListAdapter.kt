package com.bitxflow.sungmin_android.biz.setting

import com.bitxflow.sungmin_android.biz.photo.Photo

import android.content.Context
import android.graphics.BitmapFactory
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bitxflow.sungmin_android.DB.User
import com.bitxflow.sungmin_android.R
import com.bitxflow.sungmin_android.biz.board.Reply
import com.bitxflow.sungmin_android.util.DownloadImage
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper
import com.ortiz.touchview.TouchImageView
import kotlinx.android.synthetic.main.home_list_header.*
import java.util.ArrayList

class MultyLoginListAdapter(
    context: Context,
    photoListItem: ArrayList<User>
) :
    BaseAdapter() {
    var mInflater: LayoutInflater
    var member: ArrayList<User>
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
        val res: Int = R.layout.multy_login_item
        val convertView = mInflater.inflate(res, parent, false)
        val name_tx =
            convertView.findViewById<View>(R.id.multy_login_name_tx) as TextView
        val img_iv =
            convertView.findViewById<View>(R.id.multy_login_iv) as ImageView

        name_tx.text = member[position].userName

        val str = member[position].imgSrc
//        if(str.equals("last"))
//        {
//            img_iv.setImageResource(R.drawable.profileimage)
//        }
        if(str!!.isNotEmpty())
        {
            val bmp = BitmapFactory.decodeFile(str)
            img_iv.setImageBitmap(bmp)
        }
        else{
            img_iv.setImageResource(R.drawable.profileimage)
        }


//        UrlImageViewHelper.setUrlDrawable(img_iv, member[position].)

        return convertView
    }

    init {
        mInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        member = photoListItem
        _context = context
    }
}