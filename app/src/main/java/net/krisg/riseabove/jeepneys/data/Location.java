package net.krisg.riseabove.jeepneys.data;

import android.content.ContentValues;

/**
 * Created by KrisEmmanuel on 9/29/2014.
 */
public class Location {
    private long id;
    private String name;
    private long idVertex;

    private long idLocationCategory;
    private byte[] photo1;
    private byte[] photo2;
    private byte[] photo3;
    private String description;

    public Location(long id, String name, long idVertex, long idLocationCategory, byte[] photo1, byte[] photo2, byte[] photo3, String description) {
        this.id = id;
        this.name = name;
        this.idVertex = idVertex;
        this.idLocationCategory = idLocationCategory;
        this.photo1 = photo1;
        this.photo2 = photo2;
        this.photo3 = photo3;
        this.description = description;
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

    public long getIdLocationCategory() {
        return idLocationCategory;
    }

    public void setIdLocationCategory(long idLocationCategory) {
        this.idLocationCategory = idLocationCategory;
    }

    public byte[] getPhoto1() {
        return photo1;
    }

    public void setPhoto1(byte[] photo1) {
        this.photo1 = photo1;
    }

    public byte[] getPhoto2() {
        return photo2;
    }

    public void setPhoto2(byte[] photo2) {
        this.photo2 = photo2;
    }

    public byte[] getPhoto3() {
        return photo3;
    }

    public void setPhoto3(byte[] photo3) {
        this.photo3 = photo3;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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




    public static ContentValues makeContentValues(String name, long idVertex, long idLocationCategory, String description)
    {
        ContentValues values = new ContentValues();
        values.put(JeepneysContract.LocationEntry.COLUMN_NAME, name);
        values.put(JeepneysContract.LocationEntry.COLUMN_VERTEX_IDVERTEX, idVertex);

        values.put(JeepneysContract.LocationEntry.COLUMN_LOCATIONCATEGORY_IDLOCATIONCATEGORY, idLocationCategory);


        values.put(JeepneysContract.LocationEntry.COLUMN_DESCRIPTION, description);
        return values;
    }

}
