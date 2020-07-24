import java.util.concurrent.Semaphore;
import java.util.LinkedList; 
import java.util.Queue; 
import java.util.Iterator;

enum Ingredient {
  SALT, BLACK_PEPPER, GREEN_PEPPER, HOT_PEPPER,
  RICE, SPAGHETTI, LASAGNE, PENNE, FETTUCCINE, ORZO, POTATO,
  GROUND_BEEF, BEEF, LAMB, CHICKEN, TURKEY, PORK, COD, HERRING, SHRIMP, SCALOP, SALMON,
  TOMATO, LETTICE, SPINACH, ARIGULA, CUCUMBER, BROCCOLI, ZUCCHINI, CARROT,
  BAY_LEAF, CARDAMOM, GARLIC, CINAMON, SPICE,
  TOMATO_SAUCE, ROSE_SAUCE, FETTUCCINE_SAUCE, BABECUE_SAUCE, BUTTER, OIL
}

class menu_item {
  Queue <Ingredient> ingredients;
  Iterator it;
  String name;
  customer owner;
  public menu_item (){
    ingredients = new LinkedList<>();
  }
  public void add_ingredient(Ingredient ing){
    ingredients.add(ing);
  }
  public void reset_iterator() {
    it = ingredients.iterator();
  }
  public Ingredient get_next_ingredient () {
    Ingredient ing;
    try { ing = (Ingredient) it.next(); }
    catch (java.util.NoSuchElementException e) { ing = null; }
    return ing;
  }
  public String toString(){
    return name;
  }
}

class order_fifo {
  private int fifo_size;
  menu_item[] orfer_buffer;
  private Semaphore wrSemaphore;
  private Semaphore rdSemaphore;
  private Semaphore mutex;
  private Logger logger;
  private int rd_indx, wr_indx;

  public order_fifo(int size, Logger _log) {
    logger = _log;
    fifo_size = size;
    rd_indx = 0;
    wr_indx = 0;
    orfer_buffer = new menu_item[fifo_size];
    wrSemaphore = new Semaphore(fifo_size);
    rdSemaphore = new Semaphore(0);
    mutex = new Semaphore(1);
  }
  public void add_order_item(menu_item _item){
    try {
      wrSemaphore.acquire();
      mutex.acquire();
      orfer_buffer[wr_indx % fifo_size] = _item;
      wr_indx++;
      logger.log("order #" + wr_indx + " added by " + _item.owner + ", type: " + _item.name);
      mutex.release();
      rdSemaphore.release();
    } catch (InterruptedException e) {}
  }
  public menu_item get_next_item(){
    menu_item item = null;

    try {
      rdSemaphore.acquire();
      mutex.acquire();
      item = orfer_buffer[rd_indx % fifo_size];
      rd_indx++;
      mutex.release();
      wrSemaphore.release();
    } catch (InterruptedException e) {}
    return item;
  }
  public int get_fill_level() {
    int val = 0;
    try {
      mutex.acquire();
      val = wr_indx - rd_indx;
      mutex.release();
    } catch (InterruptedException e) {}
    return val;
  }
}