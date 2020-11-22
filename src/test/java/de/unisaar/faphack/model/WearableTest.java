package de.unisaar.faphack.model;

import de.unisaar.faphack.model.map.FloorTile;
import de.unisaar.faphack.model.map.Room;
import de.unisaar.faphack.model.map.Tile;
import org.junit.jupiter.api.Test;

import static de.unisaar.faphack.model.TestUtils.createSimpleRoom;
import static org.junit.jupiter.api.Assertions.*;

class WearableTest {


  /* implemented test for if you can only pick up items from your current tile
  @Test
  void pickUp2() { // todo test something that shouldn't work
    Wearable item = TestUtils.createWearable(1,false);
    Room room = createSimpleRoom(4,4, 1);
    Tile currenttile = room.getTiles()[2][2];
    item.onTile = currenttile;
    Character character = TestUtils.createBaseCharacter("Foo", 1, 1);
    character.move(currenttile);
    item.pickUp(character);
    assertEquals(character, item.character);
    assertNull(item.onTile);
  } */

  @Test
  void pickUp() {
    Wearable item = TestUtils.createWearable(1,false);
    item.onTile = new FloorTile();
    Character character = TestUtils.createBaseCharacter("Foo", 1, 1);
    item.pickUp(character);
    assertEquals(character, item.character);
    assertNull(item.onTile);
  }

  @Test
  void drop() {
    Wearable item = TestUtils.createWearable(1,false);
    item.character =  TestUtils.createBaseCharacter("Foo", 1, 1);
    item.onTile =  null;
    FloorTile t = new FloorTile();
    item.drop(t);
    assertEquals(t, item.onTile);
    assertNull(item.character);
  }
}