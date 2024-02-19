package com.example.shemajamebelin.presentation.screen.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shemajamebelin.databinding.BottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ImageAddBottomSheetDialogFragment: BottomSheetDialogFragment() {
    private var _binding: BottomSheetBinding? = null
    private val binding get() = _binding!!
    lateinit var cameraOnClick: () -> Unit
    lateinit var galleryOnClick: () -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        openCameraListener()
    }

    private fun openCameraListener(){
        binding.btnTakeImage.setOnClickListener {
            cameraOnClick()
        }
        binding.btnGetImageFromGallery.setOnClickListener {
            galleryOnClick()
        }
    }
}