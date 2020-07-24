class Ambulance extends Thread { // the Class for the Ambulance thread

  private int id;
  private int port;
  private Ferry fry;
  Logger logger;

  public Ambulance(int id, int prt, Ferry ferry, Logger logger)
  {
    this.port = prt;
    this.fry = ferry;
    this.id = id;
    this.logger = logger;
  }

  public void run() {
     while (true) {
      // Attente
      try {sleep((int) (1000*Math.random()));} catch (Exception e) { break;}
      System.out.println("Ambulance " + id + " arrives at port " + port);
  
      // Board
      try{
        Ferry.loadingSemaphores.get(port).acquire();
      }
      catch(Exception e)
      {

      }
      // If no permit is available then the current thread becomes disabled for thread scheduling purposes and lies dormant until one of two things happens:
      // Some other thread invokes the release() method for this semaphore and the current thread is next to be assigned a permit; or
      // Some other thread interrupts the current thread.
      if(!fry.running) break;
      System.out.println("Ambulance " + id + " boards the ferry at port " + port);
      logger.check (fry.getPort() == port, "error loading at wrong port");
      fry.loadAmbulance();  // increment the load
      try {
        Ferry.sailSemaphore.acquire();
      }
      catch(Exception e){}
      // Arrive at the next port
      port = 1 - port ;   
      
      //Disembarkment
      try{
        Ferry.unloadingSemaphores.get(port).acquire();
      }
      catch(Exception e)
      {
        
      } 
      System.out.println("Ambulance " + id + " disembarks the ferry at port " + port);
      logger.check(fry.getPort() == port, "error unloading at wrong port");
      fry.unloadAmbulance();   // Reduce load
      // Terminate
      if(isInterrupted()) break;
    }
    System.out.println("Ambulance " + id + " terminated.");
  }
}
