package br.com.paulosalvatore.networking_kotlin.model

import com.google.gson.annotations.SerializedName

data class GithubRepositoriesResult(
		@SerializedName(value = "items")
		val repositories: List<Repository>
)

data class Repository(val id: Long?,
                      val name: String?,
                      val full_name: String?,
                      val owner: Owner,
                      val private: Boolean,
                      val html_url: String?,
                      val description: String?)

data class Owner(val login: String?,
                 val id: Long?,
                 val avatar_url: String?)
