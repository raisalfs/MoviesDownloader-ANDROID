package com.rafslab.movie.dl.ui.fragment.sheet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.rafslab.movie.dl.R
import com.rafslab.movie.dl.ui.activity.ResultActivity
import com.rafslab.movie.dl.utils.BaseUtils
import com.rafslab.movie.dl.view.FilterSeekBar
import net.idik.lib.cipher.so.CipherClient
import org.json.JSONArray
import org.json.JSONException

/**
 * Created by: Rais AlFani Lubis
 * Date: October 18, 2020
 */

class ViewCategories : SuperBottomSheetFragment(){
    private lateinit var cancel : Button
    private lateinit var done : Button
    private lateinit var appBar : AppBarLayout
    private lateinit var toolbar : Toolbar
    private lateinit var categoriesItems: ChipGroup
    private lateinit var ratingSeekBar: FilterSeekBar
    private lateinit var minSeekBar: TextView
    private lateinit var maxSeekBar: TextView
    private lateinit var checkComplete: MaterialCheckBox
    private lateinit var checkOnGoing: MaterialCheckBox
    private lateinit var tagItems: ChipGroup
    private lateinit var progressTags: ProgressBar
    private lateinit var progressCategories: ProgressBar
    private lateinit var queryList: MutableList<String>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_filter_categories,container, false)
        cancel = view.findViewById(R.id.cancel)
        done = view.findViewById(R.id.done)
        appBar = view.findViewById(R.id.app_bar)
        toolbar = view.findViewById(R.id.toolbar)
        categoriesItems = view.findViewById(R.id.categories_group)
        ratingSeekBar = view.findViewById(R.id.filter_rating)
        minSeekBar = view.findViewById(R.id.min_seek)
        maxSeekBar = view.findViewById(R.id.max_seek)
        checkComplete = view.findViewById(R.id.complete)
        checkOnGoing = view.findViewById(R.id.on_going)
        tagItems = view.findViewById(R.id.tag_group)
        progressTags = view.findViewById(R.id.progress_bar)
        progressCategories = view.findViewById(R.id.progress_bar2)
        return view
    }
    override fun getExpandedHeight() = ViewGroup.LayoutParams.WRAP_CONTENT
    override fun isSheetAlwaysExpanded() = true
    override fun getCornerRadius()= 16f
    override fun animateCornerRadius() = true
    override fun animateStatusBar() = true
    override fun isSheetCancelableOnTouchOutside() = false
    override fun isSheetCancelable() = false

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cancel.setOnClickListener {
            dismiss()
        }
        checkComplete.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                if (checkOnGoing.isChecked){
                    checkOnGoing.isChecked = false
                }
            }
        }
        checkOnGoing.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                if (checkComplete.isChecked){
                    checkComplete.isChecked = false
                }
            }
        }
        toolbar.title = "Filter"
        prepareCategoriesData()
        prepareTags()
        setClickAction()
        ratingSeekBar.setOnRangeSeekbarChangeListener(object : OnRangeSeekbarFinalValueListener,
            OnRangeSeekbarChangeListener {
            override fun finalValue(minValue: Number?, maxValue: Number?) {
                val min = BaseUtils.division(minValue as Float)
                val max = BaseUtils.division(maxValue as Float)
                minSeekBar.text = BaseUtils.formatSeekBar(min.toFloat())
                maxSeekBar.text = BaseUtils.formatSeekBar(max.toFloat())
            }
            override fun valueChanged(minValue: Number?, maxValue: Number?) {
                val min = BaseUtils.division(minValue as Float)
                val max = BaseUtils.division(maxValue as Float)
                minSeekBar.text = BaseUtils.formatSeekBar(min.toFloat())
                maxSeekBar.text = BaseUtils.formatSeekBar(max.toFloat())
            }
        })
    }
    private fun prepareCategoriesData() {
        val url = CipherClient.BASE_URL() + CipherClient.API_DIR() + "korean-categories" + CipherClient.Extension()
        AndroidNetworking.get(url).setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONArray(object : JSONArrayRequestListener {
                override fun onResponse(response: JSONArray?) {
                    queryList = ArrayList()
                    for (i in 0 until response!!.length()){
                        try {
                            val jsonObject = response.getJSONObject(i)
                            val chip = Chip(categoriesItems.context)
                            val chipStateTextColors = R.color.chips_text_state_colors
                            val chipStateBackgroundColors = R.color.chips_state_colors
                            chip.setTextColor(context?.let { AppCompatResources.getColorStateList(it, chipStateTextColors) })
                            chip.chipBackgroundColor = context?.let { AppCompatResources.getColorStateList(it, chipStateBackgroundColors) }
                            chip.text = jsonObject.getString("title")
                            chip.isCheckable = true
                            categoriesItems.addView(chip)
                            chip.setOnCheckedChangeListener { buttonView, isChecked ->
                                if(isChecked){
                                    queryList.add(buttonView?.text as String)
                                } else {
                                    queryList.remove(buttonView?.text as String)
                                }
                            }
                            progressCategories.visibility = View.GONE
                        } catch (e: JSONException){
                            e.printStackTrace()
                        }
                    }
                }

                override fun onError(anError: ANError?) {
                }
            })
    }
    private fun prepareTags() {
        val url = CipherClient.BASE_URL() + CipherClient.API_DIR() + "korean-tags" + CipherClient.Extension()
        AndroidNetworking.get(url).setPriority(Priority.MEDIUM).build()
            .getAsJSONArray(object : JSONArrayRequestListener {
                override fun onResponse(response: JSONArray?) {
                    queryList = ArrayList()
                    for (i in 0 until response!!.length()){
                        try {
                            val jsonObject = response.getJSONObject(i)
                            val chip = Chip(tagItems.context)
                            val chipStateTextColors = R.color.chips_text_state_colors
                            val chipStateBackgroundColors = R.color.chips_state_colors
                            chip.setTextColor(context?.let { AppCompatResources.getColorStateList(it, chipStateTextColors) })
                            chip.chipBackgroundColor = context?.let { AppCompatResources.getColorStateList(it, chipStateBackgroundColors) }
                            chip.text = jsonObject.getString("tags")
                            chip.isCheckable = true
                            tagItems.addView(chip)
                            chip.setOnCheckedChangeListener { buttonView, isChecked ->
                                if(isChecked){
                                    queryList.add(buttonView?.text as String)
                                } else {
                                    queryList.remove(buttonView?.text as String)
                                }
                            }
                            progressTags.visibility = View.GONE
                        } catch (e : JSONException){
                            e.printStackTrace()
                        }
                    }
                }

                override fun onError(anError: ANError?) {

                }
            })
    }
    private fun setClickAction(){
        done.setOnClickListener {
            val selectedQuery = queryList.toString()
            val min = BaseUtils.division(ratingSeekBar.selectedMinValue as Float)
            val max = BaseUtils.division(ratingSeekBar.selectedMaxValue as Float)
            val complete = checkComplete.isChecked
            val onGoing = checkOnGoing.isChecked
            val intent = Intent(context, ResultActivity::class.java)
            intent.putExtra("min", min)
            intent.putExtra("max", max)
            intent.putExtra("identity", "fromSheet")
            if (!(queryList.toString().contains("1")) && complete){
                queryList.add("1")
                queryList.remove("0")
                intent.putExtra("all", true)
            } else if (!(queryList.toString().contains("0")) && onGoing){
                queryList.add("0")
                queryList.remove("1")
                intent.putExtra("all", true)
            } else {
                intent.putExtra("all", false)
            }
            val isRating = !(min.toString() == "0.0" && max.toString() == "10.0")
            if (selectedQuery == "[]" && !isRating && !complete && !onGoing){
                BaseUtils.showMessage(context, "Nothing selected", Toast.LENGTH_SHORT)
            } else {
                intent.putStringArrayListExtra("queryList", queryList as java.util.ArrayList<String>?)
                startActivity(intent)
            }
        }
    }
}
