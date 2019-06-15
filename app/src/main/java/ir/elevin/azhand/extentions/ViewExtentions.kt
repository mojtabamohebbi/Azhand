package ir.elevin.azhand.extentions

import androidx.core.view.ViewCompat
import android.view.View
import io.reactivex.Completable
import io.reactivex.subjects.CompletableSubject

fun View.fadeIn(duration: Long = 200): Completable {
    val animationSubject = CompletableSubject.create()
    return animationSubject.doOnSubscribe {
        ViewCompat.animate(this)
                .setDuration(duration)
                .alpha(1f)
                .withEndAction {
                    animationSubject.onComplete()
                }
    }
}