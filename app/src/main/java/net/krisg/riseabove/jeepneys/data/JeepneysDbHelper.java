package net.krisg.riseabove.jeepneys.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.krisg.riseabove.jeepneys.data.JeepneysContract.JeepneyEntry;
import net.krisg.riseabove.jeepneys.data.JeepneysContract.LocationEntry;
import net.krisg.riseabove.jeepneys.data.JeepneysContract.PathEntry;
import net.krisg.riseabove.jeepneys.data.JeepneysContract.PointEntry;
import net.krisg.riseabove.jeepneys.data.JeepneysContract.RouteEntry;
import net.krisg.riseabove.jeepneys.data.JeepneysContract.VectorEntry;
import net.krisg.riseabove.jeepneys.data.JeepneysContract.WaypointEntry;


/**
 * Created by KrisEmmanuel on 9/22/2014.
 */
public class JeepneysDbHelper extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "jeepneys.db";
    public JeepneysDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        /*
        CREATE TABLE IF NOT EXISTS `jeepneysdb`.`Route` (
          `idRoute` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Route id',
          `routeNumber` VARCHAR(20) NOT NULL COMMENT 'Route number',
          PRIMARY KEY (`idRoute`))
        ENGINE = InnoDB;
        */
        final String SQL_CREATE_ROUTE_TABLE =
                "CREATE TABLE "+ RouteEntry.TABLE_NAME + "(" +
                RouteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ," +
                RouteEntry.COLUMN_ROUTENUMBER + " VARCHAR(20) NOT NULL" +
                ");";

        /*
        CREATE TABLE IF NOT EXISTS `jeepneysdb`.`Jeepney` (
          `idJeepney` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Jeepney unit id',
          `platenumber` VARCHAR(12) NULL COMMENT 'Jeepney plate number',
          `Route_idRoute` INT UNSIGNED NOT NULL COMMENT 'Jeepney route',
          PRIMARY KEY (`idJeepney`),
          UNIQUE INDEX `platenumber_UNIQUE` (`platenumber` ASC),
          INDEX `fk_Jeepney_Route1_idx` (`Route_idRoute` ASC),
          UNIQUE INDEX `idJeepney_UNIQUE` (`idJeepney` ASC),
          CONSTRAINT `fk_Jeepney_Route1`
            FOREIGN KEY (`Route_idRoute`)
            REFERENCES `jeepneysdb`.`Route` (`idRoute`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION)
        ENGINE = InnoDB;
        */
        final String SQL_CREATE_JEEPNEY_TABLE =
                "CREATE TABLE " + JeepneyEntry.TABLE_NAME + "(" +
                JeepneyEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ," +
                JeepneyEntry.COLUMN_PLATENUMBER + " VARCHAR(12)," +
                JeepneyEntry.COLUMN_ROUTE_IDROUTE + " INTEGER NOT NULL ," +
                "FOREIGN KEY(" + JeepneyEntry.COLUMN_ROUTE_IDROUTE + ")" +
                "REFERENCES " + RouteEntry.TABLE_NAME + "(" + RouteEntry._ID + ")" +
                ");";


        /*
        CREATE TABLE IF NOT EXISTS `jeepneysdb`.`Point` (
          `idPoint` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Point id',
          `latitude` DOUBLE NOT NULL,
          `longitude` DOUBLE NOT NULL,
          PRIMARY KEY (`idPoint`))
        ENGINE = InnoDB;
        */
        final String SQL_CREATE_POINT_TABLE =
                "CREATE TABLE " + PointEntry.TABLE_NAME + "(" +
                PointEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ," +
                PointEntry.COLUMN_LATITUDE + " DOUBLE NOT NULL," +
                PointEntry.COLUMN_LONGITUDE + " DOUBLE NOT NULL" +
                ");";

        /*
        CREATE TABLE IF NOT EXISTS `jeepneysdb`.`Vector` (
          `idVector` INT NOT NULL AUTO_INCREMENT,
          `Point_idPointFrom` INT UNSIGNED NOT NULL,
          `Point_idPointTo` INT UNSIGNED NOT NULL,
          `distanceMeter` DOUBLE NULL,
          PRIMARY KEY (`idVector`),
          INDEX `fk_Vector_Point1_idx` (`Point_idPointFrom` ASC),
          INDEX `fk_Vector_Point2_idx` (`Point_idPointTo` ASC),
          CONSTRAINT `fk_Vector_Point1`
            FOREIGN KEY (`Point_idPointFrom`)
            REFERENCES `jeepneysdb`.`Point` (`idPoint`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION,
          CONSTRAINT `fk_Vector_Point2`
            FOREIGN KEY (`Point_idPointTo`)
            REFERENCES `jeepneysdb`.`Point` (`idPoint`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION)
        ENGINE = InnoDB;
        */
        final String SQL_CREATE_VECTOR_TABLE =
        "CREATE TABLE " + VectorEntry.TABLE_NAME + "(" +
                VectorEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                VectorEntry.COLUMN_POINT_IDPOINTFROM + " INTEGER NOT NULL ," +
                VectorEntry.COLUMN_POINT_IDPOINTTO + " INTEGER NOT NULL ," +
                VectorEntry.COLUMN_DISTANCEMETER + " DOUBLE," +
                "FOREIGN KEY(" + VectorEntry.COLUMN_POINT_IDPOINTFROM + ")" +
                "REFERENCES " + PointEntry.TABLE_NAME + "(" + PointEntry._ID + ")," +
                "FOREIGN KEY(" + VectorEntry.COLUMN_POINT_IDPOINTTO + ")" +
                "REFERENCES " + PointEntry.TABLE_NAME + "(" + PointEntry._ID + ")" +
                ");";



        /*
        CREATE TABLE IF NOT EXISTS `jeepneysdb`.`Waypoint` (
          `idWaypoint` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Waypoint id',
          `vectorIndex` INT NOT NULL COMMENT 'Integer value for the indexing of vectors of the waypoint.',
          `Vector_idVector` INT NOT NULL,
          PRIMARY KEY (`idWaypoint`),
          INDEX `fk_Waypoint_Vector1_idx` (`Vector_idVector` ASC),
          CONSTRAINT `fk_Waypoint_Vector1`
            FOREIGN KEY (`Vector_idVector`)
            REFERENCES `jeepneysdb`.`Vector` (`idVector`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION)
        ENGINE = InnoDB;
         */
        final String SQL_CREATE_WAYPOINT_TABLE =
                "CREATE TABLE " + WaypointEntry.TABLE_NAME + "(" +
                WaypointEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ," +
                WaypointEntry.COLUMN_VECTORINDEX + " INTEGER NOT NULL," +
                WaypointEntry.COLUMN_VECTOR_IDVECTOR + " INTEGER NOT NULL," +
                "FOREIGN KEY(" + WaypointEntry.COLUMN_VECTOR_IDVECTOR + ")" +
                "REFERENCES " + VectorEntry.TABLE_NAME + "(" + VectorEntry._ID + ")" +
                ");";

        /*
        CREATE TABLE IF NOT EXISTS `jeepneysdb`.`Path` (
          `idPath` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Path id',
          `Route_idRoute` INT UNSIGNED NOT NULL COMMENT 'Path route',
          `Waypoint_idWaypoint` INT UNSIGNED NOT NULL COMMENT 'Path waypoint',
          `description` VARCHAR(255) NULL,
          PRIMARY KEY (`idPath`, `Route_idRoute`, `Waypoint_idWaypoint`),
          INDEX `fk_Path_Route1_idx` (`Route_idRoute` ASC),
          INDEX `fk_Path_Waypoint1_idx` (`Waypoint_idWaypoint` ASC),
          CONSTRAINT `fk_Path_Route1`
            FOREIGN KEY (`Route_idRoute`)
            REFERENCES `jeepneysdb`.`Route` (`idRoute`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION,
          CONSTRAINT `fk_Path_Waypoint1`
            FOREIGN KEY (`Waypoint_idWaypoint`)
            REFERENCES `jeepneysdb`.`Waypoint` (`idWaypoint`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION)
        ENGINE = InnoDB;
         */
        final String SQL_CREATE_PATH_TABLE =
                "CREATE TABLE " + PathEntry.TABLE_NAME + "(" +
                PathEntry._ID + " INTEGER NOT NULL ," +
                PathEntry.COLUMN_ROUTE_IDROUTE + " INTEGER NOT NULL ," +
                PathEntry.COLUMN_WAYPOINT_IDWAYPOINT + " INTEGER NOT NULL ," +
                PathEntry.COLUMN_DESCRIPTION + " VARCHAR(255)," +
                "PRIMARY KEY(" + PathEntry._ID + "," + PathEntry.COLUMN_ROUTE_IDROUTE + "," + PathEntry.COLUMN_WAYPOINT_IDWAYPOINT + ")," +
                "FOREIGN KEY(" + PathEntry.COLUMN_ROUTE_IDROUTE + ")" +
                "REFERENCES " + RouteEntry.TABLE_NAME + "(" + RouteEntry._ID + ")," +
                "FOREIGN KEY(" + PathEntry.COLUMN_WAYPOINT_IDWAYPOINT + ")" +
                "REFERENCES " + WaypointEntry.TABLE_NAME + "(" + WaypointEntry._ID + ")" +
                ");";


        /*
        CREATE TABLE IF NOT EXISTS `jeepneysdb`.`Location` (
          `idLocation` INT NOT NULL AUTO_INCREMENT COMMENT 'Location id',
          `name` VARCHAR(100) NOT NULL COMMENT 'Location name',
          `Point_idPoint` INT UNSIGNED NOT NULL,
          PRIMARY KEY (`idLocation`),
          INDEX `fk_Location_Point1_idx` (`Point_idPoint` ASC),
          CONSTRAINT `fk_Location_Point1`
            FOREIGN KEY (`Point_idPoint`)
            REFERENCES `jeepneysdb`.`Point` (`idPoint`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION)
        ENGINE = InnoDB;
         */
        final String SQL_CREATE_LOCATION_TABLE =
                "CREATE TABLE " + LocationEntry.TABLE_NAME + "(" +
                LocationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                LocationEntry.COLUMN_NAME + " VARCHAR(100) NOT NULL," +
                LocationEntry.COLUMN_POINT_IDPOINT + " INTEGER NOT NULL ," +
                "FOREIGN KEY(" + LocationEntry.COLUMN_POINT_IDPOINT + ")" +
                "REFERENCES " + PointEntry.TABLE_NAME + "(" + PointEntry._ID + ")" +
                ");";

        db.execSQL(SQL_CREATE_ROUTE_TABLE);
        db.execSQL(SQL_CREATE_JEEPNEY_TABLE);
        db.execSQL(SQL_CREATE_POINT_TABLE);
        db.execSQL(SQL_CREATE_VECTOR_TABLE);
        db.execSQL(SQL_CREATE_WAYPOINT_TABLE);
        db.execSQL(SQL_CREATE_PATH_TABLE);
        db.execSQL(SQL_CREATE_LOCATION_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RouteEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + JeepneyEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PointEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + VectorEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WaypointEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PathEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME);
        onCreate(db);
    }

}
