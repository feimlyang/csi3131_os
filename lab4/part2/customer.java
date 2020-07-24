import java.util.concurrent.Semaphore;
import java.util.Random;

class customer implements Runnable{ 
  public int nReaders, nWriters, nWaitingReaders;
  public Logger logger;
  private int id;
  private menu m;
  Random rand; 
  order_fifo fifo;
  public final Semaphore mutex = new Semaphore(0);
  private static final Semaphore cnt_mutex = new Semaphore(1);
  static int num_order_requested = 0;
  static int num_order_served = 0;

  public customer(int _id, Logger _log, menu _m, order_fifo _fifo) {
    logger = _log;
    id = _id;
    m = _m;
    fifo = _fifo;
    rand = new Random();
  }
  public void run(){
    logger.log(this + " thread started");
    try {
      Thread.sleep (rand.nextInt(1000));
      // logger.log(this + " step1");
      menu_item item = m.get_next_order();
      cnt_mutex.acquire();
      num_order_requested++;
      cnt_mutex.release();
      logger.log(this + " ordered " + item);
      logger.log(this + "cunstomer orders so far: " + num_order_requested + " served so far: " + num_order_served);
      item.owner = this;
      // logger.log(this + " step3");
      fifo.add_order_item(item);
      // logger.log(this + " step4");
      mutex.acquire();
      logger.log(this + "order is ready");
      cnt_mutex.acquire();
      num_order_served ++;
      cnt_mutex.release();
      logger.log(this + "cunstomer orders so far: " + num_order_requested + " served so far: " + num_order_served);
    } catch (InterruptedException e) {}
  }
  public String toString() { return ("customer_" + id); } 
  public static int get_num_order_req() { return num_order_requested; }
  public static int get_num_order_served() { return num_order_served; }
}
