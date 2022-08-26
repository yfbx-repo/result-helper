package com.yfbx.helper

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import java.util.concurrent.atomic.AtomicInteger

/**
 * Date: 2022-08-25
 * Author: Edward
 * Desc:
 */
private val mNextLocalRequestCode = AtomicInteger()


inline fun <reified T> Context.start(init: IntentBuilder.() -> Unit) {
    val intent = IntentBuilder().apply(init)
    intent.setClass(this, T::class.java)
    start(intent) {
        intent.callback?.invoke(it)
    }
}


class IntentBuilder : Intent() {
    var callback: ((ActivityResult) -> Unit)? = null

    fun onResult(callback: (ActivityResult) -> Unit) {
        this.callback = callback
    }
}

fun Context.start(intent: Intent, callback: ((ActivityResult) -> Unit)) {
    start(intent, ActivityResultContracts.StartActivityForResult()) {
        callback.invoke(it)
    }
}


/**
 * 注册请求，回调之后立即解除注册
 */
fun <I, O> Context.start(
    input: I,
    contract: ActivityResultContract<I, O>,
    callback: ActivityResultCallback<O>
) {
    require(this is ComponentActivity) { "Context must be ComponentActivity" }
    var launcher: ActivityResultLauncher<I>? = null
    launcher = registerForResult(contract) {
        callback.onActivityResult(it)
        launcher?.unregister()
    }
    launcher.launch(input)
}

/**
 * LifecycleOwners must call register before they are STARTED.
 *
 * When calling this, you must call {@link ActivityResultLauncher#unregister()} on the
 * returned {@link ActivityResultLauncher} when the launcher is no longer needed to
 * release any values that might be captured in the registered callback.
 *
 */
fun <I, O> ComponentActivity.registerForResult(
    contract: ActivityResultContract<I, O>,
    callback: ActivityResultCallback<O>
): ActivityResultLauncher<I> {
    //调用这个方法不用传生命周期，但需要调用{@link ActivityResultLauncher#unregister()}解除注册
    return activityResultRegistry.register(
        "activity_result_rq#" + mNextLocalRequestCode.getAndIncrement(), contract, callback
    )
}