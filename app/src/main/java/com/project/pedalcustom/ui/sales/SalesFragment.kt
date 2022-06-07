package com.project.pedalcustom.ui.sales

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.project.pedalcustom.R
import com.project.pedalcustom.databinding.FragmentSalesBinding
import java.text.SimpleDateFormat
import java.util.*


class SalesFragment : Fragment() {

    private var _binding: FragmentSalesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var status = "weekly"
    private var logTransactionList = ArrayList<SalesModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSalesBinding.inflate(inflater, container, false)

        initStatusColor()

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun initBarChart() {
        val sold = ArrayList<Entry>()

        val monthFormat = SimpleDateFormat("MM", Locale.getDefault())
        val dayFormat = SimpleDateFormat("dd", Locale.getDefault())
        val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
        val date = Date()
        val currentMonth: String = monthFormat.format(date)
        val currentYear: String = yearFormat.format(date)

        val c = Calendar.getInstance()
        c.firstDayOfWeek = Calendar.MONDAY
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val currentMonday : String = dayFormat.format(c.time)
        val currentMondayInMillis = dayFormat.parse(currentMonday)

        val days = arrayOfNulls<Long>(7)
        for (i in 0..6) {
            days[i] = dayFormat.format(c.time).toLong()
            c.add(Calendar.DAY_OF_MONTH, 1)

            Log.e("tag", days[i].toString())
        }

        val lastMonth = currentMonth.toLong() - 1


        when (status) {
            "weekly" -> {

                val viewModel = ViewModelProvider(this)[SalesViewModel::class.java]

                viewModel.setSalesDate(currentMondayInMillis.time, date.time)
                viewModel.getSales().observe(viewLifecycleOwner) { salesList ->
                    logTransactionList.clear()
                    logTransactionList.addAll(salesList)

                }

                Handler().postDelayed({
                    var bikesCnt = 0
                    var sparePartCnt = 0
                    var accessoriesCnt = 0
                    var customCnt = 0

                    var monCnt = 0
                    var tueCnt = 0
                    var wedCnt = 0
                    var thuCnt = 0
                    var friCnt = 0
                    var satCnt = 0
                    var sunCnt = 0

                    for(i in logTransactionList.indices) {
                        when (logTransactionList[i].category) {
                            "bikes" -> {
                                bikesCnt++
                            }
                            "spare part" -> {
                                sparePartCnt++
                            }
                            "accessories" -> {
                                accessoriesCnt++
                            }
                            "custom bike" -> {
                                customCnt++
                            }
                        }


                        when (logTransactionList[i].date) {
                            days[0] -> {
                                monCnt++
                            }
                            days[1] -> {
                                tueCnt++
                            }
                            days[2] -> {
                                wedCnt++
                            }
                            days[3] -> {
                                thuCnt++
                            }
                            days[4] -> {
                                friCnt++
                            }
                            days[5] -> {
                                satCnt++
                            }
                            days[6] -> {
                                sunCnt++
                            }
                        }
                    }

                    binding.bikesSold.text = "Bikes : $bikesCnt"
                    binding.accessoriesSold.text = "Accessories : $accessoriesCnt"
                    binding.sparePartSold.text = "Sparepart : $sparePartCnt"
                    binding.customSold.text = "Custom Bike : $customCnt"

                    sold.clear()
                    sold.add(Entry(1F, monCnt.toFloat()))
                    sold.add(Entry(2F, tueCnt.toFloat()))
                    sold.add(Entry(3F, wedCnt.toFloat()))
                    sold.add(Entry(4F, thuCnt.toFloat()))
                    sold.add(Entry(5F, friCnt.toFloat()))
                    sold.add(Entry(6F, satCnt.toFloat()))
                    sold.add(Entry(7F, sunCnt.toFloat()))
                },1000)
            }
            "monthly" -> {
                /// get current month

                val viewModel = ViewModelProvider(this)[SalesViewModel::class.java]

                viewModel.setSalesMonthly(lastMonth, currentMonth.toLong())
                viewModel.getSales().observe(viewLifecycleOwner) { salesList ->
                    logTransactionList.clear()
                    logTransactionList.addAll(salesList)
                }

                Handler().postDelayed({
                    var bikesCnt = 0
                    var sparePartCnt = 0
                    var accessoriesCnt = 0
                    var customCnt = 0

                    var currentMonthCnt = 0
                    var lastMonthCnt = 0
                    for(i in logTransactionList.indices) {
                        when (logTransactionList[i].category) {
                            "bikes" -> {
                                bikesCnt++
                            }
                            "spare part" -> {
                                sparePartCnt++
                            }
                            "accessories" -> {
                                accessoriesCnt++
                            }
                            "custom bike" -> {
                                customCnt++
                            }
                        }

                        when (logTransactionList[i].month) {
                            lastMonth -> {
                                lastMonthCnt++
                            }
                            currentMonth.toLong() -> {
                                currentMonthCnt++
                            }
                        }
                    }

                    binding.bikesSold.text = "Bikes : $bikesCnt"
                    binding.accessoriesSold.text = "Accessories : $accessoriesCnt"
                    binding.sparePartSold.text = "Sparepart : $sparePartCnt"
                    binding.customSold.text = "Custom Bike : $customCnt"

                    sold.clear()
                    sold.add(Entry(1F, lastMonthCnt.toFloat()))
                    sold.add(Entry(2F, currentMonthCnt.toFloat()))
                },1000)
            }
            else -> {
                val viewModel = ViewModelProvider(this)[SalesViewModel::class.java]

                viewModel.setSalesYearly(currentYear.toLong())
                viewModel.getSales().observe(viewLifecycleOwner) { salesList ->
                    logTransactionList.clear()
                    logTransactionList.addAll(salesList)
                }

                Handler().postDelayed({
                    var bikesCnt = 0
                    var sparePartCnt = 0
                    var accessoriesCnt = 0
                    var customCnt = 0

                    var jan = 0
                    var feb = 0
                    var mar = 0
                    var apr = 0
                    var may = 0
                    var jun = 0
                    var jul = 0
                    var aug = 0
                    var sep = 0
                    var oct = 0
                    var nov = 0
                    var dec = 0

                    for(i in logTransactionList.indices) {
                        when (logTransactionList[i].category) {
                            "bikes" -> {
                                bikesCnt++
                            }
                            "spare part" -> {
                                sparePartCnt++
                            }
                            "accessories" -> {
                                accessoriesCnt++
                            }
                            "custom bike" -> {
                                customCnt++
                            }
                        }

                        when (logTransactionList[i].month) {
                            1L -> {
                                jan++
                            }
                            2L -> {
                                feb++
                            }
                            3L -> {
                                mar++
                            }
                            4L -> {
                                apr++
                            }
                            5L -> {
                                may++
                            }
                            6L -> {
                                jun++
                            }
                            7L -> {
                                jul++
                            }
                            8L -> {
                                aug++
                            }
                            9L -> {
                                sep++
                            }
                            10L -> {
                                oct++
                            }
                            11L -> {
                                nov++
                            }
                            12L -> {
                                dec++
                            }
                        }
                    }

                    binding.bikesSold.text = "Bikes : $bikesCnt"
                    binding.accessoriesSold.text = "Accessories : $accessoriesCnt"
                    binding.sparePartSold.text = "Sparepart : $sparePartCnt"
                    binding.customSold.text = "Custom Bike : $customCnt"

                    sold.clear()
                    sold.add(Entry(1F, jan.toFloat()))
                    sold.add(Entry(2F, feb.toFloat()))
                    sold.add(Entry(3F, mar.toFloat()))
                    sold.add(Entry(4F, apr.toFloat()))
                    sold.add(Entry(5F, may.toFloat()))
                    sold.add(Entry(6F, jun.toFloat()))
                    sold.add(Entry(7F, jul.toFloat()))
                    sold.add(Entry(8F, aug.toFloat()))
                    sold.add(Entry(9F, sep.toFloat()))
                    sold.add(Entry(10F, oct.toFloat()))
                    sold.add(Entry(11F, nov.toFloat()))
                    sold.add(Entry(12F, dec.toFloat()))
                },1000)
            }
        }


        Handler().postDelayed({
            val soldLineDataSet = LineDataSet(sold, "Product Sold")
            soldLineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
            soldLineDataSet.color = Color.BLUE
            soldLineDataSet.circleRadius = 5f
            soldLineDataSet.setCircleColor(Color.BLUE)

            //Setup Legend
            val legend = binding.lineGraph.legend
            legend.isEnabled = true
            legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            legend.orientation = Legend.LegendOrientation.HORIZONTAL
            legend.setDrawInside(false)

            binding.lineGraph.description.isEnabled = false
            binding.lineGraph.setTouchEnabled(true)
            binding.lineGraph.isDragEnabled = true
            binding.lineGraph.setScaleEnabled(true)
            binding.lineGraph.xAxis.position = XAxis.XAxisPosition.BOTTOM
            binding.lineGraph.data = LineData(soldLineDataSet)
            binding.lineGraph.animateXY(100, 500)
        },1000)
    }

    private fun initStatusColor() {
        initBarChart()
        binding.weekly.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_rv)
        binding.weekly.setTextColor(Color.parseColor("#FFFFFF"))
        binding.weekly.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), android.R.color.holo_red_light)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.weekly.setOnClickListener {
            if (status != "weekly") {
                status = "weekly"
                initBarChart()
                binding.weeklyText.text = "Sales from monday (1) to sunday (7)"
                binding.weekly.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_rv)
                binding.weekly.setTextColor(Color.parseColor("#FFFFFF"))
                binding.weekly.backgroundTintList = ContextCompat.getColorStateList(
                    requireContext(),
                    android.R.color.holo_red_light
                )

                binding.monthly.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_border)
                binding.monthly.setTextColor(Color.parseColor("#000000"))
                binding.monthly.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), android.R.color.white)

                binding.yearly.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_border)
                binding.yearly.setTextColor(Color.parseColor("#000000"))
                binding.yearly.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), android.R.color.white)

            }
        }

        binding.monthly.setOnClickListener {
            if (status != "monthly") {
                status = "monthly"
                binding.weeklyText.text = "Sales from latest month (1.0) to current month (2.0)"

                initBarChart()
                binding.monthly.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_rv)
                binding.monthly.setTextColor(Color.parseColor("#FFFFFF"))
                binding.monthly.backgroundTintList = ContextCompat.getColorStateList(
                    requireContext(),
                    android.R.color.holo_red_light
                )

                binding.weekly.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_border)
                binding.weekly.setTextColor(Color.parseColor("#000000"))
                binding.weekly.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), android.R.color.white)


                binding.yearly.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_border)
                binding.yearly.setTextColor(Color.parseColor("#000000"))
                binding.yearly.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), android.R.color.white)

            }

        }

        binding.yearly.setOnClickListener {
            if (status != "yearly") {
                status = "yearly"
                initBarChart()
                binding.weeklyText.text = "Sales from january (1) to december (12)"

                binding.yearly.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_rv)
                binding.yearly.setTextColor(Color.parseColor("#FFFFFF"))
                binding.yearly.backgroundTintList = ContextCompat.getColorStateList(
                    requireContext(),
                    android.R.color.holo_red_light
                )

                binding.weekly.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_border)
                binding.weekly.setTextColor(Color.parseColor("#000000"))
                binding.weekly.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), android.R.color.white)

                binding.monthly.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_border)
                binding.monthly.setTextColor(Color.parseColor("#000000"))
                binding.monthly.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), android.R.color.white)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}