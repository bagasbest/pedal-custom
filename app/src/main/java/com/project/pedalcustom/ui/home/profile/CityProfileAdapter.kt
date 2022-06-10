package com.project.pedalcustom.ui.home.profile

import android.annotation.SuppressLint
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.project.pedalcustom.databinding.ItemEditAddressBinding

class CityProfileAdapter(
    private val cityAddressList: ArrayList<CityAddressModel>,
    private val cityList: ArrayList<String>,
) : RecyclerView.Adapter<CityProfileAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemEditAddressBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        fun bind(model: CityAddressModel, position: Int) {
            with(binding) {

                val adapter =
                    ArrayAdapter(
                        itemView.context,
                        android.R.layout.simple_expandable_list_item_1,
                        cityList
                    )

                city.adapter = adapter
                val spinnerPosition: Int = adapter.getPosition(model.city)
                city.setSelection(spinnerPosition)
                city.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // Do nothing
                    }
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, i: Int, id: Long) {
                        val selection = parent?.getItemAtPosition(i)
                        cityAddressList[position].city = selection as String
                    }
                }


                address.setText(model.address)
                address.addTextChangedListener(object : TextWatcher{
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }

                    override fun afterTextChanged(p0: Editable?) {
                        val address = p0.toString()
                        cityAddressList[position].address = address
                    }
                })

                deleteBtn.setOnClickListener {
                    cityAddressList.removeAt(position)
                    notifyDataSetChanged()

                    Toast.makeText(
                        itemView.context,
                        "Successfully delete city & address!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemEditAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cityAddressList[position], position)
    }

    override fun getItemCount(): Int = cityAddressList.size
}