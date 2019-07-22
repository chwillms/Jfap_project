package de.unisaar.faphack.model;

/**
 * Wearables are Items that can be carried by a Character. These include armor,
 * weapons, food, potions, key and others.
 *
 * @author
 *
 */
public class Wearable extends Item {
  /**
   * The weight of the item.
   */
  protected int weight;

  /**
   *
   */
  protected boolean isWeapon;

  /**
   * The character who carries this item. This is null if the Item is placed on a
   * Tile.
   */
  protected Character character;

  public Wearable() {

  }

  @Override
  public void marshal(MarshallingContext c) {
    super.marshal(c);
    c.write("weight", this.weight);
    c.write("isWeapon", this.isWeapon ? 1 : 0);
    c.write("character", this.character);
  }

  @Override
  public void unmarshal(MarshallingContext c) {
    super.unmarshal(c);
    this.weight = c.readInt("weight");
    this.isWeapon = c.readInt("isWeapon") == 1;
    this.character = c.read("character");
  }

}
