package com.app.demo

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.MimeTypeMap
import androidx.appcompat.app.AppCompatActivity
import com.app.demo.databinding.ActivityPostBinding
import com.app.demo.utils.createProgressBar
import com.app.demo.utils.createShortToast
import com.app.demo.utils.openActivity
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.hendraanggrian.appcompat.socialview.Hashtag
import com.hendraanggrian.appcompat.widget.HashtagArrayAdapter


class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding
    private var imageUri: Uri? = null
    private lateinit var imageUrl: String

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            imageUri = result.uriContent
            binding.imageAdded.setImageURI(imageUri)
        } else {
            createShortToast("Try again!")
            openActivity(MainActivity::class.java)
            finish()
        }
    }

    private fun startCrop() {
        cropImage.launch(
            options {
                setGuidelines(CropImageView.Guidelines.ON)
                setOutputCompressFormat(Bitmap.CompressFormat.PNG)
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.close.setOnClickListener {
            openActivity(MainActivity::class.java)
            finish()
        }
        binding.post.setOnClickListener {
            upload()
        }
        startCrop()
    }

    private fun upload() {
        val progressBar = createProgressBar(this, binding.ll)
        progressBar.visibility = View.VISIBLE

        if (imageUri != null) {
            val filePath = FirebaseStorage.getInstance().getReference("Posts")
                .child(System.currentTimeMillis().toString() + "." + getFileExtension(imageUri!!))

            val uploadTask = filePath.putFile(imageUri!!)
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                filePath.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    imageUrl = downloadUri.toString()
                    val postId = postById()
                    postHashTags(postId)
                    progressBar.visibility = View.GONE
                    openActivity(MainActivity::class.java)
                    finish()
                }
            }.addOnFailureListener { e ->
                createShortToast(e.message!!)
            }
        } else {
            createShortToast("No image was selected")
        }
    }

    private fun postById(): String {
        val map = HashMap<String, Any>()
        val ref = FirebaseDatabase.getInstance().getReference("Posts")
        val postId = ref.push().key
        map["postid"] = postId.toString()
        map["imageurl"] = imageUrl
        map["description"] = binding.description.text.toString()
        map["publisher"] = FirebaseAuth.getInstance().currentUser?.uid!!
        ref.child(postId!!).setValue(map)
        return postId
    }

    private fun postHashTags(postId: String) {
        val map = HashMap<String, Any>()
        val hashtagRef = FirebaseDatabase.getInstance().reference.child("HashTags")
        val hashTags = binding.description.hashtags
        if (hashTags.isNotEmpty()) {
            for (tag in hashTags) {
                map["tag"] = tag.lowercase()
                map["postid"] = postId
                hashtagRef.child(tag.lowercase()).child(postId).setValue(map)
            }
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        val contentResolver = contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    override fun onStart() {
        super.onStart()
        val hashtagArrayAdapter = HashtagArrayAdapter<Hashtag>(applicationContext)

        FirebaseDatabase.getInstance().reference.child("HashTags").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    hashtagArrayAdapter.add(Hashtag(snapshot.key!!, snapshot.childrenCount.toInt()))
                }
            }

            override fun onCancelled(error: DatabaseError) = Unit
        })

        binding.description.hashtagAdapter = hashtagArrayAdapter
    }
}