package com.bitxflow.sungmin_android.biz.setting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.bitxflow.sungmin_android.DB.MemberDatabase
import com.bitxflow.sungmin_android.R
import com.bitxflow.sungmin_android.biz.board.BoardActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_setting.*
import kotlinx.android.synthetic.main.fragment_setting.view.*

class SettingFragment : Fragment() {

    private var user_id: String = ""
    private var userDB: MemberDatabase? = null
    private var isAbsent: Boolean = false

//    private val noticeList: ArrayList<String>? = null
//    private val noticeDateList: ArrayList<String>? = null
//    private val noticeContentList: ArrayList<String>? = null

    private var adapter: ArrayAdapter<String>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user_id = arguments!!.getString("user_id").toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting, null)
        activity!!.main_progressbar.visibility = View.GONE

        view.setting_account_bt.setOnClickListener{
            val nextIntent = Intent(context, AccountActivity::class.java)
            nextIntent.putExtra("user_id", user_id)
            startActivity(nextIntent)
        }
        view.setting_multy_login_bt.setOnClickListener{
            val nextIntent = Intent(context, MultyLoginActivity::class.java)
            startActivity(nextIntent)
        }

        if(isAdded) {
            activity!!.nav_home_ll.isClickable = true
            activity!!.nav_photo_ll.isClickable = true
            activity!!.nav_letter_ll.isClickable = true
            activity!!.nav_plan_ll.isClickable = true
            activity!!.nav_all_board_ll.isClickable = true
            activity!!.nav_board_ll.isClickable = true
            activity!!.nav_setting_ll.isClickable = true
            activity!!.nav_calendar_ll.isClickable = true
        }
        return view
    }
}