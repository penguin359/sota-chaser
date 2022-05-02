package org.northwinds.app.sotachaser.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SummitDao {
    @Query("SELECT * FROM association")
    fun getAssociations(): List<Association>

    @Insert
    fun insertAll(vararg users: Association)

    @Query("SELECT * FROM region WHERE association_id IN (:associationId)")
    fun getRegionsInAssociation(associationId: Long): List<Region>

    @Query("DELETE FROM association")
    fun clear()
}
    //@Query("SELECT * FROM user WHERE uid IN (:userIds)")
    //fun loadAllByIds(userIds: IntArray): List<User>
    //
    //@Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
    //        "last_name LIKE :last LIMIT 1")
    //fun findByName(first: String, last: String): User
    //
    //@Insert
    //fun insertAll(vararg users: User)
    //
    //@Delete
    //fun delete(user: User)
