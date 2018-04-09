package manak.echo.fragments


import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.media.Image
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import kotlinx.android.synthetic.main.fragment_song_playing.*
import manak.echo.CurrentSongHelper
import manak.echo.Database.EchoDatabase
import manak.echo.R
import manak.echo.R.id.seekBar
import manak.echo.Songs
import manak.echo.fragments.SongPlayingFragment.Statified.seekbar
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * A simple [Fragment] subclass.
 */
class SongPlayingFragment : Fragment() {
    object Statified {
        var startTimeText: TextView? = null
        var endTimeText: TextView? = null
        var playpauseImageButton: ImageButton? = null
        var previousImageButton: ImageButton? = null
        var nextImageButton: ImageButton? = null
        var loopImageButton: ImageButton? = null
        var shuffleImageButton: ImageButton? = null
        var seekbar: SeekBar? = null
        var songArtistView: TextView? = null
        var songTitleView: TextView? = null
        var currentSongHelper: CurrentSongHelper? = null
        var fetchSongs: ArrayList<Songs>? = null
        var currentPosition: Int = 0
        var myActivity: Activity? = null
        var mediaplayer: MediaPlayer? = null
        var audioVisualization: AudioVisualization? = null
        var glView: GLAudioVisualizationView? = null
        var fab: ImageButton? = null
        var favouriteContent: EchoDatabase? = null

        var updateSongTime = object : Runnable {
            override fun run() {
                val getCurrent = mediaplayer?.currentPosition
                startTimeText?.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes(getCurrent?.toLong() as Long),
                        TimeUnit.MILLISECONDS.toSeconds(getCurrent?.toLong() as Long) -
                                TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getCurrent?.toLong() as Long))))
                Handler().postDelayed(this, 1000)
            }
        }
    }

    object Staticated{
        var MY_PREFS_SHUFFLE ="Shuffle feature"
        var MY_PREFS_LOOP ="Loop feature"

        fun onSongComplete(){
            if(Statified.currentSongHelper?.isShuffle as Boolean){
                playNext("PlayNextLikeNormalShuffle")
                Statified.currentSongHelper?.isPlaying=true

            }else{
                if(Statified.currentSongHelper?.isLoop as Boolean){
                    Statified.currentSongHelper?.isPlaying=true
                    var nextSong= Statified.fetchSongs?.get(Statified.currentPosition)
                    Statified.currentSongHelper?.songTitle=nextSong?.songTitle
                    Statified.currentSongHelper?.songPath=nextSong?.songData
                    Statified.currentSongHelper?.currentPosition= Statified.currentPosition
                    Statified.currentSongHelper?.songId=nextSong?.songID as Long
                    updateTextViews(Statified.currentSongHelper?.songTitle as String, Statified.currentSongHelper?.songArtist as String)
                    Statified.mediaplayer?.reset()
                    try {
                        Statified.mediaplayer?.setDataSource(Statified.myActivity,Uri.parse(Statified.currentSongHelper?.songPath))
                        Statified.mediaplayer?.prepare()
                        Statified.mediaplayer?.start()
                        processInformation(Statified.mediaplayer as MediaPlayer)
                    }catch(e:Exception){
                        e.printStackTrace()
                    }
                }else{
                    playNext("PlayNextNormal")
                    Statified.currentSongHelper?.isPlaying=true
                }
            }
            if (Statified.favouriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt()as Int) as Boolean){
                Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity,R.drawable.favorite_on))
            }else{
                Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity,R.drawable.favorite_off))
            }
        }


        fun updateTextViews(songtitle: String, songArtist: String){
            Statified.songTitleView?.setText(songtitle)
            Statified.songArtistView?.setText(songArtist)
        }
        fun processInformation(mediaplayer: MediaPlayer){
            var finalTime=mediaplayer.duration
            var startTime=mediaplayer.currentPosition
            seekbar?.max= finalTime
            Statified.startTimeText?.setText(String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(startTime?.toLong()as Long),
                    TimeUnit.MILLISECONDS.toSeconds(startTime?.toLong()as Long)-
                            TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime?.toLong()as Long))))
            Statified.endTimeText?.setText(String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(finalTime?.toLong()as Long),
                    TimeUnit.MILLISECONDS.toSeconds(finalTime?.toLong()as Long)-
                            TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime?.toLong()as Long))))
            seekbar?.setProgress(startTime)
            Handler().postDelayed(Statified.updateSongTime,1000)

        }

        fun playNext(check:String){
            if(check.equals("PlayNextNormal",true)){
                Statified.currentPosition=Statified.currentPosition+1
            }else if(check.equals("PlayNextLikeNormalShuffle",true)){
                var randomObject=Random()
                var randomPosition=randomObject.nextInt(Statified.fetchSongs?.size?.plus(1) as Int)
                var currentPosition=randomPosition
            }
            if(Statified.currentPosition == Statified.fetchSongs?.size){
                Statified.currentPosition=0
            }
            Statified.currentSongHelper?.isLoop=false
            var nextSong=Statified.fetchSongs?.get(Statified.currentPosition)
            Statified.currentSongHelper?.songTitle=nextSong?.songTitle
            Statified.currentSongHelper?.songPath=nextSong?.songData
            Statified.currentSongHelper?.currentPosition= Statified.currentPosition
            Statified.currentSongHelper?.songId=nextSong?.songID as Long
            updateTextViews(Statified.currentSongHelper?.songTitle as String, Statified.currentSongHelper?.songArtist as String)
            Statified.mediaplayer?.reset()
            try {
                Statified.mediaplayer?.setDataSource(Statified.myActivity,Uri.parse(Statified.currentSongHelper?.songPath))
                Statified.mediaplayer?.prepare()
                Statified.mediaplayer?.start()
                processInformation(Statified.mediaplayer as MediaPlayer)
            }catch(e:Exception){
                e.printStackTrace()
            }
            if (Statified.favouriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt()as Int) as Boolean){
                Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity,R.drawable.favorite_on))
            }else{
                Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity,R.drawable.favorite_off))
            }
        }
    }



    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view= inflater!!.inflate(R.layout.fragment_song_playing, container, false)
        seekbar=view?.findViewById(R.id.seekBar)
        Statified.startTimeText=view?.findViewById(R.id.startTime)
        Statified.endTimeText=view?.findViewById(R.id.endTime)
        Statified.playpauseImageButton=view?.findViewById(R.id.playPauseButton)
        Statified.previousImageButton=view?.findViewById(R.id.previousButton)
        Statified.nextImageButton=view?.findViewById(R.id.nextButton)
        Statified.loopImageButton=view?.findViewById(R.id.loopButton)
        Statified.shuffleImageButton=view?.findViewById(R.id.shuffleButton)
        Statified.songArtistView=view?.findViewById(R.id.songArtist)
        Statified.songTitleView=view?.findViewById(R.id.songTitle)
        Statified.glView=view?.findViewById(R.id.visualizer_view)
        Statified.fab=view?.findViewById(R.id.favouriteIcon)
        Statified.fab?.alpha=0.8f

        return view
    }

     override fun onAttach(context: Context?) {
        super.onAttach(context)
         Statified.myActivity=context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        Statified.myActivity= activity
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Statified.audioVisualization=Statified.glView as AudioVisualization
    }

    override fun onResume() {
        super.onResume()
        Statified.audioVisualization?.onResume()
    }

    override fun onPause() {
        Statified.audioVisualization?.onPause()
        super.onPause()
    }

    override fun onDestroyView() {
        Statified.audioVisualization?.release()
        super.onDestroyView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Statified.favouriteContent= EchoDatabase(Statified.myActivity)
        Statified.currentSongHelper=CurrentSongHelper()
        Statified.currentSongHelper?.isPlaying=true
        Statified.currentSongHelper?.isLoop=false
        Statified.currentSongHelper?.isShuffle=false

        var path:String?=null
        var _songTitle:String?=null
        var _songArtist:String?=null
        var songId: Long=0
        try {
            path=arguments.getString("path")
            _songTitle=arguments.getString("songTitle")
            _songArtist=arguments.getString("songArtist")
            songId=arguments.getInt("songId").toLong()
            Statified.currentPosition=arguments.getInt("songsPosition")
            Statified.fetchSongs=arguments.getParcelableArrayList("songData")

            Statified.currentSongHelper?.songPath=path
            Statified.currentSongHelper?.songTitle=_songTitle
            Statified.currentSongHelper?.songArtist=_songArtist
            Statified.currentSongHelper?.songId=songId
            Statified.currentSongHelper?.currentPosition=Statified.currentPosition
            Staticated.updateTextViews(Statified.currentSongHelper?.songTitle as String, Statified.currentSongHelper?.songArtist as String)



        }catch (e:Exception){
            e.printStackTrace()
        }

var fromFavBottomBar = arguments.get("FavBottomBar") as? String
        if(fromFavBottomBar != null){
            Statified.mediaplayer = FavouriteFragment.Statified.mediaplayer
        }else {

            Statified.mediaplayer = MediaPlayer()
            Statified.mediaplayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            try {
                Statified.mediaplayer?.setDataSource(Statified.myActivity, Uri.parse(path))
                Statified.mediaplayer?.prepare()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Statified.mediaplayer?.start()
        }
        Staticated.processInformation(Statified.mediaplayer as MediaPlayer)
        if(Statified.currentSongHelper?.isPlaying as Boolean){
            Statified.playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)

        }else{
            Statified.playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
        }
        Statified.mediaplayer?.setOnCompletionListener {
            Staticated.onSongComplete()
        }
        clickHandler()
        var visualizationHandler=DbmHandler.Factory.newVisualizerHandler(Statified.myActivity as Context,0)
        Statified.audioVisualization?.linkTo(visualizationHandler)

        var prefsForShuffle= Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE,Context.MODE_PRIVATE)
        var isShuffleAllowed= prefsForShuffle?.getBoolean("feature", false)
        if(isShuffleAllowed as Boolean){
            Statified.currentSongHelper?.isShuffle=true
            Statified.currentSongHelper?.isLoop=false
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)

        }else{
            Statified.currentSongHelper?.isShuffle=false
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
        }

        var prefsForLoop= Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP,Context.MODE_PRIVATE)
        var isLoopAllowed= prefsForShuffle?.getBoolean("feature", false)
        if(isLoopAllowed as Boolean){
            Statified.currentSongHelper?.isShuffle=false
            Statified.currentSongHelper?.isLoop=true
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_icon)

        }else{
            Statified.currentSongHelper?.isLoop=false
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        }
        if (Statified.favouriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt()as Int) as Boolean){
            Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity,R.drawable.favorite_on))
        }else{
            Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity,R.drawable.favorite_off))
        }

    }


fun clickHandler(){
    Statified.fab?.setOnClickListener({
        if (Statified.favouriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt()as Int) as Boolean){
            Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity,R.drawable.favorite_on))
            Statified.favouriteContent?.deleteFavourite(Statified.currentSongHelper?.songId?.toInt() as Int)
            Toast.makeText(Statified.myActivity,"Removed from favourites", Toast.LENGTH_SHORT ).show()
        }else{
            Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity,R.drawable.favorite_off))
            Statified.favouriteContent?.storeAsFavourite(Statified.currentSongHelper?.songId?.toInt(), Statified.currentSongHelper?.songArtist, Statified.currentSongHelper?.songTitle,
                    Statified.currentSongHelper?.songPath)
            Toast.makeText(Statified.myActivity,"Added to favourites", Toast.LENGTH_SHORT ).show()
        }
    })
    Statified.shuffleImageButton?.setOnClickListener({
        var editorShuffle=Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
        var editorLoop=Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)?.edit()

        if(Statified.currentSongHelper?.isShuffle as Boolean){

            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            Statified.currentSongHelper?.isShuffle=false
            editorShuffle?.putBoolean("feature", false)
            editorShuffle?.apply()

        }else{
            Statified.currentSongHelper?.isShuffle=true
            Statified.currentSongHelper?.isLoop=false
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
            editorShuffle?.putBoolean("feature", true)
            editorShuffle?.apply()
            editorLoop?.putBoolean("feature", false)
            editorLoop?.apply()
        }
    })
    Statified.nextImageButton?.setOnClickListener({
        Statified.currentSongHelper?.isPlaying=true
        if(Statified.currentSongHelper?.isShuffle as Boolean){
            Staticated.playNext("PlayNextLikeNormalShuffle")
        }else{
            Staticated.playNext("PlayNextNormal")
        }
    })
    Statified.previousImageButton?.setOnClickListener({
        Statified.currentSongHelper?.isPlaying=true
        if(Statified.currentSongHelper?.isLoop as Boolean){
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        }
        playPrevious()
    })
    Statified.loopImageButton?.setOnClickListener({
        var editorShuffle=Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
        var editorLoop=Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)?.edit()
        if(Statified.currentSongHelper?.isLoop as Boolean){
            Statified.currentSongHelper?.isLoop=false
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
            editorLoop?.putBoolean("feature", false)
            editorLoop?.apply()
        }else{
            Statified.currentSongHelper?.isLoop=true
            Statified.currentSongHelper?.isShuffle=false
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            editorShuffle?.putBoolean("feature", false)
            editorShuffle?.apply()
            editorLoop?.putBoolean("feature", true)
            editorLoop?.apply()
        }
    })
    Statified.playpauseImageButton?.setOnClickListener({
        if(Statified.mediaplayer?.isPlaying as Boolean){
            Statified.mediaplayer?.pause()
            Statified.currentSongHelper?.isPlaying=false
            Statified.playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
        }else{
            Statified.mediaplayer?.start()
            Statified.currentSongHelper?.isPlaying=true
            Statified.playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)

        }
    })
}





    fun playPrevious(){
        Statified.currentPosition=Statified.currentPosition-1
        if(Statified.currentPosition==-1){
            Statified.currentPosition=0
        }
        if(Statified.currentSongHelper?.isPlaying as Boolean){
            Statified.playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        }else{
            Statified.playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
        }
        Statified.currentSongHelper?.isLoop=false
        var nextSong=Statified.fetchSongs?.get(Statified.currentPosition)
        Statified.currentSongHelper?.songTitle=nextSong?.songTitle
        Statified.currentSongHelper?.songPath=nextSong?.songData
        Statified.currentSongHelper?.currentPosition= Statified.currentPosition
        Statified.currentSongHelper?.songId=nextSong?.songID as Long
        Staticated.updateTextViews(Statified.currentSongHelper?.songTitle as String, Statified.currentSongHelper?.songArtist as String)
        Statified.mediaplayer?.reset()
        try {
            Statified.mediaplayer?.setDataSource(Statified.myActivity,Uri.parse(Statified.currentSongHelper?.songPath))
            Statified.mediaplayer?.prepare()
            Statified.mediaplayer?.start()
            Staticated.processInformation(Statified.mediaplayer as MediaPlayer)
        }catch(e:Exception){
            e.printStackTrace()
        }
        if (Statified.favouriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt()as Int) as Boolean){
            Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity,R.drawable.favorite_on))
        }else{
            Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity,R.drawable.favorite_off))
        }
    }






}// Required empty public constructor
