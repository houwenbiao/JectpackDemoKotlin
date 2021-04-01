/**
 * Created with JackHou
 * Date: 2021/4/1
 * Time: 14:34
 * Description:
 */

package com.qtimes.jetpackdemokotlin.paging.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.qtimes.jetpackdemokotlin.R
import com.qtimes.jetpackdemokotlin.model.GithubRepository


class RepositoryAdapter : PagingDataAdapter<GithubRepository, RepositoryAdapter.ViewHolder>(COMPARATOR) {

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<GithubRepository>() {
            override fun areItemsTheSame(oldItem: GithubRepository, newItem: GithubRepository): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: GithubRepository, newItem: GithubRepository): Boolean {
                return oldItem == newItem
            }
        }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.txt_github_repository_name)
        val description: TextView = itemView.findViewById(R.id.txt_github_repository_desc)
        val starCount: TextView = itemView.findViewById(R.id.txt_github_repository_start)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val repo = getItem(position)
        if (repo != null) {
            holder.name.text = repo.name
            holder.description.text = repo.description
            holder.starCount.text = repo.starCount.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.github_repository_item, parent, false)
        return ViewHolder(view)
    }

}