package com.ml.carrycooldude.docqa.domain

import android.util.Log
import com.couchbase.lite.MutableDocument
import com.ml.carrycooldude.docqa.data.Document
import com.ml.carrycooldude.docqa.data.DocumentsDB
import com.ml.carrycooldude.docqa.domain.readers.Readers
import com.ml.carrycooldude.docqa.domain.splitters.WhiteSpaceSplitter
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import setProgressDialogText

@Singleton
class DocumentsUseCase
@Inject
constructor(private val chunksUseCase: ChunksUseCase, private val documentsDB: DocumentsDB) {

    suspend fun addDocument(
        inputStream: InputStream,
        fileName: String,
        documentType: Readers.DocumentType
    ) =
        withContext(Dispatchers.IO) {
            val text =
                Readers.getReaderForDocType(documentType).readFromInputStream(inputStream)
                    ?: return@withContext
            Log.e("APP", "PDF Text: $text")
            
            // Convert Document to MutableDocument
            val document = Document(
                docText = text,
                docFileName = fileName,
                docAddedTime = System.currentTimeMillis()
            )
            val documentMutable = MutableDocument().apply {
                setString("docText", document.docText)
                setString("docFileName", document.docFileName)
                setLong("docAddedTime", document.docAddedTime)
            }
            
            val newDocId = documentsDB.addDocument(documentMutable)
            
            setProgressDialogText("Creating chunks...")
            val chunks = WhiteSpaceSplitter.createChunks(text, chunkSize = 500, chunkOverlap = 50)
            setProgressDialogText("Adding chunks to database...")
            val size = chunks.size
            chunks.forEachIndexed { index, s ->
                setProgressDialogText("Added ${index+1}/${size} chunk(s) to database...")
                chunksUseCase.addChunk(newDocId.toLong(), fileName, s)  // Convert newDocId to Long
            }
        }

    fun getAllDocuments(): Flow<List<Document>> {
        return documentsDB.getAllDocuments().map { mutableList ->
            mutableList.map { mutableDocument ->
                Document(
                    docText = mutableDocument.getString("docText") ?: "",
                    docFileName = mutableDocument.getString("docFileName") ?: "",
                    docAddedTime = mutableDocument.getLong("docAddedTime") ?: 0L
                )
            }
        }
    }

//    fun removeDocument(docId: Long) {
//        documentsDB.removeDocument(docId.toString())  // Convert docId to String
//        chunksUseCase.removeChunks(docId)
//    }

//    fun getDocsCount(): Long {
//        return documentsDB.getDocsCount()
//    }
}