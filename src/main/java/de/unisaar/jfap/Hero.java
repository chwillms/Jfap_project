package de.unisaar.jfap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Hero {

  private static Logger LOGGER = LoggerFactory.getLogger(Hero.class);

  Role r ;
  int x , y ;

  /* * Change my position by the given vector
   * of length <= sqrt (2)
   * @param deltaX the x component of the vector
   * @param deltaY the y component of the vector
   * @return true if this movement is possible
   */
  boolean moveTo ( int deltaX , int deltaY ) {
    LOGGER.info("Moving to {},{}", deltaX, deltaY);
    return false;
  }

}
