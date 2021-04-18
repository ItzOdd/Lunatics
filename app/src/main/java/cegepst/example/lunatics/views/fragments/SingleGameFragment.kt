package cegepst.example.lunatics.views.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cegepst.example.lunatics.R
import cegepst.example.lunatics.models.baseModels.Game
import cegepst.example.lunatics.models.baseModels.Platform
import cegepst.example.lunatics.views.activities.GameAchievementActivity
import cegepst.example.lunatics.views.activities.SameSeriesActivity
import cegepst.example.lunatics.views.adapters.PlatformAdapter
import com.bumptech.glide.Glide

private const val ARG_GAME_ID = "gameId"

class SingleGameFragment : Fragment() {

    private var actionBar: androidx.appcompat.app.ActionBar? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var gameDescription: TextView
    private lateinit var adapter: PlatformAdapter
    private lateinit var game: Game
    private var length = 0
    private var gameId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            gameId = it.getInt(ARG_GAME_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_single_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setContent(game)
    }

    @SuppressLint("SetTextI18n")
    fun setContent(game: Game) {
        this.adapter = PlatformAdapter(game.platforms as ArrayList<Platform>)
        this.recyclerView = view!!.findViewById(R.id.listPlatforms)
        this.recyclerView.adapter = PlatformAdapter(game.platforms)
        this.recyclerView.layoutManager = LinearLayoutManager(view!!.context)
        this.adapter.notifyDataSetChanged()
        this.gameDescription = view?.findViewById(R.id.gameDescription)!!
        setImage(game)
        setSameSeriesButton()
        view!!.findViewById<TextView>(R.id.gameRating).text = "Rating | ${game.rating}"
        view!!.findViewById<TextView>(R.id.gameWebsite).text = game.website
        formatDescription(game.description)
        setOnClickEvents(game)
    }

    private fun setSameSeriesButton() {
        if (length == 0) {
            view!!.findViewById<Button>(R.id.actionSameLineup).visibility = View.GONE
        }
    }

    private fun setImage(game: Game) {
        Glide.with(view!!.context).load(game.imageUrl).centerCrop()
                .into(view?.findViewById(R.id.gameImage)!!)
    }

    private fun formatDescription(description: String) {
        val description = stripHtml(description)
        val parts = description.split(".")
        val output = parts[0] + parts[1] + parts[2] + "."
        view!!.findViewById<TextView>(R.id.gameDescription).text = output
    }

    private fun stripHtml(html: String): String {
        return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString()
    }

    private fun setOnClickEvents(game: Game) {
        view!!.findViewById<TextView>(R.id.gameWebsite).setOnClickListener {
            changeActivity(Intent(Intent.ACTION_VIEW, Uri.parse(game.website)))
        }
        view!!.findViewById<Button>(R.id.actionSameLineup).setOnClickListener {
            changeActivity(
                Intent(view?.context, SameSeriesActivity::class.java).putExtra(
                    "gameId",
                    game.id
                )
            )
        }
        view!!.findViewById<Button>(R.id.actionTrophies).setOnClickListener {
            changeActivity(
                Intent(view?.context, GameAchievementActivity::class.java).putExtra(
                    "gameId",
                    game.id
                )
            )
        }
    }

    private fun changeActivity(intent: Intent) {
        startActivity(intent)
    }

    companion object {
        @JvmStatic
        fun newInstance(gameId: Int, game: Game, length: Int) =
                SingleGameFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_GAME_ID, gameId.toString())
                    }
                    this.game = game
                    this.length = length
                    Log.d("LENGTH", this.length.toString())
                }
    }
}