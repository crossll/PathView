package cross.model;

import android.graphics.Path;

/**
 * Created by cross on 17/1/20.
 */

public class PathArea {
    private String AreaName;
    private Path path;
    private int AreaColor;
    public String getAreaName() {
        return AreaName;
    }

    public void setAreaName(String areaName) {
        AreaName = areaName;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public int getAreaColor() {
        return AreaColor;
    }

    public void setAreaColor(int areaColor) {
        AreaColor = areaColor;
    }
}
