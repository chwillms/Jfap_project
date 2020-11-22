package de.unisaar.faphack.model.map;

import java.util.List;

import de.unisaar.faphack.model.Character;
import de.unisaar.faphack.model.Direction;
import de.unisaar.faphack.model.MarshallingContext;
import de.unisaar.faphack.model.Storable;
import static java.lang.Math.max;
import java.lang.IndexOutOfBoundsException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author
 *
 */
public class Room implements Storable {

  /** The world this room belongs to */
  private World w;

  /** The Characters that currently are in this room */
  private List<Character> inhabitants = new ArrayList<>();

  /**
   * A 2-dimensional Array defining the layout of the tiles in the room.
   */
  private Tile[][] tiles;

  public Room(){}

  Room(Tile[][] tiles) { this.tiles = tiles; }

  /**
   *
   * @return size of room on the xaxis (length of tiles)
   */
  private int get_x_dimension_size() {
    return this.tiles.length;
  }

  /**
   *
   * @return size of room on yaxis (length of sublists of tiles)
   */
  private int get_y_dimension_size() {
    int l1 = get_x_dimension_size();
    int l2 = 0;
    if (l1 != 0) {
      l2 = this.tiles[0].length;
      // todo: asserts all sublists have the same length!
    }
    return l2;
  }

  /**
   * This method returns a tile determined by the specified tile <code> t </ code> and the <code> direction </ code> d.
   * If the path between the specified tile and the derived tile is blocked by a wall,
   * the wall tile is returned.
   *
   * HINT: use the computeDDA to compute the path
   *
   * @param t the start tile
   * @param d the direction to follow
   * @return
   */
  public Tile getNextTile(Tile t, Direction d) {
    // TODO REIMPLEMENT THIS METHOD TO FOLLOW NEW REQUIREMENTS (SEE COMMENT ABOVE)
    int newx = max(t.x + d.x, 0);  // negative positions are not allowed
    int newy = max(t.y + d.y, 0);
    // todo: should we move somewhere if concrete direction not possible or just return old tile?
    // todo: what if room is empty? should raise Exception todo define own exception: room is empty exception?
    // todo: what if tile not in room? your fault, don't care but maybe cannot change to other tile..
    // todo: if total direction not possible, raise Exception or try to get as close as possible?
    // todo write tests, afterwards proceed to character
    int xdimsize = this.get_x_dimension_size();
    int ydimsize = this.get_y_dimension_size();
    // If one of the room dimensions is empty, cannot move...
    if (xdimsize == 0 || ydimsize == 0) throw new IndexOutOfBoundsException("Room is empty, cannot get next tile!");
    // if we would move out of the room (too high index, set to maximum index
    // todo: assert  xdimsize, ydimsize != 0
    if (newx >= xdimsize) { newx = xdimsize-1; }
    if (newy >= ydimsize) { newy = ydimsize-1; }
    Tile desttile = this.tiles[newx][newy];
    return desttile;
  }


  private List<Tile> computeDDA(Tile t, Direction d){
    List<Tile> path = new ArrayList<>();

    // Calculate number of steps
    int steps = Math.abs(d.x) > Math.abs(d.y) ? Math.abs(d.x) : Math.abs(d.y);


    // Calculate increments
    double xIncrement = d.x / (float) steps;
    double yIncrement = d.y / (float) steps;

    // Compute points
    double x = t.x;
    double y = t.y;
    path.add(tiles[(int) x][((int) y)]);
    for (int i = 0; i < steps; i++) {
      x += xIncrement;
      y += yIncrement;
      if (x >= tiles.length || y >= tiles[0].length)
        break;
      Tile tile = tiles[(int) Math.round(x)][(int) Math.round(y)];
      path.add(tile);
    }
    return path;
  }

  public Tile[][] getTiles() {
    return tiles;
  }

  public List<Character> getInhabitants() {
    return inhabitants;
  }

  /**
   * Replace the tile at (x,y) with newtile
   *
   * @param x x position of tile to be changed
   * @param y y position of tile to be changed
   * @param newtile new Tile, should be placed at (x,y) in room (old tile removed)
   * @return <code>true</code> if successfully replaced, <code>false</code> otherwise
   * */
  public boolean replaceTile(int x, int y, Tile newtile) {
    // added to also inform the room if a tile is added to it (before only changed in Tile)
    if (tiles == null) return false;
    if (!(0 <= x  && x < get_x_dimension_size()) || !(0 <= y  && y < get_y_dimension_size())) {
      //throw new IllegalArgumentException(String.format("Position doesn't exist in this room! (%d , %d)", x, y));
      return false;
    }
    tiles[x][y] = newtile;
    return true;
  }

  @Override
  public void marshal(MarshallingContext c) {
    c.write("world", this.w);
    c.write("inhabitants", this.inhabitants);
    c.write("tiles", this.tiles);
  }

  @Override
  public void unmarshal(MarshallingContext c) {
    this.w = c.read("world");
    c.readAll("inhabitants", this.inhabitants); // todo: ?
    this.tiles = c.readBoard("tiles");

  }
}
