package com.zera.android.model.remote.client

import com.zera.android.model.entity.config.Environments
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SupabaseStorageTest {
    private val environments = Environments(
        admCoreApiUrl = "https://adm.example.com",
        admCoreApiKey = "adm-key",
        inventoryApiUrl = "https://inventory.example.com",
        supabaseUrl = "https://abc.supabase.co/",
        supabaseAnonKey = "anon-key",
        supabaseAvatarsBucket = "avatars",
    )

    @Test
    fun publicUrlStripsTrailingSlashAndBuildsObjectPath() {
        val path = SupabaseStorage.objectPath("user-1", "image/png")
        assertEquals("user-1/profile.png", path)
        assertEquals(
            "https://abc.supabase.co/storage/v1/object/public/avatars/user-1/profile.png",
            SupabaseStorage.publicUrl(environments, path),
        )
    }

    @Test
    fun isConfiguredRequiresAllThreeFields() {
        assertTrue(SupabaseStorage.isConfigured(environments))
        assertFalse(
            SupabaseStorage.isConfigured(environments.copy(supabaseAnonKey = "")),
        )
    }

    @Test
    fun jpegDefaultsToJpgExtension() {
        assertEquals("jpg", SupabaseStorage.extensionFor("image/jpeg"))
        assertEquals("jpg", SupabaseStorage.extensionFor("image/jpg"))
        assertEquals("webp", SupabaseStorage.extensionFor("image/webp"))
        assertEquals("image/jpeg", SupabaseStorage.canonicalMime("image/jpg"))
    }

    @Test
    fun storageErrorMessageUsesSupabaseBody() {
        val body = """{"statusCode":"400","error":"InvalidMimeType","message":"mime type not supported"}"""
        assertEquals(
            "Não foi possível enviar a foto (400): mime type not supported",
            SupabaseStorage.storageErrorMessage(400, body),
        )
    }
}
