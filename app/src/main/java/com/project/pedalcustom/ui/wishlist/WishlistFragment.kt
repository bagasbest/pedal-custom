package com.project.pedalcustom.ui.wishlist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.project.pedalcustom.authentication.LoginActivity
import com.project.pedalcustom.databinding.FragmentWishlistBinding

class WishlistFragment : Fragment() {

    private var _binding: FragmentWishlistBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var user : FirebaseUser? = null
    private var adapter : WishlistAdapter? = null
    private var wishList = ArrayList<WishlistModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentWishlistBinding.inflate(inflater, container, false)

        user = FirebaseAuth.getInstance().currentUser
        checkIsLoginOrNot()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginBtn.setOnClickListener {
            startActivity(Intent(activity, LoginActivity::class.java))
        }
    }

    private fun checkIsLoginOrNot() {
        if(user != null) {
            binding.content.visibility = View.VISIBLE
            initViewModel()
        } else {
            binding.notLogin.visibility = View.VISIBLE
        }
    }

    private fun initRecyclerView() {
        binding.rvWishList.layoutManager =
            LinearLayoutManager(activity)
        adapter = WishlistAdapter(wishList, user?.uid!!)
        binding.rvWishList.adapter = adapter
    }

    private fun initViewModel() {
        val viewModel = ViewModelProvider(this)[WishlistViewModel::class.java]

        binding.progressBar.visibility = View.VISIBLE
        viewModel.setWishList(user?.uid!!)
        viewModel.getWishList().observe(viewLifecycleOwner) { itemList ->
            if (itemList.size > 0) {
                wishList.addAll(itemList)
                binding.noData.visibility = View.GONE
                initRecyclerView()
            } else {
                binding.noData.visibility = View.VISIBLE
            }
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}