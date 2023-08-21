package com.example.ambulance.ui.client.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ambulance.R
import com.example.ambulance.adapter.CommentsAdapter
import com.example.ambulance.databinding.FragmentComplainBinding
import com.example.ambulance.model.TextMessages
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ComplainFragment : Fragment() {
    private var _binding: FragmentComplainBinding? = null
    val binding get() = _binding!!

 private lateinit var db: FirebaseFirestore
    private lateinit var commentsAdapter: CommentsAdapter
    private val commentsList = mutableListOf<TextMessages>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentComplainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        // Initialize RecyclerView and its adapter

        commentsAdapter = CommentsAdapter(commentsList)
        binding.rvViewMessages.layoutManager = LinearLayoutManager(requireContext())
        binding.rvViewMessages.adapter = commentsAdapter

        // Load comments
        loadComments()

        // Setup UI components to add a new comment


        binding.ivSend.setOnClickListener {
            val author = FirebaseAuth.getInstance().currentUser!!.uid
            Log.d("UserName","Author Name $author")
            val userRef = FirebaseDatabase.getInstance().getReference("users/$author")
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val userName = dataSnapshot.child("name").getValue(String::class.java)!!


                    val content = binding.evMessage.text.toString().trim()
                    Log.d("UserName","Message Name $content")
                    if (author != null && content.isNotEmpty()) {
                        addComment(userName, content)
                        binding.evMessage.setText("")
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        }
    }
    private fun loadComments() {
        db.collection("comments")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle the error
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    commentsList.clear()
                    for (document in snapshot.documents) {
                        val comment = document.toObject(TextMessages::class.java)
                        comment?.let {
                           // it.id = document.id
                            commentsAdapter.notifyItemInserted(commentsList.size - 1) // Notify the adapter that a new item is added
                            commentsList.add(it)
                        }
                    }
                    commentsAdapter.notifyDataSetChanged()
                }
            }
    }

    private fun addComment(author: String, content: String) {
        val comment = TextMessages(null, author, content, System.currentTimeMillis())

        // Add the comment to Firestore
        GlobalScope.launch(Dispatchers.IO) {
            db.collection("comments")
                .add(comment)
                .addOnSuccessListener { documentReference ->
                    // The comment was added successfully
                    comment.id = documentReference.id
//                    commentsAdapter.notifyItemInserted(commentsList.size - 1) // Notify the adapter that a new item is added
                    binding.rvViewMessages.scrollToPosition(binding.rvViewMessages.adapter!!.itemCount - 1)

                }
                .addOnFailureListener { e ->
                    // Handle any errors that occurred while adding the comment
                }
        }
    }
}