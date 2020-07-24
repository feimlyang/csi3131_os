import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.Scanner;

public class FerryApp
{
  // Configuration
  final static int PORT0 = 0;
  final static int PORT1 = 1;
  final static int MAXLOAD = 5;
  static Logger logger;

  public static void main(String args[]) {
     int NUM_CARS = 10;
     int NUM_AMBS = 2;
     int NUM_CROSSING = 20;
    int i;
    
    System.out.println ("num args: " + args.length);
    if (args.length > 0) {
      try {
        NUM_CROSSING = Integer.parseInt(args[0]);
      } catch (NumberFormatException e) {}
    }
    if (args.length > 1) {
      try {
        NUM_CARS = Integer.parseInt(args[1]);
      } catch (NumberFormatException e) {}
    }
    if (args.length > 2) {
      try {
        NUM_AMBS = Integer.parseInt(args[2]);
      } catch (NumberFormatException e) {}
    }
    logger = new assertLooger();

    Ferry ferry = new Ferry(PORT0,NUM_CROSSING, logger);

    Auto [] automobile = new Auto[NUM_CARS];
    for (i=0; i<MAXLOAD || i<NUM_CARS/2; i++) automobile[i] = new Auto(i, PORT0, ferry, logger);
    for ( ; i<NUM_CARS ; i++) automobile[i] = new Auto(i, PORT1, ferry, logger);

    // Ambulance ambulance = new Ambulance(PORT0,ferry);
    Ambulance [] ambulance = new Ambulance[NUM_AMBS];
    for (i=0; i< NUM_AMBS; i++) ambulance[i] = new Ambulance(i, (PORT0+i) % 2,ferry, logger);

    /* Start the threads */
    ferry.start();   // Start the ferry thread.
    for (i=0; i<NUM_CARS; i++) automobile[i].start();  // Start automobile threads
    for (i=0; i<NUM_AMBS; i++) ambulance[i].start();  // Start the ambulance thread.

    try {ferry.join();} catch(InterruptedException e) { }; // Wait until ferry terminates.
    System.out.println("Ferry stopped.");
    // Stop other threads.
    for (i=0; i<NUM_CARS; i++) automobile[i].interrupt(); // Let's stop the auto threads.
    for (i=0; i<NUM_AMBS; i++) ambulance[i].interrupt(); // Stop the ambulance thread.
  }
}




