package com.example.myapplication

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.Gson

data class Task(
    val id: Int,
    val name: String,
    val description: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(description)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task = Task(parcel)
        override fun newArray(size: Int): Array<Task?> = arrayOfNulls(size)
    }
}
