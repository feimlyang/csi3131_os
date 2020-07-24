class chef implements Runnable {
  private Logger logger;
  public order_fifo fifo;
  public int id;
  kitchen the_kitchen;
  public chef (int _id, Logger _log, kitchen _kit, int fifo_size) {
    id = _id;
    logger = _log;
    fifo = new order_fifo(fifo_size, logger);
    the_kitchen = _kit; 
  }
  public void run() {
    Ingredient ingredient;
    Ingredient [] ingredient_list = Ingredient.values();
    logger.log ("chef " + id + " thread started");
    while (true) {
      menu_item item = fifo.get_next_item();
      logger.log("chef " + id + " grabbed order " + item + " for " + item.owner);
      item.reset_iterator();
      ingredient = null;
      for (Ingredient ing: ingredient_list){
        do {
          ingredient = item.get_next_ingredient();
          if (ingredient == ing){
            logger.log("chef " + id + " requesting " + ingredient + " from kitchen ...");
            the_kitchen.get_ingredient(ingredient);
            logger.log("chef " + id + " got " + ingredient + " from kitchen");
            //try {Thread.sleep((int) (50*Math.random()) + 2);} catch (Exception e) { }
          }
        } while (ingredient != null);
        item.reset_iterator();
      }
      try {Thread.sleep((int) (50*Math.random()) + 2);} catch (Exception e) { }
      ingredient = null;
      do {
        ingredient = item.get_next_ingredient();
        logger.log("chef " + id + " returning " + ingredient + " to kitchen ...");
        the_kitchen.return_ingredient(ingredient);
        logger.log("chef " + id + " returned " + ingredient + " to kitchen");
      } while (ingredient != null);
      item.owner.mutex.release();
    }
  }
}

class dispatcher implements Runnable {
  private Logger logger;
  private order_fifo fifo;
  public chef[] chefs;
  kitchen the_kitchen;

  public dispatcher(Logger _log, order_fifo _fifo, int chef_fifo_size){
    fifo = _fifo;
    logger = _log;
    the_kitchen = new kitchen(logger);
    chefs = new chef[10];
    for (int i=0; i<10; i++) chefs[i] = new chef(i, logger, the_kitchen, chef_fifo_size);
  }
  public void run() {
    logger.log("dispatcher thread started ...");
    while (true) {
      menu_item item = fifo.get_next_item();
      logger.log("dispatcher grabbed order " + item + " for " + item.owner);
      int chef_indx = 0;
      int min_fill = 100;
      int current_fill;
      for (int i=0; i<10; i++) {
        current_fill = chefs[i].fifo.get_fill_level();
        if (current_fill < min_fill) {
          min_fill = current_fill;
          chef_indx = i;
        }
      }
      chefs[chef_indx].fifo.add_order_item(item);
    }
  }
}
