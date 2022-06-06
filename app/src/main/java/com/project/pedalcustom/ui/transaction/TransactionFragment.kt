package com.project.pedalcustom.ui.transaction

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pedalcustom.R
import com.project.pedalcustom.authentication.LoginActivity
import com.project.pedalcustom.databinding.FragmentTransactionBinding

class TransactionFragment : Fragment() {

    private var _binding: FragmentTransactionBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var status = "On Process"
    private var adapter : TransactionAdapter ? = null
    private var user : FirebaseUser ? = null
    private var role = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTransactionBinding.inflate(inflater, container, false)

        user = FirebaseAuth.getInstance().currentUser

        initStatusColor()
        checkRole()


        return binding.root
    }

    private fun checkRole() {
        if(user != null) {
            binding.content.visibility = View.VISIBLE
            binding.notLogin.visibility = View.GONE
            FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(user?.uid!!)
                .get()
                .addOnSuccessListener {
                    role = "" + it.data!!["role"]

                    initRecyclerView()
                    initViewModel()
                }
        }
    }

    private fun initRecyclerView() {
        binding.rvTransaction.layoutManager =
            LinearLayoutManager(activity)
        adapter = TransactionAdapter()
        binding.rvTransaction.adapter = adapter
    }

    private fun initViewModel() {
        val viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]
        binding.progressBar.visibility = View.VISIBLE

        if(role == "user") {
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            viewModel.setTransactionListByUid(uid, status)
        } else {
            viewModel.setTransactionListAll(status)
        }

        viewModel.getTransaction().observe(viewLifecycleOwner) { transactionList ->
            if (transactionList.size > 0) {
                binding.noData.visibility = View.GONE
                adapter?.setData(transactionList)
            } else {
                binding.noData.visibility = View.VISIBLE
            }
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun initStatusColor() {
        binding.onProcess.background =  ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_rv)
        binding.onProcess.setTextColor(Color.parseColor("#FFFFFF"))
        binding.onProcess.backgroundTintList =  ContextCompat.getColorStateList(requireContext(), android.R.color.holo_red_light)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.onProcess.setOnClickListener {
            if(status != "On Process") {
                status = "On Process"
                initRecyclerView()
                initViewModel()

                binding.onProcess.background =  ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_rv)
                binding.onProcess.setTextColor(Color.parseColor("#FFFFFF"))
                binding.onProcess.backgroundTintList =  ContextCompat.getColorStateList(requireContext(), android.R.color.holo_red_light)

                binding.onDelivery.background =  ContextCompat.getDrawable(requireContext(), R.drawable.bg_border)
                binding.onDelivery.setTextColor(Color.parseColor("#000000"))
                binding.onDelivery.backgroundTintList =  ContextCompat.getColorStateList(requireContext(), android.R.color.white)

                binding.delivered.background =  ContextCompat.getDrawable(requireContext(), R.drawable.bg_border)
                binding.delivered.setTextColor(Color.parseColor("#000000"))
                binding.delivered.backgroundTintList =  ContextCompat.getColorStateList(requireContext(), android.R.color.white)

            }
        }

        binding.onDelivery.setOnClickListener {
            if(status != "On Delivery") {
                status = "On Delivery"
                initRecyclerView()
                initViewModel()

                binding.onDelivery.background =  ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_rv)
                binding.onDelivery.setTextColor(Color.parseColor("#FFFFFF"))
                binding.onDelivery.backgroundTintList =  ContextCompat.getColorStateList(requireContext(), android.R.color.holo_red_light)

                binding.onProcess.background =  ContextCompat.getDrawable(requireContext(), R.drawable.bg_border)
                binding.onProcess.setTextColor(Color.parseColor("#000000"))
                binding.onProcess.backgroundTintList =  ContextCompat.getColorStateList(requireContext(), android.R.color.white)


                binding.delivered.background =  ContextCompat.getDrawable(requireContext(), R.drawable.bg_border)
                binding.delivered.setTextColor(Color.parseColor("#000000"))
                binding.delivered.backgroundTintList =  ContextCompat.getColorStateList(requireContext(), android.R.color.white)

            }

        }

        binding.delivered.setOnClickListener {
            if(status != "Delivered") {
                status = "Delivered"
                initRecyclerView()
                initViewModel()

                binding.delivered.background =  ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_rv)
                binding.delivered.setTextColor(Color.parseColor("#FFFFFF"))
                binding.delivered.backgroundTintList =  ContextCompat.getColorStateList(requireContext(), android.R.color.holo_red_light)

                binding.onProcess.background =  ContextCompat.getDrawable(requireContext(), R.drawable.bg_border)
                binding.onProcess.setTextColor(Color.parseColor("#000000"))
                binding.onProcess.backgroundTintList =  ContextCompat.getColorStateList(requireContext(), android.R.color.white)

                binding.onDelivery.background =  ContextCompat.getDrawable(requireContext(), R.drawable.bg_border)
                binding.onDelivery.setTextColor(Color.parseColor("#000000"))
                binding.onDelivery.backgroundTintList =  ContextCompat.getColorStateList(requireContext(), android.R.color.white)
            }
        }

        binding.loginBtn.setOnClickListener {
            startActivity(Intent(activity, LoginActivity::class.java))
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}