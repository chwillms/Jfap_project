package de.unisaar.faphack.model;

import de.unisaar.faphack.model.effects.ItemEffect;
import de.unisaar.faphack.model.MarshallingContext;
import de.unisaar.faphack.model.map.Tile;

/**
 * Keys are used to unlock doors.
 * To unlock a door a character has to have a key with a matching keyID.
 * Keys don't have character modifier effects
 */
public class Key extends Wearable implements Storable {
  /**
   * To unlock a door, you need a key with a matching keyID
   * */
  private int keyID;

  //todo: Fab better method to get trait into the constructor
  public Key(Tile where, Character c, int kid) {
    super(where, KEY, null, c);
    this.keyID = kid;
  }

  public int getKeyID() {
    return this.keyID;
  }

  @Override
  public void marshal(MarshallingContext c) {
    super.marshal(c);
    // c.write("stair", this.stair);
    c.write("keyID", this.keyID);
  }

  @Override
  public void unmarshal(MarshallingContext c) {
    super.unmarshal(c);
    this.keyID = c.readInt("keyID");
  }

}
