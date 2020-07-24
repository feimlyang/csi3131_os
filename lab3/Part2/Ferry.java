
import java.util.concurrent.Semaphore;
import java.util.ArrayList;

class Ferry extends Thread{ // The ferry Class
  final static int MAXLOAD = 5;
  private int port=0;  // Start at port 0
  private int load=0;  // Load is zero
  private int numCrossings;  // number of crossings to execute
  private boolean ambulance_loaded = false;
  Logger logger;
  public boolean running = false;
  // Semaphores
  private static Semaphore loadingDone = new Semaphore(0);
  private static Semaphore unloadingDone = new Semaphore(0);
  public static ArrayList<Semaphore> loadingSemaphores = new ArrayList<Semaphore>();
  public static ArrayList<Semaphore> unloadingSemaphores = new ArrayList<Semaphore>();
  public static Semaphore sailSemaphore = new Semaphore(0);

  public static void Setup()
  {
    loadingSemaphores.add(new Semaphore(0));
    loadingSemaphores.add(new Semaphore(0));
    unloadingSemaphores.add(new Semaphore(0));
    unloadingSemaphores.add(new Semaphore(0));

  }

  public Ferry(int prt, int nbtours, Logger logger)
  {
    this.port = prt;
    numCrossings = nbtours;
    this.logger = logger;
    Setup();
  }

  public void run() {
    running = true;
    System.out.println("Start at port " + port + " with a load of " + load + " vehicles");

    // numCrossings crossings in our day
    for(int i=0 ; i < numCrossings ; i++) {
      // The crossing
      try{
          loadingSemaphores.get(port).release(1);
          loadingDone.acquire();
      }
      catch(Exception e)
      {};
      System.out.println("Departure from port " + port + " with a load of " + load + " vehicles");
      System.out.println("Crossing " + i + " with a load of " + load + " vehicles");
      if (ambulance_loaded) {
        logger.check(load > 0 && load <= MAXLOAD, "error ferry leaving with less load! ");
      } 
      else {
        logger.check(load == MAXLOAD, "error ferry leaving with less load!, load is " + load + " ");
      }
      port = 1 - port;
      try {sleep((int) (100*Math.random()));} catch (Exception e) { }
      // Arrive at port
      System.out.println("Arrive at port " + port + " with a load of " + load + " vehicles");
      sailSemaphore.release(load);
      try{
        unloadingSemaphores.get(port).release(1);
        unloadingDone.acquire();
      }catch(Exception e)
      {}
      // Disembarkment et loading
    }
    running = false;
  }

  // methodes to manipulate the load of the ferry
  public int getLoad() { return(load); }
  public int getPort() { return(port); }
  public synchronized void addLoad() {
    logger.check(load < MAXLOAD, "error loadig in a full Ferry!");
    load = load + 1; 
    System.out.println ("added load, now " + load);
    if(MAXLOAD == load || ambulance_loaded)
    {
      loadingDone.release();
    }
    else
    {
      loadingSemaphores.get(port).release(1);
    }
  }
  public synchronized void reduceLoad()  {
    logger.check(load > 0, "error unloading an empty Ferry!");
    load = load - 1 ; 
    System.out.println ("removed load, now " + load);
    if(0 == load)
    {
      unloadingDone.release();
    }
    else
    {
      unloadingSemaphores.get(port).release(1);
    }
  }
  public synchronized void loadAmbulance() {
    ambulance_loaded = true;
    addLoad();
  }
  public synchronized void unloadAmbulance(){
    ambulance_loaded = false;
    reduceLoad();
  }
}
