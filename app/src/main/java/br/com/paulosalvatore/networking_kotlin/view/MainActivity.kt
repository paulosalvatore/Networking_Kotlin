package br.com.paulosalvatore.networking_kotlin.view

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import br.com.paulosalvatore.networking_kotlin.R
import br.com.paulosalvatore.networking_kotlin.adapter.ProgrammingLanguageAdapter
import br.com.paulosalvatore.networking_kotlin.adapter.RepositoryAdapter
import br.com.paulosalvatore.networking_kotlin.api.RepositoryRetriever
import br.com.paulosalvatore.networking_kotlin.model.GithubRepositoriesResult
import br.com.paulosalvatore.networking_kotlin.model.ProgrammingLanguage
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.longToast
import org.jetbrains.anko.yesButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
	private val repositoryRetriever = RepositoryRetriever()

	private val callback = object : Callback<GithubRepositoriesResult> {
		override fun onFailure(call: Call<GithubRepositoriesResult>?,
		                       t: Throwable?) {
			// Implementação em caso de falha
			longToast("Fail loading repositories.")

			Log.e("MainActivity", "Problem calling Github API", t)
			Log.d("MainActivity", "Fail on URL: ${call?.request()?.url()}")

		}

		override fun onResponse(call: Call<GithubRepositoriesResult>?,
		                        response: Response<GithubRepositoriesResult>?) {
			// Implementação em caso de sucesso
			longToast("Load finished.")

			response?.isSuccessful.let {
				response?.body()?.repositories?.let {
					val resultList = response.body()?.repositories ?: emptyList()
					recyclerView.adapter =
							RepositoryAdapter(
									resultList,
									this@MainActivity) {
								longToast("Clicked item: ${it.full_name}")
							}
				}
			}
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		if (isNetworkConnected()) {
			loadDefaultRecyclerView()
		} else {
			alert ("Please check your internet connection and try again.",
					"No internet connection") {
				this.iconResource = android.R.drawable.ic_dialog_alert
				yesButton {  }
			}.show()
		}

		btReload.setOnClickListener { _ ->
			loadDefaultRecyclerView()
		}
	}

	private fun isNetworkConnected(): Boolean {
		val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		val networkInfo = connectivityManager.activeNetworkInfo
		return networkInfo != null && networkInfo.isConnected
	}

	private fun loadDefaultRecyclerView() {
		// Lista na Vertical
		val layoutManager = LinearLayoutManager(this)
		recyclerView.layoutManager = layoutManager

		recyclerView.adapter =
				ProgrammingLanguageAdapter(
						recyclerViewItems(),
						this) {
					longSnackbar(root, "Loading ${it.title} repositories...")

					repositoryRetriever.getLanguageRepositories(
							callback,
							it.title
					)
				}
	}

	private fun recyclerViewItems(): List<ProgrammingLanguage> {

		val kotlin = ProgrammingLanguage(R.drawable.kotlin,
				"Kotlin",
				2010,
				"Kotlin é uma Linguagem de programação que compila para a Máquina virtual Java e que também pode ser traduzida para JavaScript e compilada para código nativo. É desenvolvida pela JetBrains, seu nome é baseado na ilha de Kotlin onde se situa a cidade russa de Kronstadt, próximo à São Petersburgo. Apesar de a sintaxe de Kotlin diferir da de Java, Kotlin é projetada para ter uma interoperabilidade total com código Java. Foi considerada pelo público a 2ª linguagem 'mais amada', de acordo com uma pesquisa conduzida pelo site Stack Overflow em 2018.")

		return listOf(kotlin, kotlin)
	}
}
