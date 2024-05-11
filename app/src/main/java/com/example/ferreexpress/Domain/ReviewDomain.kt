package com.example.ferreexpress.Domain

import java.io.Serializable

class ReviewDomain : Serializable {

    var nameUser: String = ""
    var comentary: String = ""
    var PicUrl: String = ""
    var rating: Double = 0.0

    constructor(name: String, description: String, picUrl: String, rating: Double)
}
