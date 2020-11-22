package de.unisaar.faphack.model.effects;

import de.unisaar.faphack.model.CharacterModifier;

/**
 * Additive effects directly modify the health and magic of a character.
 *
 * @author
 *
 */
public class AdditiveEffect extends ModifyingEffect {

  public AdditiveEffect(){}

  public AdditiveEffect(int h, int m, int p) {
    health = h;
    magic = m;
    power = p;
  }

  /**
   * Adapts the values the given Modifier. Returns a new Modifier, without
   * affecting the argument object.
   *
   * the duration of the effect is always unaffected.
   *
   * @param c
   * @return a new CharacterModifier
   */
  public CharacterModifier apply(CharacterModifier c) {
    if (c == null) {
      return null;}
    CharacterModifier new_c = new CharacterModifier();
    new_c.health = (int) (c.health + this.health);
    new_c.magic = (int) (c.magic + this.magic);
    new_c.power = (int) ( c.power + this.power);

    return new_c;
  }

}
