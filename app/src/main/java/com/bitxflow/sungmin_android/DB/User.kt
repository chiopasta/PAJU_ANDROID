package com.bitxflow.sungmin_android.DB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
class User (@PrimaryKey(autoGenerate = true) var id: Int,
            @ColumnInfo(name = "user_name")  var userName: String? = null,
            @ColumnInfo(name = "user_id")    var userId: String? = null,
            @ColumnInfo(name = "class_name")    var className: String? = null,
            @ColumnInfo(name = "class_sid")    var classSid: String? = null,
            @ColumnInfo(name = "user_password")    var userPassword: String? = null,
            @ColumnInfo(name = "user_img_src")    var imgSrc: String? = null,
            @ColumnInfo(name = "multy_login")    var multy_login: Boolean? = null)
{
    constructor() : this(0,"","","","","","",false)
}

//    var userName : String ? = null
//    var userId : String? = null
//    var className : String? = null
//    var classSid : String? =null
//    var userPassword : String? = null

//}