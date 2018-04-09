package manak.echo.activities

import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import manak.echo.R
import manak.echo.adapters.NavigationDrawerAdapter
import manak.echo.fragments.MainScreenFragment
import android.support.v7.widget.Toolbar

class MainActivity : AppCompatActivity() {
    var navigationDrawerIconsList: ArrayList<String> = arrayListOf()
    var imagesForNavDrawer = intArrayOf(R.drawable.navigation_allsongs,
            R.drawable.navigation_favorites,R.drawable.navigation_settings,R.drawable.navigation_aboutus)
   object statified{
       var drawerLayout: DrawerLayout?=null
   }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val toolbar= findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        MainActivity.statified.drawerLayout= findViewById(R.id.drawer_layout)
        navigationDrawerIconsList.add("All Songs")
        navigationDrawerIconsList.add("Favorites")
        navigationDrawerIconsList.add("Settings")
        navigationDrawerIconsList.add("About Us")
        val toggle= ActionBarDrawerToggle(this@MainActivity, statified.drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        MainActivity.statified.drawerLayout?.setDrawerListener(toggle)
        toggle.syncState()
        val mainScreenFragment=MainScreenFragment()
        this.supportFragmentManager
                .beginTransaction()
                .add(R.id.details_fragment, mainScreenFragment, "MainScreenFragment")
                .commit()
        var _navigationAdapter = NavigationDrawerAdapter(navigationDrawerIconsList,imagesForNavDrawer,this)
        _navigationAdapter.notifyDataSetChanged()
       var navigation_recycler_view = findViewById(R.id.navigation) as RecyclerView
        navigation_recycler_view.layoutManager= LinearLayoutManager(this)
        navigation_recycler_view.itemAnimator=DefaultItemAnimator()
        navigation_recycler_view.adapter = _navigationAdapter
        navigation_recycler_view.setHasFixedSize(true)



    }

    override fun onStart() {
        super.onStart()
    }

}
