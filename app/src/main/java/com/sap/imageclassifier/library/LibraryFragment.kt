package com.sap.imageclassifier.library

import android.content.ContentUris
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sap.imageclassifier.R
import com.sap.imageclassifier.databinding.FragmentLibraryBinding
import com.sap.imageclassifier.ml.ModelUnquant
import kotlinx.coroutines.*
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder


class LibraryFragment : Fragment() {
    private lateinit var model: ModelUnquant
    private lateinit var myCoroutineScope: CoroutineScope
    private var _binding : FragmentLibraryBinding? = null
    private val binding get() = _binding!!
    private var imageUris: ArrayList<Uri> = ArrayList()
    private lateinit var bitmap: Bitmap
    val hashMap = HashMap<Int, HashMap<String, Array<Uri>>>()
    lateinit var labels : Array<String>
    lateinit var job: Job

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding=FragmentLibraryBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadImage()

        try {
            val assetManager = context?.assets
            val inputStream = assetManager?.open("labels.txt")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val label = mutableListOf<String>()
            reader.useLines { lines -> lines.forEach { label.add(it) } }
            labels = label.toTypedArray()
        }catch ( e : java.lang.Exception){
            e.printStackTrace()
        }

        job = Job()
        val uiScope = CoroutineScope(Dispatchers.Default)
        uiScope.launch {
            Log.d("mytag","here")
            for ( item in imageUris){
                try{
                    item.let {
                        if (Build.VERSION.SDK_INT < 32){
                            bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, item)
                        }
                        else {
                            val source = ImageDecoder.createSource(context?.contentResolver!!, item)
                            bitmap = ImageDecoder.decodeBitmap(source)
                        }
                    }
                }
                catch ( e : Exception){
                    e.printStackTrace()
                }
                try {
                    if (isAdded) {
                        model = ModelUnquant.newInstance(requireContext().applicationContext)
                    }
                    // Creates inputs for reference.
                    val inputFeature0 =
                        TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
                    bitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, false)
                    var byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3)
                    byteBuffer.order(ByteOrder.nativeOrder())


                    val intValues = IntArray(224 * 224)
                    bitmap.getPixels(
                        intValues,
                        0,
                        bitmap.getWidth(),
                        0,
                        0,
                        bitmap.getWidth(),
                        bitmap.getHeight()
                    )

                    // iterate over pixels and extract R, G, and B values. Add to bytebuffer.

                    // iterate over pixels and extract R, G, and B values. Add to bytebuffer.
                    var pixel = 0
                    for (i in 0 until 224) {
                        for (j in 0 until 224) {
                            val value = intValues[pixel++] // RGB
                            byteBuffer.putFloat((value shr 16 and 0xFF) * (1f / 255f))
                            byteBuffer.putFloat((value shr 8 and 0xFF) * (1f / 255f))
                            byteBuffer.putFloat((value and 0xFF) * (1f / 255f))
                        }
                    }

                    inputFeature0.loadBuffer(byteBuffer)

                    // Runs model inference and gets result.
                    val outputs = model.process(inputFeature0)
                    val outputFeature0 = outputs.outputFeature0AsTensorBuffer
                    val confidences = outputFeature0.floatArray
                    // find the index of the class with the biggest confidence.
                    // find the index of the class with the biggest confidence.
                    var maxPos = 0
                    var maxConfidence = 0f
                    for (i in confidences.indices) {
                        if (confidences[i] > maxConfidence) {
                            maxConfidence = confidences[i]
                            maxPos = i
                        }
                    }

                    if (hashMap[maxPos] != null) {
                        val innerHashMap = hashMap[maxPos]
                        val uriArray = innerHashMap?.get(labels[maxPos])
                        val newUriArray = uriArray?.plus(Array(1) { item })
                        if (newUriArray != null) {
                            innerHashMap?.put(labels[maxPos], newUriArray)
                        }
                    } else {
                        val innerHashMap = HashMap<String, Array<Uri>>()
                        innerHashMap.put(labels[maxPos], arrayOf(item))
                        hashMap.put(maxPos, innerHashMap)
                    }

                    withContext(Dispatchers.Main) {
                        val categoryAdapter = CategoryAdapter(hashMap)
                        val layoutManager = GridLayoutManager(context, 3)
                        binding.catRecyclerView.layoutManager = layoutManager
                        binding.catRecyclerView.adapter = categoryAdapter
                    }

                    Log.d("mytag1", hashMap.toString())

                    // Releases model resources if no longer used.
                    model.close()
                } catch (e : Exception){
                    e.printStackTrace()
                }
            }
        }

    }

    fun  getMax( arr : FloatArray) : Int {
        var max=0
        for (i in 0..10){
            if(arr[i]>arr[max]){
                max = i
            }
        }
        return max
    }

    private fun loadImage() {
        imageUris = getImagesFromExternalStorage() as ArrayList<Uri>
    }

    private fun getImagesFromExternalStorage(): MutableList<Uri> {
        val imageUris = mutableListOf<Uri>()
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
        val selection = null
        val selectionArgs = null
        val queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val cursor = requireContext().contentResolver.query(
            queryUri,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                imageUris.add(contentUri)
            }
            cursor.close()
        }
        return imageUris
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        _binding=null
    }

}