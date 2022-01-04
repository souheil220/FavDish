package com.hasnaoui.favdish.view.fragments

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.hasnaoui.favdish.R
import com.hasnaoui.favdish.application.FavDishApplication
import com.hasnaoui.favdish.databinding.FragmentDishDetailBinding
import com.hasnaoui.favdish.model.entities.FavDish
import com.hasnaoui.favdish.utils.Constants
import com.hasnaoui.favdish.viewmodel.FavDishViewModel
import com.hasnaoui.favdish.viewmodel.FavDishViewModelFactory
import java.io.IOException
import java.util.*


class DishDetailFragment : Fragment() {
    private var mFavDishDetail:FavDish?=null
    private var mBinding: FragmentDishDetailBinding? = null

    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory(((requireActivity().application) as FavDishApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_share,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_share_dish->{
                val type = "text/plain"
                val subject= "Checkout this dish recipe"
                var extraText = ""
                val shareWith = "Share with"

                mFavDishDetail?.let {
                    var image = ""
                    if(it.imageSource == Constants.DISH_IMAGE_SOURCE_ONLINE){
                        image = it.image
                    }
                    var cookingInstructions =""
                    if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.N){
                        cookingInstructions = Html.fromHtml(it.directionToCook,Html.FROM_HTML_MODE_COMPACT).toString()
                    }else{
                        @Suppress("DEPRECATION")
                        cookingInstructions = Html.fromHtml(it.directionToCook).toString()
                    }
                    extraText = "$image \n"+
                                "\n Title: ${it.title}\n\n Type: ${it.type} \n\n"+
                                "Category: ${it.category}\n\n"+
                                "Ingredients: \n ${it.ingredients}\n\n"+
                                "Instructions To Cook: \n $cookingInstructions \n\n"+
                                "Time required to cook the dish approx "+
                                "${it.cookingTime} minutes"
                }
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = type
                intent.putExtra(Intent.EXTRA_SUBJECT,subject)
                intent.putExtra(Intent.EXTRA_TEXT, extraText)

                startActivity(Intent.createChooser(intent,shareWith))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mBinding = FragmentDishDetailBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: DishDetailFragmentArgs by navArgs()
        Log.i("title", args.dishDetail.title)
        Log.i("type", args.dishDetail.type)

        mFavDishDetail = args.dishDetail

        args.let {
            try {
                Glide.with(requireActivity())
                    .load(it.dishDetail.image)
                    .centerCrop()
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.i("Tag", "Error Loading Image", e)
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            resource.let {
                                Palette.from(resource!!.toBitmap()).generate { palette ->
                                    val intColor = palette?.vibrantSwatch?.rgb ?: 0
                                    mBinding!!.rlDishDetailMain.setBackgroundColor(intColor)
                                }
                            }
                            return false
                        }
                    })
                    .into(mBinding!!.ivDishImage)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            mBinding!!.tvTitle.text = it.dishDetail.title
            mBinding!!.tvType.text = it.dishDetail.title.capitalize(Locale.ROOT)
            mBinding!!.tvCategory.text = it.dishDetail.category
            mBinding!!.tvIngredients.text = it.dishDetail.ingredients
//            mBinding!!.tvCookingDirection.text = it.dishDetail.directionToCook
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.N){
                mBinding!!.tvCookingDirection.text = Html.fromHtml(it.dishDetail.directionToCook,Html.FROM_HTML_MODE_COMPACT).toString()
            }else{
                @Suppress("DEPRECATION")
                mBinding!!.tvCookingDirection.text = Html.fromHtml(it.dishDetail.directionToCook).toString()
            }
            mBinding!!.tvCookingTime.text =
                resources.getString(R.string.lbl_estimate_cooking_time, it.dishDetail.cookingTime)
            if (args.dishDetail.favoriteDish) {
                mBinding!!.ivFavoriteDish.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_favorite_selected
                    )
                )
            } else {
                mBinding!!.ivFavoriteDish.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_favorite_unselected
                    )
                )
            }

        }

        mBinding!!.ivFavoriteDish.setOnClickListener {
            args.dishDetail.favoriteDish = !args.dishDetail.favoriteDish
            mFavDishViewModel.update(args.dishDetail)
            if (args.dishDetail.favoriteDish) {
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
            } else {
                mBinding!!.ivFavoriteDish.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_favorite_unselected
                    )
                )
                Toast.makeText(
                    requireActivity(),
                    R.string.msg_removed_from_favorite,
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