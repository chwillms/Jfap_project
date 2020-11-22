package de.unisaar.faphack.model.effects;

import de.unisaar.faphack.model.CharacterModifier;

/**
 * Additive effects directly modify the health and magic of a character.
 *
 * @author
 *
 */
public class MultiplicativeEffect extends ModifyingEffect {

  public MultiplicativeEffect(){}

  public MultiplicativeEffect(double h, double m, double p, double hl) {
    health = h;
    magic = m;
    power = p;
  }

  /**
   * Modifies the values the given character.
   *
   * @param on
   * @return void
   */
  public CharacterModifier apply(CharacterModifier c) {
    if (c == null) {
      throw new IllegalArgumentException("Charactermodifier is nulll");
    }
    c.health *= this.health;
    c.magic *= this.magic;
    c.power *= this.power;
    return null;
  }

}
