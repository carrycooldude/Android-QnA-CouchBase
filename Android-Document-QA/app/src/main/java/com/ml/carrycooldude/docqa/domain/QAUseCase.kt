package com.ml.carrycooldude.docqa.domain

import android.util.Log
import com.ml.carrycooldude.docqa.data.QueryResult
import com.ml.carrycooldude.docqa.data.RetrievedContext
import com.ml.carrycooldude.docqa.domain.llm.GeminiRemoteAPI
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Singleton
class QAUseCase
@Inject
constructor(
    private val documentsUseCase: DocumentsUseCase,
    private val chunksUseCase: ChunksUseCase,
    private val geminiRemoteAPI: GeminiRemoteAPI
) {

    fun getAnswer(query: String, prompt: String, onResponse: ((QueryResult) -> Unit)) {
        var jointContext = ""
        val retrievedContextList = ArrayList<RetrievedContext>()
        
        // Convert query to FloatArray
        val queryEmbedding = getQueryEmbedding(query)
        
        chunksUseCase.getSimilarChunks(queryEmbedding, n = 5).forEach {
            jointContext += " " + it.second.chunkData
            retrievedContextList.add(RetrievedContext(it.second.docFileName, it.second.chunkData))
        }
        Log.e("APP", "Context: $jointContext")
        val inputPrompt = prompt.replace("\$CONTEXT", jointContext).replace("\$QUERY", query)
        CoroutineScope(Dispatchers.IO).launch {
            geminiRemoteAPI.getResponse(inputPrompt)?.let { llmResponse ->
                onResponse(QueryResult(llmResponse, retrievedContextList))
            }
        }
    }

//    fun canGenerateAnswers(): Boolean {
//        return documentsUseCase.getDocsCount() > 0
//    }

    private fun getQueryEmbedding(query: String): FloatArray {
        // Implement the logic to convert the query string to a FloatArray
        // This could involve calling an embedding model or API
        // For example:
        // return embeddingModel.getEmbedding(query)
        // Here, we'll use a placeholder implementation
        return FloatArray(128) { 0.0f } // Replace with actual embedding logic
    }
}