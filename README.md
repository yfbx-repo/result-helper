# result-helper
[![](https://jitpack.io/v/yfbx-repo/result-helper.svg)](https://jitpack.io/#yfbx-repo/result-helper)
[![License](https://img.shields.io/github/license/yfbx-repo/result-helper)](https://mit-license.org/)
[![](https://img.shields.io/badge/release-1.0.0-blue.svg)](https://github.com/yfbx-repo/result-helper/releases)

使用新API`registerForActivityResult`处理跳转返回.    
解决 `LifecycleOwners must call register before they are STARTED.` 问题.        

### 一、使用方法
```
repositories {
    maven { url 'https://jitpack.io' }
}


dependencies {
    implementation 'com.github.yfbx-repo:result-helper:1.0.0'
}
```

```
/**
 * Activity 跳转
 */
private fun startTestActivity() {
    start<TestActivity> {
        putExtra("key", "value")

        //onActivityResult
        onResult { result ->
            val msg = result.data?.getStringExtra("data")
            Toast.makeText(this@MainActivity, msg, Toast.LENGTH_LONG).show()
        }
    }
}

/**
 * 拍照
 */
private fun startCamera() {
    start(null, ActivityResultContracts.TakePicturePreview()) {
        imageView.setImageBitmap(it)
    }
}

```
ActivityResultContracts 提供的默认协议类：
|协议类|入参|返回|作用|
|---|---|---|---|
|StartActivityForResult()|Intent|ActivityResult|通用协议|
|TakePicturePreview()|null|Bitmap|拍照|
|TakePicture()|Uri|Boolean|拍照|
|TakeVideo()|Uri|Bitmap|拍视频|
|GetContent()|String(文件类型)|Uri|获取单个文件|
|GetMultipleContents()|String(文件类型)|List\<Uri\>|获取多个文件|
|CreateDocument()|String(文件名)|Uri|创建文件|
|OpenDocument()|Array\<String\>(mime type)|Uri|打开文件|
|OpenMultipleDocuments()|Array\<String\>(mime type)|List\<Uri\>|打开多个文件|
|OpenDocumentTree()|Uri|Uri|打开文件目录|
|PickContact()|null|Uri|选择联系人|
|RequestPermission()|String|Boolean|请求权限|
|RequestPermission()|Array\<String\>|Map\<String,Boolean\>|请求多个权限|


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
可以在生命周期onDestroy时解除注册，也可以再写一个扩展，在结果回调之后立即解除注册：    

```
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
```
