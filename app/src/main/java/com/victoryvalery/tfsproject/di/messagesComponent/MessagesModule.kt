package com.victoryvalery.tfsproject.di.messagesComponent

import android.content.Context
import coil.ImageLoader
import coil.request.Disposable
import coil.request.ImageRequest
import coil.size.Precision
import com.victoryvalery.tfsproject.domain.repository.ZulipRepository
import com.victoryvalery.tfsproject.domain.usecases.DeleteEventUseCase
import com.victoryvalery.tfsproject.domain.usecases.GetAllStreamTopicsUseCase
import com.victoryvalery.tfsproject.domain.usecases.RegisterEventUseCase
import com.victoryvalery.tfsproject.domain.usecases.messages.*
import dagger.Module
import dagger.Provides
import io.noties.markwon.Markwon
import io.noties.markwon.image.AsyncDrawable
import io.noties.markwon.image.coil.CoilImagesPlugin
import okhttp3.Interceptor
import okhttp3.OkHttpClient

@Module
class MessagesModule {
    @MessagesScope
    @Provides
    fun provideImageLoader(context: Context, zulipInterceptor: Interceptor) = ImageLoader.Builder(context)
        .apply {
            availableMemoryPercentage(0.5)
            bitmapPoolPercentage(0.5)
            crossfade(true)
        }.okHttpClient {
            OkHttpClient.Builder()
                .addInterceptor(zulipInterceptor)
                .build()
        }
        .build()

    @MessagesScope
    @Provides
    fun provideCoilPlugin3(context: Context, imageLoader: ImageLoader): CoilImagesPlugin {
        val loader = imageLoader
        return CoilImagesPlugin.create(
            object : CoilImagesPlugin.CoilStore {
                override fun load(drawable: AsyncDrawable): ImageRequest {
                    return ImageRequest.Builder(context)
                        .defaults(loader.defaults)
                        .size(500, 500)
                        .precision(Precision.INEXACT)
                        .data(drawable.destination)
                        .crossfade(false)
                        .build()
                }

                override fun cancel(disposable: Disposable) {
                    disposable.dispose()
                }
            },
            loader
        )
    }

    @MessagesScope
    @Provides
    fun provideMarkwon(context: Context, coilPlugin3: CoilImagesPlugin) =
        Markwon.builder(context)
            .usePlugin(coilPlugin3)
            .build()

    @MessagesScope
    @Provides
    fun provideGetMessagesUseCase(repository: ZulipRepository): GetMessagesUseCase {
        return GetMessagesUseCase(repository)
    }

    @MessagesScope
    @Provides
    fun providePostMessageUseCase(repository: ZulipRepository): PostMessageUseCase {
        return PostMessageUseCase(repository)
    }

    @MessagesScope
    @Provides
    fun provideAddEmojiUseCase(repository: ZulipRepository): AddEmojiUseCase {
        return AddEmojiUseCase(repository)
    }

    @MessagesScope
    @Provides
    fun provideRemoveEmojiUseCase(repository: ZulipRepository): RemoveEmojiUseCase {
        return RemoveEmojiUseCase(repository)
    }

    @MessagesScope
    @Provides
    fun provideRegisterEventUseCase(repository: ZulipRepository): RegisterEventUseCase {
        return RegisterEventUseCase(repository)
    }

    @MessagesScope
    @Provides
    fun provideGetMessagesEventUseCase(repository: ZulipRepository): GetMessagesEventUseCase {
        return GetMessagesEventUseCase(repository)
    }

    @MessagesScope
    @Provides
    fun provideGetEmojiEventUseCase(repository: ZulipRepository): GetEmojiEventUseCase {
        return GetEmojiEventUseCase(repository)
    }

    @MessagesScope
    @Provides
    fun provideDeleteEventUseCase(repository: ZulipRepository): DeleteEventUseCase {
        return DeleteEventUseCase(repository)
    }

    @MessagesScope
    @Provides
    fun provideSendFileUseCase(repository: ZulipRepository): SendFileUseCase {
        return SendFileUseCase(repository)
    }

    @MessagesScope
    @Provides
    fun provideSaveCacheMessagesUseCase(repository: ZulipRepository): SaveCacheMessagesUseCase {
        return SaveCacheMessagesUseCase(repository)
    }

    @MessagesScope
    @Provides
    fun provideDeleteMessageUseCase(repository: ZulipRepository): DeleteMessageUseCase {
        return DeleteMessageUseCase(repository)
    }

    @MessagesScope
    @Provides
    fun provideEditMessageUseCase(repository: ZulipRepository): EditMessageUseCase {
        return EditMessageUseCase(repository)
    }

    @MessagesScope
    @Provides
    fun provideMoveMessageUseCase(repository: ZulipRepository): MoveMessageUseCase {
        return MoveMessageUseCase(repository)
    }

    @MessagesScope
    @Provides
    fun provideGetSingleMessageUseCase(repository: ZulipRepository): GetSingleMessageUseCase {
        return GetSingleMessageUseCase(repository)
    }

    @MessagesScope
    @Provides
    fun provideGetAllStreamTopicsUseCase(repository: ZulipRepository): GetAllStreamTopicsUseCase {
        return GetAllStreamTopicsUseCase(repository)
    }

    @MessagesScope
    @Provides
    fun provideDownloadFileUseCase(repository: ZulipRepository): DownloadFileUseCase {
        return DownloadFileUseCase(repository)
    }
}
