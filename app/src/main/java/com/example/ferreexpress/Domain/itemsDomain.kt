package com.example.ferreexpress.Domain

import java.io.Serializable

public class itemsDomain(
    public var title: String = "",
    public var category: String = "",
    public var description: String = "",
    public var picUrl: ArrayList<String> = ArrayList(),
    public var price: Double = 0.0,
    public var oldPrice: Double = 0.0,
    public var review: Int = 0,
    public var rating: Double = 0.0,
    public var numberinCart: Int = 0
) : Serializable