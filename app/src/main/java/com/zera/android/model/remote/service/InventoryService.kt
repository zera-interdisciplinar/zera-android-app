package com.zera.android.model.remote.service

import com.zera.android.model.dto.inventory.CategoryResponseDTO
import com.zera.android.model.dto.inventory.CreateDisposalRequestDTO
import com.zera.android.model.dto.inventory.CreateModelRequestDTO
import com.zera.android.model.dto.inventory.DashboardHomeResponseDTO
import com.zera.android.model.dto.inventory.DisposalResponseDTO
import com.zera.android.model.dto.inventory.IndicatorsResponseDTO
import com.zera.android.model.dto.inventory.ItemResponseDTO
import com.zera.android.model.dto.inventory.MaterialCatalogDTO
import com.zera.android.model.dto.inventory.ModelResponseDTO
import com.zera.android.model.dto.inventory.PagedItemsDTO
import com.zera.android.model.dto.inventory.PagedModelsDTO
import com.zera.android.model.dto.inventory.RejectItemRequestDTO
import com.zera.android.model.dto.inventory.UpdateItemRequestDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface InventoryService {
    @GET("api/v1/dashboard/home")
    suspend fun getHome(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 5,
    ): DashboardHomeResponseDTO

    @GET("api/v1/dashboard/indicators")
    suspend fun getIndicators(
        @Query("from") from: String? = null,
        @Query("to") to: String? = null,
    ): IndicatorsResponseDTO

    @GET("api/v1/items")
    suspend fun getItems(
        @Query("status") status: String? = null,
        @Query("categoryId") categoryId: String? = null,
        @Query("q") q: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): PagedItemsDTO

    @GET("api/v1/items/{id}")
    suspend fun getItem(
        @Path("id") id: String,
    ): ItemResponseDTO

    @GET("api/v1/categories")
    suspend fun getCategories(): List<CategoryResponseDTO>

    @GET("api/v1/models")
    suspend fun getModels(
        @Query("q") q: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): PagedModelsDTO

    @POST("api/v1/models")
    suspend fun createModel(
        @Body body: CreateModelRequestDTO,
    ): ModelResponseDTO

    @GET("api/v1/materials")
    suspend fun getMaterials(): List<MaterialCatalogDTO>

    @POST("api/v1/items/{id}/approve")
    suspend fun approveItem(
        @Path("id") id: String,
    ): ItemResponseDTO

    @POST("api/v1/items/{id}/reject")
    suspend fun rejectItem(
        @Path("id") id: String,
        @Body body: RejectItemRequestDTO,
    ): ItemResponseDTO

    @PATCH("api/v1/items/{id}")
    suspend fun updateItem(
        @Path("id") id: String,
        @Body body: UpdateItemRequestDTO,
    ): ItemResponseDTO

    @POST("api/v1/disposals")
    suspend fun createDisposal(
        @Body body: CreateDisposalRequestDTO,
    ): DisposalResponseDTO
}
