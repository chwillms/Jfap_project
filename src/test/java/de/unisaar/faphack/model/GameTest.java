package de.unisaar.faphack.model;

import com.jme3.math.Ring;
import de.unisaar.faphack.model.effects.MoveEffect;
import de.unisaar.faphack.model.map.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.unisaar.faphack.model.TestUtils.*;
import static de.unisaar.faphack.model.TestUtils.placeCharacter;
import static org.junit.jupiter.api.Assertions.*;

class GameTest {

  /**
   * Check if the a character is able to pickup the item correctly.
   * The item should no longer be in the tiles items list but in the inventory of the character
   */
  @Test
  void pickUp() {
    Game game = TestUtils.createGame();
    Room room = game.getWorld().getMapElements().get(0);
    Character testObject = createBaseCharacter("Foo", 2, 2);
    addCharacter(room, 1, 2, testObject);
    Wearable item1 = createWearable(2, false);
    placeItemsInRoom(room, 1, 2, item1);
    assertTrue(game.pickUp(testObject, item1));
    // the item should have been removed from the tile and moved into the inventory of the character
    assertTrue(testObject.items.contains(item1));
    assertTrue(!room.getTiles()[1][2].onTile().contains(item1));
    assertEquals(testObject, item1.character);
    assertNull(item1.onTile);
    Fixtures fountain = new Fixtures();
    placeItemsInRoom(room, 1, 2, fountain);
    assertFalse(game.pickUp(testObject, fountain));
  }

  /**
   * The game.move method returns <code>true</code> if a character was able to perform a move action
   * and <code>false</code> otherwise
   */
  @Test
  void move() {
    Game game = TestUtils.createGame();
    Room room = game.getWorld().getMapElements().get(2);
    Character testObject = room.getInhabitants().get(0);
    assertTrue(game.move(testObject, new Direction(1, 0)));
    assertTrue(game.move(testObject, new Direction(0, -1)));
    assertTrue(game.move(testObject, new Direction(-1, 0)));
    assertFalse(game.move(testObject, new Direction(-1, 0)));
  }

  /**
   * The game.listItems() method returns a list of all Items on a tile, which is determined by
   * a character and a direction.
   * 1. get all items on the tile which is left of the character
   * 2. get all items on the tile of the character
   */
  @Test
  void listItems() {
    Game game = TestUtils.createGame();
    Room room = game.getWorld().getMapElements().get(0);
    Character testObject = room.getInhabitants().get(0);
    Wearable item1 = createWearable(2, false);
    Wearable item2 = createWearable(2, false);
    placeItemsInRoom(room, 1, 2, item1, item2);
    ArrayList<Item> expected = new ArrayList<>(Arrays.asList(new Item[]{item1, item2}));
    List<Item> actual = game.listItems(testObject, new Direction(-1, 0));
    for (Item item : actual) {
      assertTrue(expected.contains(item));
      expected.remove(item);
    }
    assertTrue(expected.isEmpty());
    placeCharacter(testObject, room.getNextTile(testObject.tile, new Direction(-1, 0)));
    expected = new ArrayList<>(Arrays.asList(new Item[]{item1, item2}));
    actual = game.listItems(testObject, new Direction(0, 0));
    for (Item item : actual) {
      assertTrue(expected.contains(item));
      expected.remove(item);
    }
    assertTrue(expected.isEmpty());
  }

  /**
   * Resting will increase the character's power by 5
   */
  @Test
  void rest() {
    Game game = createGame();
    Character character = game.getWorld().getMapElements().get(0).getInhabitants().get(0);
    System.out.println(character.power);
    game.rest(character);
    assertEquals(15, character.getPower());
  }

  @Test
  void drop() {
    Game game = createGame();
    // this character has only one wearable in its inventory, which is also the character's active weapon
    Character character = game.getWorld().getMapElements().get(0).getInhabitants().get(0);
    Armor armor = createArmor(1, 1, 1);
    equipArmor(armor, character);
    Wearable sword = character.getActiveWeapon();
    assertTrue(game.drop(character, sword));
    assertTrue(character.tile.onTile().contains(sword));
    // now remove the armor from the inventory
    assertTrue(character.dropItem(armor));
    // try to remove an item which is not part of the inventory : returns false
    Wearable w = createWearable(1, false);
    assertFalse(game.drop(character, w));
  }

  @Test
  void equip() {
    Game game = createGame();
    // this character has only one wearable in its inventory, which is also the character's active weapon
    Character character = game.getWorld().getMapElements().get(0).getInhabitants().get(0);

    // Equip an armor
    Armor armor = createArmor(1, 1, 1);
    character.items.add(armor);
    // the armor should be in the character's armor list
    assertTrue(game.equip(character, armor));

    // Equip a weapon
    Wearable weapon = createWearable(1, true);
    character.items.add(weapon);
    assertTrue(game.equip(character, weapon));

    // Illegal equip ( item not in inventory)
    Wearable item = createWearable(1, true);
    assertFalse(game.equip(character, item));
  }


  @Test
  void setProtagonist() {
    // Create new game, game includes the sketchy protagonist "the guy"
    Game game = TestUtils.createGame();

    // set protagonist
    Character testProt = game.getProtagonist();

    game.setProtagonist(testProt);
    Tile protTile = testProt.getTile();
    assertTrue(protTile instanceof FloorTile);

  }

  @Test
  void toyGameInteraction() {
      //Create Game
      Game game = TestUtils.createToyGame();

      //Place Character
      Character protagonist = game.getProtagonist();
      List<Room> rooms = game.getWorld().getMapElements();
      Room first_room = rooms.get(0);
      Tile[][] first_room_tiles = first_room.getTiles();
      protagonist.tile = first_room_tiles[1][2];
      List<Character> inhabitants = first_room.getInhabitants();
      inhabitants.add(protagonist);

      // Create different move effects
      // horizontal and vertical moves
      MoveEffect UP = new MoveEffect(new Direction(0, 1));
      MoveEffect RIGHT = new MoveEffect(new Direction(1, 0));
      MoveEffect LEFT = new MoveEffect(new Direction(-1, 0));
      MoveEffect DOWN = new MoveEffect(new Direction(0, -1));
      // Diagonal moves
      MoveEffect UPLEFT = new MoveEffect(new Direction(-1, 1));
      MoveEffect UPRIGHT = new MoveEffect(new Direction(1, 1));
      MoveEffect DOWNLEFT = new MoveEffect(new Direction(-1, -1));
      MoveEffect DOWNRIGHT = new MoveEffect(new Direction(1, -1));
      // Stay, if Power == 0 then automatically rest
      MoveEffect STAY = new MoveEffect(new Direction(0, 0));


      // 1. First room (obstacles), moves and first door interaction to second room
      DOWN.apply(protagonist); //not possible as rest is needed first
//      S1 = {R1[1][2], level=1, h=100, m=0, p=0, currentWeight=0}
      assertEquals(100, protagonist.health);
      assertEquals(0, protagonist.magic);
      assertEquals(0, protagonist.power);
      assertEquals(0, protagonist.currentWeight);
      assertEquals(protagonist.getRoom().getTiles()[1][2], protagonist.getTile());
      assertEquals(1, protagonist.level);

      // 2. Gain power
      STAY.apply(protagonist);
//    S2 = {R1[1][2], level=1, h=100, m=0, p=5, currentWeight=0}

      assertEquals(100, protagonist.health);
      assertEquals(0, protagonist.magic);
      assertEquals(5, protagonist.power);
      assertEquals(0, protagonist.currentWeight);
      assertEquals(protagonist.getRoom().getTiles()[1][2], protagonist.getTile());
      assertEquals(1, protagonist.level);

      UP.apply(protagonist); //not possible as indestructible wall


      DOWN.apply(protagonist); //stays on same tile as fixture mirror, mirror does not block move
//      protagonist.getTile().onTile().get(0);


      // Test if wall tile is destroyed (added a get-function in WallTile)
      WallTile testWall = (WallTile) protagonist.getRoom().getTiles()[2][1];
      assertEquals(testWall.getDestructible(), 2);
      RIGHT.apply(protagonist); //should destroy wall and should loose power
      WallTile testWallAfterMove = (WallTile) protagonist.getRoom().getTiles()[2][1];
      assertEquals(testWall.getDestructible(), -1);

      // Test the door from the first room into the second room
      RIGHT.apply(protagonist); //should move onto door

      // Come back from room 2 to room 1
      LEFT.apply(protagonist);

      // Move onto stairtile and enter room3
      DOWNLEFT.apply(protagonist);

      // Move onto tile with the shield
      DOWN.apply(protagonist);

      // Pick up the shield
      Wearable foo = (Wearable) protagonist.getTile().onTile().get(0);
      protagonist.pickUp(foo);

//    pick up shield when it's already picked up
      protagonist.pickUp(foo);
//    todo AK:  assert
    }
  }

