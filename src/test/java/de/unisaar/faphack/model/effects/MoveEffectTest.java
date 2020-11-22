package de.unisaar.faphack.model.effects;

import de.unisaar.faphack.model.Character;
import de.unisaar.faphack.model.Direction;
import de.unisaar.faphack.model.TestUtils;
import de.unisaar.faphack.model.map.ObstacleTile;
import de.unisaar.faphack.model.map.Room;
import de.unisaar.faphack.model.map.Tile;
import de.unisaar.faphack.model.map.WallTile;
import org.junit.jupiter.api.Test;

import static de.unisaar.faphack.model.TestUtils.addCharacter;
import static de.unisaar.faphack.model.TestUtils.createBaseCharacter;
import static org.junit.jupiter.api.Assertions.*;

class MoveEffectTest {

  /**
   * Move a character which is placed on tile 1x1 in a square 4 x 4 room .
   * 1: Direction (0,1) to the right
   * 2: Direction (0,-1) to the left ( now we should be back where we started)
   * 3. Direction (-1, 1) down (this should have now effect as there is a wall )
   * 4. Direction (0,2) exception expected as only directions with values from -1 to 1 are allowed in MoveEffects
   * 5. move actions are not allowed when the character's power is 0
   * 6. destructible wall move: test whether moving onto a destructible wall removes power
   * 7. try to move on obstacle tile - has to fail
   *
   * Possible further tests:
   * todo: if Direction initialized to null, moveeffect shouldn't throw plain nullpointer exception
   */
  @Test
  void apply() {
    Room room = TestUtils.createSimpleRoom(8,8,1);
    Tile[][] tiles = room.getTiles();
    Character testObject = createBaseCharacter("Foo", 2, 2);
    addCharacter(room, 1, 1, testObject);
    //1.
    MoveEffect moveEffect = new MoveEffect(new Direction(0,1));
    moveEffect.apply(testObject);
    assertEquals(tiles[1][2], testObject.getTile());
    //2.
    moveEffect = new MoveEffect(new Direction(0,-1));
    moveEffect.apply(testObject);
    assertEquals(tiles[1][1], testObject.getTile());
    //3.
    moveEffect = new MoveEffect(new Direction(-1,0)); //should this not be -1 -1
    moveEffect.apply(testObject);
    assertEquals(tiles[1][1], testObject.getTile());
    //4.
    assertThrows(IllegalArgumentException.class, () ->  new MoveEffect(new Direction(0, 2)));

    //5. create a testObject with 0 power
    Character testObject2 = createBaseCharacter("Bar", 0, 2);
    addCharacter(room, 2,2, testObject2);
    moveEffect = new MoveEffect(new Direction(1,1));
    moveEffect.apply(testObject2);
    assertEquals(tiles[2][2], testObject2.getTile());

    // 6. destructible wall move: test whether moving onto a destructible wall removes power
    Character testObject3 = createBaseCharacter("Hans", 10, 2);
    WallTile wall = new WallTile(2, 2, room, 5); // destructible wall tile
    addCharacter(room, 1,1, testObject3);
    moveEffect = new MoveEffect(new Direction(1,1));
    moveEffect.apply(testObject3);
    assertEquals(5, testObject3.getPower());  // todo shouldn't fail!

    // 7. try to move on obstacle tile - has to fail
    Character testObject4 = createBaseCharacter("dummy", 10, 2);
    ObstacleTile obstile = new ObstacleTile(3, 3, room); // indestructible tile
    addCharacter(room, 2,3, testObject4);
    moveEffect = new MoveEffect(new Direction(1,0));
    moveEffect.apply(testObject4);
    assertNotEquals(room.getTiles()[3][3], testObject4.getTile());
    assertEquals(room.getTiles()[2][3], testObject4.getTile());
    // todo: another test with destructible obstacle tile (similar to destructible wall?)

    // moveEffect with null direction
    Character testObjectx = createBaseCharacter("Baz", 10, 2);
    addCharacter(room, 2,2, testObjectx);
    moveEffect = new MoveEffect(null); // todo what to do here?
    moveEffect.apply(testObjectx);
  }
}