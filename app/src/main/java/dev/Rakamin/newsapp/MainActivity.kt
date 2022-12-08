package dev.Rakamin.newsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.Rakamin.newsapp.adapter.HeadlineAdapter
import dev.Rakamin.newsapp.data.News
import dev.Rakamin.newsapp.network.NewsService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import dev.Rakamin.newsapp.adapter.AllNewsAdapter
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var headlineRecView: RecyclerView
    lateinit var allNewsRecView: RecyclerView

    lateinit var headlineAdapter: HeadlineAdapter
    lateinit var allNewsAdapter: AllNewsAdapter

    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var progressBar_ActivityMain: ProgressBar

    lateinit var topBarLayout: LinearLayout
    lateinit var BreakingNewsLayout: LinearLayout
    lateinit var allNewsLayout: LinearLayout

    val allNewsLayoutManager = LinearLayoutManager(this)

    var pageNum = 1
    var totalAllNews = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        headlineRecView = findViewById(R.id.headlineRecView)
        allNewsRecView = findViewById(R.id.allNewsRecView)
        swipeRefreshLayout = findViewById(R.id.swipeContainer_ActivityMain)
        topBarLayout = findViewById(R.id.topBarLayout)
        BreakingNewsLayout = findViewById(R.id.BreakingNewsLayout)
        allNewsLayout = findViewById(R.id.allNewsLayout)
        progressBar_ActivityMain = findViewById(R.id.progressBar_ActivityMain)

        hideAll()
        getAllNews()
        getHeadLines()
        showAll()

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
            getHeadLines()
            getAllNews()
        }

      allNewsRecView.addOnScrollListener(object : RecyclerView.OnScrollListener(){

          override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
              super.onScrollStateChanged(recyclerView, newState)

              if( totalAllNews > allNewsLayoutManager.itemCount && allNewsLayoutManager.findFirstVisibleItemPosition() >= allNewsLayoutManager.itemCount - 1 ){
                  pageNum++
                  getAllNews()
              }

          }

          override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
              super.onScrolled(recyclerView, dx, dy)

          }
      })

    }

    private fun showAll() {
        progressBar_ActivityMain.visibility = View.INVISIBLE
        topBarLayout.visibility = View.VISIBLE
        BreakingNewsLayout.visibility = View.VISIBLE
        allNewsLayout.visibility = View.VISIBLE

    }

    private fun hideAll() {
        topBarLayout.visibility = View.INVISIBLE
        BreakingNewsLayout.visibility = View.INVISIBLE
        allNewsLayout.visibility = View.INVISIBLE
        progressBar_ActivityMain.visibility = View.VISIBLE
    }

    private fun getAllNews() {
        val news = NewsService.newsInstance.getAllNews("indonesia",pageNum)
        news.enqueue(object : Callback<News> {

            override fun onResponse(call: Call<News>, response: Response<News>) {
                val news = response.body()
                if (news != null) {
                    totalAllNews = news.totalResults
                    allNewsAdapter = AllNewsAdapter(this@MainActivity)
                    allNewsAdapter.clear()
                    allNewsAdapter.addAll(news.articles)
                    allNewsRecView.adapter = allNewsAdapter
                    allNewsRecView.layoutManager = allNewsLayoutManager

                }

            }

            override fun onFailure(call: Call<News>, t: Throwable) {
                Log.d("Senior Expert", "Error in Fatching News", t)
            }

        })
    }


    private fun getHeadLines() {

        val news = NewsService.newsInstance.getHeadLines("id", 1)
        news.enqueue(object : Callback<News> {

            override fun onResponse(call: Call<News>, response: Response<News>) {
                val news = response.body()
                if (news != null) {
                    headlineAdapter = HeadlineAdapter(this@MainActivity)
                    headlineAdapter.clear()
                    headlineAdapter.addAll(news.articles)
                    headlineRecView.adapter = headlineAdapter
                    headlineRecView.layoutManager = LinearLayoutManager(this@MainActivity,LinearLayoutManager.HORIZONTAL, false)

                }

            }

            override fun onFailure(call: Call<News>, t: Throwable) {
                Log.d("Log:", "Error in Fatching News", t)
            }

        })
    }

}