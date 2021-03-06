package cegepst.example.lunatics.viewModels

import android.widget.TextView
import androidx.lifecycle.ViewModel
import cegepst.example.lunatics.models.baseModels.Game
import cegepst.example.lunatics.models.managers.ErrorManager
import cegepst.example.lunatics.models.managers.LoadingManager
import cegepst.example.lunatics.services.RawgService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SingleGameViewModel : ViewModel() {

    private val loadingManager = LoadingManager()
    private val errorManager = ErrorManager()
    private var arrayLenght = 0

    private val rawgService by lazy {
        RawgService.create()
    }

    fun giveComponents(textView: TextView) {
        errorManager.setComponents(textView)
    }

    fun fetchSingleGames(gameId: Int, lambda: (Game, Int) -> Unit, viewModel: SameSeriesViewModel) {
        rawgService.getSingleGame(gameId.toString(), RawgService.API_KEY)
                .enqueue(object : Callback<Game> {
                    override fun onResponse(call: Call<Game>, response: Response<Game>) {
                        val content = response.body()
                        val game = Game(
                            gameId,
                            content!!.name,
                            content.description,
                            content.imageUrl,
                            content.rating,
                            content.metacritic,
                            content.released,
                            content.website,
                            content.platformParents
                        )
                        val callback =
                            { gameList: ArrayList<Game> -> callback(gameList, game, lambda) }
                        viewModel.fetchSameSeriesGame(gameId, callback)
                    }

                    override fun onFailure(call: Call<Game>, t: Throwable) {
                        errorManager.raiseError(t.message ?: "Unrecognized error")
                    }
                })
    }

    private fun callback(gameList: ArrayList<Game>, game: Game, lambda: (Game, Int) -> Unit) {
        lambda(game, gameList.size)
    }
}