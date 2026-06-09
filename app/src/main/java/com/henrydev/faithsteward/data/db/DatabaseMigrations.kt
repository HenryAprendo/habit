package com.henrydev.faithsteward.data.db

import androidx.room.migration.Migration

/**
 * Room schema migrations for [HabitDatabase].
 *
 * The DB is currently at version 1, so there are no migrations yet. This object is the
 * single place where future migrations live and get registered.
 *
 * Recipe for the NEXT schema change (NEVER wipe data on upgrade):
 *  1. Bump `version` in [HabitDatabase] (e.g. 1 -> 2). Room will export the new schema JSON.
 *  2. Add a Migration here, for example:
 *
 *       val MIGRATION_1_2 = object : Migration(1, 2) {
 *           override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
 *               db.execSQL("ALTER TABLE <table> ADD COLUMN <name> <type> NOT NULL DEFAULT 0")
 *           }
 *       }
 *
 *  3. Register it in [ALL] (e.g. `arrayOf(MIGRATION_1_2)`).
 *
 * Destructive fallback is intentionally NOT used for upgrades (it would erase real users'
 * habits, streaks, XP and history). Only a downgrade triggers a destructive reset, configured
 * in DatabaseModule.
 */
object DatabaseMigrations {
    val ALL: Array<Migration> = emptyArray()
}
