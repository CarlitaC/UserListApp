package com.example.userlistapp


import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray

class MainActivity : AppCompatActivity() {

    private lateinit var usersRecyclerView: RecyclerView
    private lateinit var fetchUsersButton: Button
    private lateinit var progressBar: ProgressBar

    private val userList = mutableListOf<User>()
    private lateinit var userAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        usersRecyclerView =
            findViewById(R.id.usersRecyclerView)

        fetchUsersButton =
            findViewById(R.id.fetchUsersButton)

        progressBar =
            findViewById(R.id.progressBar)

        usersRecyclerView.layoutManager =
            LinearLayoutManager(this)

        userAdapter = UserAdapter(userList) {
            Toast.makeText(
                this@MainActivity,
                "You selected.",
                Toast.LENGTH_SHORT
            ).show()
        }

        usersRecyclerView.adapter = userAdapter

        fetchUsersButton.setOnClickListener {
            fetchUsers()
        }
    }

    private fun fetchUsers() {
        progressBar.visibility = View.VISIBLE
        fetchUsersButton.isEnabled = false

        val client = AsyncHttpClient()

        client.get(
            "https://jsonplaceholder.typicode.com/users",
            object : JsonHttpResponseHandler() {

                override fun onSuccess(
                    statusCode: Int,
                    headers: Array<Header>?,
                    response: JSONArray?
                ) {
                    progressBar.visibility = View.GONE
                    fetchUsersButton.isEnabled = true

                    if (response == null) {
                        showFailure()
                        return
                    }

                    userList.clear()

                    for (i in 0 until response.length()) {
                        val jsonUser =
                            response.getJSONObject(i)

                        userList.add(
                            User(
                                name = jsonUser.optString("name"),
                                email = jsonUser.optString("email"),
                                phone = jsonUser.optString("phone")
                            )
                        )
                    }

                    userAdapter.notifyDataSetChanged()

                    if (userList.isEmpty()) {
                        Toast.makeText(
                            this@MainActivity,
                            "No users found.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    statusCode: Int,
                    headers: Array<Header>?,
                    throwable: Throwable?,
                    errorResponse: JSONArray?
                ) {
                    progressBar.visibility = View.GONE
                    fetchUsersButton.isEnabled = true
                    showFailure()
                }
            }
        )
    }

    private fun showFailure() {
        Toast.makeText(
            this,
            "Failed to fetch data.",
            Toast.LENGTH_SHORT
        ).show()
    }
}
