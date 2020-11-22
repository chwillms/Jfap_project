package de.unisaar.faphack.model;

import de.unisaar.faphack.model.map.DoorTile;
import de.unisaar.faphack.model.map.FloorTile;
import de.unisaar.faphack.model.map.Tile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KeyTest {

  @Test
  void getKeyID() {
    int keyid = 42;
    int keyid2 = -42;
    DoorTile door = new DoorTile(0,0, null, false, true);
    door.setKeyId(keyid);
    Tile t2 = new FloorTile(0,1, null);
    Key key1 = new Key(t2, null, keyid);
    Key key2 = new Key(t2, null, keyid2);
    assertEquals(key1.getKeyID(), keyid);
    // todo: test if we can open door with key (have also second nonmatching key
    // todo: test afterwards if door now unlocked
  }
}