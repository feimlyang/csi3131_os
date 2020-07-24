import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.Scanner;

public class restaurantApp
{
  
  public static void main(String args[]) {
    int NUM_CUSTOMERS = 500;
    int ORDER_FIFO_SIZE = 50;
    int CHEF_FIFO_SIZE = 5;
    int customers_served = 0;
    Logger logger = new assertLooger();
    menu the_menu = new menu();
    
    System.out.println ("num args: " + args.length);
    if (args.length > 0) {
      try {
        NUM_CUSTOMERS = Integer.parseInt(args[0]);
      } catch (NumberFormatException e) {}
    }
    if (args.length > 1) {
      try {
        ORDER_FIFO_SIZE = Integer.parseInt(args[1]);
      } catch (NumberFormatException e) {}
    }
    if (args.length > 2) {
      try {
        CHEF_FIFO_SIZE = Integer.parseInt(args[2]);
      } catch (NumberFormatException e) {}
    }
    order_fifo o_fifo = new order_fifo(ORDER_FIFO_SIZE, logger);
    dispatcher the_dispatcher = new dispatcher(logger, o_fifo, CHEF_FIFO_SIZE);
    customer[] customers = new customer[NUM_CUSTOMERS];
    for (int i=0; i<NUM_CUSTOMERS; i++) customers[i] = new customer(i, logger, the_menu, o_fifo);
    
    logger.log ("starting application threads, number of customers: " + NUM_CUSTOMERS + 
                ", order fifo size: " + ORDER_FIFO_SIZE + ", chef fifo size: " + CHEF_FIFO_SIZE);
    // start all threads
    Thread thread;
    thread = new Thread (the_dispatcher);
    thread.start();
    for (int i=0; i<the_dispatcher.chefs.length; i++) {
      thread = new Thread (the_dispatcher.chefs[i]);
      thread.start();
    }
    for (int i=0; i<customers.length; i++) {
      thread = new Thread (customers[i]);
      thread.start();
    }
    customers_served = customer.get_num_order_served();
    while (customers_served < NUM_CUSTOMERS) {
      logger.log ("main: customer served " + customer.get_num_order_served());
      try { Thread.sleep (1000); }
      catch (InterruptedException e) {}
      customers_served = customer.get_num_order_served();
    }
    // exit
    logger.log ("all customers are served, exiting ... ");
    System.exit(0);
  }
}




