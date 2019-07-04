package de.unisaar.jfap;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class HeroTest {

  @Test
  void moveTo() {
    Hero testObject = new Hero();
    assertFalse(testObject.moveTo(0,0));
  }
}