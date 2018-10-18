package com.smartlife_solutions.android.navara_store.Models

class ItemModel(var id: String, var title: String, var unitPrice: Float,
                var imageURL: String, var quantity: Int = 1, var isChecked: Boolean = false,
                var currencyCode: String = "S.P")