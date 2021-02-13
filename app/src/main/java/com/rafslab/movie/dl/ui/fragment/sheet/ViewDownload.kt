package com.rafslab.movie.dl.ui.fragment.sheet

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageButton
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import com.rafslab.movie.dl.R
import com.rafslab.movie.dl.adapter.ResolutionAdapter
import com.rafslab.movie.dl.adapter.TabAdapter
import com.rafslab.movie.dl.model.child.ChildData
import com.rafslab.movie.dl.model.child.Download
import com.rafslab.movie.dl.model.child.ResolutionValue
import com.rafslab.movie.dl.ui.fragment.ItemDownloadFragment
import java.util.*

/**
 * Created by: Rais AlFani Lubis
 * Date: October 18, 2020
 */

class ViewDownload : SuperBottomSheetFragment(){
    companion object {
        fun newInstance(data: ChildData): ViewDownload{
            val args = Bundle()
            args.putSerializable("data", data)
            val fragment = ViewDownload()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var closeExpanded : ImageButton

    private lateinit var appBar : AppBarLayout
    private lateinit var toolbarContainer : RelativeLayout
    private lateinit var toolbar : Toolbar
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var resolutionSelected: AppCompatTextView
    private lateinit var resolutionDropDown: ImageFilterView
    private lateinit var resolutionList: RecyclerView
    private lateinit var resolutionSelection: MotionLayout
    private lateinit var saveDataResolution: SharedPreferences
    private lateinit var mPrefsEdit: SharedPreferences.Editor
    private val mSharedKey = "resolutionPrefs"
    private val mResolutionKey = "resolutionKey"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.download_bottom_sheet, container,false)
        closeExpanded = view.findViewById(R.id.close_bottom_sheet)
        appBar = view.findViewById(R.id.app_bar)
        tabLayout = view.findViewById(R.id.tab_layout)
        viewPager = view.findViewById(R.id.view_pager)
        toolbarContainer = appBar.findViewById(R.id.toolbar_container)
        toolbar = toolbarContainer.findViewById(R.id.toolbar)
        resolutionSelected = view.findViewById(R.id.resolution_selected)
        resolutionList = view.findViewById(R.id.resolution_list)
        resolutionSelection = view.findViewById(R.id.resolution_selection)
        resolutionDropDown = view.findViewById(R.id.drop_down)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        saveDataResolution = context?.getSharedPreferences(mSharedKey, Context.MODE_PRIVATE)!!
        mPrefsEdit = saveDataResolution.edit()
        closeExpanded.setOnClickListener {
        }
        resolutionSelected.setOnClickListener {
            resolutionSelection.setTransition(R.id.resolution_selection_scene)
            resolutionSelection.transitionToEnd()
        }
        resolutionDropDown.setOnClickListener {
            resolutionSelection.setTransition(R.id.resolution_selection_scene)
            resolutionSelection.transitionToEnd()
        }
        closeExpanded.setOnClickListener {
            dismiss()
        }
        toolbar.title = "Download"
        if (arguments != null){
            val childData = requireArguments().getSerializable("data") as ChildData
            val path = childData.downloads[0].resolution.resolutionValues[0].values.episode.toLowerCase(Locale.getDefault())
            getDataTabLayout(childData, tabLayout, viewPager, path, childData.downloads, 0)
            val restoreResolutionData = saveDataResolution.getString(mResolutionKey, "360p")
            if (restoreResolutionData != null){
                resolutionSelected.text = restoreResolutionData
            } else {
                resolutionSelected.text = childData.downloads[0].resolution.resolutionValues[0].name
            }
        }
    }
    private fun getResolutionData(childData: ChildData, recyclerView: RecyclerView, resolutionValues: List<ResolutionValue>){
        val restoreResolutionData = saveDataResolution.getString(mResolutionKey, "360p")
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = ResolutionAdapter(requireContext(), resolutionValues, ResolutionAdapter.ResolutionCallBack { data, position ->
            resolutionSelected.text = data.name
            mPrefsEdit.putString(mResolutionKey, data.name)
            mPrefsEdit.apply()
            val path = childData.title.toLowerCase(Locale.getDefault()).replace(" ", "-").replace(":", "")
            getDataTabLayout(childData, tabLayout, viewPager, path, childData.downloads, position)
        }, resolutionSelection, restoreResolutionData)
    }
    private fun getDataTabLayout(childData: ChildData, tabLayout: TabLayout, viewPager: ViewPager, path : String, downloads: List<Download>, resolutionPosition: Int){
        val fragment = ItemDownloadFragment()
        val adapter = TabAdapter(childFragmentManager)
        for (i in downloads.indices){
            tabLayout.addTab(tabLayout.newTab().setText(downloads[i].name))
            fragment.getToolbar(toolbar)
            adapter.addFragment(fragment.newInstance(childData, downloads[i], path, i, resolutionPosition), downloads[i].name)
            getResolutionData(childData, resolutionList, childData.downloads[i].resolution.resolutionValues)
        }
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
    }
    override fun getExpandedHeight() = ViewGroup.LayoutParams.WRAP_CONTENT
    override fun isSheetAlwaysExpanded() = true
    override fun getCornerRadius()= 16f
    override fun animateCornerRadius() = true
    override fun animateStatusBar() = true
    
    override fun onStart() {
        super.onStart()
        view?.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val behavior = BottomSheetBehavior.from(view!!.parent as View)
                behavior.peekHeight = view!!.height
                view?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
            }
        })
    }
}