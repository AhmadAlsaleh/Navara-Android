package com.smartlife_solutions.android.navara_store.Dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.smartlife_solutions.android.navara_store.R
import com.smartlife_solutions.android.navara_store.StaticInformation
import com.smartlife_solutions.android.navara_store.Statics
import kotlinx.android.synthetic.main.dialog_sure_to_do.*

class SureToDoDialog(var mContext: Context, var confirmMessage: String) : Dialog(mContext) {

    var isTrue: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_sure_to_do)
        val lang = try {
            Statics.getLanguageJSONObject(mContext as Activity)
        } catch (err: Exception) {
            null
        }
        // region font
        val myFont = StaticInformation().myFont(context)
        sureMessage.typeface = myFont
        noDeleteBTN.typeface = myFont
        yesDeleteBTN.typeface = myFont
        // endregion


        if (lang != null) {
            yesDeleteBTN.text = lang.getString("yes")
            noDeleteBTN.text = lang.getString("no")
        }
        sureMessage.text = confirmMessage

        noDeleteBTN.setOnClickListener {
            this.isTrue = false
            dismiss()
        }

        yesDeleteBTN.setOnClickListener {
            this.isTrue = true
            dismiss()
        }

    }

}