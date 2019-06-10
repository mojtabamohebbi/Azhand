package ir.elevin.mykotlinapplication.Customs

import android.content.Context
import android.graphics.Color
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatEditText
import ir.elevin.mykotlinapplication.R
import android.graphics.PorterDuff
import android.util.AttributeSet

class EditTextWithoutLine : AppCompatEditText {

    constructor(
            context: Context)
            : super(context)

    constructor(
            context: Context,
            attrs: AttributeSet? = null)
            : super(context, attrs)

    constructor(
            context: Context,
            attrs: AttributeSet?,
            defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    init {
        this.background.mutate().setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
    }

}