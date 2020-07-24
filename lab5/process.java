import java.util.Random;

class process implements Runnable{ 
  private Logger logger;
  private int id;
  Random rand; 
  mmu_monitor mmu;

  public process(int _id, Logger _log, mmu_monitor _mmu) {
    logger = _log;
    id = _id;
    mmu = _mmu;
    rand = new Random();
  }
  public String toString() { return ("process_" + id); } 
  public void run(){
    int sz = (rand.nextInt(500) + 1) * 1024; // 1-500 KB
    logger.log(this + " started");
    try {
      Thread.sleep (rand.nextInt(1000));
      //logger.log(this + String.format(" allocating 0x%x Bytes ... ", sz));
      mem_segment s;
      do { 
        Thread.sleep (rand.nextInt(10)); 
        s = mmu.allocate(sz);
      } while (s == null);
      logger.log(this + String.format(" allocated 0x%x Bytes ... ", sz) + s);
      Thread.sleep (rand.nextInt(10000));
      logger.log(this + String.format(" freeing 0x%x Bytes ... ", sz) + s);
      mmu.free(s);
    } catch (InterruptedException e) {}
  }
}

class checker implements Runnable{ 
  private Logger logger;
  mmu_monitor mmu;

  public checker(Logger _log, mmu_monitor _mmu) {
    logger = _log;
    mmu = _mmu;
  }
  public void run(){
    logger.log("checker started");
    try {
      Thread.sleep (1000);
      mmu.check();
    } catch (InterruptedException e) {}
  }
}