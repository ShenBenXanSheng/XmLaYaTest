package com.example.ximalaya.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.ximalaya.R
import com.example.ximalaya.databinding.DialogXimalayaAllBinding

class DialogControl : Dialog, View.OnClickListener {
    constructor(context: Context) : this(context, 0)
    constructor(context: Context, themeRes: Int) : super(context, themeRes)

    private val dialogLayout: DialogXimalayaAllBinding by lazy {
        DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_ximalaya_all,
            null,
            false
        )
    }

    private lateinit var onDialogClearClickListener: OnDialogClearClickListener
    private lateinit var onDialogDeleteClickListener: OnDialogDeleteClickListener
    private lateinit var onDialogAddClickListener: OnDialogAddClickListener


    enum class DialogStatus {
        ADD, IS_ADD, DELETE, CLEAR, SEARCH
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(dialogLayout.root)
        //hideAllView()
        initListener()
    }

    fun upDateDialogState(dialogStatus: DialogStatus) {
        println(dialogStatus.name)
        hideAllView()
        dialogLayout.apply {

            when (dialogStatus) {
                DialogStatus.ADD -> {
                    dialogAddContainer.visibility = View.VISIBLE
                }

                DialogStatus.IS_ADD -> {
                    dialogIsAddContainer.visibility = View.VISIBLE
                }
                DialogStatus.DELETE -> {
                    dialogDeleteContainer.visibility = View.VISIBLE
                }
                DialogStatus.CLEAR -> {
                    dialogClearContainer.visibility = View.VISIBLE
                }

                DialogStatus.SEARCH -> {
                    dialogClearSearchContainer.visibility = View.VISIBLE
                }
                else -> {

                }
            }
        }
    }

    private fun hideAllView() {
        dialogLayout.apply {
            dialogAddContainer.visibility = View.GONE
            dialogIsAddContainer.visibility = View.GONE
            dialogDeleteContainer.visibility = View.GONE
            dialogClearContainer.visibility = View.GONE
            dialogClearSearchContainer.visibility = View.GONE
        }
    }

    private fun initListener() {
        dialogLayout.apply {
            dialogAddConfirm.setOnClickListener {
                if (this@DialogControl::onDialogAddClickListener.isInitialized) {
                    onDialogAddClickListener.onAddClick()
                    this@DialogControl.dismiss()
                }
            }

            dialogDeleteConfirm.setOnClickListener {
                if (this@DialogControl::onDialogDeleteClickListener.isInitialized) {
                    onDialogDeleteClickListener.onDeleteClick()
                    this@DialogControl.dismiss()
                }
            }

            dialogClearConfirm.setOnClickListener {
                if (this@DialogControl::onDialogClearClickListener.isInitialized) {
                    onDialogClearClickListener.onClearClick()
                    this@DialogControl.dismiss()
                }
            }

            dialogClearSearchConfirm.setOnClickListener {
                if (this@DialogControl::onDialogClearClickListener.isInitialized){
                    onDialogClearClickListener.onClearClick()
                    this@DialogControl.dismiss()
                }
            }
            dialogAddGiveUp.setOnClickListener(this@DialogControl)
            dialogIsAddConfirm.setOnClickListener(this@DialogControl)
            dialogDeleteGiveUp.setOnClickListener(this@DialogControl)
            dialogClearGiveUp.setOnClickListener(this@DialogControl)
            dialogClearSearchGiveUp.setOnClickListener(this@DialogControl)
        }
    }

    override fun onClick(v: View?) {
        dismiss()
    }

    fun setOnDialogAddClickListener(onDialogAddClickListener: OnDialogAddClickListener) {
        this.onDialogAddClickListener = onDialogAddClickListener
    }

    fun setOnDialogDeleteClickListener(onDialogDeleteClickListener: OnDialogDeleteClickListener) {
        this.onDialogDeleteClickListener = onDialogDeleteClickListener
    }

    fun setOnDialogClearClickListener(onDialogClearClickListener: OnDialogClearClickListener) {
        this.onDialogClearClickListener = onDialogClearClickListener
    }



    interface OnDialogAddClickListener {
        fun onAddClick()
    }

    interface OnDialogDeleteClickListener {
        fun onDeleteClick()
    }

    interface OnDialogClearClickListener {
        fun onClearClick()
    }

}