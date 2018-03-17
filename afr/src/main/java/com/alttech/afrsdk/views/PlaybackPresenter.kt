package com.alttech.afrsdk.views

import com.alttech.afrsdk.Config
import com.alttech.afrsdk.data.PlaybackList
import com.alttech.afrsdk.data.Show
import com.alttech.afrsdk.data.WidgetDataResult
import com.alttech.afrsdk.network.ApiCalls
import rx.subscriptions.CompositeSubscription

/**
 * Created by bubu on 26/10/2017.
 */

class PlaybackPresenter(private val config: Config) {

  var view: PlaybackView? = null

  var playbackListPosition = -1

  val cs = CompositeSubscription()

  fun subscribe(view: PlaybackView) {
    this.view = view
  }

  fun unSubscribe() {
    cs.clear()
    this.view = null
  }


  fun fetchWidgetData() {
    cs.add(ApiCalls.getShows(config.appId!!, config.resId!!)
        .subscribe({ t: WidgetDataResult ->
          view?.showWidgetData(t)
        }, {
          it.printStackTrace()
        })
    )
  }

  fun getPlaybackList(show: Show, position: Int) {
    if (playbackListPosition > 0) view?.removeItem(playbackListPosition)

    val col = if (position > playbackListPosition) position - 1 else position

    val p = position - if (playbackListPosition < position) 1 else 0
    playbackListPosition = p + (view!!.getColumnSize() - (p % view!!.getColumnSize()))
    view?.addPlaybackList(playbackListPosition, PlaybackList(col % view!!.getColumnSize()))
  }

  interface PlaybackView {
    fun showWidgetData(data: WidgetDataResult)
    fun loadDataError()
    fun removeItem(pos: Int)
    fun addPlaybackList(pos: Int, playbackList: PlaybackList)
    fun getColumnSize(): Int
    fun progressView(show: Boolean)
    fun showErrorText(txt: String)
    fun getPlaybackPos(): Int
  }
}