# permission-helper
[![](https://jitpack.io/v/yfbx-repo/permission-helper.svg)](https://jitpack.io/#yfbx-repo/permission-helper)
[![License](https://img.shields.io/github/license/yfbx-repo/permission-helper)](https://mit-license.org/)
[![](https://img.shields.io/badge/release-1.0.0-blue.svg)](https://github.com/yfbx-repo/permission-helper/releases)    

使用新API`registerForActivityResult`进行权限请求.    
解决 `LifecycleOwners must call register before they are STARTED.` 问题.    

### 一、使用方法
```
repositories {
    maven { url 'https://jitpack.io' }
}


dependencies {
    implementation 'com.github.yfbx-repo:permission-helper:1.0.0'
}
```

```
private fun requestCamera() {
   require(Manifest.permission.CAMERA) {
       onGrant {
           //获得授权
       }

       onDeny {
           //拒绝授权
       }
   }
}

```

### 二、核心代码
```
fun <I, O> ComponentActivity.registerForResult(
    contract: ActivityResultContract<I, O>,
    callback: ActivityResultCallback<O>
): ActivityResultLauncher<I> {
    //调用这个方法不用传生命周期，但需要调用{@link ActivityResultLauncher#unregister()}解除注册
    return activityResultRegistry.register(
        "activity_rq#" + mNextLocalRequestCode.getAndIncrement(), contract, callback
    )
}
```

注意，此方法未关联生命周期，拿到`ActivityResultLauncher`后，需要手动调用`unregister`解除注册.    
可以再写一个扩展，在结果回调之后立即解除注册：

```
fun ComponentActivity.registerForPermissions(callback: PermissionsCallback): ActivityResultLauncher<Array<out String>> {
    var launcher: ActivityResultLauncher<Array<out String>>? = null
    launcher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            callback.invoke(it)
            //回调之后立即解除注册
            launcher?.unregister()
        }
    return launcher
}
```

### 三、由于隐私合规等问题，可以进一步封装
```

/**
 * 1. 请求权限之前，说明权限用途
 * 2. 请求权限
 * 3. 权限拒绝后，提示功能不可用
 */
fun Context.require(
    vararg permissions: String,
    tip: String,
    alert: String,
    callback: VoidCallback
) {
    showTipDialog(tip) {
        if (it) {
            require(*permissions) {
                onGrant {
                    callback.invoke()
                }
                onDeny {
                    showAlertDialog(alert)
                }
            }
        }
    }
}
```

可以在单独文件中统一管理全局权限请求
```

/**
 * 请求相机权限
 */
fun Context.requireCamera(callback: () -> Unit) = require(
    Manifest.permission.CAMERA,
    tip = "我们需要使用您的相机，以实现拍照上传功能",
    alert = "未获得授权，相机不可用",
    callback = callback
)

```

这样，实际使用时，只需要关心获得授权的逻辑

```
 private fun takePhoto() = requireCamera {
    //调用相机
 }
```
