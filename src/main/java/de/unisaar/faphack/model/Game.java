package de.unisaar.faphack.model;

import de.unisaar.faphack.model.effects.MoveEffect;
import de.unisaar.faphack.model.map.Room;
import de.unisaar.faphack.model.map.Tile;
import de.unisaar.faphack.model.map.World;
import sun.font.TrueTypeFont;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author
 *
 */
public class Game implements Storable {
  private World world;
  private Character protagonist;

  public Game() {

  }

  /**
  * return the protagonist of this game
  */
  public Character getProtagonist() {
    return protagonist;
  }

  /**
   * tries to move the character into the given direction.
   * @param whom
   * @param direction
   * @return boolean
   */
  public boolean move(Character whom, Direction direction) {
    // TODO please implement me!
    return false;
  }

  /**
   * The character rests, i.e. it moves with direction (0,0) and its power increases by 5
  */
  public boolean rest(Character whom){
    // get the power value and position of the character before resting
    int prev_power = whom.getPower();
    Tile prev_tile = whom.tile;
    // let character rest
    whom.rest();
    // get new power value and position after resting
    int new_power = whom.getPower();
    Tile new_tile = whom.tile;
    // check that the new power value is the old one + 5 and new position is the old position
    if (new_power == (prev_power + 5) && new_tile.equals(prev_tile)) {
      return true;
    }
    return false;
  }

  /**
   *
   * @param who
   * @param direction
   * @return List<Item>
   */
  public List<Item> listItems(Character who, Direction direction) {
    Tile current = who.getTile();
    Tile next = current.getNextTile(direction);
    return next.onTile();
  }

  /**
   * Let a character pickup the given item
   * @param who the character
   * @param item the item to be picked up
   * @return boolean <code>true</code> if the character managed to pickup the item, <code>false</code> otherwise
   */
  public boolean pickUp(Character who, Item item) {
    // TODO: fill this
    return false;
  }

  /**
   * Removes an item from the given characters inventory and places it on the tile
   * @param who the character performing the action
   * @param what the item to be removed
   * @return <code>true</code> if the action was successful, <code>false</code> otherwise
   */
  public boolean drop(Character who, Wearable what){
    // TODO please implement me!
    return false;
  }

  /**
   * Equips the given Wearable as active Weapon or armor depending
   *
   * @param who the character performing the action
   * @param what the item to be equipped
   * @return <code>true</code> the action was successful, <code>false</code> otherwise
   */
  public boolean equip(Character who, Wearable what){
    return (who.equipItem(what));
  }

  @Override
  public void marshal(MarshallingContext c) {
    c.write("world", this.world);
    c.write("protagonist", this.protagonist);
  }

  @Override
  public void unmarshal(MarshallingContext c) {
    this.world = c.read("world");
    this.protagonist = c.read("protagonist");
  }

  public World getWorld() {
    return world;
  }

  /** Add the game's protagonist to a random floor tile in the first room */
  public void setProtagonist(Character prot) {
    // Get the list of rooms in the world and get the first room
    List<Room> rooms = this.world.getMapElements();
    Room first_room = rooms.get(0);
    // Get the tiles in the first room
    Tile[][] first_room_tiles = first_room.getTiles();
    // Generate a random tile in the room until we find one that will take the prot
    Boolean found = false;
    while (found == false) {
      // Generate a random tile in the room
      Tile random_tile = generateRandomTile(first_room_tiles);
      // Check if tile will take prot
      if (random_tile.willTake(prot) != null) {
        found = true;
        // If yes, set prots position to tile, add prot to inhabitants of the room?
        prot.tile = random_tile;
      }
    }
  }

  public Tile generateRandomTile(Tile[][] room_tiles) {
    int x_pos = new Random().nextInt(room_tiles[0].length + 1);
    int y_pos = new Random().nextInt(room_tiles[1].length + 1);
    Tile random_tile = room_tiles[x_pos][y_pos];
    return random_tile;
  }

  /** get the game's protagonist */
  public Character getProtagonist(Character prot) {
    // TODO: fill here
    return null;
  }
}
