package com.funnywolf.littledemon.demo

import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import com.funnywolf.littledemon.R
import com.funnywolf.littledemon.live.LiveList
import com.funnywolf.littledemon.live.LiveListSource
import com.funnywolf.littledemon.model.Paging
import com.funnywolf.littledemon.model.PagingList
import com.funnywolf.littledemon.simpleadapter.SimpleAdapter
import com.funnywolf.littledemon.utils.RecyclerViewLoadMore
import com.funnywolf.littledemon.utils.getRandomString
import com.funnywolf.littledemon.utils.getRandomStrings
import com.funnywolf.littledemon.views.LoadingView
import kotlinx.android.synthetic.main.fragment_layout_demo_list.*
import kotlinx.coroutines.*
import java.lang.Exception
import kotlin.random.Random

/**
 * 列表的 demo，包含常用的列表操作：刷新、加载更多、删除、更新、批量选择等
 *
 * @author funnywolf
 */

/**
 * 列表 demo 页面，处理 UI 相关数据
 */
class ListDemoFragment: Fragment(), HolderCallback {
    private lateinit var viewModel: ListDemoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ListDemoViewModel::class.java)
        viewModel.getRefreshLiveData().observe(this, Observer { onRefreshChanged(it ?: false) })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_layout_demo_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refreshLayout.setOnRefreshListener {
            viewModel.refresh()
        }

        val adapter = SimpleAdapter.Builder(viewModel.getLiveListSource().get())
            .add(ListDemoHolderInfo(this))
            .build()
        recyclerView.adapter = adapter
        viewModel.getLiveListSource().bind(adapter)

        RecyclerViewLoadMore.bind(recyclerView) {
            viewModel.loadMore()
        }

        viewModel.refresh()
    }

    override fun refresh(demoModel: DemoModel) {
        viewModel.refresh(demoModel)
    }

    override fun stopRefresh(demoModel: DemoModel) {
        viewModel.stopRefresh(demoModel)
    }

    override fun delete(demoModel: DemoModel) {
        viewModel.delete(demoModel)
    }

    private fun onRefreshChanged(refreshing: Boolean) {
        refreshLayout.isRefreshing = refreshing
    }

}

data class DemoModel(var text: String) {

    var isRefreshing = false

    fun update(demoModel: DemoModel) {
        text = demoModel.text
    }

    companion object {
        fun random(): DemoModel {
            return DemoModel(getRandomString())
        }
    }
}

interface HolderCallback {
    fun refresh(demoModel: DemoModel)
    fun stopRefresh(demoModel: DemoModel)
    fun delete(demoModel: DemoModel)
}

class ListDemoHolderInfo(var callback: HolderCallback?): SimpleAdapter.HolderInfo<DemoModel>(DemoModel::class.java, R.layout.view_holder_list_demo) {

    override fun onCreate(holder: SimpleAdapter.SimpleHolder<DemoModel>) {
        holder.itemView.setOnClickListener {
            Toast.makeText(it.context, "Clicked ${holder.currentData.text}", Toast.LENGTH_SHORT).show()
        }
        holder.getView<LoadingView>(R.id.ivLoading).setOnClickListener {
            if (holder.currentData.isRefreshing) {
                callback?.stopRefresh(holder.currentData)
            } else {
                callback?.refresh(holder.currentData)
            }
        }
        holder.getView<ImageView>(R.id.ivDelete).setOnClickListener {
            callback?.delete(holder.currentData)
        }
    }

    override fun onBind(holder: SimpleAdapter.SimpleHolder<DemoModel>) {
        holder.getView<TextView>(R.id.tvTitle).text = holder.currentData.text
        holder.getView<LoadingView>(R.id.ivLoading).loading(holder.currentData.isRefreshing)
    }

}

class ListDemoViewModel: ViewModel() {
    private val liveList = LiveList<DemoModel>()
    private var paging: Paging? = null
    private val refreshLiveData = MutableLiveData<Boolean>()

    private val refreshingArray = SparseArray<Job>()
    private fun Job.addTo(key: Int) {
        refreshingArray.put(key, this)
    }

    fun getLiveListSource(): LiveListSource<DemoModel> = liveList

    fun getRefreshLiveData(): LiveData<Boolean> = refreshLiveData

    fun refresh() {
        refreshLiveData.value = true
        viewModelScope.launch {
            val list = ListDemoRepository.loadData().await()
            refreshLiveData.value = false
            paging = list.paging
            liveList.clearAndSet(list)
        }
    }

    fun loadMore() {
        if (paging?.isEnd == true || refreshLiveData.value == true) { return }
        viewModelScope.launch {
            val list = ListDemoRepository.loadData(paging?.next).await()
            paging = list.paging
            liveList.addAll(list)
        }
    }

    fun refresh(demoModel: DemoModel) {
        val index = liveList.get().indexOf(demoModel)
        if (index < 0) { return }
        viewModelScope.launch {
            liveList.update(index) {
                it.isRefreshing = true
            }
            val newDemoModel = ListDemoRepository.refresh(demoModel).await()
            refreshingArray.remove(demoModel.hashCode())
            // 数据可能已删除，或者位置已经改变，这里需要再次拿到下标
            val newIndex = liveList.get().indexOf(demoModel)
            if (newIndex < 0) { return@launch }
            liveList.update(newIndex) {
                it.update(newDemoModel)
                it.isRefreshing = false
            }
        }.addTo(demoModel.hashCode())
    }

    fun stopRefresh(demoModel: DemoModel) {
        refreshingArray.get(demoModel.hashCode())?.cancel()
    }

    fun delete(demoModel: DemoModel) {
        stopRefresh(demoModel)
        liveList.remove(demoModel)
        viewModelScope.launch {
            ListDemoRepository.delete(demoModel).await()
        }
    }

}

object ListDemoRepository {

    fun loadData(next: String? = null, limit: Int = 10): Deferred<PagingList<DemoModel>> {
        return GlobalScope.async(Dispatchers.IO) {
            delay(300)
            val pagingList = PagingList<DemoModel>(Paging(isStart(next), isEnd(next), next(next, limit)))
            repeat(limit) {
                pagingList.add(DemoModel.random())
            }
            if (pagingList.size == 0) {
                pagingList.paging.isEnd = true
            }
            return@async pagingList
        }
    }

    fun refresh(demoModel: DemoModel) = GlobalScope.async(Dispatchers.IO) {
        delay(2000)
        demoModel.text = getRandomString()
        return@async demoModel
    }

    fun delete(demoModel: DemoModel) = GlobalScope.async(Dispatchers.IO) {
        delay(100)
        return@async Random.nextBoolean()
    }

    private fun isStart(next: String?): Boolean {
        return next.isNullOrBlank()
    }

    private fun isEnd(next: String?): Boolean {
        return toIntOrZero(next) > 50
    }

    private fun next(next: String?, limit: Int): String {
        return "${toIntOrZero(next) + limit}"
    }

    private fun toIntOrZero(number: String?): Int {
        return try {
            number?.toInt() ?: 0
        } catch (e: Exception) {
            0
        }
    }

}
