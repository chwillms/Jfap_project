package de.unisaar.faphack.model;

import de.unisaar.faphack.model.map.Tile;

/**
 * Items are things that can be placed on a tile or - in case of wearables,
 * carried around by a character. Items can have character modifier effects.
 *
 * @author weissenh
 */
public abstract class Item extends AbstractObservable<TraitedTileOccupier>
implements Storable, TraitedTileOccupier {
  /**
   * The Tile on which the item is placed. This is null if the Item is in the
   * inventory of a character.
   */
  protected Tile onTile;

  /**
   * The trait of this item.
   */
  protected String trait;

  /**
   * The Effect connected to the item.
   */
  private  CharacterModifier effect;

  public Item() {

  }

  public Item(Tile where, String trait, CharacterModifier effect) {
    this.onTile = where;
    this.trait = trait; // todo input validation: check if valid trait string!
    this.effect = effect;
  }

  public void marshal(MarshallingContext c) {
    c.write("onTile", this.onTile);
    c.write("trait", this.trait);
    c.write("effect", this.effect);
  }

  public void unmarshal(MarshallingContext c) {
    this.onTile = c.read("onTile");
    this.trait = c.readString("trait");
    this.effect = c.read("effect");
  }

  @Override
  public Tile getTile() { return onTile; }

  @Override
  public String getTrait() { return trait; }

  public CharacterModifier getCharacterModifier(){
    return effect;
  }
}
