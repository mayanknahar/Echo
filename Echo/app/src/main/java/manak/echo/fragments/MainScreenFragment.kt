package manak.echo.fragments


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import manak.echo.R
import manak.echo.Songs
import manak.echo.adapters.MainScreenAdapter


/**
 * A simple [Fragment] subclass.
 */
class MainScreenFragment : Fragment() {
    var getSongsList:ArrayList<Songs>?=null
    var nowPlayingBottomBar:RelativeLayout? =null
    var playPauseButton:ImageButton? =null
    var songTitle: TextView?=null
    var visibleLayout: RelativeLayout?=null
    var noSongs:RelativeLayout?=null
    var recyclerView: RecyclerView?=null
    var myActivity: Activity?=null
    var _mainScreenAdapter:MainScreenAdapter?=null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_main_screen, container, false)
        visibleLayout= view?.findViewById<RelativeLayout>(R.id.noSongs)
        nowPlayingBottomBar= view?.findViewById<RelativeLayout>(R.id.hiddenBarMainScreen)
        songTitle=view?.findViewById<TextView>(R.id.songTitleMainScreen)
        playPauseButton= view?.findViewById<ImageButton>(R.id.playpauseButton)
        recyclerView= view?.findViewById<RecyclerView>(R.id.ContentMain)
        return view

    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getSongsList= getSongsFromPhone()
        _mainScreenAdapter= MainScreenAdapter(getSongsList as ArrayList<Songs>, myActivity as Context)
        val mLayoutManager= LinearLayoutManager(myActivity)
        recyclerView?.layoutManager=mLayoutManager
        recyclerView?.itemAnimator=DefaultItemAnimator()
        recyclerView?.adapter=_mainScreenAdapter
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity=context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity=activity
    }
    fun getSongsFromPhone(): ArrayList<Songs>{
        var arrayList= ArrayList<Songs>()
        var contentResolver=myActivity?.contentResolver
        var songsUri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var songCursor= contentResolver?.query(songsUri,null,null,null, null, null)
        if(songCursor!= null && songCursor.moveToFirst()){
            val songID= songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songTitle= songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist= songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songData= songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val dateIndex= songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
            while(songCursor.moveToNext()){
                var currentId= songCursor.getLong(songID)
                var currentTitle= songCursor.getString(songTitle)
                var currentArtist= songCursor.getString(songArtist)
                var currentData= songCursor.getString(songData)
                var currentDate= songCursor.getLong(dateIndex)
                arrayList.add(Songs(currentId, currentTitle, currentArtist, currentData, currentDate))
            }
        }
    return arrayList

    }


}// Required empty public constructor

