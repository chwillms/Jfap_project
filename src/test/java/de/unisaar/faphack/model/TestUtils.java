package de.unisaar.faphack.model;

import de.unisaar.faphack.model.effects.MultiplicativeEffect;
import de.unisaar.faphack.model.map.*;
import org.junit.jupiter.api.Test;
import org.lwjgl.Sys;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class TestUtils {

  private static final File testResourceDir = new File("src/test/resources");

  private static void modifyField(Object modifierObject,boolean inSuperClass, String fieldname, Object value)  {
    try {
      Field f;
      if (inSuperClass){
        f = modifierObject.getClass().getSuperclass().getDeclaredField(fieldname);
      } else {
        f = modifierObject.getClass().getDeclaredField(fieldname);
      }
      f.setAccessible(true);
      f.set(modifierObject, value);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void modifySuperSuperField(Object modifiedObject, String fieldname, Object value){
    try {
      Field f = modifiedObject.getClass().getSuperclass().getSuperclass().getDeclaredField(fieldname);
      f.setAccessible(true);
      f.set(modifiedObject, value);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static String getTestResource(String subdir, String name) {
    return getTestResourceFile(subdir, name).getPath();
  }

  public static File getTestResourceFile(String subdir, String name) {
    return new File(new File(testResourceDir, subdir), name);
  }


  /**
   * Create a default game, the default world ( three rooms, connected by stairs and hallways, two characters and some items)
   * @return an instance of the default game
   */
  public static Game createGame(){
    Game game = new Game();
    World world = createWorld();
    // register the world with the game and vice versa
    modifyField(game, false, "world", world);
    modifyField(world, false, "g", game);

    // add protagonist
    addProtagonist(game, "The guy");

    return game;
  }

  /**   CreateToyGame
   *
   *    # = Wall indestructible
   *    % = Wall destructible
   *    . = Wall destroyed
   *    c = Character initial
   *    F = Fixture
   *    K = Key
   *    T = Trap
   *    S = Stairway
   *    w = Wearable //not capitalized because it is not stationary
   *
   *
   *    This is the final plan, only room 1 is fully implemented yet
   *    Room 1:    Room 2
   *    ####       ####
   *    #c #      =D  #
   *    #F%D====== #  #
   *    ##S#       ####
   *      |
   *    ##S#       ####
   *    #ww#       #  #
   *    #wwD=======D  #
   *    ####       ####
   *    Room 3     Room 4
   *
   *
   * @return Game
   */

  //Test what happens if room has no or little occupiable floor tiles
  public static Game createToyGame(){
    Game game = new Game();
    World world = new World();
    List<Room> mapElements = new ArrayList<>();

    // add protagonist
    addProtagonist(game, "The walking potato");

    //First room containts obstacles, e.g. fountain and destroyable rock
    Room room1 = createSimpleRoom(4,4,  1);

    //Second room containts nothing (could implement a trap here)
    Room room2 = createSimpleRoom(4,4, 2);

    //Third room todo: holds weapons and wearables with effect
    Room room3 = createSimpleRoom(4,4, 3);

    //Fourth room todo: specify e.g. holds other characters that can be attacked
    Room room4 = createSimpleRoom(4,4, 4);

    //1. First room (obstacles)
    //Tile[1][1] holds a fixture
    Item mirrorOfHell = new Fixtures();
    List<Item> onTile = new ArrayList<>();
    onTile.add(mirrorOfHell);
    modifyField(mirrorOfHell, true, "effect", new CharacterModifier(-10, 0, 0, 1));
    modifyField(mirrorOfHell, true, "onTile", room1.getTiles()[1][1]);
    modifyField(room1.getTiles()[1][1], false, "items", onTile);

    // Tile[2][1] contains a destructible wall
    Tile[][] tiles = room1.getTiles();
    tiles[2][1] = new WallTile(2, 1 , room1, 2);
    modifyField(room1, false,"tiles", tiles);

    // Tile[3][1] contains a door, connects to second room
    Tile[][] tiles_r1_door_mod = room1.getTiles();
    tiles_r1_door_mod[3][1] = new DoorTile(3, 1 , room1, false, true); // locked door
    modifyField(room1, false,"tiles", tiles_r1_door_mod); //no door provided so room1 had to be modified
    DoorTile doorTile1 = (DoorTile) room1.getTiles()[3][1];
    DoorTile doorTile2 = (DoorTile) room2.getTiles()[0][2]; //door was already provided by simple room method
    connectTiles(doorTile1, doorTile2);

    // Tile[2][0] contains a stairway, connects to the third room
    // room 1
    Tile[][] tiles_r1_stair_mod = room1.getTiles();
    tiles_r1_door_mod[2][0] = new StairTile(3, 1 , room1); //
    modifyField(room1, false,"tiles", tiles_r1_door_mod);
    // room 3 [2][3]
    Tile[][] tiles_r3_stair_mod = room3.getTiles();
    tiles_r3_stair_mod[2][3] = new StairTile(2, 3 , room3); //
    modifyField(room3, false,"tiles", tiles_r3_stair_mod);
    // connect room 1 and room 3
    StairTile stairTile2 = (StairTile) room1.getTiles()[2][0];
    StairTile stairTile1 = (StairTile) room3.getTiles()[2][3];
    connectStairTiles(stairTile2, stairTile1,false);


    // 2.Third room Weapons and Items with effect
    Wearable spear = createWearable(2, true);
    Armor shield = createArmor(20, 0, 0);

    Wearable poison = createWearable(1, false);
    modifyField(poison, true, "effect", new CharacterModifier(-2, 0, 0, 1));
    Wearable sauerkrautSaft = createWearable(1, false);
    modifyField(sauerkrautSaft, true, "effect", new CharacterModifier(+3, 0, 0, 1));

    //remove the stair tile that was placed by createSimpleRoom
    // Tile[2][2] contains no stair tile
    Tile[][] tiles_r3_stair_del_mod = room3.getTiles();
    tiles_r3_stair_del_mod[2][2] = new FloorTile(2, 2, room3);
    modifyField(room3, false,"tiles", tiles_r3_stair_del_mod);

    modifyField(room1, false,"tiles", tiles);

    // Place wearables in the room
    placeItemsInRoom(room3, 1, 2, spear);
    placeItemsInRoom(room3, 2, 2, shield);
    placeItemsInRoom(room3, 1, 1, poison);
    placeItemsInRoom(room3, 2, 1, sauerkrautSaft);



//    get tiles for rooms
    Tile[][] tiles1 = room1.getTiles();
    Tile[][] tiles2 = room2.getTiles();
    Tile[][] tiles3 = room3.getTiles();
    Tile[][] tiles4 = room4.getTiles();

//    specify tile types (for all objects)
    tiles3[3][2] = new StairTile(3, 2, room3); // one-way stair
    tiles4[0][1] = new StairTile(0, 1, room4);

    tiles4[0][2] = new StairTile(0, 2, room4); // stair without end
    tiles4[3][2] = new DoorTile(3, 2, room4, true, false); // door without end

    tiles4[1][3] = new DoorTile(1, 3, room4, true, false); // opened door
    tiles2[1][0] = new DoorTile(1, 0, room2, false, false); // unlocked door




//    update the room
    modifyField(room1, false, "tiles", tiles1);
    modifyField(room2, false, "tiles", tiles2);
    modifyField(room3, false, "tiles", tiles3);
    modifyField(room4, false, "tiles", tiles4);

//    create objects
    //    Connect Room3 and Room4 by a one-way stair
    StairTile stairTile3 = (StairTile) room3.getTiles()[3][2];
    StairTile stairTile4 = (StairTile) room4.getTiles()[0][1];
    connectStairTiles(stairTile3, stairTile4, true);

//    Stair without toTile
    StairTile stairTile5 = (StairTile) room4.getTiles()[0][2];

//    Door without toTile
    DoorTile doorTile5 = (DoorTile) room4.getTiles()[3][2];

    // Unlocked Door, one door opened, the other closed
    DoorTile doorTile3 = (DoorTile) room4.getTiles()[1][3];
    DoorTile doorTile4 = (DoorTile) room2.getTiles()[1][0];
    connectTiles(doorTile3, doorTile4);

    //haracter c# = createBaseCharacter("#", #, #);
    //    Character c# = createBaseCharacter("#", #, #);
    //    addCharacter(room#, #,#, c#);
    //    addCharacter(room#, #, #,c#);

    Character m1 = createBaseCharacter("flyingSpaghettiMonster", 10, 2);
    Character m2 = createBaseCharacter("evilHamster", 2, 1);
    addCharacter(room4, 1, 2, m1);
    addCharacter(room4, 2, 2, m2);

    //Add Rooms to the world after modifications
    modifyField(room1, false,"w", world);
    mapElements.add(room1);
    modifyField(room2, false,"w", world);
    mapElements.add(room2);
    modifyField(room3, false,"w", world);
    mapElements.add(room3);
    modifyField(room4, false,"w", world);
    mapElements.add(room4);

    //Add mapElements to the world though mapElements
    modifyField(world, false,"mapElements", mapElements);

    //Add game to the world and vice versa
    modifyField(game, false, "world", world);
    modifyField(world, false, "g", game);
    return game;
  }

  /**
   * the default world ( three rooms, connected by stairs and hallways, two characters and some items)
   * @return an instance of the default world
   */
  public static World createWorld()  {
    World world = new World();
    List<Room> mapElements = new ArrayList<>();
    // Create the rooms
    Room room1 = createSimpleRoom(8,8,  1);
    modifyField(room1, false,"w", world);
    mapElements.add(room1);
    Room room2 = createSimpleRoom(7,7, 2);
    modifyField(room2, false,"w", world);
    mapElements.add(room2);
    Room room3 = createSimpleRoom(4, 10, 3);
    modifyField(room3, false,"w", world);
    mapElements.add(room3);
    modifyField(world, false,"mapElements", mapElements);

    // Connect Room 1 and 2 with a hallway
    DoorTile doorTile1 = (DoorTile) room1.getTiles()[0][8/2];
    DoorTile doorTile2 = (DoorTile) room2.getTiles()[0][7/2];
    connectTiles(doorTile1, doorTile2);
    // Connect Room 2 and 3 with stairs
    StairTile stairTile1 = (StairTile) room2.getTiles()[7/2][7/2];
    StairTile stairTile2 = (StairTile) room3.getTiles()[4/2][10/2];
    connectStairTiles(stairTile1, stairTile2,false);

    // add two characters to the world
    Character c1 = createBaseCharacter("Foo", 10, 10);
    Character c2 = createBaseCharacter("Bar", 10, 10);
    addCharacter(room1, 2,2,c1);
    addCharacter(room3, 1,4,c2);

    // create a bunch of items and place them in the world
    Wearable sword = createWearable(2, true);
    modifyField(sword, false, "character",  c1);
    modifyField(c1, false, "activeWeapon", sword );
    c1.items.add(sword);
    Item fountain = new Fixtures();
    List<Item> onTile = new ArrayList<>();
    onTile.add(fountain);
    modifyField(fountain, true, "onTile", room1.getTiles()[3][3]);
    modifyField(room1.getTiles()[3][3], false, "items", onTile);

    Wearable rottenApple = createWearable(1, false);
    placeItemsInRoom(room3,2,8, rottenApple);
    modifyField(rottenApple, true, "effect", new CharacterModifier(1, 0, 0, 1));
    return world;
  }


  /**
   * Creates a simple room of the size x,y ... the room number can be used to identify the room when debugging
   * @param x the 1 dimension of the room
   * @param y the 2 dimension of the room
   * @param roomNo the room number
   * @return a simple room
   */
  public static Room createSimpleRoom(int x, int y, int roomNo){
    Room r = new Room();
    Tile[][] tiles = createTiles(x, y, r, roomNo);
    modifyField(r, false,"tiles", tiles);
    return r;
  }

  /**
   * create the tiles used to create a room
   * @param x the 1 dimension of the room
   * @param y the 2 dimension of the room
   * @param room the room the tiles belong to
   * @param roomNo the room's number
   * @return a Tile[][] used in the room
   */
  private static Tile[][] createTiles(int x, int y, Room room, int roomNo){
    Tile[][] result = new Tile[x][y];
    for(int i = 0; i < x; i++){
      for(int j = 0; j< y; j++){
        if (i == 0 || j == 0 || i == (x-1) || j == (y-1)){
          result[i][j] = new WallTile(i, j ,room);
        } else {
          result[i][j] = new FloorTile(i, j ,room);
        }
      }
    }
    // place a door
    if (roomNo < 3) result[0][y/2] = new DoorTile(0, y/2 , room);
    // place a stair tile right in the center of the room
    if (roomNo > 1) result[x/2][y/2] = new StairTile(x/2, y/2, room);
    return result;
  }


  /**
   * Add the given Character to the room at the given position
   * @param room the room the character should be added to
   * @param x the x position
   * @param y
   * @param character
   */
  public static void addCharacter(Room room, int x, int y, Character character){
    List<Character> inhabitants = room.getInhabitants();
    inhabitants.add(character);
    modifyField(room,false, "inhabitants", inhabitants);
    placeCharacter(character,room.getTiles()[x][y]);
    //modifyField(character, false, "tile", room.getTiles()[x][y]);
  }

  public static CharacterModifier createCharacterModifier(int health, int magic, int power, int howlong ) {
    CharacterModifier attack = new CharacterModifier();
    attack.health = health;
    attack.magic = magic;
    attack.power = power;
    modifyField(attack, false, "howLong", howlong);
    return attack;
  }

  @Test
  public void testCreateWorld(){
    World testObject = createWorld();
    testDefaultWordData(testObject);
  }


  /**
   * This method can be used to check whether everything in out test/default world is at its place.
   * @param testObject the world to test.
   */
  public static void testDefaultWordData(World testObject){
    assertNotNull(testObject);
    List<Room> mapElements = testObject.getMapElements();
    assertEquals(3, mapElements.size());
    // Test if the rooms are instantiated and connected correctly
    Room r1 = null;
    Room r2 = null;
    Room r3 = null;
    for(Room r : mapElements){
      if(r.getTiles().length == 8 && r.getTiles()[0].length == 8){
        r1 = r;
      } else if(r.getTiles().length == 7 && r.getTiles()[0].length == 7){
        r2 = r;
      } else if(r.getTiles().length == 4 && r.getTiles()[0].length == 10){
        r3 = r;
      }

    }
    // Test if the rooms are instantiated and connected correctly
    assertNotNull(r1);
    assertNotNull(r2);
    assertNotNull(r3);
    // r1 should be of size [8][8], have a door at [0][4]
    assertEquals(8,r1.getTiles().length);
    assertEquals(8,r1.getTiles()[0].length);
    DoorTile dr1 = (DoorTile) r1.getTiles()[0][4];
    assertEquals(DoorTile.class, dr1.getClass());
    // r2 should be of size [7][7], have a door at [0][7/2] and stairs at [7/2][7/2]
    assertEquals(7,r2.getTiles().length);
    assertEquals(7,r2.getTiles()[0].length);
    DoorTile dr2 = (DoorTile) r2.getTiles()[0][7/2];
    StairTile sr2 = (StairTile) r2.getTiles()[7/2][7/2];
    assertEquals(DoorTile.class, dr2.getClass());
    // both doors are connected
    assertEquals(dr1.getHallway(), dr2.getHallway());
    // r3 should be of size [4][10], have a door at [0][5] and stairs at [2][5]
    assertEquals(4,r3.getTiles().length);
    assertEquals(10,r3.getTiles()[0].length);
    StairTile sr3 = (StairTile) r3.getTiles()[2][5];
    // both stairs are connected
    assertEquals(sr2.getStair(), sr3.getStair());
    // there should be a character named "Foo" in room1 carrying a sword
    Character foo = r1.getInhabitants().get(0);
    assertNotNull(foo);
    assertEquals("Foo",foo.name);
    assertNotNull(foo.activeWeapon);
    assertEquals(2,foo.activeWeapon.weight);
    // there should be a character named "Bar" in room3
    Character bar = r3.getInhabitants().get(0);
    assertNotNull(bar);
    // there should be a fixture in room 1 on tile [3][3]
    assertEquals(Fixtures.class, r1.getTiles()[3][3].onTile().get(0).getClass());
    // there should be a Wearable  in room 3 on tile [2][8]
    assertEquals(Wearable.class, r3.getTiles()[2][8].onTile().get(0).getClass());
  }

  /**
   * Place items at the specified position in the room
   * @param x the x coordinate
   * @param room the room in which the items should appear
   * @param y the y coordinate
   * @param items the items to be added
   */
  public static void placeItemsInRoom(Room room, int x, int y, Item... items) {
    List<Item> onTile = new ArrayList<>(Arrays.asList(items));
    for(Item wearable : onTile){
      if (wearable instanceof  Armor)
        modifySuperSuperField(wearable, "onTile", room.getTiles()[x][y]);
      else
        modifyField(wearable, true, "onTile", room.getTiles()[x][y]);
    }
    modifyField(room.getTiles()[x][y], false, "items", onTile);
  }

  /**
   * Place the character on the given tile.
   * @param testObject
   * @param tile
   */
  public static void placeCharacter(Character testObject, Tile tile) {
    modifyField(testObject, false, "tile", tile);
  }

  /**
   * Create a simple instance of Wearable
   * @param weight the weight of the wearable
   * @param isWeapon boolean determining whether the wearable is a weapon
   * @return the new wearable
   */
  public static Wearable createWearable(int weight, boolean isWeapon) {
    Wearable item = new Wearable();
    modifyField(item, false, "weight", weight);
    modifyField(item, false, "isWeapon", isWeapon );
    return item;
  }

  /**
   * create a doortile with the given properties
   * @param destructible 0 if the door is indestructible, else power needed to force door open
   * @param isLocked true if the door is locked, otherwise false
   * @return
   */
  public static DoorTile createDoorTile(int destructible, boolean isLocked) {
    DoorTile doorTile = new DoorTile();
    modifyField(doorTile, false, "locked", isLocked);
    modifyField(doorTile, true, "destructible", destructible);
    return doorTile;
  }

  /**
   * create a walltile with the given properties
   * @param destructible 0 if the door is indestructible, else power needed to force door open
   * @return
   */
  public static WallTile createWallTile(int destructible){
    WallTile wallTile = new WallTile();
    modifyField(wallTile, false, "destructible", destructible);
    return wallTile;
  }

  /**
   * Create a default character, specified by it name, power, and the max weight he can carry
   * @param name the name of the character
   * @param power the character's power
   * @param maxWeight the max weight the character can carry
   * @return the default character
   */
  public static Character createBaseCharacter(String name, int power, int maxWeight ){
    Character character = new Character();
    modifyField(character, false, "name", name);
    modifyField(character,false, "items", new ArrayList<>());
    modifyField(character, false, "maxWeight", maxWeight);
    modifyField(character, false, "power", power);
    modifyField(character, false, "magic", 50);
    modifyField(character, false, "role", character.WARRIOR);
    return character;
  }


  /**
   * connect two doorTiles with a hallway
   * @param t1 the "start" doorTile
   * @param t2 the "end" doorTile
   * @return a new instance of Hallway
   */
  public static Hallway connectTiles(DoorTile t1, DoorTile t2) {
    Hallway connector = new Hallway();
    modifyField(connector,true,"fromTile", t1);
    modifyField(connector,true,"toTile", t2);
    modifyField(t1, false, "hallway", connector);
    TestUtils.modifyField(t2, false, "hallway", connector);
    return connector;
  }

  /**
   * connect two stairtiles with a stairs
   * @param t1 the "start" doorTile
   * @param t2 the "end" doorTile
   * @return a new instance of stair
   */
  public static Stair connectStairTiles(StairTile t1, StairTile t2, boolean isOneWay) {
    Stair connector = new Stair();
    modifyField(connector, false, "oneWay", isOneWay);
    modifyField(connector,true,"fromTile", t1);
    modifyField(connector,true,"toTile", t2);
    modifyField(t1, false, "stair", connector);
    TestUtils.modifyField(t2, false, "stair", connector);
    return connector;
  }

  /**
   * creates an armor
   * @param health double defining by how much the armor protects the characters health
   * @param magic double defining by how much the armor protects the characters magic
   * @param power double defining by how much the armor protects the characters power
   * @return
   */
  public static Armor createArmor(double health, double magic, double power ){
    Armor armor = new Armor();
    MultiplicativeEffect multiplicativeEffect = new MultiplicativeEffect();
    modifyField(multiplicativeEffect, true, "health", health);
    modifyField(multiplicativeEffect, true, "magic", magic);
    modifyField(multiplicativeEffect, true, "power", power);
    modifyField(armor, false, "modifyingEffect", multiplicativeEffect);
    return armor;
  }

  /**
   * equip the given wearable as the active weapon of the character
   * @param wearable the weapon
   * @param character the character that will carry it
   */
  public static void equipWeapon(Wearable wearable, Character character){
    character.activeWeapon = wearable;
  }

  /**
   * equip the given armor as the armor of the character
   * @param armor the armor, and
   * @param character the character that will carry it
   */
  public static void equipArmor(Armor armor, Character character){
    character.armor.add(armor);
    character.items.add(armor);
  }

  /** Add a protagonist with given name to the game */
  public static Character addProtagonist(Game game, String name){
    Character character = new Character();
    modifyField(game, false, "protagonist", character);
    modifyField(character, false, "name", name);
    modifyField(character, false, "role", character.WARRIOR);
    return character;
  }

  public static void placeTrapOnTile(Trap trap, Tile tile){
    trap.onTile = tile;
    if (tile instanceof  StairTile) {
      modifyField(trap, false, "trapDoor", tile);
      modifyField(tile, false, "trap", trap);
    }
  }
}