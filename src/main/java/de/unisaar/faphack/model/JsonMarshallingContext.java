package de.unisaar.faphack.model;

import de.unisaar.faphack.model.map.Tile;
import org.json.simple.JSONArray;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;

public class JsonMarshallingContext implements MarshallingContext {

  private final File file;

  private static StorableFactory factory;

  private IdentityHashMap<Storable, String> writecache;

  private Deque<JSONObject> stack;

  private Map<String, Storable> readcache;

  private int idGenerator = 1;

  private static final String NAMEIDSEPARATOR = "@";

  public JsonMarshallingContext(File f, StorableFactory fact) {
      file = f;
      writecache = new IdentityHashMap<>();
      readcache = new HashMap<>();
      stack = new ArrayDeque<>();
      factory = fact; // todo: input validation?
  }

  private String getNewId() {
    String idstr = String.format("%06d", idGenerator);
    // %06 : fill with zeros, such that string has at least length 6
    // assert id wasn't used before
    idGenerator++;
    return idstr;
  }

  /**
   * Get unique string for a storable object containing classname and some unique number
   *
   * @param s Storable for which we would like to get an id
   * @return String like World@00001 if s is a World object
   */
  private String getClassNamePlusID(Storable s) {
    assert !this.writecache.containsKey(s); // only get string for things we haven't seen before
    String id = this.getNewId();  // e.g. 000001
    // getSimpleName will return "" for anonymous inner class
    String classname = s.getClass().getSimpleName();  // e.g. World  or Item  or ...
    return classname + NAMEIDSEPARATOR + id; // World@00001
  }

  /**
   * Get a new instance of class mentioned at beginning of str
   *
   * e.g. str = "World@0001"  -> get World object!
   * @param str string consisting of classname, Nameidseparator, unique id
   * @return new instance of the class
   */
  private Storable extractClassFromString(String str) {
    String[] parts = str.split(NAMEIDSEPARATOR);
    if (parts.length != 2) {
      // wellformed input has only one @
      // (assume neither class name nor id can contain '@')
      // but this str wasn't wellformed
      return null;
    }
    // assert parts.length == 2;
    String classname = parts[0];
    Storable c = factory.newInstance(classname);
    return c; // note: can be null!
  }

  @Override
  public void save(Storable s) {
    JSONObject jo = getJOforStorable(s);
    try {
      FileWriter fw = new FileWriter(this.file);
      // enhancement: how to write *formatted* JSON?
      // fw.write(jo.toJSONString());
      fw.write(jo.toString());
      fw.flush();
      fw.close();
    }
    catch (java.io.IOException e) {
      System.err.println("Writing to file not possible: " + file.toString());
      e.printStackTrace();
    } // todo: what's the best way to open a file? (always close?)
  }

  private Storable getStorableFromJsonObject(JSONObject jobj) {
    // used while reading
    assert jobj != null;
    // ask json object for id
    String id = (String) jobj.get("id");  // e.g. id = "World@000001":
    if (id == null) {
      // jobj had no 'id' key or value of id is null
      return null;
    }
    Storable s = this.readcache.get(id); // ask readcache if id seen
    if (s != null) { // already seen this object, just return it!
      // enhancement: maybe check if matched other fields if any?
      return s;
    }
    // haven't seen object before, so let's read it in from jobj
    assert s == null;
    assert !this.readcache.containsKey(id);
    stack.push(jobj);  // current jobj needs to be read in
    s = this.extractClassFromString(id); // get new (uninitialized) Storable
    // todo: input validation: what if s is null (couldn't extract class name)
    // put new storable s in cache (now we have seen the storable!)
    this.readcache.put(id, s);
    // unmarshal: fill instance variables using the values of jobj
    s.unmarshal(this);
    stack.pop(); // finished reading in from json object
    return s;
  }

  public Storable read() {
    JSONParser parser = new JSONParser();
    try {
      FileReader reader = new FileReader(file);
      Object obj = parser.parse(reader);
      JSONObject jobj = (JSONObject) obj;
      Storable s = getStorableFromJsonObject(jobj);
      return s;
    }
    catch (ParseException pe) {
      System.err.println("position: " + pe.getPosition());
      System.err.println(pe);
    }
    catch (FileNotFoundException e) {
      System.err.println("File not found: " + file.toString());
      e.printStackTrace();
    }
    catch (IOException e) {
      System.err.println("File error for file: " + file.toString());
      e.printStackTrace();
    }
    return null;
  }

  private JSONObject getJOforStorable(Storable s) {
    // used while writing
    JSONObject jobj = new JSONObject();
    if (s == null) { // cannot create json object for null
        return null;
    }
    String id = this.writecache.get(s);
    if (id != null) { // seen before!
      // want jobj to be just  { id : classname@uniquenumber }
      jobj.put("id", id);
      return jobj;
    }
    else { // not seen before! add to cache and marshall object
      String classnameplusid = this.getClassNamePlusID(s);
      this.writecache.put(s, classnameplusid);
      jobj.put("id", classnameplusid);
      // json object still empty, pop from stack if completely filled with all instance variables
      stack.push(jobj);
      s.marshal(this); // add all attributes of s to the json object
      // note: marshal calls write and write can access stack, writechache for inner objects
      assert stack.size() > 0;
      jobj = stack.pop(); // jobj completely processed
      return jobj;
    }
  }

  @Override
  public void write(String key, Storable object) {
    JSONObject jo4storable = getJOforStorable(object); // value
    assert stack.size() > 0;
    JSONObject currentjo = this.stack.pop();
    currentjo.put(key, jo4storable); // key : value
    this.stack.push(currentjo);
  }

  @Override
  public <T extends Storable> T read(String key) {
    // should mirror write(String key, Storable object)
    // todo: change method to look less ugly
    assert stack.size() > 0;
    JSONObject jobj = stack.pop();
    Object o = jobj.get(key);
    if (o == null) { // key not in jobj or value is null
      stack.push(jobj);
      return null;
    }
    JSONObject value = (JSONObject) o;
    T t = (T) getStorableFromJsonObject(value); // todo: input validation: cast not possible?
    stack.push(jobj);
    return t;
  }

  @Override
  public void write(String key, int object) {
    assert stack.size() > 0;
    JSONObject jo = stack.peek();
    jo.put(key, object);
  }

  @Override
  public int readInt(String key) {
    assert stack.size() > 0;
    JSONObject jobj = stack.peek();
    Object o = jobj.get(key);
    // todo: input validation: what if key not present?
    // todo: input validation: can throw NullPointerException
    Long lo = (Long) o;
    int result = lo.intValue();
    // todo: input validation: can throw classcastexpection?
    return result;
  }

  @Override
  public void write(String key, double object) {
    assert stack.size() > 0;
    JSONObject jo = this.stack.peek();
    jo.put(key, object);
  }

  @Override
  public double readDouble(String key) {
    assert stack.size() > 0;
    JSONObject jobj = stack.peek();
    Object o = jobj.get(key);
    // todo: input validation: what if key not present?
    // todo: input validation: can throw NullPointerException
    double result = (double) o;
    // todo: input validation: can throw classcastexpection?
    return result;
  }

  @Override
  public void write(String key, String object) {
    assert stack.size() > 0;
    JSONObject jo = this.stack.peek();
    jo.put(key, object);
  }

  @Override
  public String readString(String key) {
    assert stack.size() > 0;
    JSONObject jobj = stack.peek();
    Object o = jobj.get(key);
    // todo: input validation: what if key not present?
    // todo: input validation: can throw NullPointerException
    String result = (String) o;
    // todo: input validation: can throw classcasexpection?
    return result;
  }

  @Override
  public void write(String key, Collection<? extends Storable> coll) {
    /*
    "meineliste" : [
    {"id": "Wearable@000001"},
    {"id": "Wearable@000002",
      "otherstuff" : "otherstuff",
      "otherstuff2" : "otherstuff2",
      "othercomplex" : {
        "id": "complexthing@001",
        "otherstuff" : "otherstuff"
      }
    }
  ]
   */
    // json object on stack, would like to add a "key":  pair to it
    // if (this.stack.size() == 0) raise Error? shouldnt happen
    assert stack.size() > 0;
    JSONObject jo = this.stack.pop();
    // for each element in the collection, write it (either id if written before or complete object)
    JSONArray jarray = new JSONArray();
    // for each item (storable!) in the collection coll: get its json object
    if (coll == null) {
      jo.put(key, null);
      stack.push(jo);
      return;
    }
    for (Storable item : coll) {
      JSONObject joitem = getJOforStorable(item);
      jarray.add(joitem);
    }
    // todo: assert that jarray has same size as collection
    jo.put(key, jarray);  // "key" : [ { <item 1> }, { <item 2> }, ...]
    this.stack.push(jo);
  }

  private <T extends Storable> void convertJArray2Collection(JSONArray jarray, Collection<T> coll) {
    assert coll != null;  // cannot add things to a null object
    for (Object item : jarray) {
      // convert each object in the jarray into a storable
      // and then add it to the collection
      // todo: input validation: what if casts fail?
      JSONObject jitem = (JSONObject) item;
      Storable s = getStorableFromJsonObject(jitem);
      coll.add((T) s);
    }
  }

  @Override
  public void readAll(String key, Collection<? extends Storable> coll) {
    // we ask the current json object for the value of key
    // this value should be a json array which
    // elements should be converted to Storables and then added to coll
    // todo what if coll null?
    assert stack.size() > 0;
    // peek: retrieve topmost stack element, without removing it
    // we would like to keep it on the stack for further read in
    JSONObject jobj = stack.pop();
    Object o = jobj.get(key);
    if (o == null) { // json object doesn't have this key
      stack.push(jobj);
      return;
    }
    JSONArray jarray = (JSONArray) o;
    convertJArray2Collection(jarray, coll);
    stack.push(jobj);
  }

  @Override
  public void write(String key, Tile[][] coll) {
    /*
    key: [
      [ Tileobj1 , Tileobj2 , ... ],
      [ Tileobj1, Tileobj2, ...],
      [ Tileobj1 , Tileobj2, ....]
    ]
    */
    // if (this.stack.size() == 0) raise Error? shouldnt happen
    assert stack.size() > 0;
    JSONObject jo = this.stack.pop();
    JSONArray outerjsonarray = new JSONArray();
    if (coll == null) {
      jo.put(key, null);
      stack.push(jo);
      return;
    }
    for (Tile[] innerarray : coll) {
      // start array for innerlist
      JSONArray innerjsonarray = new JSONArray();
      // fill this array with the json objects for the Tiles
      for (Tile cell : innerarray) {
        JSONObject jotile = getJOforStorable(cell);
        innerjsonarray.add(jotile);
      } // for each cell/Tile
      // built one jsonarray with tiles as json objects
      // add it to our outer array, collecting the rows of Tiles
      outerjsonarray.add(innerjsonarray);
    } // innerarray (for each row of Tiles)
    jo.put(key, outerjsonarray);
    this.stack.push(jo);
  }

  @Override
  public Tile[][] readBoard(String key) {
    assert stack.size() > 0;
    JSONObject jobj = stack.pop();
    Object value = jobj.get(key);
    if (value == null) {  // json object didn't have that key or value was null
      stack.push(jobj);
      return null;
    }
    JSONArray outerjarray = (JSONArray) value; // todo: input validation! (cast possible?)
    Collection<Tile[]> tilematrix = new ArrayList<>();
    for (Object innerarray: outerjarray) {
      Collection<Tile> tileaccumulator = new ArrayList<>();
      JSONArray innerjarray = (JSONArray) innerarray;  // todo: input validation! (cast possible?)
      convertJArray2Collection(innerjarray, tileaccumulator);
      Tile[] tilerow = tileaccumulator.toArray(new Tile[0]);
      tilematrix.add(tilerow);
    }
    Tile[][] tiles = tilematrix.toArray(new Tile[0][0]);
    stack.push(jobj);
    return tiles;
  }
}
