/**
 * Created with JackHou
 * Date: 2021/3/31
 * Time: 19:16
 * Description:Github仓库实体
 */

package com.qtimes.jectpackdemokotlin.model

import com.google.gson.annotations.SerializedName


data class GithubRepository(
    @SerializedName("id") val id: Int,
    @SerializedName("name")val name: String,
    @SerializedName("description")val description: String,
    @SerializedName("stargazers_count")val starCount: Int
)