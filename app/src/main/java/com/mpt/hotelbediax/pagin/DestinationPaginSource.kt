package com.mpt.hotelbediax.pagin

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mpt.hotelbediax.dao.DestinationDao
import com.mpt.hotelbediax.models.Destination

class DestinationPagingSource(
    private val destinationDao: DestinationDao
) : PagingSource<Int, Destination>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Destination> {
        return try {
            val nextPageNumber = params.key ?: 0
            val response = destinationDao.getDestinationsPaged(nextPageNumber * params.loadSize, params.loadSize)
            LoadResult.Page(
                data = response,
                prevKey = if (nextPageNumber > 0) nextPageNumber - 1 else null,
                nextKey = if (response.isNotEmpty()) nextPageNumber + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Destination>): Int? {
        return state.anchorPosition
    }
}