package com.example.stepsync.sharedpreferanceUtils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.net.toUri
import com.example.stepsync.R
import java.io.FileNotFoundException
import javax.inject.Inject


const val userName = "user name"
const val userPic = "user picture"
const val isUserNew = "is user new"
class UserData @Inject constructor (
    private val context: Context
) {


    private val userPreferance = context.getSharedPreferences(context.getString(R.string.userData),Context.MODE_PRIVATE)

    fun setUserName(name : String){
        userPreferance.edit().putString(userName,name).apply()
    }

    fun setUserOld(){
        userPreferance.edit().putBoolean(isUserNew,false).apply()
    }

    fun isUserNew(): Boolean{
        return userPreferance.getBoolean(isUserNew,true)
    }

    fun getUserName(): String{
        val userNameData = userPreferance.getString(userName,"not found")
        return userNameData!!
    }

    fun setUserPicUri(uri : String){
        val fileOutputStream = context.openFileOutput(userPic,Context.MODE_PRIVATE)
        val userFileInputStream = context.contentResolver.openInputStream(uri.toUri())
        val userImgBitmap = BitmapFactory.decodeStream(userFileInputStream)

        userImgBitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream)
        fileOutputStream.close()
    }

    fun getUserPic(): Bitmap?{
        try {
            val fileInputStream = context.openFileInput(userPic)
            val bitmap = BitmapFactory.decodeStream(fileInputStream)

            return bitmap
        }catch (e: FileNotFoundException){
            return null
        }

    }

}