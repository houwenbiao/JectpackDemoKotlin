/**
 * Created with JackHou
 * Date: 2021/5/10
 * Time: 10:32
 * Description:
 */

package com.qtimes.jetpackdemokotlin.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.qtimes.jetpackdemokotlin.R
import com.qtimes.jetpackdemokotlin.model.CameraAngle
import com.qtimes.jetpackdemokotlin.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_camera_type.*

/**
 * Author: JackHou
 * Date: 2021/5/10.
 * 摄像头类型选择界面
 */
class CameraTypeFragment : BaseFragment() {

    private var cameraAngle = CameraAngle.ZERO

    override fun getLayoutId(): Int {
        return R.layout.fragment_camera_type
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val safeArgs: CameraTypeFragmentArgs by navArgs()
        val doorInfo = safeArgs.doorType
        camera_type_back.onBackClickListener {
            mNavController.navigateUp()
        }

        rg_camera_angle.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.camera_angle_0 -> cameraAngle = CameraAngle.ZERO
                R.id.camera_angle_90 -> cameraAngle = CameraAngle.NINETY
            }
        }



        btn_camera_type_next_step.setOnClickListener {
            mNavController.navigate(
                CameraTypeFragmentDirections.actionCameraTypeFragmentToAtcInfoConfirmFragment(
                    doorInfo,
                    cameraAngle
                )
            )
        }
    }
}