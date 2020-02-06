package com.bitxflow.sungmin_android.biz.board

class Reply {
    var id: String? = null
    var name: String? = null
    var contents: String? = null

    constructor() {}
    constructor(id: String?, name: String?, contents: String?) : super() {
        this.id = id
        this.name = name
        this.contents = contents
    }

}
