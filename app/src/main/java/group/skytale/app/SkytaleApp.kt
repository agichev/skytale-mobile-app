package group.skytale.app

import android.app.Application
import group.skytale.app.data.AppGraph

class SkytaleApp : Application() {
    val graph: AppGraph by lazy { AppGraph(this) }
}

