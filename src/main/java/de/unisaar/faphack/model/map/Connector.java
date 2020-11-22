package de.unisaar.faphack.model.map;

import de.unisaar.faphack.model.MarshallingContext;
import de.unisaar.faphack.model.Storable;

/**
 * @author
 *
 */
public abstract class Connector<T extends Tile> implements Storable {
  protected T fromTile;
  protected T toTile;

  public Connector() {
    this(null, null);
  }

  public Connector(T from, T to) {
    this.fromTile = from;
    this.toTile = to;
  }

  public T from() {
    return fromTile;
  }

  public T to() {
    return toTile;
  }

  public void marshal(MarshallingContext c) {
    c.write("fromTile", this.fromTile);
    c.write("toTile", this.toTile);
  }

  public void unmarshal(MarshallingContext c) {
    this.fromTile = c.read("fromTile");
    this.toTile = c.read("toTile");
  }
}
