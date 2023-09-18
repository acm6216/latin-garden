package me.qingshu.latin.preference

import android.os.Bundle
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import me.qingshu.latin.R
import me.qingshu.latin.extensions.dp

class Developer: BasePreferenceFragment() {

    override fun isShowIcon(): Boolean {
        return false
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.abouts, rootKey)
    }

    override fun apply(recyclerView: RecyclerView) {
        recyclerView.clipToPadding = false
        recyclerView.updatePadding(recyclerView.paddingLeft,recyclerView.paddingTop,recyclerView.paddingRight,56.dp)
    }
}