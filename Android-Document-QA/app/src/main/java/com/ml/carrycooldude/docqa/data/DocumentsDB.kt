package com.ml.carrycooldude.docqa.data

import android.content.Context
import com.couchbase.lite.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class DocumentsDB(context: Context) {

    private val database: Database

    init {
        CouchbaseLite.init(context)
        val config = DatabaseConfiguration()
        database = Database("documents-db", config)
    }

    fun addDocument(document: MutableDocument): String {
        database.save(document)
        return document.id
    }

    fun getAllDocuments(): Flow<List<MutableDocument>> {
        return flow<List<MutableDocument>> {
            val query = QueryBuilder.select(SelectResult.all())
                .from(DataSource.database(database))
            val result = query.execute()
            val documents = mutableListOf<MutableDocument>()
            for (row in result) {
                val dictionary: Dictionary? = row.getDictionary(database.name)
                val document = MutableDocument().apply {
                    dictionary?.let { dict ->
                        setString("docId", dict.getString("docId"))
                        setString("fileName", dict.getString("fileName"))
                        setString("chunkText", dict.getString("chunkText"))
                        setArray("chunkEmbedding", dict.getArray("chunkEmbedding"))
                    }
                }
                documents.add(document)
            }
            emit(documents)
        }.flowOn(Dispatchers.IO)
    }

    fun removeDocument(docId: String) {
        val document = database.getDocument(docId)?.toMutable()
        if (document != null) {
            database.delete(document)
        }
    }

//    fun getDocsCount(): Long {
//        val query = QueryBuilder.select(SelectResult.expression(Function.count(Expression.all())))
//            .from(DataSource.database(database))
//        val result = query.execute()
//        return result.next()?.getLong(0) ?: 0
//    }
}