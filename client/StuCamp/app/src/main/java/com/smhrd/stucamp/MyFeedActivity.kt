package com.smhrd.stucamp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.smhrd.stucamp.VO.FeedVO
import com.smhrd.stucamp.VO.MyFeedVO
import com.smhrd.stucamp.VO.UserVO
import org.json.JSONObject

class MyFeedActivity : AppCompatActivity() {

    lateinit var rc : RecyclerView
    lateinit var btnFeedUpdate : Button
    lateinit var btnFeedDelete : Button

    lateinit var reqQueue: RequestQueue
    lateinit var myFeedList: ArrayList<MyFeedVO>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_feed)

        rc = findViewById(R.id.rcMyFeed)
        reqQueue = Volley.newRequestQueue(this)

        //spf(로그인한 이메일 가져오기)
        val spf = getSharedPreferences("mySPF", Context.MODE_PRIVATE)
        val user = Gson().fromJson(spf.getString("user", ""), UserVO::class.java)
        val user_email = user.user_email
        Log.d("user_email",user_email)

        val feedList = ArrayList<FeedVO>()

//        feedList.add(MyFeedVO("hihi", 3, "하이하이"))
//        feedList.add(MyFeedVO("ID1", 2, "하이하이1"))
//        feedList.add(MyFeedVO("ID2", 5, "하이하이2"))
//        feedList.add(MyFeedVO("ID3", 3, "하이하이3"))

        //myfeed 불러오기(서버 통신)
        val request = object : StringRequest(
            Request.Method.GET,
            "http://172.30.1.42:8888/feed/$user_email",
            { response ->
                Log.d("response", response.toString())
                val result = JSONObject(response).getJSONArray("feedDetails")

                for (i in 0 until result.length()) {
                    val feed = result.getJSONObject(i)
                    val feed_content = feed.getString("feed_content").toString()
                    val feed_img = feed.getString("feed_imgpath").toString()
                    val user_nickname = feed.getJSONObject("user").getString("user_nickname")
                    val feed_like_cnt = feed.getInt("feed_like_cnt")
                    val feed_id = feed.getInt("feed_id")
                    val comment = feed.getJSONArray("comment")
                    feedList.add(FeedVO(user_nickname, feed_like_cnt, feed_content, feed_img, feed_id))
                }
                Log.d("feedList", feedList.toString())
                val adapter = MyFeedAdapter(feedList, this)
                rc.layoutManager = LinearLayoutManager(this)
                rc.adapter = adapter
            },
            { error ->
                Log.d("error", error.toString())
            }
        ) {}
        reqQueue.add(request)
    }
}
