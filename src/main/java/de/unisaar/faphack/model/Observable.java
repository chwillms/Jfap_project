package de.unisaar.faphack.model;
///
public interface Observable<T> {
  public void register(Observer<T> observer);

  public void notifyObservers(T object);
}
