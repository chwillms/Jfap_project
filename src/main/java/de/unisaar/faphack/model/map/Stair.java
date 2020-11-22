package de.unisaar.faphack.model.map;

import de.unisaar.faphack.model.MarshallingContext;

/**
 * A Stair connects two StairTiles
 *
 * Eventually you can only use the stair in one way (from -> to)
 * Both of the StairTiles have this object as their stair instance variable
 *
 * @author weissenh
 *
 */
public class Stair extends Connector<StairTile> {
  /**
   * If true, can only be used in direction from -> to
   */
  private boolean oneWay = false;

  public Stair() {
    this(null, null, false);
  }

  public Stair(StairTile from, StairTile to, boolean oneway) {
    super(from, to);
    if (from != null) { // todo what if only one of from/to is null?
      from.stair = this;
    }
    if (to != null) { // todo what if only one of from/to is null?
      to.stair = this;
    }
    this.oneWay = oneway;
  }

  public boolean onlyDown() {
    return oneWay;
  }

  @Override
  public void marshal(MarshallingContext c) {
    super.marshal(c);
    c.write("oneWay", this.oneWay ? 1 : 0);
  }

  @Override
  public void unmarshal(MarshallingContext c) {
    super.unmarshal(c);
    this.oneWay = c.readInt("oneWay") == 1;
  }
}
