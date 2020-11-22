package de.unisaar.faphack.model;

import de.unisaar.faphack.model.effects.AdditiveEffect;
import de.unisaar.faphack.model.effects.ModifyingEffect;
import de.unisaar.faphack.model.map.FloorTile;
import de.unisaar.faphack.model.map.Tile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/// @author weissenh
class ArmorTest {

  @Test
  void getCharacterModifier() {
    Armor armor = new Armor();
    CharacterModifier cm = armor.getCharacterModifier();
    assertNull(cm);
  }

  @Test
  void getModifyingEffect() {
    // test with zero parameter constructor
    Armor armor = new Armor();
    ModifyingEffect me = armor.getModifyingEffect();
    assertNull(me);
    // test with nonzero parameter constructors
    me = new AdditiveEffect(5,0,0);
    Tile tile = new FloorTile();
    Character character = new Character();
    Armor armor1 = new Armor(me, tile);
    ModifyingEffect me2;
    me2 = armor1.getModifyingEffect();
    assertEquals(me, me2);
    Armor armor2 = new Armor(me, character);
    me2 = armor2.getModifyingEffect();
    assertEquals(me, me2);
  }
}