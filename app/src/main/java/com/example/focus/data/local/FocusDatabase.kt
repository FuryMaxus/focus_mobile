package com.example.focus.data.local

import androidx.room.Database
import com.example.focus.data.local.dao.FocusSessionDao
import com.example.focus.data.local.entity.FocusSessionEntity

@Database(entities = [FocusSessionEntity::class], version = 1, exportSchema = false)
abstract class FocusDatabase {

    abstract fun focusSessionDao(): FocusSessionDao

}
