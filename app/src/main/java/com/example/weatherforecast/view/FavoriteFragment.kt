package com.example.weatherforecast.view

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.R
import com.example.weatherforecast.adapter.FavoriteAdapter
import com.example.weatherforecast.databinding.FragmentFavoriteBinding
import com.example.weatherforecast.model.Constants
import com.example.weatherforecast.model.Weather
import com.example.weatherforecast.utilities.Utility
import com.example.weatherforecast.viewmodel.FavoriteViewModel
import com.example.weatherforecast.viewmodel.HomeViewModel
import com.google.android.gms.maps.model.LatLng
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceAutocompleteFragment
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceSelectionListener


class FavoriteFragment : Fragment(),FavoriteRecyclerClickListener{
    private var _binding: FragmentFavoriteBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!
    lateinit var done: MutableLiveData<Boolean>
    var firstTime: Boolean=true
    lateinit var newLocation:LatLng
    lateinit var favoriteAdapter: FavoriteAdapter
    lateinit var viewModel:FavoriteViewModel
    lateinit var weatherList:MutableList<Weather>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(Utility.isMorning()){
            binding.addBtn.backgroundTintList=context?.resources?.getColorStateList(com.example.weatherforecast.R.color.sun)


        }else{
            binding.addBtn.backgroundTintList=context?.resources?.getColorStateList(com.example.weatherforecast.R.color.night)
        }
        done=MutableLiveData()
        done.postValue(false)
        viewModel= ViewModelProvider(this).get(FavoriteViewModel::class.java)
        viewModel.getFavoriteWeatherList(this)

        done.observe(viewLifecycleOwner,{
            if(it){
                viewModel.insertFavoriteLocation(newLocation.latitude,newLocation.longitude,requireContext())
            }
        })


        viewModel.favoriteWeatherList.observe(viewLifecycleOwner,{
            weatherList= it as MutableList<Weather>
            favoriteAdapter= FavoriteAdapter(it,this)
            binding.favoriteRecView.adapter=favoriteAdapter
            updateDataFromApi(it)
            firstTime=false

        })
        binding.addBtn.setOnClickListener {
            if (Utility.isOnline(requireContext())){
                showAutoCompleteBar()
            }else{
                Toast.makeText(requireContext(),R.string.noInternet,Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun updateDataFromApi(weatherList:List<Weather>) {
        if (Utility.isOnline(requireContext())) {
            if (firstTime) {
                for (weather in weatherList) {
                    viewModel.insertFavoriteLocation(weather.lat, weather.lon, requireContext())
                }
            }
        }
    }

    private fun showAutoCompleteBar(){
        binding.searchFragmentContainer.visibility= View.VISIBLE
        val autocompleteFragment = PlaceAutocompleteFragment.newInstance(Constants.MAPBOX_API_KEY)
        var transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.add(R.id.search_fragment_container, autocompleteFragment, "places")
        transaction?.commit()

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(carmenFeature: CarmenFeature) {
//                // TODO: Use the longitude and latitude
//                Toast.makeText(context,"latitude ${carmenFeature.center()?.latitude()} \n longitude ${carmenFeature.center()?.longitude()}"
//                    , Toast.LENGTH_LONG).show()
                newLocation= LatLng(carmenFeature.center()?.latitude()!!,carmenFeature.center()?.longitude()!!)
                done.postValue(true)
                activity?.supportFragmentManager?.beginTransaction()?.remove(autocompleteFragment)?.commit()
                binding.searchFragmentContainer.visibility= View.GONE
            }

            override fun onCancel() {
                activity?.supportFragmentManager?.beginTransaction()?.remove(autocompleteFragment)?.commit()
                binding.searchFragmentContainer.visibility= View.GONE
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun deleteItem(weather: Weather) {
        val dialog=AlertDialog.Builder(requireContext()).setTitle(R.string.warning).setMessage(R.string.deleteFav).setPositiveButton(R.string.yes) { dialog, which ->
            viewModel.deleteFavoriteItem(weather)
        }.setNegativeButton(R.string.no,DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
        }).setCancelable(false).show()

    }


    override fun onItemLongClick() {
        TODO("Not yet implemented")
    }
}