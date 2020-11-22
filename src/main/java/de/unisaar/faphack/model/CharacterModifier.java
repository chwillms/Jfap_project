package de.unisaar.faphack.model;

import de.unisaar.faphack.model.effects.AdditiveEffect;
import de.unisaar.faphack.model.effects.ModifyingEffect;

public class CharacterModifier implements Storable {
  // what this modifier does to the various aspects of a character
  public int health;
  public int magic;
  public int power;

  private int howLong;

  public CharacterModifier(){}

  public CharacterModifier(int h, int m, int p, int hl) {
    health = h;
    magic = m;
    power = p;
    howLong = hl;
  }

  /**
   * Apply the changes of this modifier to c, but only if howLong is not zero
   */
  public boolean applyTo(Character c) {

//    AK: added input check
    if(c == null) {return false;} // todo AK: should return something else? Exception?

//    todo AK:
    if (this.howLong() == 0) {
      return false;}
    c.health += this.health;
    c.magic += this.magic;
    c.power += this.power;
    return true;
  }

  public int howLong() {
    return howLong;
  }

  @Override
  public void marshal(MarshallingContext c) {
    c.write("health", this.health);
    c.write("magic", this.magic);
    c.write("power", this.power);
    c.write("howLong", this.howLong);
  }

  @Override
  public void unmarshal(MarshallingContext c) {
    this.health = c.readInt("health");
    this.magic = c.readInt("magic");
    this.power = c.readInt("power");
    this.howLong = c.readInt("howLong");
  }
}
