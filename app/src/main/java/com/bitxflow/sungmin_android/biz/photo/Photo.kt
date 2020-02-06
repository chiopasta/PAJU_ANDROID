package com.bitxflow.sungmin_android.biz.photo

class Photo {
    var boardSid: String? = null
    var title: String? = null
    var contents: String? = null
    var pUrl: String? = null

    constructor() {}
    constructor(boardSid: String?, name: String?, contents: String?, pUrl: String? ) : super() {
        this.boardSid = boardSid
        this.title = name
        this.contents = contents
        this.pUrl = pUrl
    }

}
