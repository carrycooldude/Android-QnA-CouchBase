package com.ml.carrycooldude.docqa.data

import android.content.Context
import com.couchbase.lite.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ChunksDB(context: Context) {

    private val database: Database

    init {
        CouchbaseLite.init(context)
        val config = DatabaseConfiguration()
        database = Database("chunks-db", config)
    }

    fun addChunk(chunk: MutableDocument) {
        database.save(chunk)
    }

    fun getSimilarChunks(queryEmbedding: FloatArray, n: Int = 5): List<Pair<Float, MutableDocument>> {
        // This is a placeholder implementation. Couchbase Lite does not have built-in support for nearest neighbor search.
        // You would need to implement this functionality yourself or use an external library.
        val query = QueryBuilder.select(SelectResult.all())
            .from(DataSource.database(database))
            .where(Expression.property("chunkEmbedding").notNullOrMissing())
        val result = query.execute()

        // Placeholder for similarity calculation
        val similarChunks = mutableListOf<Pair<Float, MutableDocument>>()
        for (row in result) {
            val dictionary = row.getDictionary(database.name)
            val document = MutableDocument().apply {
                dictionary?.let {
                    setString("docId", it.getString("docId"))
                    setString("fileName", it.getString("fileName"))
                    setString("chunkText", it.getString("chunkText"))
                    setArray("chunkEmbedding", it.getArray("chunkEmbedding"))
                }
            }
            val embeddingArray = document.getArray("chunkEmbedding")
            val embedding = embeddingArray?.toList()?.map { (it as Number).toDouble() }?.toFloatArray() ?: floatArrayOf()
            val similarityScore = calculateSimilarity(queryEmbedding, embedding)
            similarChunks.add(Pair(similarityScore, document))
        }

        return similarChunks.sortedByDescending { it.first }.take(n)
    }

    private fun calculateSimilarity(queryEmbedding: FloatArray, chunkEmbedding: FloatArray): Float {
        // Placeholder for actual similarity calculation
        return 0.0f
    }
}

fun List<Double>.toFloatArray(): FloatArray {
    val floatArray = FloatArray(this.size)
    for (i in this.indices) {
        floatArray[i] = this[i].toFloat()
    }
    return floatArray
}