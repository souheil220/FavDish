package com.hasnaoui.favdish.view.fragments

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.hasnaoui.favdish.R
import com.hasnaoui.favdish.application.FavDishApplication
import com.hasnaoui.favdish.databinding.FragmentRandomDishBinding
import com.hasnaoui.favdish.model.entities.FavDish
import com.hasnaoui.favdish.model.entities.RandomDish
import com.hasnaoui.favdish.utils.Constants
import com.hasnaoui.favdish.viewmodel.FavDishViewModel
import com.hasnaoui.favdish.viewmodel.FavDishViewModelFactory
import com.hasnaoui.favdish.viewmodel.RandomDishViewModel

@Suppress("DEPRECATION")
class RandomDishFragment : Fragment() {

    private lateinit var mRandomDishViewModel: RandomDishViewModel

    private var mBinding: FragmentRandomDishBinding? = null

    private var mProgressDialog:Dialog?=null



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentRandomDishBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRandomDishViewModel = ViewModelProvider(this)[RandomDishViewModel::class.java]
        mRandomDishViewModel.getRandomRecipeFromAPI()
        randomDishViewModelObserver()
        mBinding!!.srlRandomDish.setOnRefreshListener{
            mRandomDishViewModel.getRandomRecipeFromAPI()
        }
    }


    private fun showCustomProgressDialog(){
        mProgressDialog = Dialog(requireActivity())
        mProgressDialog?.let {
            it.setContentView(R.layout.dialog_custom_progress)
            it.show()
        }
    }

    private fun hideCustomProgressDialog(){
        mProgressDialog?.let {
            it.dismiss()
        }
    }
    private fun randomDishViewModelObserver() {
        mRandomDishViewModel.randomDishResponse.observe(viewLifecycleOwner,
            { randomDishResponse ->
                randomDishResponse?.let {
                    Log.i("Random dish response", "${randomDishResponse.recipes[0]}")
                    if(mBinding!!.srlRandomDish.isRefreshing){
                        mBinding!!.srlRandomDish.isRefreshing = false
                    }
                    setRandomDishResponseInUI(randomDishResponse.recipes[0])

                }
            }
        )
        mRandomDishViewModel.randomDishLoadingError.observe(viewLifecycleOwner, { dataError ->
            dataError?.let {
                Log.e("Random dish API Error", "$dataError")
                if(mBinding!!.srlRandomDish.isRefreshing){
                    mBinding!!.srlRandomDish.isRefreshing = false
                }
            }
        })
        mRandomDishViewModel.loadRandomDish.observe(viewLifecycleOwner, { loadRandomDish ->
            loadRandomDish?.let {
                Log.i("Random dish loading", "$loadRandomDish")
                if(loadRandomDish && !mBinding!!.srlRandomDish.isRefreshing){
                showCustomProgressDialog()
                }else{
                    hideCustomProgressDialog()
                }
            }
        })
    }

    private fun setRandomDishResponseInUI(recipe: RandomDish.Recipe) {
        Glide.with(requireActivity())
            .load(recipe.image)
            .centerCrop()
            .into(mBinding!!.ivDishImage)

        mBinding!!.tvTitle.text = recipe.title

        var dishType = "other"

        if (recipe.dishTypes.isNotEmpty()) {
            dishType = recipe.dishTypes[0]
            mBinding!!.tvType.text = dishType
        }
        mBinding!!.tvCategory.text = "other"
        var ingredients = "other"

        for (value in recipe.extendedIngredients) {
            ingredients = if (ingredients.isEmpty()) {
                value.original
            } else {
                ingredients + ", \n" + value.original
            }
        }

        mBinding!!.tvIngredients.text = ingredients

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mBinding!!.tvCookingDirection.text = Html.fromHtml(
                recipe.instructions,
                Html.FROM_HTML_MODE_COMPACT
            )
        } else {
            @Suppress("DEPRECATION")
            mBinding!!.tvCookingDirection.text = Html.fromHtml(recipe.instructions)
        }

        mBinding!!.ivFavoriteDish.setImageDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.ic_favorite_unselected
            )
        )

        var addedToFavorites = false

        mBinding!!.tvCookingTime.text = resources.getString(
            R.string.lbl_estimate_cooking_time,
            recipe.readyInMinutes.toString()
        )

        mBinding!!.ivFavoriteDish.setOnClickListener {
            if(addedToFavorites){
                Toast.makeText(
                    requireActivity(),
                    R.string.msg_already_added_to_favorites,
                    Toast.LENGTH_SHORT
                ).show()
            }else{
                val randomDishDetails = FavDish(
                    recipe.image,
                    Constants.DISH_IMAGE_SOURCE_ONLINE,
                    recipe.title,
                    dishType,
                    "Other",
                    ingredients,
                    recipe.readyInMinutes.toString(),
                    recipe.instructions,
                    true
                )
                val mFavDishViewModel: FavDishViewModel by viewModels {
                    FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
                }

                mFavDishViewModel.insert(randomDishDetails)
                addedToFavorites = true
                mBinding!!.ivFavoriteDish.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_favorite_selected
                    )
                )
                Toast.makeText(
                    requireActivity(),
                    R.string.msg_added_to_favorites,
                    Toast.LENGTH_SHORT
                ).show()
            }


        }

    }


    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }

}