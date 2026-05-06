package com.example.cursova.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.cursova.domain.BikePart
import com.example.cursova.domain.PartType
import com.example.cursova.domain.SavedBuild
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [BikePart::class, SavedBuild::class], version = 5)
abstract class AppDatabase : RoomDatabase() {

    abstract fun bikePartDao(): BikePartDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bike_configurator_db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.bikePartDao())
                }
            }
        }

        suspend fun populateDatabase(dao: BikePartDao) {
            val initialParts = listOf(
                // --- FRAMES ---
                BikePart("frame_sw_wc", "S-Works Enduro World Cup", PartType.FRAME, 3800.0, "frame_sworksworldcup"),
                BikePart("frame_sw_carbon", "S-Works Enduro Carbon", PartType.FRAME, 2400.0, "frame_sworks"),
                BikePart("frame_sw_alu", "S-Works Enduro Aluminium", PartType.FRAME, 1600.0, "frame_sworksalu"),
                
                // Santa Cruz Frames
                BikePart("frame_sc_hightower", "Santa Cruz Hightower", PartType.FRAME, 3400.0, "frame_santacruz_hightower"),
                BikePart("frame_sc_megatower_black", "Santa Cruz Megatower Black ", PartType.FRAME, 3600.0, "frame_santacruz_megatowerblack"),
                BikePart("frame_sc_megatower_blue", "Santa Cruz Megatower Blue", PartType.FRAME, 3600.0, "frame_santacruz_megatowerblue"),
                BikePart("frame_sc_megatower_grey", "Santa Cruz Megatower Grey", PartType.FRAME, 3600.0, "frame_santacruz_megatowergrey"),
                BikePart("frame_sc_megatower_lightgrey", "Santa Cruz Megatower Light Grey", PartType.FRAME, 3600.0, "frame_santacruz_megatowerlightgrey"),
                BikePart("frame_sc_megatower_red", "Santa Cruz Megatower Red", PartType.FRAME, 3600.0, "frame_santacruz_megatowerred"),
                BikePart("frame_sc_megatower_slabgrey", "Santa Cruz Megatower Slab Grey", PartType.FRAME, 3600.0, "frame_santacruz_megatowerslabgrey"),

                // Nukeproof Frames
                BikePart("frame_np_mega290", "Nukeproof Mega 290", PartType.FRAME, 2800.0, "frame_nukeproofmega290black"),
                BikePart("frame_np_giga290", "Nukeproof Giga 290", PartType.FRAME, 2950.0, "frame_nukeproofgiga290black"),

                // Transition Frames
                BikePart("frame_tr_sentinelv3", "Transition Sentinel V3", PartType.FRAME, 3400.0, "frame_transitionsentinelv3_purple"),

                // --- FORKS ---
                BikePart("fork_fox38_f", "Fox 38 Factory Grip2", PartType.FORK, 1250.0, "fork_fox38"),
                BikePart("fork_fox38_o", "Fox 38 Factory (Orange)", PartType.FORK, 1250.0, "fork_fox38orange"),
                BikePart("fork_rs_zeb_u", "RockShox ZEB Ultimate (Black)", PartType.FORK, 1100.0, "fork_zebblack"),
                BikePart("fork_rs_zeb_slab", "RockShox ZEB Ultimate (Slab Grey)", PartType.FORK, 1100.0, "fork_rszebslabgrey"),
                BikePart("fork_rs_zeb_red", "RockShox ZEB Ultimate (Red)", PartType.FORK, 1100.0, "fork_rszebred"),

                // --- REAR SHOCKS ---
                BikePart("shock_fox_x2", "Fox Float X2 Factory", PartType.REAR_SHOCK, 799.0, "shock_floatfactorygenie"),
                BikePart("shock_fox_x_neo", "Fox Float X NEO", PartType.REAR_SHOCK, 950.0, "shock_floatxneometric"),
                BikePart("shock_fox_dhx", "Fox Float DHX NEO Coil", PartType.REAR_SHOCK, 850.0, "shock_dhxneo"),
                BikePart("shock_rs_sd_u", "RockShox Super Deluxe Ultimate", PartType.REAR_SHOCK, 650.0, "shock_superdeluxeultimaterc2t"),
                BikePart("shock_rs_sd_c", "RockShox Super Deluxe Coil", PartType.REAR_SHOCK, 600.0, "shock_superdeluxeultimaterc2tcoil"),
                BikePart("shock_rs_vivid", "RockShox Vivid Ultimate", PartType.REAR_SHOCK, 780.0, "shock_vividairultimaterc2t"),

                // --- WHEELS (with audioUrl) ---
                BikePart("wheel_dt_541_c", "DT Swiss FR 541 (Custom)", PartType.WHEELS, 1100.0, "wheel_dt", audioUrl = "dt_swiss_541"),
                BikePart("wheel_dt_541_s", "DT Swiss FR 541 Standard", PartType.WHEELS, 900.0, "wheel_dtfr571", audioUrl = "dt_swiss_541"),
                BikePart("wheel_dt_xcm", "DT Swiss XCM 1200", PartType.WHEELS, 700.0, "wheel_dtxcm1200", audioUrl = "dt_swiss_541"),

                // --- PERIPHERALS ---
                BikePart("cockpit_renthal", "Renthal Fatbar Cockpit", PartType.COCKPIT, 280.0, "cocpit"),
                BikePart("brake_sram_code", "SRAM Code Stealth Ultimate", PartType.BRAKES, 550.0, "brake_sramcode"),
                BikePart("drop_oneup_v3", "OneUp V3 Dropper Post", PartType.DROPPER, 270.0, "dropper_oneupv3"),
                BikePart("drop_fox_tf", "Fox Transfer Factory", PartType.DROPPER, 360.0, "dropper_foxtransferkashima"),
                BikePart("saddle_spank", "Spank Ozy 220 Saddle", PartType.SADDLE, 95.0, "seat_spankozzy220"),

                // --- BOTTOM BRACKETS ---
                BikePart("bb_sram_dub", "SRAM DUB System", PartType.BOTTOM_BRACKET, 45.0, "sramgroupset", "DUB"),
                BikePart("bb_shim_ht2", "Shimano Hollowtech II", PartType.BOTTOM_BRACKET, 35.0, "shimanogroupset", "HOLLOWTECH"),

                // --- SRAM DRIVETRAIN (DUB) ---
                BikePart("crank_sram_xx1", "SRAM XX1 Eagle Carbon", PartType.CRANKS, 560.0, "crank_sramxx1_carbon", "DUB"),
                BikePart("der_sram_xx_sl", "SRAM XX SL Eagle T-Type", PartType.DRIVETRAIN, 650.0, "derailleur_sramxx", "DUB"),
                BikePart("der_sram_x0", "SRAM X0 Eagle T-Type", PartType.DRIVETRAIN, 480.0, "deraillieur_x0", "DUB"),
                BikePart("der_sram_gx", "SRAM GX Eagle T-Type", PartType.DRIVETRAIN, 320.0, "derailleur_gx", "DUB"),
                BikePart("cass_sram_xx", "SRAM XX Eagle Cassette", PartType.CASSETTE, 475.0, "cassette_sramxx", "DUB"),
                BikePart("cass_sram_x0", "SRAM X0 Eagle Cassette", PartType.CASSETTE, 380.0, "cassette_sramx0", "DUB"),
                BikePart("cass_sram_gx", "SRAM GX Eagle Cassette", PartType.CASSETTE, 215.0, "cassette_srameagle70", "DUB"),
                BikePart("chain_sram_xx1", "SRAM XX1 Eagle Chain", PartType.CHAIN, 90.0, "chain", "DUB"),

                // --- SHIMANO DRIVETRAIN (HOLLOWTECH) ---
                BikePart("crank_shim_xtr", "Shimano XTR M9100", PartType.CRANKS, 420.0, "cranks_shimanoxtr", "HOLLOWTECH"),
                BikePart("der_shim_xtr", "Shimano XTR M9050 Di2", PartType.DRIVETRAIN, 620.0, "xtrm9250di2_longcage", "HOLLOWTECH"),
                BikePart("der_shim_xt", "Shimano XT M8150 Di2", PartType.DRIVETRAIN, 350.0, "xtm8250di2_longcage", "HOLLOWTECH"),
                BikePart("cass_shim_xtr", "Shimano XTR CS-M9100", PartType.CASSETTE, 320.0, "cassette_xtrm9200", "HOLLOWTECH"),
                BikePart("cass_shim_xt", "Shimano XT CS-M8100", PartType.CASSETTE, 160.0, "cassette_xtm8200", "HOLLOWTECH"),
                BikePart("cass_shim_slx", "Shimano SLX CS-M7100", PartType.CASSETTE, 100.0, "cassette_slxm7100", "HOLLOWTECH"),
                BikePart("cass_shim_deore", "Shimano Deore CS-M6100", PartType.CASSETTE, 65.0, "cassette_deorem6100", "HOLLOWTECH"),
                BikePart("chain_shim_xtr", "Shimano XTR Chain", PartType.CHAIN, 60.0, "chain", "HOLLOWTECH")
            )

            dao.insertAllParts(initialParts)
        }
    }
}
