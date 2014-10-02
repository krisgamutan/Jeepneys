package net.krisg.riseabove.jeepneys.data;

import android.content.ContentValues;

/**
 * Created by KrisEmmanuel on 9/29/2014.
 */
public class Location {
    private long id;
    private String name;
    private long idVertex;

    public Location(long id, String name, long idVertex) {
        this.id = id;
        this.name = name;
        this.idVertex = idVertex;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getIdVertex() {
        return idVertex;
    }

    public void setIdVertex(long idVertex) {
        this.idVertex = idVertex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;

        Location location = (Location) o;

        if (id != location.id) return false;
        if (idVertex != location.idVertex) return false;
        if (name != null ? !name.equals(location.name) : location.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (int) (idVertex ^ (idVertex >>> 32));
        return result;
    }
    public static ContentValues makeContentValues(String name, long idVertex)
    {
        ContentValues values = new ContentValues();
        values.put(JeepneysContract.LocationEntry.COLUMN_NAME, name);
        values.put(JeepneysContract.LocationEntry.COLUMN_VERTEX_IDVERTEX, idVertex);
        return values;
    }

}
