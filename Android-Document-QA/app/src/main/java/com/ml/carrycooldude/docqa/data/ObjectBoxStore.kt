package com.ml.carrycooldude.docqa.data

import android.content.Context
import com.couchbase.lite.*

object CouchbaseLiteStore {

    lateinit var database: Database
        private set

    fun init(context: Context, dbName: String) {
        CouchbaseLite.init(context)
        val config = DatabaseConfiguration()
        database = Database(dbName, config)
    }
}
