package net.blwsmartware.qrcodescanner.base


import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import androidx.viewbinding.ViewBinding
import net.blwsmartware.qrcodescanner.app.countGrantCamera
import net.blwsmartware.qrcodescanner.app.countGrantContact
import net.blwsmartware.qrcodescanner.app.countGrantFile
import net.blwsmartware.qrcodescanner.app.countGrantNoti
import com.dong.baselib.api.hideSystemBar
import com.dong.baselib.api.setFullScreen
import net.blwsmartware.qrcodescanner.base.BaseActivity.TypeGoSettings
import net.blwsmartware.qrcodescanner.dialog.DialogGotoSetting
import net.blwsmartware.qrcodescanner.dialog.OnGotoSetting
import java.io.File
import java.io.Serializable
data class PermissionsState(var state: Boolean = false, var typeGoSettings: TypeGoSettings)
@Suppress("DEPRECATION")
abstract class BaseActivity<VB : ViewBinding>(
    val bindingFactory: (LayoutInflater) -> VB,
    private var fullStatus: Boolean = false,
    private var lightStatus: Boolean = true
) : AppCompatActivity() ,FragmentAttachEvent{
    val binding: VB by lazy { bindingFactory(layoutInflater) }
    private val statusBarHeight: Int
        @SuppressLint("DiscouragedApi", "InternalInsetResource") get() {
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            return if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 0
        }

    abstract fun backPressed()
    abstract fun initialize()
    abstract fun VB.setData()

    abstract fun VB.onClick()
    enum class TypeGoSettings {
        NONE, CAMERA, STORAGE, NOTIFICATION, CONTACT, LOCATION,
    }

    open fun closeFragment(fragment: Fragment) {
        if (fragment.isAdded && fragment.isVisible) {
            supportFragmentManager.beginTransaction()
                .remove(fragment)
                .commitNowAllowingStateLoss()
        }
    }

    fun copyToClipBoard(value: String, action: (() -> Unit)? = null) {
        val clipboard =
            this@BaseActivity.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Copy", value)
        clipboard.setPrimaryClip(clip)
        action?.invoke()
    }


    fun getMediaStoreUriFromFilePath(context: Context, filePath: String): Uri? {
        var uri: Uri? = null
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val selection = MediaStore.Images.Media.DATA + " = ?"
        val selectionArgs = arrayOf(filePath)

        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,  // Change to MediaStore.Video.Media or MediaStore.Audio.Media if needed
            projection, selection, selectionArgs, null
        )

        if (cursor != null && cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
            uri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toLong()
            )
            cursor.close()
        }

        return uri
    }

    fun getUriFromFile(filePath: String): Uri? {
        val file = File(filePath)
        return if (file.exists()) {
            FileProvider.getUriForFile(
                this@BaseActivity, "${this@BaseActivity.packageName}.provider", file
            )
        } else {
            null
        }
    }



    fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            val originalBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(contentResolver, uri)
                ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                    decoder.isMutableRequired = true
                }
            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, uri)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (originalBitmap.config == Bitmap.Config.HARDWARE) {
                    val softwareBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)
                    originalBitmap.recycle()
                    softwareBitmap
                } else {
                    originalBitmap
                }
            } else {
                originalBitmap
            }

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getData(key: String?, default: Serializable? = null): Serializable? {
        return if (!key.isNullOrEmpty() && intent?.extras?.containsKey(key) == true) {
            intent?.extras?.getSerializable(key)
        } else default
    }

    fun getData(key: String?): Serializable? {
        return if (key != null && intent?.extras != null && intent.extras!!.containsKey(key)) {
            intent.extras!!.getSerializable(key)
        } else null
    }



    open fun showKeyboard(view: View?) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }


    open fun showKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.root, InputMethodManager.SHOW_IMPLICIT)
    }

    open fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.rootView.windowToken, 0)
    }


    var requestCameraLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                countGrantCamera++
                if (countGrantCamera > 1) {
                    gotToSetting(TypeGoSettings.CAMERA){}
                }
            }
        }
    }




     val permissionsLiveData = MutableLiveData<PermissionsState?>(null)

    var requestFileLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        var allPermissionsGranted = true
        permissions.entries.forEach { entry ->
            if (!entry.value) {
                allPermissionsGranted = false
            }
        }
        if (!allPermissionsGranted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES)) {
                    countGrantFile++
                    if (countGrantFile > 1) {
                        permissionsLiveData.postValue(
                            PermissionsState(
                                true, TypeGoSettings.STORAGE
                            )
                        )
                    }
                }
            } else {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) && !shouldShowRequestPermissionRationale(
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    ) {
                        countGrantFile++
                        if (countGrantFile > 1) {
                            permissionsLiveData.postValue(
                                PermissionsState(
                                    true, TypeGoSettings.STORAGE
                                )
                            )
                        }
                    }
                }

            }
        }
    }
    var requestNotificationLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { permissions ->
        if (!permissions) {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                countGrantNoti++
                if (countGrantNoti > 1) {
                    val intent = when {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                            }
                        }
                        else -> {
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", packageName, null)
                            }
                        }
                    }
                    startActivity(intent)
                }
            }
        }
    }


    inline fun <reified T : Any> launchActivity(
        params: HashMap<String, Any>? = null,
    ) {
        val intent = newIntent<T>(this)
        val bundle = Bundle()
        params?.let { map ->
            for ((key, value) in map) {
                when (value) {
                    is Int -> bundle.putInt(key, value)
                    is String -> bundle.putString(key, value)
                    is Boolean -> bundle.putBoolean(key, value)
                    is Float -> bundle.putFloat(key, value)
                    is Long -> bundle.putLong(key, value)
                    is Double -> bundle.putDouble(key, value)
                    is Char -> bundle.putChar(key, value)
                    is CharSequence -> bundle.putCharSequence(key, value)
                    is Bundle -> bundle.putBundle(key, value)
                    // Add more types as needed
                    else -> throw IllegalArgumentException("Unsupported bundle component (${value.javaClass})")
                }
            }
            intent.putExtras(bundle)
        }
        startActivity(intent, bundle)
    }

    open fun listenerResult(result: ActivityResult) {}
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.data != null) {
            Log.d("ActivityResult", "Received data: ${result.data?.extras}")
        } else {
            Log.d("ActivityResult", "Result data is null!")
        }

        listenerResult(result)
        activityResultCallback?.invoke(result)
        activityResultCallback = null
    }
    var activityResultCallback: ((ActivityResult) -> Unit)? = null

    inline fun <reified T : Any> launcherForResult(
        params: HashMap<String, Any>? = null,
        noinline dataResult: (ActivityResult) -> Unit = { _ -> }
    ) {
        activityResultCallback = dataResult
        val intent = Intent(this, T::class.java)
        val bundle = Bundle()

        params?.forEach { (key, value) ->
            when (value) {
                is Int -> bundle.putInt(key, value)
                is String -> bundle.putString(key, value)
                is Boolean -> bundle.putBoolean(key, value)
                is Float -> bundle.putFloat(key, value)
                is Long -> bundle.putLong(key, value)
                is Double -> bundle.putDouble(key, value)
                is Char -> bundle.putChar(key, value)
                is CharSequence -> bundle.putCharSequence(key, value)
                is Bundle -> bundle.putBundle(key, value)
                else -> throw IllegalArgumentException("Unsupported bundle component (${value.javaClass})")
            }
        }

        intent.putExtras(bundle)
        resultLauncher.launch(intent)
    }

    val requestContactsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        var allPermissionsGranted = true
        permissions.entries.forEach { entry ->
            if (!entry.value) {
                allPermissionsGranted = false
            }
        }
        if (!allPermissionsGranted) {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) && !shouldShowRequestPermissionRationale(
                    Manifest.permission.WRITE_CONTACTS
                )
            ) {
                countGrantContact++
                if (countGrantContact > 1) {
                    permissionsLiveData.postValue(
                        PermissionsState(
                            true, TypeGoSettings.CONTACT
                        )
                    )
                }
            }
        }
    }

    fun lightStatusBar(status: Boolean) {
        val insertController = WindowCompat.getInsetsController(window, binding.root)
        insertController.apply {
            hide(WindowInsetsCompat.Type.systemBars())
            show(WindowInsetsCompat.Type.statusBars())
            hide(WindowInsetsCompat.Type.navigationBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            isAppearanceLightStatusBars = status
        }
    }

    fun hideNavigation() {
        val windowInsetsController = if (Build.VERSION.SDK_INT >= 30) {
            ViewCompat.getWindowInsetsController(window.decorView)
        } else {
            WindowInsetsControllerCompat(window, binding.root)
        }

        windowInsetsController?.let {
            it.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            it.hide(WindowInsetsCompat.Type.navigationBars())

            window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
                if (visibility == 0) {
                    Handler().postDelayed({
                        val controller = if (Build.VERSION.SDK_INT >= 30) {
                            ViewCompat.getWindowInsetsController(window.decorView)
                        } else {
                            WindowInsetsControllerCompat(window, binding.root)
                        }
                        controller?.hide(WindowInsetsCompat.Type.navigationBars())
                    }, 3000)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        SystemUtil.setLocale(this@BaseActivity, SystemUtil.getPreLanguage(this@BaseActivity)?:"en")
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(binding.root)
        val paddingTop = binding.root.paddingTop + statusBarHeight
        if (!fullStatus) {
            binding.root.setPadding(
                binding.root.paddingLeft,
                paddingTop,
                binding.root.paddingRight,
                binding.root.paddingBottom
            )
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backPressed()
            }
        })
        initialize()

        binding.setData()
        binding.onClick()
        window.hideSystemBar()
        window.setFullScreen()
        hideNavigation()


    }

    fun addFragment(
        fragment: Fragment,
        containerId: Int = android.R.id.content,
        addToBackStack: Boolean = false
    ) {
        hideKeyboard()

        val tag = fragment.javaClass.simpleName
        val existingFragment = supportFragmentManager.findFragmentByTag(tag)
        if (existingFragment != null && existingFragment.isAdded) {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    com.dong.baselib.R.anim.enter_from_right,
                    com.dong.baselib.R.anim.exit_to_left,
                    com.dong.baselib.R.anim.enter_from_left,
                    com.dong.baselib.R.anim.exit_to_right
                )
                .show(existingFragment)
                .commitAllowingStateLoss()
            return
        }
        supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(
                com.dong.baselib.R.anim.enter_from_right,
                com.dong.baselib.R.anim.exit_to_left,
                com.dong.baselib.R.anim.enter_from_left,
                com.dong.baselib.R.anim.exit_to_right
            )
            supportFragmentManager.fragments.lastOrNull()?.let { hide(it) }
            add(containerId, fragment, tag)

            if (addToBackStack) {
                addToBackStack(tag)
            }
            commitAllowingStateLoss()
        }
    }

    // Alternative version with replace instead of add
    fun replaceFragment(
        fragment: Fragment,
        containerId: Int = android.R.id.content,
        addToBackStack: Boolean = false
    ) {
        hideKeyboard()

        val tag = fragment.javaClass.simpleName

        supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(
                com.dong.baselib.R.anim.enter_from_right,
                com.dong.baselib.R.anim.exit_to_left,
                com.dong.baselib.R.anim.enter_from_left,
                com.dong.baselib.R.anim.exit_to_right
            )

            replace(containerId, fragment, tag)

            if (addToBackStack) {
                addToBackStack(tag)
            }

            commitAllowingStateLoss()
        }
    }

    // Extension to handle fragment animations
    private fun FragmentTransaction.setDefaultAnimations() = apply {
        setCustomAnimations(
            com.dong.baselib.R.anim.enter_from_right,
            com.dong.baselib.R.anim.exit_to_left,
            com.dong.baselib.R.anim.enter_from_left,
            com.dong.baselib.R.anim.exit_to_right
        )
    }


    fun shouldShowDialog(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                return true
            }
        }
        return false
    }

    var dialogGotoSetting: DialogGotoSetting? = null

    open fun gotToSetting(typeGoSettings: TypeGoSettings, onDeny: () -> Unit={}) {
        dialogGotoSetting =
            DialogGotoSetting(typeGoSettings, this@BaseActivity, false, object : OnGotoSetting {
                override fun onAgree() {
                    dialogGotoSetting?.dismiss()
                    val intent = Intent()
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.setData(uri)
                    startActivity(intent)
                }

                override fun onDeny() {
                    onDeny.invoke()
                }
            })
        dialogGotoSetting?.show()
    }


    override fun onDestroy() {
        super.onDestroy()


    }

    override fun onPause() {
        super.onPause()


    }

    override fun onRestart() {
        super.onRestart()

    }

}