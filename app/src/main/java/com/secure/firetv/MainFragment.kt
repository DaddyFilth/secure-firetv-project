package com.secure.firetv

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import java.io.IOException

class MainFragment : BrowseSupportFragment() {

    // REPLACE THIS WITH YOUR COMPUTER'S LOCAL WIFI IP ADDRESS
    private val BACKEND_URL = "http://192.168.X.X:8080/api/catalog"
    
    private val client = OkHttpClient()
    private val gson = Gson()
    private val uiHandler = Handler(Looper.getMainLooper())

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        title = "SecureStream Catalog"
        
        setupEventListeners()
        fetchCatalog()
    }

    private fun fetchCatalog() {
        val request = Request.Builder().url(BACKEND_URL).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                uiHandler.post { Toast.makeText(context, "Failed to connect to broker", Toast.LENGTH_LONG).show() }
            }

            override fun onResponse(call: Call, response: Response) {
                val json = response.body?.string()
                if (json != null) {
                    val itemType = object : TypeToken<List<Movie>>() {}.type
                    val movies: List<Movie> = gson.fromJson(json, itemType)
                    
                    uiHandler.post { buildRowsAdapter(movies) }
                }
            }
        })
    }

    private fun buildRowsAdapter(movies: List<Movie>) {
        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        val cardPresenter = CardPresenter()

        val listRowAdapter = ArrayObjectAdapter(cardPresenter)
        for (movie in movies) {
            listRowAdapter.add(movie)
        }

        val header = HeaderItem(0, "Trending Movies")
        rowsAdapter.add(ListRow(header, listRowAdapter))
        
        adapter = rowsAdapter
    }

    private fun setupEventListeners() {
        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
            if (item is Movie) {
                // When a movie is clicked, pass its target to the hidden WebView in MainActivity
                (activity as? MainActivity)?.loadStream(item.id.toString())
            }
        }
    }
}
