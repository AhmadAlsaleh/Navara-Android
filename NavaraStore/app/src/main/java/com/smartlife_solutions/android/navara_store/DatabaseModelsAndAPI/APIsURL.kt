package com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI

class APIsURL {

    val BASE_URL = "http://api.navarastore.com/"

    // region user account
    val USER_URL = BASE_URL + "Users/"
    val REGISTER_URL = USER_URL + "Register/"
    val LOGIN_URL = USER_URL + "SignIn/"
    val LOGOUT_URL = USER_URL + "SignOut/"
    val CHANGE_PASSWORD_URL = USER_URL + "ChangePassword/"
    val CONFIRM = USER_URL + "Confirm?"
    val RESEND_CODE = USER_URL + "ResendCode/"
    val RESET_PASSWORD = USER_URL + "ResetPasswordOrder/"

    val ACCOUNT_URL = BASE_URL + "Account/"
    val GET_USER_INFORMATION = ACCOUNT_URL + "GetInformation/"
    val UPDATE_USER_INFORMATION = ACCOUNT_URL + "UpdateInformation/"
    val GET_CART = ACCOUNT_URL + "GetCart/"
    val GET_ORDERS = ACCOUNT_URL + "GetOrders/"
    // endregion

    val ORDERS_URL = BASE_URL + "Orders/"
    val CREATE_ORDER = ORDERS_URL + "CreateOrder/"
    val GET_ORDER = ORDERS_URL + "Get/"

    // region items

    val ITEMS_URL = BASE_URL + "items/"
    val BASIC_ITEMS = ITEMS_URL + "GetBasic/"
    val GET_ITEM = ITEMS_URL + "Get/"
    // endregion

    // region Category

    val CATEGORY_URL = BASE_URL + "ItemCategories/"
    val CATEGORY_GET = CATEGORY_URL + "Get/"

    // endregion

    // region Offers

    val OFFERS_URL = BASE_URL + "Offers/"
    val OFFERS_GET = OFFERS_URL + "GetBasic/"
    val GET_OFFER = OFFERS_URL + "Get/"

    // endregion

    // region operations

    val OPERATIONS_URL = BASE_URL + "operations/"
    val CONTACT_US_URL = OPERATIONS_URL + "contactUs/"

    // endregion

    // region cart

    val CART_URL = BASE_URL + "Cart/"
    val ADD_ITEM_TO_CART = CART_URL + "AddItemToCart/"
    val ADD_OFFER_TO_CART = CART_URL + "AddOfferToCart/"
    val REMOVE_FROM_CART = CART_URL + "RemoveFromCart/"
    val REMOVE_OFFER_FROM_CART = CART_URL + "RemoveOfferFromCart/"

    // endregion

}