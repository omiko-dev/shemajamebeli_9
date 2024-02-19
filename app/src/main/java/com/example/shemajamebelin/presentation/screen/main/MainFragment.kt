package com.example.shemajamebelin.presentation.screen.main

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.shemajamebelin.databinding.FragmentMainBinding
import com.example.shemajamebelin.presentation.base.BaseFragment
import com.example.shemajamebelin.presentation.screen.dialog.ImageAddBottomSheetDialogFragment
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import kotlin.random.Random


class MainFragment : BaseFragment<FragmentMainBinding>(FragmentMainBinding::inflate) {
    private var dialog: ImageAddBottomSheetDialogFragment? = null
    private lateinit var galleryResultLauncher: ActivityResultLauncher<String>

    override fun bind() {
        showDialog()
        getGalleryResult()
        binding.btnUpload.setOnClickListener {
            if(binding.ivImage.drawable != null){
                val bitmap = (binding.ivImage.drawable as BitmapDrawable).bitmap
                uploadImage(bitmap)
            }else {
                Toast.makeText(requireContext(), "please add image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDialog() {
        binding.btnImage.setOnClickListener {
            if (dialog == null) {
                dialog = ImageAddBottomSheetDialogFragment()
            }
            dialog!!.show(parentFragmentManager, "test")
            dialog!!.cameraOnClick = {
                val intent = Intent().apply {
                    action = MediaStore.ACTION_IMAGE_CAPTURE
                    takePictureLauncher.launch(this)
                }
                startActivity(intent)
            }
            dialog!!.galleryOnClick = {
                chooseFromGallery()
            }
        }
    }

    private fun processSelectedImage(uri: Uri) {
        val bitmap = if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
        } else {
            val source = ImageDecoder.createSource(requireContext().contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        }
        changeImageDisplay(bitmap)
    }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val imageBitmap: Bitmap? = data?.getParcelableExtra("data")
                imageBitmap?.let {
                    changeImageDisplay(it)
                }
            } else {
                Log.i("omiko", "error")
            }
        }

    private fun changeImageDisplay(bitmap: Bitmap) {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val compressedImage =
            BitmapFactory.decodeByteArray(outputStream.toByteArray(), 0, outputStream.size())
        binding.ivImage.setImageBitmap(compressedImage)
    }

    private fun getGalleryResult() {
        galleryResultLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    processSelectedImage(uri)
                }
            }
    }

    private fun chooseFromGallery() {
        galleryResultLauncher.launch("image/*")
    }

    private fun uploadImage(bitmap: Bitmap){
        val storage = Firebase.storage("gs://test-3708a.appspot.com")
        val storageRef = storage.reference
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val randomInt = Random.nextInt(10000000, 20000000)
        val mountainsRef = storageRef.child("$randomInt.jpg")
        val uploadTask  = mountainsRef.putBytes(data)


        uploadTask.addOnFailureListener {
            Log.i("omiko", it.message ?: "error")
            binding.loader.visibility = View.GONE
        }.addOnSuccessListener { taskSnapshot ->
            binding.loader.visibility = View.GONE
        }.addOnProgressListener {
            binding.loader.visibility = View.VISIBLE
        }
    }
}