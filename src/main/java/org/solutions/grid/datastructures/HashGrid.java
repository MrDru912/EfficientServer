package org.solutions.grid.datastructures;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Class for the fast search for same locations within a graph.
 */
public class HashGrid {
    private final Map<GridCell, LocationRecord> grid = new HashMap<>();
    public static final int CELL_SIZE = 650;

    /**
     * Searches for the same location in the grid.
     * @param location location to search for.
     * @return existing location in the grid and null otherwise
     */
    @Nullable
    public LocationRecord locationIsInGrid(LocationRecord location){
        int cellX = location.x() / CELL_SIZE;
        int cellY = location.y() / CELL_SIZE;
        GridCell gridCell = new GridCell(0, 0);
        for (int i = cellX - 1; i <= cellX + 1; i++) {
            for (int j = cellY - 1; j <= cellY + 1; j++) {
                gridCell.setX(i);
                gridCell.setY(j);
                LocationRecord locationsInNeighbourCell = grid.get(gridCell);
                if (locationsInNeighbourCell != null
                        && euclideanDistance(location, locationsInNeighbourCell) <= 500){
                    return locationsInNeighbourCell; /* returning a gridCell coordinates where we have found cluster location */
                }
            }
        }
        return null;
    }

    /**
     * Adds a location to the grid.
     *
     * @param location the location to add
     * @return the existing location if there is a same location in the grid,
     *         otherwise the provided {@code location}
     */
    public LocationRecord addLocation(LocationRecord location) {
        LocationRecord existingLocation = locationIsInGrid(location);
        if (existingLocation == null) {
            GridCell gridCell = new GridCell(location);
            grid.put(gridCell, location);
            return location;
        } else {
            return existingLocation;
        }
    }

    /**
     * Counts Euclidean distance between two locations
     */
    public long euclideanDistance(LocationRecord l1, LocationRecord l2){
        double x = l1.x() - l2.x();
        double y = l1.y() - l2.y();
        return (long)Math.sqrt(x * x + y * y);
    }

    public void clear(){
        grid.clear();
    }

}
