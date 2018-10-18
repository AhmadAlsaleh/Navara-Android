package com.smartlife_solutions.android.navara_store.Dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.smartlife_solutions.android.navara_store.R
import com.smartlife_solutions.android.navara_store.StaticInformation
import kotlinx.android.synthetic.main.dialog_sure_to_do.*

class SureToDoDialog(context: Context, var confirmMessage: String) : Dialog(context) {

    var isTrue: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_sure_to_do)

        // region font
        val myFont= StaticInformation().myFont(context)
        sureMessage.typeface = myFont
        noDeleteBTN.typeface = myFont
        yesDeleteBTN.typeface = myFont
        // endregion

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