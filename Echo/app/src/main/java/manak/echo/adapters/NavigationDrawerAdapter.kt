package manak.echo.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import manak.echo.R
import manak.echo.activities.MainActivity
import manak.echo.fragments.AboutUsFragment
import manak.echo.fragments.FavouriteFragment
import manak.echo.fragments.MainScreenFragment
import manak.echo.fragments.SettingFragment

/**
 * Created by Marcoos on 01/02/2018.
 */
class NavigationDrawerAdapter(_contentList: ArrayList<String>, _getImages: IntArray, _context: Context):
        RecyclerView.Adapter<NavigationDrawerAdapter.NavViewHolder>() {
    override fun getItemCount(): Int {
        return contentList?.size as Int
    }

    var contentList:ArrayList<String>?=null
    var getImages:IntArray?=null
    var mcontext: Context?=null
    init {
        this.contentList=_contentList
        this.getImages=_getImages
        this.mcontext=_context
    }
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): NavViewHolder {
        var itemView= LayoutInflater.from(parent?.context).inflate(R.layout.row_custom_navigationdrawer, parent, false)
        val returnThis = NavViewHolder(itemView)
        return returnThis
        }





    override fun onBindViewHolder(holder: NavViewHolder?, position: Int) {
        holder?.icon_GET?.setBackgroundResource(getImages?.get(position) as Int)
        holder?.text_GET?.setText(contentList?.get(position))
        holder?.ContentHolder?.setOnClickListener({
            MainActivity.statified.drawerLayout?.closeDrawers()
            if(position == 0){
                val mainScreenFragment=MainScreenFragment()
                (mcontext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragment, mainScreenFragment)
                        .commit()
            }else if (position == 1)
            {  val favouriteFragment= FavouriteFragment()
                (mcontext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragment, favouriteFragment)
                        .commit()

            }
            else if (position == 2)
            {  val settingFragment= SettingFragment()
                (mcontext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragment, settingFragment)
                        .commit()

            }
            else if (position == 3)
            {  val aboutUsFragment= AboutUsFragment()
                (mcontext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragment, aboutUsFragment)
                        .commit()

            }
        })
    }

    class NavViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        var icon_GET: ImageView?=null
        var text_GET: TextView?=null
        var ContentHolder: RelativeLayout?=null
        init {
            icon_GET=itemView?.findViewById(R.id.icon_navdrawer)
            text_GET=itemView?.findViewById(R.id.text_navdrawer)
            ContentHolder=itemView?.findViewById(R.id.navdrawer_item_content_holder)
        }
    }
}