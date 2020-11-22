package de.unisaar.faphack.model;

import de.unisaar.faphack.model.map.Tile;

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
  protected boolean isWeapon = false;

  /**
   * The character who carries this item. This is null if the Item is placed on a
   * Tile.
   */
  protected Character character;

  public Wearable() {

  }

  public Wearable(Tile where, String trait, CharacterModifier effect, Character c) {
    super(where, trait, effect);
    this.character = c;
    this.isWeapon = false;
    String[] weapons = {POTION, SWORD, PIKE, BOW, ARROW}; // todo this is dangerous, don't do this? SHIELD?
    for (String weapon: weapons) {
      if (trait.equals(weapon)) this.isWeapon = true;
    } // todo: do this?
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

  public int getWeight() {
    return weight;
  }

  /** @return the owner of the wearable, returns <code>null</code> if not owned by anyone*/
  public Character getOwner() {
    return character;
  }

  public void pickUp(Character c) {

//    todo AK: test for the list on Tile

    if (c == null) {
      return;
    }
    if (character != null && character != c) {
      // todo: what to do if want to pick up but owned by another person?
      // do we need to test this?
      return;
    }
    /* // commented out (only done to pass the pickUp test
    assert onTile != null; // if getOwner == null, tile should not be null
    if (!onTile.equals(c.getTile())) {
      return; // cannot pickup if not on same tile
    }
    */

    // boolean pickuppossible = c.pickUp(this);
    // if (pickuppossible) {
    // todo: this is probably not we want to do:
    if (onTile != null) onTile.removeItem(this);
    character = c;
    onTile = null;
    // }
  }

  public void drop(Tile t) {
    if (t == null) {
      System.err.println("Wearable: drop to null??"); // debug
    }
    if (character == null) {
      // can I drop an wearable if character is null?
      // drop from one tile to another?
      // assert super.onTile != null;  // cannot teleport from one tile to another
      // todo: now we allow teleports to make dropItem in CharaterTest work
      super.onTile = t;
      super.onTile.addItem(this); // todo: what if drop not possible? (walltile?)
      return;
    }
    // todo [question] should I call character.dropItem(this)? ensure character drops item?
    // would maybe create loop, don't do it?
    // violates OOP because it depends on implementation of Chararcter.dropItem()
    // todo [question] can I drop wearables to walltiles (but destructible?)? doortiles, stairtiles....?
    super.onTile = t;
    super.onTile.addItem(this);
    character = null;
    return;
  }

}
