package com.hasnaoui.favdish.view.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.hasnaoui.favdish.R
import com.hasnaoui.favdish.application.FavDishApplication
import com.hasnaoui.favdish.databinding.DialogCustomListBinding
import com.hasnaoui.favdish.databinding.FragmentAllDishesBinding
import com.hasnaoui.favdish.model.entities.FavDish
import com.hasnaoui.favdish.utils.Constants
import com.hasnaoui.favdish.view.activities.AddUpdateDishActivity
import com.hasnaoui.favdish.view.activities.MainActivity
import com.hasnaoui.favdish.view.adapters.CustomListItemAdapter
import com.hasnaoui.favdish.view.adapters.FavDishAdapter
import com.hasnaoui.favdish.viewmodel.FavDishViewModel
import com.hasnaoui.favdish.viewmodel.FavDishViewModelFactory

class AllDishesFragment : Fragment() {

    private lateinit var mBinding: FragmentAllDishesBinding
    private lateinit var mFavDishAdapter: FavDishAdapter
    private lateinit var mCustomListDialog:Dialog

    private val mFavDishViewModel:FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentAllDishesBinding.inflate(inflater,container,false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.rvDishesList.layoutManager = GridLayoutManager(requireActivity(),2)
        mFavDishAdapter = FavDishAdapter(this)
        mBinding.rvDishesList.adapter = mFavDishAdapter
        allDishesListObserver(mFavDishViewModel.allDishesList)

    }

    fun dishDetails(favDish:FavDish){
        findNavController().navigate(AllDishesFragmentDirections.actionAllDishesToDishDetail(
            favDish
        ))
        if(requireActivity() is MainActivity){
            (activity as MainActivity?)!!.hideBottomNavigationView()
        }
    }

    fun dishDelete(favDish:FavDish){
        val builder=AlertDialog.Builder(requireActivity())
        builder.setTitle(resources.getString(R.string.title_delete_dish))
        builder.setMessage(resources.getString(R.string.msg_delete_dish_dialog,favDish.title))
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton(resources.getString(R.string.lbl_yes)){ dialogInterface,_ ->
            mFavDishViewModel.delete(favDish)
            dialogInterface.dismiss()
        }
        builder.setNegativeButton(resources.getString(R.string.lbl_no)){ dialogInterface,_ ->
            dialogInterface.dismiss()
        }
        val alertDialog:AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun filterDishesListDialog(){
        mCustomListDialog = Dialog(requireActivity())
        val binding: DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)
        mCustomListDialog.setContentView(binding.root)
        binding.tvTitle.text = resources.getString(R.string.title_select_item_to_filter)
        val dishTypes = Constants.dishTypes()
        dishTypes.add(0,Constants.ALL_ITEMS)
        binding.rvList.layoutManager = LinearLayoutManager(requireActivity())

        val adapter = CustomListItemAdapter(requireActivity(),this,dishTypes,Constants.FILTER_SELECTION)
        binding.rvList.adapter = adapter
        mCustomListDialog.show()
    }

    override fun onResume() {
        super.onResume()
        if(requireActivity() is MainActivity){
            (activity as MainActivity?)!!.showBottomNavigationView()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_all_dishes,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add_dish -> {
                startActivity(Intent(requireActivity(),AddUpdateDishActivity::class.java))
                return true
            }
            R.id.action_filter_dishes ->{
                filterDishesListDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)

    }

    fun filterSelection(filterItemSelection:String){
        mCustomListDialog.dismiss()
//        Log.i("Filter Selection",filterItemSelection)
        if(filterItemSelection == Constants.ALL_ITEMS){
            allDishesListObserver(mFavDishViewModel.allDishesList)
        }else{
            Log.i("Filter Selection else",filterItemSelection)
            allDishesListObserver(mFavDishViewModel.filteredDishesList(filterItemSelection))
        }
    }
    private fun allDishesListObserver(allDishesList: LiveData<List<FavDish>>) {
        allDishesList.observe(viewLifecycleOwner){
                dishes ->
            dishes.let {
                if(it.isNotEmpty()){
                    mBinding.rvDishesList.visibility = View.VISIBLE
                    mBinding.tvNoDishesAddedYet.visibility = View.GONE
                    mFavDishAdapter.dishesList(it)
                }else{
                    mBinding.rvDishesList.visibility = View.GONE
                    mBinding.tvNoDishesAddedYet.visibility = View.VISIBLE
                }
            }

        }
    }
}