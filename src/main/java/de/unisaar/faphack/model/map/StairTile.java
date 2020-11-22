package de.unisaar.faphack.model.map;

import de.unisaar.faphack.model.Character;
import de.unisaar.faphack.model.CharacterModifier;
import de.unisaar.faphack.model.MarshallingContext;

/**
 * StairTiles are either the starting or ending point of a Stair.
 *
 * @author weissenh
 */
public class StairTile extends Tile {
  /** this StairTile is either the from or */
  protected Stair stair;

  /** If not null, StairTile has a trap (so this StairTile looks like a normal floor tile) */
  protected Trap trap;

  public StairTile() {
    trait = STAIR;
  }

  public StairTile(int x, int y, Room room){
    this(x, y, room, null, null);
  }

  public StairTile(int x, int y, Room room, Stair stair) {
    this(x, y, room, stair, null);
  }

  public StairTile(int x, int y, Room room, Stair stair, Trap trap) {
    super(x, y, room);
    trait = (trap != null ? FLOOR : STAIR);
    this.stair = stair;
    this.trap = trap;
  }

  /**
   * A stair can (possibly) be used in both directions: it depends on where you
   * are currently.
   *
   * Remember to update the level of the character.
   *
   * @return the new tile, or null if not possible to use
   */
  @Override
  public Tile willTake(Character c) {
    // check if we have a real character
    if (c == null) {return null;}
    // todo: check if stair is null (not initialized!
    // define the default goal tile
    Tile goalTile = stair.toTile;

    // check if the stair is one-way or two-ways
    if (this.stair.onlyDown()) {
      // only down: 'from' -> 'to'
      // doesn't go: 'to' -> 'from'
      // if the character stands on the 'to' tile: return null
      if (this == stair.toTile) {
        return null;
      } else {
        c.levelDown();
      }
    }

//    if we want to take the toTile, our goal tile is fromTile
    if (this == stair.toTile) {
      c.levelUp();
      goalTile = stair.fromTile;
    } else if (this == stair.fromTile){
      c.levelDown();
    }
    if (hasTrap() != null) {
      // if stair tile contains a trap, apply its effect onto the character
      CharacterModifier cm = trap.getCharacterModifier();
      cm.applyTo(c);
    }
    return goalTile;
  }

  /** Return non-null if this is a trap */
  @Override
  public Trap hasTrap() {
    return trap;
  }

  @Override
  public void marshal(MarshallingContext c) {
    super.marshal(c);
    c.write("stair", this.stair);
    c.write("trap", this.trap);
  }

  @Override
  public void unmarshal(MarshallingContext c) {
    super.unmarshal(c);
    this.stair = c.read("stair");
    this.trap = c.read("trap");
  }

  public Stair getStair(){
    return stair;
  }
}
