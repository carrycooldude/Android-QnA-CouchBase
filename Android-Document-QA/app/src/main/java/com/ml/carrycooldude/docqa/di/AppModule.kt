package com.ml.carrycooldude.docqa.di

import android.app.Application
import com.ml.carrycooldude.docqa.data.ChunksDB
import com.ml.carrycooldude.docqa.data.DocumentsDB
import com.ml.carrycooldude.docqa.domain.embeddings.SentenceEmbeddingProvider
import com.ml.carrycooldude.docqa.domain.llm.GeminiRemoteAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// AppModule provides dependencies that are to be injected by Hilt
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // SingletonComponent ensures that instances survive
    // across the application's lifespan
    // @Singleton creates a single instance in the app's lifespan

    @Provides
    @Singleton
    fun provideDocumentsDB(application: Application): DocumentsDB {
        return DocumentsDB(application)
    }

    @Provides
    @Singleton
    fun provideChunksDB(application: Application): ChunksDB {
        return ChunksDB(application)
    }

    @Provides
    @Singleton
    fun provideGeminiRemoteAPI(): GeminiRemoteAPI {
        return GeminiRemoteAPI()
    }

    @Provides
    @Singleton
    fun provideSentenceEncoder(application: Application): SentenceEmbeddingProvider {
        return SentenceEmbeddingProvider(application)
    }
}