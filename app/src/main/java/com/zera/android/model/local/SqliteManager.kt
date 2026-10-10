package com.zera.android.model.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

object SqliteManager {
    private const val DATABASE_NAME = "zera.db"
    private const val DATABASE_VERSION = 1
    private const val TABLE_SESSION = "session"
    private const val COLUMN_KEY = "key"
    private const val COLUMN_VALUE = "value"

    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_UNIT_ID = "unit_id"
    private const val KEY_EMAIL = "email"
    private const val KEY_PASSWORD = "password"

    private lateinit var helper: SessionDbHelper

    fun init(context: Context) {
        helper = SessionDbHelper(context.applicationContext)
    }

    @Synchronized
    fun saveSession(
        accessToken: String,
        refreshToken: String,
        userId: String,
        email: String,
        password: String,
    ) {
        val db = helper.writableDatabase
        db.beginTransaction()
        try {
            put(db, KEY_ACCESS_TOKEN, accessToken)
            put(db, KEY_REFRESH_TOKEN, refreshToken)
            put(db, KEY_USER_ID, userId)
            put(db, KEY_EMAIL, email)
            put(db, KEY_PASSWORD, password)
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun getAccessToken(): String? = get(KEY_ACCESS_TOKEN)

    fun getRefreshToken(): String? = get(KEY_REFRESH_TOKEN)

    @Synchronized
    fun updateTokens(accessToken: String, refreshToken: String) {
        val db = helper.writableDatabase
        db.beginTransaction()
        try {
            put(db, KEY_ACCESS_TOKEN, accessToken)
            put(db, KEY_REFRESH_TOKEN, refreshToken)
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun getUserId(): String? = get(KEY_USER_ID)

    @Synchronized
    fun saveUnitId(unitId: String) {
        put(helper.writableDatabase, KEY_UNIT_ID, unitId)
    }

    fun getUnitId(): String? = get(KEY_UNIT_ID)

    fun getEmail(): String? = get(KEY_EMAIL)

    fun getPassword(): String? = get(KEY_PASSWORD)

    fun hasSavedLogin(): Boolean =
        !getRefreshToken().isNullOrBlank() ||
            (!getEmail().isNullOrBlank() && !getPassword().isNullOrBlank())

    @Synchronized
    fun clearSession() {
        helper.writableDatabase.delete(TABLE_SESSION, null, null)
    }

    private fun put(db: SQLiteDatabase, key: String, value: String) {
        val values = ContentValues().apply {
            put(COLUMN_KEY, key)
            put(COLUMN_VALUE, value)
        }
        db.insertWithOnConflict(TABLE_SESSION, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    @Synchronized
    private fun get(key: String): String? {
        helper.writableDatabase.query(
            TABLE_SESSION,
            arrayOf(COLUMN_VALUE),
            "$COLUMN_KEY = ?",
            arrayOf(key),
            null,
            null,
            null,
        ).use { cursor ->
            if (!cursor.moveToFirst()) return null
            return cursor.getString(0)
        }
    }

    private class SessionDbHelper(context: Context) : SQLiteOpenHelper(
        context,
        DATABASE_NAME,
        null,
        DATABASE_VERSION,
    ) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE $TABLE_SESSION (
                    $COLUMN_KEY TEXT PRIMARY KEY NOT NULL,
                    $COLUMN_VALUE TEXT
                )
                """.trimIndent(),
            )
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
    }
}
