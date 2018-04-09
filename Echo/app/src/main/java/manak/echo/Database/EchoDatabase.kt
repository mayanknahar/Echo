package manak.echo.Database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import manak.echo.Songs

/**
 * Created by Marcoos on 23/02/2018.
 */

class EchoDatabase: SQLiteOpenHelper{

    var _songList=ArrayList<Songs>()
    object Staticated{
        var DB_VERSION=1
    var DB_NAME="Favourite Database"
    var TABLE_NAME="Favourite Table"
    var COLUMN_ID="SongID"
    var COLUMN_SONG_TITLE="songTitle"
    var COLUMN_SONG_ARTIST="SongArtist"
    var COLUMN_SONG_PATH="songPath"}
    override fun onCreate(sqliteDatabase: SQLiteDatabase?) {
       sqliteDatabase?.execSQL("CREATE TABLE" + Staticated.TABLE_NAME + " ("+ Staticated.COLUMN_ID + "INTEGER," + Staticated.COLUMN_SONG_ARTIST + "STRING," + Staticated.COLUMN_SONG_TITLE
       + "STRING," + Staticated.COLUMN_SONG_PATH + "STRING);")
    }
    constructor(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : super(context, name, factory, version)
    constructor(context: Context?) : super(context, Staticated.DB_NAME, null, Staticated.DB_VERSION)


    fun storeAsFavourite(id: Int?,artist: String?,songTitle: String?,path: String?){
        val db= this.writableDatabase
        var contentValues= ContentValues()
        contentValues.put(Staticated.COLUMN_ID,id)
        contentValues.put(Staticated.COLUMN_SONG_ARTIST,artist)
        contentValues.put(Staticated.COLUMN_SONG_TITLE,songTitle)
        contentValues.put(Staticated.COLUMN_SONG_PATH,path)
        db.insert(Staticated.TABLE_NAME,null,contentValues)
        db.close()
    }


    fun queryDBList(): ArrayList<Songs>?{
        try {
            val db=this.readableDatabase
            val query_params=" SELECT * FROM " + Staticated.TABLE_NAME
            var cSor= db.rawQuery(query_params, null)
            if(cSor.moveToFirst()){
                do{
                    var _id=cSor.getInt(cSor.getColumnIndexOrThrow(Staticated.COLUMN_ID))
                    var _artist=cSor.getString(cSor.getColumnIndexOrThrow(Staticated.COLUMN_SONG_ARTIST))
                    var _title=cSor.getString(cSor.getColumnIndexOrThrow(Staticated.COLUMN_SONG_TITLE))
                    var _path=cSor.getString(cSor.getColumnIndexOrThrow(Staticated.COLUMN_SONG_PATH))
                    _songList.add(Songs(_id.toLong(), _artist, _title, _path, 0))
                }while (cSor.moveToNext())

            }else{
                return null
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return _songList
    }


    fun checkifIdExists(_id:Int):Boolean{
        var storeId=-1090
        val db = this.readableDatabase
        val query_params= "SELECT * FROM " + Staticated.TABLE_NAME + "WHERE SongId = '$_id'"
        val cSor=db.rawQuery(query_params,null)
        if (cSor.moveToFirst()){
            do{
                storeId=cSor.getInt(cSor.getColumnIndexOrThrow(Staticated.COLUMN_ID))
            }while (cSor.moveToNext())
        }else{
            return false
        }
        return storeId !=-1090
    }


    fun deleteFavourite(_id: Int){
        val db=this.writableDatabase
        db.delete(Staticated.TABLE_NAME, Staticated.COLUMN_ID + "=" + _id,null)
        db.close()
    }

    fun checkSize(): Int{
        var counter = 0
        val db= this.readableDatabase
        var query_params ="SELECT * FROM " + Staticated.TABLE_NAME
        val cSor = db.rawQuery(query_params, null)
        if(cSor.moveToFirst()){
            do{
                counter = counter+1
            }while(cSor.moveToNext())
        }else{
            return 0
        }
        return  counter
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }



}