package de.unisaar.faphack.model.map;

import de.unisaar.faphack.model.MarshallingContext;

/**
 * An obstacle tile could have different traits, so we have to store it
 */
public class ObstacleTile extends WallTile {

  /** default trait: boulder */
  public ObstacleTile() {
    trait = BOULDER;
  }

  // indestructible obstacle tile
  public ObstacleTile(int x, int y, Room room) {
    super(x, y, room, INDESTRUCTIBLE);
    trait = BOULDER;
  }

  public ObstacleTile(int x, int y, Room room, int destructible) {
    super(x, y, room, destructible);
    trait = BOULDER;
  }

  @Override
  public String getTrait() {
    // return destructible == DESTROYED ? DESTROYED_WALL : WALL; // superclass
    return trait;
  }

  /* // We don't need to have special methods for obstacle tiles
  // because the superclass (Walltile) already (un)marshalls all instance variables,
  // so we can just use the superclass method (inherited!)
  @Override
  public void marshal(MarshallingContext c) {
    super.marshal(c);
  }

  @Override
  public void unmarshal(MarshallingContext c) {
    super.unmarshal(c);
  }
  */
}
