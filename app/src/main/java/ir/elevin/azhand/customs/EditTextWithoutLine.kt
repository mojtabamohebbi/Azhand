package ir.elevin.azhand.customs

import android.content.Context
import android.graphics.Color
import androidx.appcompat.widget.AppCompatEditText
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