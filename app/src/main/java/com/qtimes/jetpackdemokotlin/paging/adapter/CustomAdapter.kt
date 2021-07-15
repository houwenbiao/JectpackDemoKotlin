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
import com.qtimes.jetpackdemokotlin.model.DeviceMap
import com.qtimes.jetpackdemokotlin.model.GithubRepository
import com.qtimes.jetpackdemokotlin.repository.DeviceMapRepository


class GithubRepositoryAdapter :
    PagingDataAdapter<GithubRepository, GithubRepositoryAdapter.ViewHolder>(COMPARATOR) {

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<GithubRepository>() {
            override fun areItemsTheSame(
                oldItem: GithubRepository,
                newItem: GithubRepository
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: GithubRepository,
                newItem: GithubRepository
            ): Boolean {
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

class DeviceMapAdapter : PagingDataAdapter<DeviceMap, DeviceMapAdapter.ViewHolder>(COMPARATOR) {
    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<DeviceMap>() {
            override fun areItemsTheSame(oldItem: DeviceMap, newItem: DeviceMap): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: DeviceMap, newItem: DeviceMap): Boolean {
                return oldItem == newItem
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val shortAddress: TextView = itemView.findViewById(R.id.short_address_value)
        val location: TextView = itemView.findViewById(R.id.location_value)
    }

    override fun onBindViewHolder(holder: DeviceMapAdapter.ViewHolder, position: Int) {
        val repo = getItem(position)
        if (repo != null) {
            holder.shortAddress.text = repo.deviceId
            holder.location.text = repo.locationId
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceMapAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.device_map_item, parent, false)
        return DeviceMapAdapter.ViewHolder(view)
    }
}