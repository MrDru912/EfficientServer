package org.solutions.grid.datastructures;

import java.util.Objects;
import static org.solutions.grid.datastructures.HashGrid.CELL_SIZE;

/**
 * Class for representation of grid cell.
 */
public class GridCell {

    private int x;
    private int y;

    public GridCell(LocationRecord location){
        this.x = location.x() / CELL_SIZE;
        this.y = location.y() / CELL_SIZE;
    }

    public GridCell(int cellX, int cellY) {
        this.x = cellX;
        this.y = cellY;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        GridCell gridCell = (GridCell) o;
        return x == gridCell.x && y == gridCell.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y);
    }

}
