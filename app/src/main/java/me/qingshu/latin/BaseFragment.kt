package me.qingshu.latin

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.qingshu.latin.extensions.sharedPreferences
import me.qingshu.latin.model.MainViewModel
import me.qingshu.latin.model.VoiceViewModel
import kotlin.coroutines.CoroutineContext

abstract class BaseFragment<T:ViewBinding>:Fragment(),CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

    protected val navOptions = NavOptions.Builder()
        .setLaunchSingleTop(true)
        .setEnterAnim(R.anim.enter_in)
        .setPopEnterAnim(R.anim.enter_out)
        .setExitAnim(R.anim.exit_in)
        .setPopExitAnim(R.anim.exit_out)
        .build()

    private var _binding: T? = null

    protected val binding get() = _binding!!

    protected val useContext:Context get() = binding.root.context

    protected val sharedPreference get() = useContext.sharedPreferences()

    abstract fun layout():T
    open fun onClicks(){}

    protected val voiceViewModel: VoiceViewModel by activityViewModels()
    protected val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = layout()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClicks()
    }

    protected inline fun Fragment.repeatWithViewLifecycle(
        minState: Lifecycle.State = Lifecycle.State.STARTED,
        crossinline block: suspend CoroutineScope.() -> Unit
    ) {
        if (minState == Lifecycle.State.INITIALIZED || minState == Lifecycle.State.DESTROYED) {
            throw IllegalArgumentException("minState must be between INITIALIZED and DESTROYED")
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(minState) {
                block()
            }
        }
    }

    protected fun getColorForAttrId(@AttrRes resId:Int):Int{
        val typedValue = TypedValue()
        val a = useContext.obtainStyledAttributes(typedValue.data, intArrayOf(resId))
        val color = a.getColor(0, Color.BLUE)
        a.recycle()
        return color
    }

}