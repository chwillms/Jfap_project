package de.unisaar.faphack.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.unisaar.faphack.model.effects.ModifyingEffect;
import de.unisaar.faphack.model.effects.MultiplicativeEffect;
import de.unisaar.faphack.model.map.*;

/**
 * @author
 *
 */
public class Character extends AbstractObservable<TraitedTileOccupier>
implements Storable, TraitedTileOccupier {

  public int currentWeight = 0;
  public Tile getTile;
  public int level = 1;
  /**
   * I'm currently on this level
   */
//  private int level = 0;

  /**
   * The position of the character.
   */
  protected Tile tile;
  /**
   * The characters inventory. The amount of items in the inventory is limited by
   * the maxWeight value of a character.
   */
  protected List<Wearable> items = new ArrayList<>();

  /**
   * The base health of the character, which can be modified by Modifiers.
   *
   * If health is zero, this character is dead!
   */
  int health = 100;

  /**
   * The base magic of the character, which can be modified by Modifiers.
   */
  int magic = 0;

  /**
   * The base power of the character, which can be modified by Modifiers.
   */
  int power = 0;

  /**
   * This models the character's trait, i.e., how effective are the different
   * skills of the character.
   */
  protected MultiplicativeEffect skills;

  /**
   * This might be shield / bodyarmor / etc.
   */
  protected List<Armor> armor = new ArrayList<>();

  /**
   * The maximal amount of weight the character can carry. The sum of the weight
   * of all items in the character's inventory plus the armor must not exceed this
   * value.
   */
  protected int maxWeight;

  /**
   * The currentWeight is the combined weights of armor, weapon and inventory
   */
//  private int currentWeight = 0;

  /**
   * All effects that currently apply on the character, for example damage or heal
   * over time
   */
  protected Set<CharacterModifier> activeEffects = new HashSet<>();

  /**
   * That's my name
   */
  protected String name;

  /**
   * That's my role
   */
  protected String role;

  /**
   * The currently active weapon
   */
  protected Wearable activeWeapon;

  public Character() {

  }

  /**
   * Change my position to the given Tile.
   *
   * @param destination
   * @return void
   */
  public void move(Tile destination) {
    if (tile != null) {
//      todo: check if destination is not null!
//      todo AK: can it be possible that the room of the tile is not defined?
//      todo AK: test change of the room
//      todo AK: test number of inhabitants
      Room current = tile.getRoom();
      if (destination.getRoom() != current) {
        current.getInhabitants().remove(this);
        destination.getRoom().getInhabitants().add(this);
      }
    } else {
      destination.getRoom().getInhabitants().add(this);
    }
    tile = destination;
    // Check if moving to the tile has consequences (fountains, stairs, traps, ...)
    // todo: what if tile contains trap and fountain at the same time?
    // todo: can we use willTake()?
    // if floor tile getFixtures: for each get charatermodifier, apply it to the character
    if (tile instanceof FloorTile) { // todo: traps can also be placed on floortiles...
      List<Item> fixtures = ((FloorTile) tile).getFixtures();
      for (Item fixture : fixtures) {
        assert (fixture instanceof Fixtures);
        if (fixture instanceof Trap) {
          Trap t = (Trap) fixture;
          StairTile st = t.getTrapDoor();
          if (st != null) {
            Tile newdestination = st.willTake(this); // willTake will apply trap effect?
            this.tile = newdestination == null ? this.tile : newdestination;
            continue; // todo: ok? assume willTake will already apply trap effect?
            // todo: use continue or break if trap found?
          }
        }
        CharacterModifier cm = fixture.getCharacterModifier();
        if (cm != null) {
          cm.applyTo(this);
        }
      }
      // if has trap, move to...
    }
    // if stair tile try will take... todo: uncomment?
//    else if (tile instanceof StairTile) { // don't use plain if here: tile already modified in previous if branch
//      // todo: maybe we shouldn't do this in that way:
//      //  MoveEffect also uses willtake and then character.move
//      //  btw, willTake() looks like a non-modifying function, but it modifies the character:
//      //  levelup/down, apply trap character modifier effect
//      Tile newdestination = tile.willTake(this);
//      tile = newdestination == null? tile : newdestination;
//    }
  }

  /**
   * Pick up the given Wearable. Returns true if the action is possible.
   * The character can only pickup an item if it is
   * 1. on the same tile
   * 2. the current weight of all items the character carries + the weight of the item is less then maxWeight
   *
   * @param what the item to be picked up
   * @return  boolean <code>true</code> if the action was successful, <code>false</code> otherwise
   */
  public boolean pickUp(Wearable what) {
//    todo AK: what if it is an armor, why are we adding to the itelms list?
//    todo AK: test current weight change

    //    AK: added input check
    if (what == null) {return false;} // todo AK: should return something else? Exception?

    if (this.getTile() == what.getTile()) { // todo: allow character to pick up items that are not on the same tile?
      if ((this.currentWeight + what.getWeight()) <= this.maxWeight) { //less or equal instead of less
        this.items.add(what);
        this.currentWeight += what.getWeight();
        what.pickUp(this);
        return true;
      }
    }
    return false;

  }


  /**
   * @return void
   */
  public void interact() {
    // TODO Auto-generated method stub
    // todo: fabz
  }

  public Wearable activeWeapon() {
    return activeWeapon;
  }

  public Tile getTile() {
    return tile;
  }

  public Room getRoom() {
    return tile.getRoom();
  }

  public int getHealth() {
    return health;
  }

  public String getName() {
    return name;
  }

  public int getLevel() {
    return level;
  }

  public int getMagic() {
    return magic;
  }

  public int getPower() {
    return power;
  }

  public int getMaxWeight() {
    return maxWeight;
  }

  public Wearable getActiveWeapon() {
    return activeWeapon;
  }

  public int getWeight() {
    // TODO: implement
//    return 0;
    return currentWeight; // AK
  }

  public int levelDown() {
    return ++level;
  }

  public int levelUp() {
    return --level;
  }

  /**
   * Apply the effects of an attack, taking into account the armor
   */
  public void applyAttack(CharacterModifier eff) {
    /*
     * Example of an attack - an adversary uses his weapon (different dimensions,
     * like affecting health, armor, magic ability, and how long the effect
     * persists)
     *
     * - several factors modulate the outcome of this effect: current health
     * stamina, quality of different armors, possibly even in the different
     * dimensions.
     */
//    AK: added input check
    if (eff == null) {return;}

    if (this.armor.isEmpty()) {
      eff.applyTo(this);
    }
    else {
      for (int i = 0; i < this.armor.size(); i++) {
        ModifyingEffect effect = armor.get(i).getModifyingEffect();
        effect.apply(eff);
      }
      eff.applyTo(this);
    }
  }

  /**
   * Apply the effects of, e.g., a poisoning, eating something, etc.
   */
  public void applyItem(CharacterModifier eff) {

    //    AK: added input check
    if (eff == null) {return;}

    eff.applyTo(this);

  }

  /**
   * removes the given Item from the characters inventory
   * @param item the item to be removed
   * @return <code>true</code> if the action was successful, <code>false</code> otherwise
   *
   */
  public boolean dropItem(Wearable item){
    if (item == null) {
      return false;
    }
    if (this.items.contains(item)){
        this.items.remove(item);
        String tr = item.getTrait(); // AK: do we still need it?
        if (item instanceof Armor) {
          this.armor.remove(item);
        }
        //todo: what happens if you have an active item equiped that equals another item in items that is not the active weapon
        //is the active weapon dropped?
        //Item is equiped as active weapon
        if(this.activeWeapon == null || this.activeWeapon.equals(item)){
          this.activeWeapon = null;
        }
        this.currentWeight -= item.getWeight();
        item.drop(this.getTile());
        // todo AK: need to change character to null?
        return true;
    }
    return false;
  }

  /**
   * Equips the given Wearable as active Weapon or armor depending
   * @param wearable the item to be equipped
   * @return <code>true</code> the action was successful, <code>false</code> otherwise
   */
  public boolean equipItem(Wearable wearable){
    // check if the wearable item is in the items inventory
    if (this.items.contains(wearable)) {
      // if in inventory, check if the wearable item is a weapon
      if (wearable.isWeapon) {
        // if weapon, equip character with the wearable item as active weapon
        this.activeWeapon = wearable;
        return true;
      } else {
        // if not a weapon, equip character with the wearable item as armor
        this.armor.add((Armor) wearable);
        return true;
      }
    }
    // if not in inventory, return false
     return false;
    }

  @Override
  public String getTrait() { return (health == 0 ? "DEAD_" : "") + role; }

  @Override
  public void marshal(MarshallingContext c) {
    c.write("level", this.level);
    c.write("tile", this.tile);
    c.write("items", this.items);
    c.write("health", this.health);
    c.write("magic", this.magic);
    c.write("power", this.power);
    c.write("skills", this.skills);
    c.write("armor", this.armor);
    c.write("currentWeight", this.currentWeight);
    c.write("maxWeight", this.maxWeight);
    c.write("activeEffects", this.activeEffects);
    c.write("name", this.name);
    c.write("role", this.role);
    c.write("activeWeapon", this.activeWeapon);
  }

  @Override
  public void unmarshal(MarshallingContext c) {
    this.level = c.readInt("level");
    this.tile = c.read("tile");
    c.readAll("items", this.items);
    this.health = c.readInt("health");
    this.magic = c.readInt("magic");
    this.power = c.readInt("power");
    this.skills = c.read("skills");
    c.readAll("armor", this.armor);
    this.currentWeight = c.readInt("currentWeight");
    this.maxWeight = c.readInt("maxWeight");
    c.readAll("activeEffects", this.activeEffects);
    this.name = c.readString("name");
    this.role = c.readString("role");
    this.activeWeapon = c.read("activeWeapon");
  }

  public void rest() {
    this.power += 5;
  }

  public boolean owns(Item item) {
    // return items.contains(item); test for equality or identity?
    for (Item item2 : items) {
      if (item2.equals(item)) return true;
    }
    return false;
  }
}
