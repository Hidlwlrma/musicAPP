package com.example.helloWorld.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.helloWorld.ContentActivity
import com.example.helloWorld.MenuData
import com.example.helloWorld.R
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.menu_item.view.*
import kotlinx.android.synthetic.main.menu_list.*
import okhttp3.*
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class SecondMenuFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.menu_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val layoutManager = LinearLayoutManager(activity)
        menuListRecyclerView.layoutManager = layoutManager
        sendRequestWithOkHttp { data ->
            val adapter = MenuAdapter(data)
            menuListRecyclerView.adapter = adapter


            val callback: ItemTouchHelper.Callback = object : ItemTouchHelper.Callback() {
                override fun getMovementFlags(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ): Int {
                    val swipeFlag = ItemTouchHelper.LEFT
                    val dragFlag = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                    return makeMovementFlags(dragFlag, swipeFlag)
                }

                override fun canDropOver(
                    recyclerView: RecyclerView,
                    current: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return true
                }

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    adapter.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
                    Collections.swap(data, viewHolder.adapterPosition, target.adapterPosition)
                    return true
                }



                override fun isLongPressDragEnabled(): Boolean {
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, currentPostion: Int) {
                    data.removeAt(viewHolder.adapterPosition)
                    adapter.notifyItemRemoved(viewHolder.adapterPosition)
                }
            }

            val itemTouchHelper = ItemTouchHelper(callback)
            itemTouchHelper.attachToRecyclerView(menuListRecyclerView)
        }



    }

    inner class MenuAdapter(private val menuList: ArrayList<MenuData>) : RecyclerView.Adapter<MenuAdapter.ViewHolder>() {



        inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
            val menuItem: View = view.findViewById(R.id.menuItem)


        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.menu_item, parent, false)
            val holder = ViewHolder(view)
            holder.menuItem.setOnClickListener{
                ContentActivity.actionStart(parent.context, menuList[holder.adapterPosition])
            }
            return holder
        }

        override fun onBindViewHolder(holder: ViewHolder, positon: Int) {
            val menu = menuList[positon]
            holder.menuItem.title.text = menu.title
            holder.menuItem.subtitle.text = menu.artist
            context?.let { Glide.with(it).load(menu.image).into(holder.menuItem.albumArt) }
        }

        override fun getItemCount(): Int {
            return menuList.size
        }
    }


    private fun sendRequestWithOkHttp(dataCB: (ArrayList<MenuData>) -> Unit){
        var itemList: ArrayList<MenuData>
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://storage.googleapis.com/uamp/catalog.json")
            .build()
        client.newCall(request).enqueue(object :Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                if (responseData != null) {
                    itemList = parseJSONWithGSON(responseData) as ArrayList<MenuData>
                    Handler(Looper.getMainLooper()).post{ //switch thread
//                        dataCB.invoke(itemList)
                        dataCB(itemList)
                    }
                }
            }
        })
    }




    private fun parseJSONWithGSON(jsonData: String) : List<MenuData> {
        val jsonObject = JsonParser().parse(jsonData).asJsonObject
        val jsonArray = jsonObject.getAsJsonArray("music")
        val gson = Gson()
        val typeOf = object : TypeToken<List<MenuData>>() {}.type
        return gson.fromJson<List<MenuData>>(jsonArray, typeOf)
    }
}