package com.example.weather.repository.contentprovider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.example.weather.R
import com.example.weather.app.App.Companion.getHistoryDao
import com.example.weather.room.*

private const val URI_ALL = 1
private const val URI_ID = 2
private const val ENTITY_PATH = "HistoryEntity"

class HistoryContentProvider : ContentProvider() {

    private var authorities: String? = null
    private lateinit var uriMather: UriMatcher

    private var entityContentType: String? = null
    private var entityContentItemType: String? = null

    private lateinit var contentUri: Uri

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        require(uriMather.match(uri) == URI_ID) { "WRONG URI $uri" }
        val historyDao = getHistoryDao()
        val id = ContentUris.parseId(uri)
        historyDao.deleteById(id)
        context?.contentResolver?.notifyChange(uri, null)
        return 1
    }

    override fun getType(uri: Uri): String? {
        when (uriMather.match(uri)) {
            URI_ALL -> return entityContentType
            URI_ID -> return entityContentItemType
        }
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        require(uriMather.match(uri) == URI_ALL) { "WRONG URI $uri" }
        val historyDao = getHistoryDao()
        val entity = map(values)
        val id: Long = entity.id
        historyDao.insert(entity)
        val resultUri = ContentUris.withAppendedId(contentUri, id)
        context?.contentResolver?.notifyChange(resultUri, null)
        return resultUri
    }

    override fun onCreate(): Boolean {
        authorities = context?.resources?.getString(R.string.authorities)
        uriMather = UriMatcher(UriMatcher.NO_MATCH)
        uriMather.addURI(authorities, ENTITY_PATH, URI_ALL)
        uriMather.addURI(authorities, "$ENTITY_PATH/#", URI_ID)
        entityContentType = "vnd.android.cursor.dir/vnd.$authorities.$ENTITY_PATH"
        entityContentItemType = "vnd.android.cursor.item/vnd.$authorities.$ENTITY_PATH"
        contentUri = Uri.parse("content://$authorities/$ENTITY_PATH")
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        val historyDao: HistoryDao = getHistoryDao()
        val cursor = when (uriMather.match(uri)) {
            URI_ALL -> {
                historyDao.getHistoryCursor()
            }
            URI_ID -> {
                val id = ContentUris.parseId(uri)
                historyDao.getHistoryCursor(id)
            }
            else -> throw IllegalArgumentException("WRONG URI: $uri")
        }
        cursor.setNotificationUri(context!!.contentResolver, contentUri)
        return cursor
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        require(uriMather.match(uri) == URI_ID) { "WRONG URI $uri" }
        val historyDao = getHistoryDao()
        historyDao.update(map(values))
        context?.contentResolver?.notifyChange(uri, null)
        return 1
    }

    private fun map(values: ContentValues?): HistoryEntity {
        return if (values == null) {
            HistoryEntity()
        } else {
            val id = if (values.containsKey(ID)) values[ID] as Long else 0
            val city = values[CITY] as String
            val temperature = values[TEMPERATURE] as Int
            HistoryEntity(id, city, temperature)
        }
    }
}