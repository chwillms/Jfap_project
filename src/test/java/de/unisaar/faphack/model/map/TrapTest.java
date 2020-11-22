package de.unisaar.faphack.model.map;

import de.unisaar.faphack.model.*;
import de.unisaar.faphack.model.Character;
import org.junit.jupiter.api.Test;

import static de.unisaar.faphack.model.TestUtils.createBaseCharacter;
import static de.unisaar.faphack.model.TestUtils.createSimpleRoom;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author weissenh
 */
class TrapTest {

  @Test
  void move2trap() {
    CharacterModifier effect = new CharacterModifier(-2, 0, -1, 2);
    Room room = createSimpleRoom(4,4,1);
    FloorTile tile1 = new FloorTile(1, 1, room);
    FloorTile tile2 = new FloorTile(2, 2, room);
    // StairTile from = new StairTile()
    // Stair stair = new Stair(null, null, false);
    StairTile stair1from = new StairTile(1,1, room);
    StairTile stair1to = new StairTile(1,2, room);
    StairTile stair2from = new StairTile(2,1, room);
    StairTile stair2to = new StairTile(2,2, room);
    Stair stair1 = new Stair(stair1from, stair1to, false); // todo change stairtile...
    Stair stair2 = new Stair(stair2from, stair2to, false);
    Trap trap1 = new Trap(tile1, stair1from, effect);
    Trap trap2 = new Trap(tile2, null, effect);
    // test proper initialization
    assertEquals(stair1from.stair.toTile, stair1to);
    assertEquals(stair1to.stair.fromTile, stair1from);
    assertEquals(trap1, stair1from.hasTrap());
    assertEquals(stair1, stair1from.getStair());
    assertEquals(trap1, stair1to.hasTrap());
    assertEquals(stair1, stair1to.getStair());
    assertNull(stair2from.hasTrap());
    assertNull(stair2to.hasTrap());
    assertEquals(stair2, stair2from.getStair());
    assertEquals(stair2, stair2to.getStair());
    assertEquals(trap1.getTile(), tile1);
    assertEquals(trap1.getTrait(), TraitOwner.FLOOR);
    assertEquals(trap1.getCharacterModifier(), effect);
    assertEquals(trap2.getTile(), tile2);
    assertEquals(trap2.getTrait(), TraitOwner.FLOOR);
    assertEquals(trap2.getCharacterModifier(), effect);
    // move character onto trap and see what happens
    Character person = createBaseCharacter("person1", 100, 10);
    // initialized with health 100, magic 50
    assertNull(person.getTile());
    assertEquals(100, person.getHealth()); // todo value might change (100)
    assertEquals(100, person.getPower());
    assertEquals(50, person.getMagic()); // todo value might change (50)
    person.move(tile1);
    assertEquals(stair1from.stair.toTile, person.getTile());
    assertEquals(stair1to, person.getTile());
    assertEquals(98, person.getHealth());
    assertEquals(99, person.getPower());
    assertEquals(50, person.getMagic());
    // todo: also try with trap with no trapdoor?
    person.move(tile2);
    assertEquals(tile2, person.getTile());
    assertEquals(96, person.getHealth());
    assertEquals(98, person.getPower());
  }

}