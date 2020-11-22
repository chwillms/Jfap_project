package de.unisaar.faphack.model.map;

import de.unisaar.faphack.model.Character;
import de.unisaar.faphack.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author
 *
 */
public class DoorTile extends WallTile implements Storable, Observable<DoorTile> {
  private boolean open = false;

  private boolean locked = false;

  private Hallway hallway;

  private List<Observer<DoorTile>> observers; // don't marshall

  /**
   * To be opened by an item (key) the Effect of that item needs to create a m
   * atching ID.
   */
  private int keyId;



  public DoorTile() {
  }

  //todo: Fab should a door tile also have destructible value e.g. 0 or 1, see below
  public DoorTile(int x, int y, Room room, int destructible){
    super(x, y, room, destructible);
  }


  public DoorTile(int x, int y, Room room){
    this(x, y, room, 1);
    //super(x, y, room);
  }

  public DoorTile(int x, int y, Room room, boolean op, boolean lock){
    this(x, y, room, 1);
    this.open = op;
    this.locked = lock;
    //super(x, y, room);
  }

  @Override
  public Tile willTake(Character c) {
    // TODO: FILL THIS

    // check if we have a real character
    if (c == null) return null;

    // we need to check from which side we come and what is our goal tile.
    // default goal tile is the toTile
    Tile goalTile = hallway.toTile;

    // if we want to step on the toTile, then our goal tile is fromTile
    if (this == hallway.toTile) {
      goalTile = hallway.fromTile;
    }

    // if the door is open, we can directly go to the goal tile
    if (this.open) {
      return goalTile;
    }

    // if the door is closed, but not locked, we can still go through
    if (!this.locked) {
      return goalTile;
    }
    /*
    // if the door is locked, we can either open it using a key, or we can open it with force
    else {
      // if the door is closed, we can open with a key (if we have the correct key)
      Key k = new Key(new DoorTile(), "key", null, null, this.keyId); // changed just for compiling
      if (c.owns(k)) {
        this.locked = false; // we open the door and leave it open
        return goalTile;
      }

     */

    if (this.destructible == DESTROYED) {return goalTile;}
    if (this.destructible == INDESTRUCTIBLE) {return null;}
    if (this.destructible <= c.getPower()) {
        int p = - this.destructible;
        int h = 0;
        int m = 0;
        int hl = 1;
        CharacterModifier characterModifier = new CharacterModifier(h, m, p, hl);
        c.applyItem(characterModifier);
        this.destructible = DESTROYED;

        return goalTile;
      }

    return null;
  }

  @Override
  public void marshal(MarshallingContext c) {
    super.marshal(c);
    c.write("open", this.open ? 1 : 0);
    c.write("locked", this.locked ? 1 : 0);
    c.write("hallway", this.hallway);
    c.write("keyId", this.keyId);
  }

  @Override
  public void unmarshal(MarshallingContext c) {
    super.unmarshal(c);
    this.open = c.readInt("open") == 1;
    this.locked = c.readInt("locked") == 1;
    this.hallway = c.read("hallway");
    this.keyId = c.readInt("keyId");
  }

  public Hallway getHallway(){
    return hallway;
  }

  public void setKeyId(int kid) {
    this.keyId = kid;
  }

  @Override
  public String getTrait() { return open ? OPENDOOR : DOOR; }

  @Override
  public void register(Observer<DoorTile> observer) {
    // lazy initialization
    // copied from abstractObservable
    if (observers == null) {
      observers = new ArrayList<>();
    }
    observers.add(observer);
  }

  @Override
  public void notifyObservers(DoorTile object) {
    // copied from abstractObservable
    if (observers != null)
      for(Observer<DoorTile> o: observers) { o.update(object); }
  }
  // todo: is occupied et al overrride?


}
