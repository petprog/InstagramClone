package com.app.demo.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.demo.adapter.TagAdapter
import com.app.demo.adapter.UserAdapter
import com.app.demo.databinding.FragmentSearchBinding
import com.app.demo.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private val users = mutableListOf<User>()
    private val hashTags = mutableListOf<String>()
    private val hashTagsCount = mutableListOf<String>()
    private lateinit var userAdapter: UserAdapter
    private lateinit var tagAdapter: TagAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        binding.recyclerViewUsers.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }
        userAdapter = UserAdapter(users, true)
        binding.recyclerViewUsers.adapter = userAdapter

        binding.recyclerViewTags.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }
        tagAdapter = TagAdapter(hashTags, hashTagsCount)
        binding.recyclerViewTags.adapter = tagAdapter

        readUsers()
        readTags()
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchUser(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                filter(s.toString())
            }
        })
        return binding.root
    }

    private fun readTags() {

        FirebaseDatabase.getInstance().reference.child("HashTags").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                hashTags.clear()
                hashTagsCount.clear()

                for (snapshot in dataSnapshot.children) {
                    hashTags.add(snapshot.key!!)
                    hashTagsCount.add(snapshot.childrenCount.toString())
                }
                tagAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun readUsers() {
        val ref = FirebaseDatabase.getInstance().reference.child("Users")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (TextUtils.isEmpty(binding.searchBar.text.toString())) {
                    users.clear()
                    for (snapshot in dataSnapshot.children) {
                        val user = snapshot.getValue(User::class.java)
                        users.add(user!!)
                    }
                    userAdapter.notifyDataSetChanged()

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun searchUser(s: String) {

        val query = FirebaseDatabase.getInstance().reference.child("Users").orderByChild("username")
            .startAt(s).endAt("$s\uf8ff")

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                users.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    users.add(user!!)
                }
                userAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun filter(text: String) {
        val searchTags = mutableListOf<String>()
        val searchTagsCount = mutableListOf<String>()

        for (s in hashTags) {
            if (s.lowercase().contains(text.lowercase())) {
                searchTags.add(s)
                searchTagsCount.add(hashTagsCount[hashTags.indexOf(s)])
            }
        }

        tagAdapter.filter(searchTags, searchTagsCount)
    }


}