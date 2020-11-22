package de.unisaar.faphack.model;

import de.unisaar.faphack.model.effects.ModifyingEffect;
import de.unisaar.faphack.model.map.Tile;

/**
 * Class for defensive items like armor, shields, certain spells
 * @author weissenh
 */
public class Armor extends Wearable {
  private ModifyingEffect modifyingEffect;


  public Armor(){
    // super.isWeapon = true; // todo ???
    // todo: have default initialization?
    this(null, ARMOR, null, null);
  }

  private Armor(Tile where, String trait, Character c, ModifyingEffect me) {
    super(where, trait, null, c); // no CharacterModifier effect for armors
    modifyingEffect = me;
    // note: need to change public constructors if there is more than one armor trait!
  }
  // public Armor(String trait, ModifyingEffect me, Character c) { this(null, trait, c, me); }
  // public Armor(String trait, ModifyingEffect me, Tile where) { this(where, trait, null, me); }
  public Armor(ModifyingEffect me, Character c) { this(null, ARMOR, c, me); }
  public Armor(ModifyingEffect me, Tile where) { this(where, ARMOR, null, me); }

  /**
   * The Effect connected to the item is always NULL.
   * The armor has a ModifyingEffect instead.
   */
   @Override
  public CharacterModifier getCharacterModifier(){
    return null;
  }

  /** @return the armor's effect: any attack is weakened by the armor (defensive weapon like shield) */
  public ModifyingEffect getModifyingEffect(){
    return modifyingEffect;
  }

  @Override
  public void marshal(MarshallingContext c) {
    super.marshal(c);
    c.write("modifyingEffect", modifyingEffect);
  }

  @Override
  public void unmarshal(MarshallingContext c) {
    super.unmarshal(c);
    this.modifyingEffect = c.read("modifyingEffect");
  }
}
