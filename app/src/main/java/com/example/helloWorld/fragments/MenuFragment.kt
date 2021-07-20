package com.example.helloWorld.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.helloWorld.R
import com.example.helloWorld.MenuData
import com.example.helloWorld.SecondMenuActivity
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.menu_item.view.*
import kotlinx.android.synthetic.main.menu_list.*
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.concurrent.thread

class MenuFragment : Fragment() {

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
        val adapter = MenuAdapter(getMenu())
        menuListRecyclerView.adapter = adapter
    }

    private fun getMenu() : List<MenuData> {
        val menuList = ArrayList<MenuData>()
        menuList.add(MenuData(title = "Recommend"))
        menuList.add(MenuData(title = "Album"))
        menuList.add(MenuData(title = "Subscription"))
        return menuList
    }

    inner class MenuAdapter(private val menuList: List<MenuData>) : RecyclerView.Adapter<MenuAdapter.ViewHolder>() {


        inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
            val menuItem: View = view.findViewById(R.id.menuItem)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.menu_item, parent, false)
            val holder = ViewHolder(view)
            holder.menuItem.setOnClickListener{
                val intent = Intent(parent.context, SecondMenuActivity::class.java)
                parent.context.startActivity(intent)
            }
            return holder
        }

        override fun onBindViewHolder(holder: ViewHolder, positon: Int) {
            val menu = menuList[positon]
            holder.menuItem.title.text = menu.title
        }

        override fun getItemCount(): Int {
            return menuList.size
        }
    }
}