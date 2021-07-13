/**
 * Created with JackHou
 * Date: 2021/7/1
 * Time: 14:23
 * Description:施工扫码界面
 */

package com.qtimes.jetpackdemokotlin.ui.fragment

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import com.qtimes.jetpackdemokotlin.R
import com.qtimes.jetpackdemokotlin.databinding.FragmentConstructionBinding
import com.qtimes.jetpackdemokotlin.ui.base.BaseFragment
import com.qtimes.jetpackdemokotlin.utils.BitmapUtil
import com.qtimes.jetpackdemokotlin.viewmodel.ConstructionViewModel
import com.qtimes.jetpackdemokotlin.zxing.CreateQRBitmp
import com.qtimes.libzxing.zxing.activity.CaptureActivity

class ConstructionFragment : BaseFragment() {

    private val constructionViewModel: ConstructionViewModel by getViewModel(ConstructionViewModel::class.java)
    private lateinit var binding: FragmentConstructionBinding

    companion object {
        const val SCAN_REQUEST_CODE_QR = 200 //扫描物理位置二维码
        const val SCAN_REQUEST_CODE_BAR = 201 //扫描短地址条形码
        const val SELECT_IMAGE_REQUEST_CODE = 202
        const val PERMS_REQUEST_CODE = 302
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnScanningBar.setOnClickListener {
            val intentBar = Intent(mContext, CaptureActivity::class.java)
            startActivityForResult(intentBar, SCAN_REQUEST_CODE_BAR)
        }

        binding.btnScanningQr.setOnClickListener {
            val intentQr = Intent(mContext, CaptureActivity::class.java)
            startActivityForResult(intentQr, SCAN_REQUEST_CODE_QR)
        }

        binding.btnSelect.setOnClickListener {
            //激活系统图库，选择一张图片
            val innerIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            val wrapperIntent = Intent.createChooser(innerIntent, "选择二维码图片")
            startActivityForResult(wrapperIntent, SELECT_IMAGE_REQUEST_CODE)
        }

        binding.generateQrCode.setOnClickListener {
            val contentString: String = binding.etInput.text.toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(contentString)) {
                showToast("请输入二维码内容")
                return@setOnClickListener
            }
            val portrait = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
            //两个方法，一个不传大小，使用默认
            val qrCodeBitmap = CreateQRBitmp.createQRCodeBitmap(contentString, portrait)
            binding.ivQrImage.setImageBitmap(qrCodeBitmap)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            SELECT_IMAGE_REQUEST_CODE -> {
                val proj = arrayOf(MediaStore.Images.Media.DATA)
                data?.let {
                    // 获取选中图片的路径
                    val cursor: Cursor? =
                        mContext!!.contentResolver.query(it.data!!, proj, null, null, null)
                    cursor?.let { it1 ->
                        if (it1.moveToFirst()) {
                            val columnIndex =
                                it1.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                            val photoPath = it1.getString(columnIndex)
                            val result: String? = BitmapUtil.parseQRcode(photoPath)
                            if (!TextUtils.isEmpty(result)) {
                                showToast("从图库选择的图片识别结果:$result")
                            } else {
                                showToast("从图库选择的图片不是二维码图片")
                            }
                        }
                        it1.close()
                    }
                }
            }

            SCAN_REQUEST_CODE_BAR -> {
                if (resultCode == Activity.RESULT_OK) {
                    val input: String? = data?.getStringExtra("result")
                    constructionViewModel.barCodeValue.postValue(input)
                    showToast("条形码扫描结果:$input")
                }
            }

            SCAN_REQUEST_CODE_QR -> {
                if (resultCode == Activity.RESULT_OK) {
                    val input: String? = data?.getStringExtra("result")
                    constructionViewModel.qrCodeValue.postValue(input)
                    showToast("二维码扫描结果:$input")
                }
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_construction
    }

    override fun bindingSetViewModels() {
        super.bindingSetViewModels()
        binding = viewDataBinding as FragmentConstructionBinding
        binding.constructionVM = constructionViewModel
    }
}