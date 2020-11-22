package de.unisaar.faphack.model.map;

import de.unisaar.faphack.model.Character;
import de.unisaar.faphack.model.CharacterModifier;
import de.unisaar.faphack.model.MarshallingContext;

/**
 * Walltiles are used to determine the arrangement of a room. They usually
 * define the outer borders of a room, but might also be used within a room to
 * separate areas.
 * __________________
 * |                |
 * |                |
 * |   _______      |
 * |  |_____  |     D
 * |________________|
 *
 * @author
 *
 */
public class WallTile extends Tile {
  /** -1 means destroyed, 0 means indestructible, destructible >= 0 must apply at least force f*/
  protected int destructible;
  protected final static int INDESTRUCTIBLE = 0;
  protected final static int DESTROYED = -1;

  public WallTile() {
    trait = WALL;
  }

  public WallTile(int x, int y, Room room, int destructible){
    super(x, y, room);
    trait = WALL;
    this.destructible = destructible;
  }

  //todo: change back to standard
  public WallTile(int x, int y, Room room){
    this(x,y,room,INDESTRUCTIBLE);
    // super(x, y, room);
    // trait = WALL
  }



  @Override
  public Tile willTake(Character c) {
    //Wall tile already destroyed
    if (this.destructible == DESTROYED){
      return this;
    }

    //Wall tile undestroyable
    else if (this.destructible == INDESTRUCTIBLE){
      return null;
    }

    //Wall is destructed if character has sufficient power
    else if(this.destructible <= c.getPower() ){
      int p = -this.destructible;
      int h = 0;
      int m = 0;
      int hl = 1;
      CharacterModifier characterModifier = new CharacterModifier(h, m, p, hl);
      c.applyItem(characterModifier);

      this.destructible = DESTROYED;
      return this;
    }

    return null;
  }
  @Override
  public boolean isOccupied(Character character){
    //Illegal Arguemnt
    if (character == null) {
      return false;
    }
    //Wall Tile is indestructible
    if (destructible == INDESTRUCTIBLE){
      return false;
    }
    //Character might be on tile
    Tile characterTile = character.getTile();
    return characterTile == this;
  }

  @Override
  public String getTrait() {
    return destructible == DESTROYED ? DESTROYED_WALL : WALL;
  }

  @Override
  public void marshal(MarshallingContext c) {
    super.marshal(c);
    c.write("destructible", this.destructible);
  }

  @Override
  public void unmarshal(MarshallingContext c) {
    super.unmarshal(c);
    c.readInt("destructible");
  }

  public int getDestructible() {
    return destructible;
  }
}
