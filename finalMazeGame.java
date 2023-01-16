import tester.Tester;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Iterator;

// To Play:
// press the "R" key to reset the maze
// press the "B" key to initate a breadth first search on the maze
// press the "D" to initiate a depth first search on the maze

// Edge class for the maze
class Edge {
  Vertex from;
  Vertex to;
  int weight;

  // constructor
  Edge(Vertex from, Vertex to, int weight) {
    this.from = from;
    this.to = to;
    this.weight = weight;
  }
}

//a vertex in a graph
class Vertex {
  int x;
  int y;
  boolean visited;
  boolean track;
  ArrayList<Edge> edge;

  Vertex(int x, int y) {
    this.y = y;
    this.x = x;
    this.edge = new ArrayList<Edge>();
    this.visited = false;
    this.track = false;
  }

  // method to identify each vertex by
  // generating a number with their x and y co-ordinates.
  int identify() {
    return 1000 * y + x;
  }
}

// interface to compare values
interface IComparator<T> {
  boolean apply(T t1, T t2);
}

// class to compare weights
class CompareEdges implements IComparator<Edge> {
  // apply method returns if edge1's weight is less than edge2's weight
  public boolean apply(Edge edge1, Edge edge2) {
    return edge1.weight < edge2.weight;
  }
}

// represents the maze game
class MazeGameWorld extends World {
  // number of cells width wise
  static int WIDTH = 50;
  // number of cells height wise
  static int HEIGHT = 30;
  // scale of the world
  static int SCALE = 10;
  // list of Vertex
  IList<Vertex> loVertex;
  // list of edges for the loEdge
  IList<Edge> loEdge;
  boolean bfs;
  boolean dfs;
  BreadthFirstSearch b;
  DepthFirstSearch d;
  Player p;

  // constructs a maze
  MazeGameWorld() {
    start();
  }

  // sets up the maze
  void start() {
    ArrayList<ArrayList<Vertex>> board = makeloVertex();
    ArrayList<Edge> allEdges = getEdges(board);
    board = kruskalVertice(board);
    loEdge = getloEdge(board, allEdges);
    loVertex = new Empty<Vertex>();
    for (ArrayList<Vertex> elem1 : board) {
      for (Vertex elem2 : elem1) {
        loVertex = loVertex.add(elem2);
      }
    }
    bfs = false;
    dfs = false;
    b = new BreadthFirstSearch(loVertex);
    d = new DepthFirstSearch(loVertex);
    p = new Player(loVertex);
  }

  // for loop which froms the edges for the maze
  IList<Edge> getloEdge(ArrayList<ArrayList<Vertex>> v, ArrayList<Edge> e) {
    IList<Edge> w = new Empty<Edge>();
    for (Edge elem : e) {
      boolean valid = true;
      for (ArrayList<Vertex> l : v) {
        for (Vertex vt : l) {
          for (Edge e2 : vt.edge) {
            if (e.equals(e2) || (elem.to == e2.from && elem.from == e2.to)) {
              valid = false;
            }
          }
        }
      }
      if (valid) {
        w = w.add(elem);
      }
    }
    return w;
  }

  // generates an arraylist of every edge in a list of loVertex
  ArrayList<Edge> getEdges(ArrayList<ArrayList<Vertex>> v) {
    ArrayList<Edge> all = new ArrayList<Edge>();
    for (ArrayList<Vertex> elem1 : v) {
      for (Vertex elem2 : elem1) {
        for (Edge elem3 : elem2.edge) {
          all.add(elem3);
        }
      }
    }
    return all;
  }

  // generates a 2d arrayList with a nested for loop.
  ArrayList<ArrayList<Vertex>> makeloVertex() {
    ArrayList<ArrayList<Vertex>> loVertex = new ArrayList<ArrayList<Vertex>>();
    for (int x = 0; x < WIDTH; x++) {
      ArrayList<Vertex> temp = new ArrayList<Vertex>();
      for (int y = 0; y < HEIGHT; y++) {
        temp.add(new Vertex(x, y));
      }
      loVertex.add(temp);
    }
    // generates random
    Random r = new Random();

    for (ArrayList<Vertex> vList : loVertex) {
      for (Vertex v : vList) {
        if (v.x != 0) {
          v.edge.add(new Edge(v, loVertex.get(v.x - 1).get(v.y), r.nextInt(1000)));
        }
        if (v.x != WIDTH - 1) {
          v.edge.add(new Edge(v, loVertex.get(v.x + 1).get(v.y), r.nextInt(1000)));
        }
        if (v.y != 0) {
          v.edge.add(new Edge(v, loVertex.get(v.x).get(v.y - 1), r.nextInt(1000)));
        }
        if (v.y != HEIGHT - 1) {
          v.edge.add(new Edge(v, loVertex.get(v.x).get(v.y + 1), r.nextInt(1000)));
        }
      }
    }
    return loVertex;
  }

  // finds the actual edges for the maze based on weights
  // and assigns them to the loVertex
  ArrayList<ArrayList<Vertex>> kruskalVertice(ArrayList<ArrayList<Vertex>> v) {
    ArrayList<Edge> allEdges = getEdges(v);
    for (ArrayList<Vertex> i : v) {
      for (Vertex j : i) {
        j.edge = new ArrayList<Edge>();
      }
    }
    int totalCells = HEIGHT * WIDTH;
    IList<Edge> sT = new Empty<Edge>();
    ArrayList<Edge> sortedEdges = sort(allEdges);
    HashMap<Integer, Integer> hash = new HashMap<Integer, Integer>();
    for (int i = 0; i <= (1000 * HEIGHT) + WIDTH; i++) {
      hash.put(i, i);
    }
    ArrayList<Edge> se = sortedEdges;
    while (sT.length() < totalCells - 1) {
      Edge e = se.get(0);
      if (this.find(hash, e.to.identify()) != this.find(hash, e.from.identify())) {
        sT = sT.add(e);
        e.from.edge.add(e);
        e.to.edge.add(new Edge(e.to, e.from, e.weight));
        int temp = (find(hash, e.to.identify()));
        hash.remove(find(hash, e.to.identify()));
        hash.put(temp, find(hash, e.from.identify()));
      }
      se.remove(0);
    }
    return v;
  }

  // method for finding the values in a hashmap
  int find(HashMap<Integer, Integer> hashmap, int num) {
    if (hashmap.get(num) == num) {
      return num;
    }
    else {
      return find(hashmap, hashmap.get(num));
    }
  }

  // sorts the list of edges by merging and sorting
  ArrayList<Edge> sort(ArrayList<Edge> loe) {
    if (loe.size() <= 1) {
      return loe;
    }
    ArrayList<Edge> l1 = new ArrayList<Edge>();
    ArrayList<Edge> l2 = new ArrayList<Edge>();
    for (int i = 0; i < loe.size() / 2; i++) {
      l1.add(loe.get(i));
    }
    for (int i = loe.size() / 2; i < loe.size(); i++) {
      l2.add(loe.get(i));
    }
    l1 = sort(l1);
    l2 = sort(l2);
    return merge(l1, l2);
  }

  // the merging step of our merge sort
  ArrayList<Edge> merge(ArrayList<Edge> l1, ArrayList<Edge> l2) {
    ArrayList<Edge> loe = new ArrayList<Edge>();
    IComparator<Edge> c = new CompareEdges();
    while (l1.size() > 0 && l2.size() > 0) {
      if (c.apply(l1.get(0), l2.get(0))) {
        loe.add(l1.get(0));
        l1.remove(0);
      }
      else {
        loe.add(l2.get(0));
        l2.remove(0);
      }
    }
    while (l1.size() > 0) {
      loe.add(l1.get(0));
      l1.remove(0);
    }
    while (l2.size() > 0) {
      loe.add(l2.get(0));
      l2.remove(0);
    }
    return loe;
  }

  // method to ensure the right color is generated
  Color generateColor(Vertex v) {

    if (v.x == WIDTH - 1 && v.y == HEIGHT - 1) {
      return Color.blue;
    }
    else if (v.track) {

      return Color.blue;
    }
    else if (v.x == 0 && v.y == 0) {

      return Color.green;
    }
    else if (v.visited) {

      return Color.cyan;
    }
    else {
      return Color.gray;
    }
  }

  // draws the world scene
  public WorldScene makeScene() {
    WorldScene w = new WorldScene(WIDTH * SCALE, HEIGHT * SCALE);
    for (Vertex v : loVertex) {
      Color color = generateColor(v);
      w.placeImageXY(new RectangleImage(SCALE, SCALE, OutlineMode.SOLID, color),
          (v.x * SCALE) + (SCALE * 1 / 2), (v.y * SCALE) + (SCALE * 1 / 2));
    }
    for (Edge e : loEdge) {
      if (e.to.x == e.from.x) {
        w.placeImageXY(new RectangleImage(SCALE, SCALE / 10, OutlineMode.SOLID, Color.black),
            (e.to.x * SCALE) + (SCALE * 1 / 2),
            ((e.to.y + e.from.y) * SCALE / 2) + (SCALE * 1 / 2));
      }
      else {
        w.placeImageXY(new RectangleImage(SCALE / 10, SCALE, OutlineMode.SOLID, Color.black),
            ((e.to.x + e.from.x) * SCALE / 2) + (SCALE * 1 / 2),
            (e.to.y * SCALE) + (SCALE * 1 / 2));
      }
    }
    return w;
  }

  // method for the on tick function
  public void onTick() {
    if (bfs) {
      if (b.hasNext()) {
        b.next();
      }
    }
    if (dfs) {
      if (d.hasNext()) {
        d.next();
      }
    }
  }

  // method for onKey event
  public void onKeyEvent(String key) {
    if (key.equals("b")) {
      bfs = true;
      dfs = false;
      reset();
    }
    else if (key.equals("d")) {
      bfs = false;
      dfs = true;
      reset();
    }
    else if (key.equals("m")) {
      bfs = false;
      dfs = false;
      reset();
    }
    else if (key.equals("r")) {
      start();
    }
  }

  // method to refresh the game
  public void reset() {
    for (Vertex v : loVertex) {
      v.track = false;
      v.visited = false;
    }
    b = new BreadthFirstSearch(loVertex);
    d = new DepthFirstSearch(loVertex);
    p = new Player(loVertex);
  }
}

// abstract class search (for BFS and DFS)
abstract class Search {

  // keeps track of the current path
  HashMap<Integer, Vertex> currPath;

  // void method to reconstruct path from the start to the end
  void reconstruct(HashMap<Integer, Vertex> hm, Vertex vertex) {
    while (hm.containsKey(vertex.identify())) {
      vertex.track = true;
      vertex = hm.get(vertex.identify());
    }
  }
}

// BFS class which extends the abstract class of Search
class BreadthFirstSearch extends Search {
  Queue<Vertex> wList;

  BreadthFirstSearch(IList<Vertex> list) {
    this.wList = new Queue<Vertex>();

    wList.enqueue(list.getData());

    list.getData().visited = true;

    currPath = new HashMap<Integer, Vertex>();
  }

  public boolean hasNext() {
    return !wList.isEmpty();
  }

  public Queue<Vertex> next() {
    Vertex u = wList.dequeue();
    for (Edge e : u.edge) {

      if (!e.to.visited) {
        currPath.put(e.to.identify(), e.from);
        if (e.to.x == MazeGameWorld.WIDTH - 1 && e.to.y == MazeGameWorld.HEIGHT - 1) {
          reconstruct(currPath, e.to);
          wList = new Queue<Vertex>();
        }
        else {
          e.to.visited = true;
          wList.enqueue(e.to);
        }
      }
    }
    return wList;
  }
}

// DFS class which extends the abstract class of Search
class DepthFirstSearch extends Search {
  Stack<Vertex> wList;

  DepthFirstSearch(IList<Vertex> list) {

    this.wList = new Stack<Vertex>();
    wList.push(list.getData());

    list.getData().visited = true;
    currPath = new HashMap<Integer, Vertex>();
  }

  // returns if the wList is empty
  public boolean hasNext() {
    return !wList.isEmpty();
  }

  public Stack<Vertex> next() {
    Vertex u = wList.pop();
    for (Edge e : u.edge) {

      if (!e.to.visited) {

        currPath.put(e.to.identify(), e.from);
        if (e.to.x == MazeGameWorld.WIDTH - 1 && e.to.y == MazeGameWorld.HEIGHT - 1) {
          reconstruct(currPath, e.to);
          wList = new Stack<Vertex>();
        }
        else {
          wList.push(u);
          e.to.visited = true;
          wList.push(e.to);
          break;
        }
      }
    }
    return wList;
  }
}

// class that represents a player 
class Player extends Search {

  Vertex current;

  boolean isFinished;

  Player(IList<Vertex> list) {
    current = list.getData();
    currPath = new HashMap<Integer, Vertex>();
    isFinished = false;
  }

  // checks if the maze is isFinished or not
  public boolean hasNext() {
    return !isFinished;
  }

  // method to move vertex
  public Vertex move(boolean b, Edge e) {
    if (b) {
      current.visited = true;
      current.track = false;
      if (!e.to.visited) {
        currPath.put(e.to.identify(), e.from);
      }
      if (e.to.x == MazeGameWorld.WIDTH - 1 && e.to.y == MazeGameWorld.HEIGHT - 1) {
        reconstruct(currPath, e.to);
      }
      else {
        current = e.to;
        current.track = true;
      }
    }
    return current;
  }

  // method moves the current postion to the left
  public Vertex moveLeft() {
    for (Edge e : current.edge) {
      move(e.to.x == current.x - 1, e);
    }
    return current;
  }

  //method moves the current postion to the right
  public Vertex moveRight() {
    for (Edge e : current.edge) {
      move(e.to.x == current.x + 1, e);
    }
    return current;
  }

  // method moves the current postion downwards
  public Vertex moveDown() {
    for (Edge e : current.edge) {
      move(e.to.y == current.y + 1, e);
    }
    return current;
  }

  // method moves the current postion upwards
  public Vertex moveUp() {
    for (Edge e : current.edge) {
      move(e.to.y == current.y - 1, e);
    }
    return current;
  }

  // void method which constructs the path
  void reconstruct(HashMap<Integer, Vertex> h, Vertex next) {
    while (h.containsKey(next.identify())) {
      next.track = true;
      next = h.get(next.identify());
    }
  }
}

// Code pertaining to IList, dequqe, and queue below

//a list of T items
interface IList<T> extends Iterable<T> {
  // determines if list is empty
  boolean isEmpty();

  // returns the length of the list
  int length();

  // to iterate over the list
  Iterator<T> iterator();

  // adds an element to the list
  IList<T> add(T t);

  // appends a list to the list
  IList<T> append(IList<T> l);

  // checks if list has another value
  boolean hasNext();

  // gets data value at this point
  T getData();

  // gets the rest of the list
  IList<T> getNext();
}

//represents an empty list
class Empty<T> implements IList<T>, Iterable<T> {

  // returns the length of the list
  public int length() {
    return 0;
  }

  // returns if list is empty
  public boolean isEmpty() {
    return true;
  }

  // used in iterating over the list
  public Iterator<T> iterator() {
    return new ListIterator<T>(this);
  }

  // adds a new element to the list
  public IList<T> add(T t) {
    return new Cons<T>(t, new Empty<T>());
  }

  // gets data from list
  public T get(int i) {
    return null;
  }

  // adds a new element to the list
  public IList<T> append(IList<T> l) {
    return l;
  }

  // checks if list has another value left
  public boolean hasNext() {
    return false;
  }

  // gets data value at this point
  public T getData() {
    return null;
  }

  // gets the rest of the list
  public IList<T> getNext() {
    throw new IllegalArgumentException();
  }
}

//represents a cons list of T
class Cons<T> implements IList<T>, Iterable<T> {
  T first;
  IList<T> rest;

  Cons(T first, IList<T> rest) {
    this.first = first;
    this.rest = rest;
  }

  // finds the length of the list
  public int length() {
    return 1 + this.rest.length();
  }

  // determines if list is empty
  public boolean isEmpty() {
    return false;
  }

  // for iterating over the list
  public Iterator<T> iterator() {
    return new ListIterator<T>(this);
  }

  // adds an element to the list
  public IList<T> add(T t) {
    if (rest.isEmpty()) {
      rest = new Cons<T>(t, new Empty<T>());
    }
    else {
      rest = rest.add(t);
    }
    return this;
  }

  // appends a list to the list
  public IList<T> append(IList<T> l) {
    if (rest.isEmpty()) {
      rest = l;
    }
    else {
      rest.append(l);
    }
    return this;
  }

  // checks if the list has next
  public boolean hasNext() {
    return true;
  }

  // gets data
  public T getData() {
    return this.first;
  }

  // gets the rest
  public IList<T> getNext() {
    return this.rest;
  }
}

//used in iterating over a list with elements of T data type
class ListIterator<T> implements Iterator<T> {
  IList<T> iter;

  ListIterator(IList<T> iter) {
    this.iter = iter;
  }

  // returns true if there is a value
  public boolean hasNext() {
    return iter.hasNext();
  }

  // returns the next value
  public T next() {
    T temp = this.iter.getData();
    this.iter = this.iter.getNext();
    return temp;
  }
}

//Deque
class Deque<T> implements Iterable<T> {
  Sentinel<T> header;

  public Iterator<T> iterator() {
    return new DequeIterator<T>(this.header);
  }

  Deque() {
    this.header = new Sentinel<T>();
  }

  Deque(Sentinel<T> header) {
    this.header = header;
  }

  // all nodes
  int size() {
    return this.header.size();
  }

  // add node at head
  T addAtHead(T t) {
    return header.addAtHead(t);
  }

  // add node at tail
  T addAtTail(T t) {
    return header.addAtTail(t);
  }

  // removes node at head
  T removeFromHead() {
    if (this.size() == 0) {
      throw new RuntimeException("empty list");
    }
    return header.removeFromHead();
  }

  // removes node at tail
  T removeFromTail() {
    if (this.size() == 0) {
      throw new RuntimeException("empty list");
    }
    return header.removeFromTail();
  }

  // finds a node that satisfies the pred
  ANode<T> find(IPred<T> p) {
    return header.find(p, true);
  }

  // removes a node from the deque
  void removeNode(ANode<T> a) {
    if (!a.equals(header)) {
      header.removeNodeFirst(a);
    }
  }
}

class DequeIterator<T> implements Iterator<T> {
  ANode<T> curr;

  DequeIterator(ANode<T> curr) {
    this.curr = curr;
  }

  public boolean hasNext() {
    return curr.hasNext();
  }

  // next value
  public T next() {
    T temp = this.curr.next.getData();
    this.curr = this.curr.next;
    return temp;
  }

  public void remove() {
    throw new UnsupportedOperationException();
  }
}

//abstract node class
abstract class ANode<T> {
  ANode<T> next;
  ANode<T> prev;

  // total number of Nodes
  int size(ANode<T> s) {
    if (this.next.equals(s)) {
      return 1;
    }
    else {
      return 1 + this.next.size(s);
    }
  }

  // removes node
  T remove() {
    this.prev.next = this.next;
    this.next.prev = this.prev;
    return this.getData();
  }

  // finds node
  ANode<T> find(IPred<T> p) {
    return this;
  }

  // helper to remove node
  void removeNode(ANode<T> a) {
    if (this.equals(a)) {
      this.remove();
    }
    else {
      this.next.removeNode(a);
    }
  }

  // gives data in node
  T getData() {
    return null;
  }

  boolean hasNext() {
    return !next.isSentinel();
  }

  boolean isSentinel() {
    return false;
  }
}

//sentinel
class Sentinel<T> extends ANode<T> {

  Sentinel() {
    this.next = this;
    this.prev = this;
  }

  // total number of nodes helper
  int size() {
    if (this.next.equals(this)) {
      return 0;
    }
    return this.next.size(this);
  }

  // add node to head
  T addAtHead(T t) {
    new Node<T>(t, this.next, this);
    return this.next.getData();
  }

  // add node to tail
  T addAtTail(T t) {
    new Node<T>(t, this, this.prev);
    return this.next.getData();
  }

  // removes node from head
  T removeFromHead() {
    return this.next.remove();
  }

  // removes node from tail e
  T removeFromTail() {
    return this.prev.remove();
  }

  // helper to find node that gets predicate
  ANode<T> find(IPred<T> p, boolean b) {
    return this.next.find(p);
  }

  // helper to remove node
  void removeNodeFirst(ANode<T> a) {
    this.next.removeNode(a);
  }

  // helper to remove node
  void removeNode(ANode<T> a) {
    return;
  }

  // checks if a node is present
  boolean isSentinel() {
    return true;
  }
}

//class for a node
class Node<T> extends ANode<T> {
  T data;

  Node(T data) {
    this.data = data;
    this.next = null;
    this.prev = null;
  }

  // places node in deque
  Node(T data, ANode<T> next, ANode<T> prev) {
    if ((next == null) || (prev == null)) {
      throw new IllegalArgumentException("Cannot accept null node");
    }
    this.data = data;
    this.next = next;
    this.prev = prev;
    prev.next = this;
    next.prev = this;
  }

  // find node 
  ANode<T> find(IPred<T> p) {
    if (p.apply(this.data)) {
      return this;
    }
    else {
      return this.next.find(p);
    }
  }

  // data from the node
  T getData() {
    return this.data;
  }
}

interface IPred<T> {
  boolean apply(T t);
}

//stack
class Stack<T> {

  Deque<T> contents;

  Stack() {
    this.contents = new Deque<T>();
  }

  Stack(IList<T> ts) {
    contents = new Deque<T>();
    for (T t : ts) {
      contents.addAtTail(t);
    }
  }

  // adds to head
  void push(T item) {
    contents.addAtHead(item);
  }

  //  checks if empty
  boolean isEmpty() {
    return contents.size() == 0;
  }

  // removes head 
  T pop() {
    return contents.removeFromHead();
  }
}

//queue
class Queue<T> {

  Deque<T> contents;

  Queue() {
    this.contents = new Deque<T>();
  }

  Queue(IList<T> ts) {
    contents = new Deque<T>();
    for (T t : ts) {
      contents.addAtTail(t);
    }
  }

  // adds to tail
  void enqueue(T item) {
    contents.addAtTail(item);
  }

  // checks if empty
  boolean isEmpty() {
    return contents.size() == 0;
  }

  // removes tail
  T dequeue() {
    return contents.removeFromTail();
  }
}

// Examples class used for testing
class ExamplesMazeGame {
  // test method to make the game and generate a world
  void testGame(Tester t) {
    MazeGameWorld m = new MazeGameWorld();
    m.bigBang(MazeGameWorld.WIDTH * MazeGameWorld.SCALE, MazeGameWorld.HEIGHT * MazeGameWorld.SCALE,
        0.005);
  }

  // tests the method identify
  void testIdentify(Tester t) {
    Vertex v1 = new Vertex(6, 4);
    Vertex v3 = new Vertex(10, 12);

    t.checkExpect(v1.identify(), 4006);
    t.checkExpect(v3.identify(), 12010);
  }

  // tests for IList

  void testIList(Tester t) {
    IList<String> mt = new Empty<String>();
    IList<String> mt2 = new Empty<String>();
    IList<String> list1 = new Cons<String>("1", new Cons<String>("2", new Cons<String>("3", mt)));
    IList<String> list2 = new Cons<String>("1", new Cons<String>("2", new Cons<String>("3", mt)));

    // testing isEmpty
    t.checkExpect(mt.isEmpty(), true);
    t.checkExpect(list1.isEmpty(), false);

    // testing length
    t.checkExpect(mt.length(), 0);
    t.checkExpect(list1.length(), 3);

    // testing add
    t.checkExpect(mt.add("123"), new Cons<String>("123", new Empty<String>()));

    // testing append
    t.checkExpect(list1.append(mt), list1);
    t.checkExpect(mt.append(mt2), mt);

  }
}