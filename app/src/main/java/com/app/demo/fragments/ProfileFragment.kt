package com.app.demo.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.app.demo.adapter.PhotoAdapter
import com.app.demo.databinding.FragmentProfileBinding
import com.app.demo.model.Post
import com.app.demo.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var photoAdapterPosts: PhotoAdapter
    private lateinit var photoAdapterSaves: PhotoAdapter
    private val posts = mutableListOf<Post>()
    private val saves = mutableListOf<Post>()

    private lateinit var profileId: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val data = requireContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE)
            .getString("profileId", "none")

        profileId = if (data == "none") {
            firebaseUser.uid
        } else {
            data!!
        }

        userInfo()

        getFollowerAndFollowingCount()

        getPostCount()

        loadPost()

        loadSaves()


        if (profileId == firebaseUser.uid) {
            binding.editProfile.text = "Edit profile"
        } else {
            checkFollowingStatus()
        }

        binding.editProfile.setOnClickListener {
            val btnText = binding.editProfile.text.toString().lowercase()

            if (btnText == "edit profile") {
                // GOTO edit activity
            } else {
                if (btnText == "follow") {
                    FirebaseDatabase.getInstance().reference.child("Follow")
                        .child(firebaseUser.uid).child("following").child(profileId)
                        .setValue(true)
                    FirebaseDatabase.getInstance().reference.child("Follow")
                        .child(profileId).child("followers").child(firebaseUser.uid)
                        .setValue(true)
                } else {
                    FirebaseDatabase.getInstance().reference.child("Follow")
                        .child(firebaseUser.uid).child("following").child(profileId).removeValue()
                    FirebaseDatabase.getInstance().reference.child("Follow")
                        .child(profileId).child("followers").child(firebaseUser.uid)
                        .removeValue()
                }
            }
        }

        binding.recyclerViewMyPictures.apply {
            layoutManager = GridLayoutManager(context, 3)
            setHasFixedSize(true)
        }
        binding.recyclerViewSavedPictures.apply {
            layoutManager = GridLayoutManager(context, 3)
            setHasFixedSize(true)
        }
        photoAdapterPosts = PhotoAdapter(posts)
        photoAdapterSaves = PhotoAdapter(saves)

        binding.recyclerViewMyPictures.adapter = photoAdapterPosts
        binding.recyclerViewSavedPictures.adapter = photoAdapterSaves

        binding.recyclerViewMyPictures.visibility = View.VISIBLE
        binding.recyclerViewSavedPictures.visibility = View.GONE

        binding.myPictures.setOnClickListener {
            binding.recyclerViewMyPictures.visibility = View.VISIBLE
            binding.recyclerViewSavedPictures.visibility = View.GONE
        }
        binding.savedPictures.setOnClickListener {
            binding.recyclerViewMyPictures.visibility = View.GONE
            binding.recyclerViewSavedPictures.visibility = View.VISIBLE
        }
        return binding.root
    }

    private fun loadSaves() {
        val savedIds = mutableListOf<String>()
        FirebaseDatabase.getInstance().reference.child("Saves").child(profileId)
            .addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        val id = snapshot.key!!
                        savedIds.add(id)
                    }
                    FirebaseDatabase.getInstance().reference.child("Posts").addValueEventListener(
                        object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                for (snapshot in dataSnapshot.children) {
                                    val post = snapshot.getValue(Post::class.java)!!
                                    for (id in savedIds) {
                                        if (post.postid == id) {
                                            saves.add(post)
                                        }
                                    }
                                }
                                photoAdapterSaves.notifyDataSetChanged()
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }
                        })
                    photoAdapterPosts.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private fun loadPost() {
        FirebaseDatabase.getInstance().reference.child("Posts").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val post = snapshot.getValue(Post::class.java)!!
                    if (post.publisher == profileId) {
                        posts.add(post)
                    }
                }
                posts.reversed()
                photoAdapterPosts.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun checkFollowingStatus() {
        FirebaseDatabase.getInstance().reference.child("Follow")
            .child(firebaseUser.uid).child("following").addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child(profileId).exists()) {
                        binding.editProfile.text = "following"
                    } else {
                        binding.editProfile.text = "follow"
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private fun getPostCount() {
        FirebaseDatabase.getInstance().reference.child("Posts").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var totalCount = 0
                    for (s in snapshot.children) {
                        val post = s.getValue(Post::class.java)!!
                        if (post.publisher == profileId) {
                            totalCount += 1
                        }
                    }
                    binding.posts.text = "$totalCount"
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun getFollowerAndFollowingCount() {
        val ref = FirebaseDatabase.getInstance().reference.child("Follow").child(profileId)

        ref.child("followers").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.followers.text = snapshot.childrenCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        ref.child("following").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.following.text = snapshot.childrenCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun userInfo() {
        FirebaseDatabase.getInstance().reference.child("Users").child(profileId)
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val user = dataSnapshot.getValue(User::class.java)!!
                        Picasso.get().load(user.imageurl).into(binding.profileImage)
                        binding.fullname.text = user.name
                        binding.username.text = user.username
                        binding.bio.text = user.bio

                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
    }
}