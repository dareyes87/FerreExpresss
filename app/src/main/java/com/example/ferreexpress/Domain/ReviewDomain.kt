package com.example.ferreexpress.Domain

import java.io.Serializable

class ReviewDomain(
    var nameUser: String = "",
    var comentary: String = "",
    var picUrl: String = "",
    var rating: Double = 0.0
) : Serializable
