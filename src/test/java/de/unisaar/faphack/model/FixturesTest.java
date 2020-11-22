package de.unisaar.faphack.model;

import de.unisaar.faphack.model.map.FloorTile;
import static de.unisaar.faphack.model.TestUtils.*;

import de.unisaar.faphack.model.map.Room;
import de.unisaar.faphack.model.map.Tile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FixturesTest {
  // todo also test chest?
  @Test
  void testFountain() {
    CharacterModifier effect = new CharacterModifier(1, 0, 0, 1);
    Room room = createSimpleRoom(4,4,1);
    FloorTile tile = new FloorTile(1, 1, room);
    Fixtures fountain = new Fixtures(tile, TraitOwner.FOUNTAIN, effect);
    // test proper initialization
    assertEquals(fountain.getTile(), tile);
    assertEquals(fountain.getTrait(), TraitOwner.FOUNTAIN);
    assertEquals(fountain.getCharacterModifier(), effect);
    Character person = createBaseCharacter("person1", 2, 10);
    assertEquals(100, person.getHealth()); // todo value might change (100)
    person.move(tile); // todo or use move effect?
    assertEquals(person.getTile(), tile);
    assertEquals(101, person.getHealth());
  }
}