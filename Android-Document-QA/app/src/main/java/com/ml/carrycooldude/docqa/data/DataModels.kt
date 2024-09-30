package com.ml.carrycooldude.docqa.data

import io.objectbox.annotation.Entity
import io.objectbox.annotation.HnswIndex
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index

import com.couchbase.lite.BasicAuthenticator
import com.couchbase.lite.Collection
import com.couchbase.lite.CouchbaseLite
import com.couchbase.lite.DataSource
import com.couchbase.lite.Database
import com.couchbase.lite.DocumentFlag
import com.couchbase.lite.ListenerToken
import com.couchbase.lite.LogDomain
import com.couchbase.lite.LogLevel
import com.couchbase.lite.MutableArray
import com.couchbase.lite.MutableDocument
import com.couchbase.lite.Parameters
import com.couchbase.lite.Query
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.Replicator
import com.couchbase.lite.ReplicatorConfiguration
import com.couchbase.lite.ReplicatorType
import com.couchbase.lite.ResultSet
import com.couchbase.lite.SelectResult
import com.couchbase.lite.URLEndpoint
import com.couchbase.lite.VectorEncoding
import com.couchbase.lite.VectorIndexConfiguration

@Entity
data class Chunk(
    @Id var chunkId: Long = 0,
    @Index var docId: Long = 0,
    var docFileName: String = "",
    var chunkData: String = "",
    @HnswIndex(dimensions = 384) var chunkEmbedding: FloatArray = floatArrayOf()
)

@Entity
data class Document(
    @Id var docId: Long = 0,
    var docText: String = "",
    var docFileName: String = "",
    var docAddedTime: Long = 0,
)

data class RetrievedContext(val fileName: String, val context: String)

data class QueryResult(val response: String, val context: List<RetrievedContext>)
