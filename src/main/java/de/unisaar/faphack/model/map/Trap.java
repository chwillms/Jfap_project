package de.unisaar.faphack.model.map;

import de.unisaar.faphack.model.CharacterModifier;
import de.unisaar.faphack.model.Fixtures;
import de.unisaar.faphack.model.MarshallingContext;

/**
 * A Trap is a special Fixture. Its effect is triggered by moving on the tile it
 * is placed on.
 *
 * @author weissenh
 *
 */
public class Trap extends Fixtures {
  /**
   * Traps might also be placed on StairTiles. In this case, the stair is masked
   * by the Trap and thus not visible for the character, i.e. a trap door.
   *
   */
  protected StairTile trapDoor = null;

  // protected CharacterModifier modifier;
  // not needed, Items (Trap is subsubclass of Item) already have a charactermodifier
  public Trap(FloorTile where, StairTile trap, CharacterModifier effect) {
    super(where, FLOOR, effect);
    this.trapDoor = trap; // todo: stairtile ensure same room? ensure from of stair?
    if (trap != null) {
      trap.trap = this;
      if (trap.stair != null && trap.stair.toTile != null) trap.stair.toTile.trap = this; // todo have a function for that
    }
  }

  public Trap() {
    // trait = FLOOR; // all traps look like normal floor tiles? todo ok?
    this(null, null, null);
  }

  /** @return trapdoor (stairtile, can be null) */
  public StairTile getTrapDoor() { return trapDoor;}

  public void marshal(MarshallingContext c) {
    super.marshal(c);
    c.write("trapDoor", this.trapDoor);
  }

  public void unmarshal(MarshallingContext c) {
    super.unmarshal(c);
    this.trapDoor = c.read("trapDoor");
  }
}
