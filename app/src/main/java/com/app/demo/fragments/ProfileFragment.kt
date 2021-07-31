package com.app.demo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

    private lateinit var profileId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        profileId = firebaseUser.uid
        userInfo()

        getFollowerAndFollowingCount()

        getPostCount()

        return binding.root
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