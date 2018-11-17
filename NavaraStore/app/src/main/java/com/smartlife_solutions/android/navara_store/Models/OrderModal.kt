package com.smartlife_solutions.android.navara_store.Models

import java.io.Serializable

class OrderModal(var accountId: String, var name: String, var phone: String, var date: String,
                 var fromTime: String, var toTime: String, var lat: Double, var lng: Double, var status: String,
                 var totalPrice: Float, var currencyCode: String = "S.P", var remark: String = "",
                 var itemsArrayList: ArrayList<ItemModel>? = null,
                 var code: String = "", var id: String = "") : Serializable