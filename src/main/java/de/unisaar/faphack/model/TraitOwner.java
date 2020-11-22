package de.unisaar.faphack.model;

import java.util.ArrayList;

public interface TraitOwner {

  /** The list of trait constants, for all characters, there exists a non-listed
   *  DEAD_trait variant!
   */
  // Tiles
  static final String FLOOR = "floor";
  static final String DOOR = "door";
  static final String OPENDOOR = "open_door";
  static final String WALL = "wall";
  static final String DESTROYED_WALL = "destroyed_wall";
  static final String STAIR = "stair";
  // Obstacle Tiles
  static final String BOULDER = "boulder";

  // Items
  static final String POTION = "potion";
  static final String SWORD = "sword";
  static final String SHIELD = "shield";
  static final String PIKE = "pike";
  static final String BOW = "bow";
  static final String ARROW = "arrow";
  static final String ARMOR = "armor";
  static final String KEY = "key";

  // Fixtures
  static final String CHEST = "chest";
  static final String FOUNTAIN = "fountain";

  // Characters
  static final String GOBLIN = "goblin";
  static final String SKELETON = "skeleton";
  static final String WIZARD = "wizard";
  static final String SORCERESS = "sorceress";
  static final String WARRIOR = "warrior";
  static final String MARKSMAN = "marksman";

  /** The trait of this trait owner */
  public abstract String getTrait();

}
