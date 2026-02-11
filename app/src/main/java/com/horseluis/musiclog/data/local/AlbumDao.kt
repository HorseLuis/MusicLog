package com.horseluis.musiclog.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {

    @Query("SELECT * FROM my_albums")
    fun observeAll(): Flow<List<AlbumEntity>>

    @Query("SELECT * FROM my_albums")
    suspend fun getAll(): List<AlbumEntity>

    @Query("SELECT * FROM my_albums WHERE name LIKE :query OR artist LIKE :query")
    suspend fun search(query: String): List<AlbumEntity>

    @Query("""
        SELECT * FROM my_albums 
        WHERE (name LIKE '%' || :query || '%' OR artist LIKE '%' || :query || '%')
        ORDER BY savedDate DESC
    """)
    fun observeFiltered(query: String): Flow<List<AlbumEntity>>

    @Query("""
        SELECT * FROM my_albums 
        WHERE (name LIKE '%' || :query || '%' OR artist LIKE '%' || :query || '%')
        AND preference = :preference
        ORDER BY savedDate DESC
    """)
    fun observeFiltered(query: String, preference: String): Flow<List<AlbumEntity>>

    @Query("SELECT id FROM my_albums")
    suspend fun getAllIds(): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(album: AlbumEntity)

    @Query("DELETE FROM my_albums WHERE id = :id")
    suspend fun delete(id: Long)
}
