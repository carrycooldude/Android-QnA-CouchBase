package com.ml.carrycooldude.docqa.domain

import android.util.Log
import com.couchbase.lite.MutableDocument
import com.ml.carrycooldude.docqa.data.ChunksDB
import com.ml.carrycooldude.docqa.domain.readers.Readers
import com.ml.carrycooldude.docqa.domain.splitters.WhiteSpaceSplitter
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import setProgressDialogText

@Singleton
class ChunksUseCase
@Inject
constructor(private val chunksDB: ChunksDB) {

    suspend fun addChunk(docId: Long, fileName: String, chunkText: String) =
        withContext(Dispatchers.IO) {
            val chunk = com.couchbase.lite.MutableDocument().apply { // Use fully qualified name here
                setString("docId", docId.toString())
                setString("fileName", fileName)
                setString("chunkText", chunkText)
            }
            chunksDB.addChunk(chunk)
        }

    fun getSimilarChunks(queryEmbedding: FloatArray, n: Int = 5): List<Pair<Float, MutableDocument>> {
        return chunksDB.getSimilarChunks(queryEmbedding, n)
    }
}