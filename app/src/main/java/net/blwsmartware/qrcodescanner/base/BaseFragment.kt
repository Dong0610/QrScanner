@file:Suppress("DEPRECATION")

package net.blwsmartware.qrcodescanner.base

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ScrollView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.INPUT_METHOD_SERVICE
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import net.blwsmartware.qrcodescanner.app.countGrantCamera
import net.blwsmartware.qrcodescanner.app.countGrantContact
import net.blwsmartware.qrcodescanner.base.BaseActivity.TypeGoSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.io.Serializable


abstract class BaseFragment<VB : ViewBinding>(val bindingFactory: (LayoutInflater) -> VB,private var isFullSc: Boolean = false) : Fragment() {

    lateinit var appContext: Context
    var fragmentAttach: FragmentAttachEvent? = null

    val binding: VB by lazy { bindingFactory(layoutInflater) }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            fragmentAttach = context as FragmentAttachEvent?
        }catch (e: Exception){
            Log.e("BaseFragment", "Class cast exception: ${e.message}")
        }

        this@BaseFragment.appContext = context
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
            countGrantContact++
        }
    }


    val requestWriteSetingLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { permissions ->

        if (!permissions) {
            if (!shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_SETTINGS)) {
                countGrantContact++

            }

        }
    }

    fun lightStatusBar(status: Boolean) {
        val insertController =
            WindowCompat.getInsetsController(requireActivity().window, binding.root)
        insertController.apply {
            hide(WindowInsetsCompat.Type.systemBars())
            show(WindowInsetsCompat.Type.statusBars())
            hide(WindowInsetsCompat.Type.navigationBars())
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            isAppearanceLightStatusBars = status
        }
    }


    fun newInstance(data: HashMap<String, Serializable>?): BaseFragment<VB> {
        val fragment = this
        val args = Bundle()
        if (data != null) {
            for ((key, value) in data) {
                args.putSerializable(key, value)
            }
        }
        fragment.setArguments(args)
        return fragment
    }

    fun getData(key: String?): Serializable? {
        val args = arguments
        return if (args != null && args.containsKey(key)) {
            args.getSerializable(key)
        } else null
    }



    inline fun <reified T : Any> launchActivity(
        params: HashMap<String, Any>? = null,
    ) {
        val intent = newIntent<T>(appContext)
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
        val intent = Intent(appContext, T::class.java)
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
        resultLauncher.launch(intent) // ✅ Launch AFTER setting callback
    }
    fun View.isBackgroundTransparent(): Boolean {
        val background = this.background
        return background is ColorDrawable && background.color == Color.TRANSPARENT
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        if (binding.root.isBackgroundTransparent()) {
            binding.root.setBackgroundColor(Color.WHITE)
        }
        binding.root.isClickable = true

        return binding.root
    }

    val statusBarHeight: Int
        @SuppressLint("DiscouragedApi", "InternalInsetResource")
        get() {
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            return if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 0
        }

    open fun backPress() {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isFullSc) {
            notFullView()
        }
        getData()
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    backPress()
                }
            })
        binding.initView()
        binding.onClick()
    }

    open fun scrollToTopRcv() {

    }


    open fun getData() {
    }


    abstract fun VB.initView()
    abstract fun VB.onClick()


    fun notFullView() {
        val paddingTop = binding.root.paddingTop + statusBarHeight
        binding.root.setPadding(
            binding.root.paddingLeft,
            paddingTop,
            binding.root.paddingRight,
            binding.root.paddingBottom
        )

    }

    open fun showKeyboard(view: View?) {
        val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }


    open fun showKeyboard() {
        val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.root, InputMethodManager.SHOW_IMPLICIT)
    }

    open fun hideKeyboard() {
        val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireActivity().window.decorView.rootView.windowToken, 0)
    }

    fun addFragment(fragment: Fragment) {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(id, fragment)
        transaction?.commitAllowingStateLoss()
    }

    fun addFragment(
        fragment: Fragment,
        id: Int = android.R.id.content,
        addToBackStack: Boolean = false
    ) {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.add(id, fragment)
        if (addToBackStack) {
            transaction?.addToBackStack(fragment.javaClass.simpleName)
        }
        transaction?.commitAllowingStateLoss()
    }

    fun replaceFullViewFragment(fragment: Fragment, addToBackStack: Boolean) {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(id, fragment)
        if (addToBackStack) {
            transaction?.addToBackStack(fragment.javaClass.simpleName)
        }
        transaction?.commitAllowingStateLoss()
    }


    fun replaceFragment(
        fragment: Fragment,
        id: Int = android.R.id.content,
        addToBackStack: Boolean = true
    ) {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(id, fragment)
        if (addToBackStack) {
            transaction?.addToBackStack(fragment.javaClass.simpleName)
        }
        transaction?.commitAllowingStateLoss()
    }

    open fun closeFragment(fragment: Fragment) {
        val activity = fragment.activity
        if (activity != null && !activity.isFinishing && !activity.isDestroyed) {
            activity.supportFragmentManager.beginTransaction()
                .setCustomAnimations(com.dong.baselib.R.anim.enter_from_right, com.dong.baselib.R.anim.exit_to_left)
                .remove(fragment)
                .commitNowAllowingStateLoss()
        }
    }

    fun Fragment.closeSelf() {
        activity?.let {
            if (!it.isFinishing && !it.isDestroyed) {
                it.supportFragmentManager.beginTransaction()
                    .setCustomAnimations(com.dong.baselib.R.anim.enter_from_right, com.dong.baselib.R.anim.exit_to_left)
                    .remove(this)
                    .commitNowAllowingStateLoss()
            }
        }
    }


    fun moveImageToGallery(cacheFilePath: String): Uri? {
        val cacheFile = File(cacheFilePath)

        if (!cacheFile.exists()) {
            return null
        }

        val contentResolver = requireActivity().contentResolver
        val uri: Uri?

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            // Android 10 and above (API level 29+)
            val contentValues = android.content.ContentValues().apply {
                put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, cacheFile.name)
                put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/QR Codes")
            }

            uri = contentResolver.insert(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )

            uri?.let {
                contentResolver.openOutputStream(it)?.use { outputStream ->
                    cacheFile.inputStream().use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                // Optionally delete the file from cache after moving
            }
        } else {
            // Android 8 and below (API level < 29)
            val picturesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .toString()
            val qrCodesDir = File(picturesDir, "QR Codes")
            if (!qrCodesDir.exists()) {
                qrCodesDir.mkdirs()
            }

            val newFile = File(qrCodesDir, cacheFile.name)
            try {
                cacheFile.copyTo(newFile, overwrite = true)
                uri = Uri.fromFile(newFile)

                val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                mediaScanIntent.data = uri
                requireActivity().sendBroadcast(mediaScanIntent)
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }
        }

        return uri
    }


    private fun saveImageToGallery(bitmap: Bitmap, fileName: String): Uri? {
        val contentValues = android.content.ContentValues().apply {
            put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/QR Codes")
        }
        val contentResolver = requireContext().contentResolver
        val uri = contentResolver.insert(
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )

        uri?.let {
            contentResolver.openOutputStream(it)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
        }
        return uri
    }

    fun reconvertUnicodeToEmoji(unicodeStr: String?): String {
        val regex = Regex("""\\u([0-9A-Fa-f]{4})""")
        if (unicodeStr == null) return ""
        else {
            return regex.replace(unicodeStr) { matchResult ->
                val codePoint =
                    matchResult.groupValues[1].toInt(16) // Convert hex code to an integer
                String(Character.toChars(codePoint)) // Convert the code point to a character (emoji)
            }
        }
    }

    private fun scrollToTop(view: View) {
        when (view) {
            is ScrollView -> {
                if (view.scrollY == 0) return // Skip if already at the top
                view.scrollTo(0, 0)
                view.post {
                    view.fullScroll(View.FOCUS_UP)
                    view.fling(0)
                    view.smoothScrollTo(0, 0)
                }
                view.requestLayout()
                for (i in 0 until view.childCount) {
                    scrollToTop(view.getChildAt(i))
                }
                Log.d("ScrollApp", "ScrollView scrolled to top")
            }

            is NestedScrollView -> {
                if (view.scrollY == 0) return // Skip if already at the top
                view.scrollTo(0, 0)
                view.post {
                    view.fullScroll(View.FOCUS_UP)
                    view.fling(0)
                    view.smoothScrollTo(0, 0)
                }
                view.requestLayout()
                for (i in 0 until view.childCount) {
                    val child = view.getChildAt(i)
                    if (child is RecyclerView) {
                        if (!child.canScrollVertically(-1)) return // Skip if RecyclerView is already at the top
                        child.scrollToPosition(0)
                    } else {
                        scrollToTop(child)
                    }
                }
                Log.d("ScrollApp", "NestedScrollView scrolled to top")
            }

            is RecyclerView -> {
                if (!view.canScrollVertically(-1)) return // Skip if RecyclerView is already at the top
                view.scrollToPosition(0)
                Log.d("ScrollApp", "RecyclerView scrolled to top")
            }

            is ViewGroup -> {
                for (i in 0 until view.childCount) {
                    scrollToTop(view.getChildAt(i))
                }
            }

            else -> {
                Log.d("ScrollApp", "View is not scrollable")
            }
        }
    }


    override fun onResume() {
        super.onResume()

    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
