package com.zera.android.model.usecase.config

import com.zera.android.model.config.AppConfig
import com.zera.android.model.entity.config.Environments
import com.zera.android.model.remote.client.ApiClient
import com.zera.android.model.remote.client.ScrapyClient

class Boot {
    suspend fun execute(): Environments {
        val environments = ScrapyClient.boot()
        AppConfig.updateEnvironments(environments)
        ApiClient.init(environments)
        return environments
    }
}
