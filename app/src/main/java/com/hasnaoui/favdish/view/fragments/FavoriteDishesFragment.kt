package com.hasnaoui.favdish.view.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.hasnaoui.favdish.R
import com.hasnaoui.favdish.application.FavDishApplication
import com.hasnaoui.favdish.databinding.FragmentAllDishesBinding
import com.hasnaoui.favdish.databinding.FragmentFavoriteDishesBinding
import com.hasnaoui.favdish.model.entities.FavDish
import com.hasnaoui.favdish.view.activities.AddUpdateDishActivity
import com.hasnaoui.favdish.view.activities.MainActivity
import com.hasnaoui.favdish.view.adapters.FavDishAdapter
import com.hasnaoui.favdish.viewmodel.DashboardViewModel
import com.hasnaoui.favdish.viewmodel.FavDishViewModel
import com.hasnaoui.favdish.viewmodel.FavDishViewModelFactory

class FavoriteDishesFragment : Fragment() {

    private lateinit var mBinding: FragmentFavoriteDishesBinding

    private val mFavDishViewModel:FavDishViewModel by viewModels {
        FavDishViewModelFactory(((requireActivity().application) as FavDishApplication).repository)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentFavoriteDishesBinding.inflate(inflater,container,false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.rvDishesList.layoutManager = GridLayoutManager(requireActivity(),2)
        val favDishAdapter = FavDishAdapter(this)
        mBinding.rvDishesList.adapter = favDishAdapter

        mFavDishViewModel.favDishesList.observe(viewLifecycleOwner){
            dishes ->
            dishes.let {
                if(it.isNotEmpty()){
                    mBinding.rvDishesList.visibility = View.VISIBLE
                    mBinding.tvNoDishesAddedYet.visibility = View.GONE
                    favDishAdapter.dishesList(it)
                }else{
                    mBinding.rvDishesList.visibility = View.GONE
                    mBinding.tvNoDishesAddedYet.visibility = View.VISIBLE
                }
            }
        }
    }

    fun dishDetails(favDish: FavDish){
        findNavController().navigate(FavoriteDishesFragmentDirections.actionFavoriteDishesToDishDetail(
            favDish
        ))
        if(requireActivity() is MainActivity){
            (activity as MainActivity?)!!.hideBottomNavigationView()
        }
    }

    override fun onResume() {
        super.onResume()
        if(requireActivity() is MainActivity){
            (activity as MainActivity?)!!.showBottomNavigationView()
        }
    }

}