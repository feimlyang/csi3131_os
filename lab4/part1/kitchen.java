import java.util.concurrent.Semaphore;
import java.util.LinkedList; 
import java.util.Queue; 
import java.util.Iterator;

class kitchen {
  private Semaphore [] ingredient_semaphores;
  private Logger logger;
  Ingredient [] ingredient_list;
  public kitchen (Logger _log) {
    logger = _log;
    ingredient_list = Ingredient.values();
    ingredient_semaphores = new Semaphore[ingredient_list.length];
    for (int i=0; i<ingredient_list.length; i++) ingredient_semaphores[i] = new Semaphore(1);
  }
  public void get_ingredient(Ingredient ingredient){
    int indx = -1;
    for (int i=0; i<ingredient_list.length; i++) {
      if (ingredient_list[i] == ingredient) {
        indx = i;
        break;
      }
    }
    logger.check(indx >= 0, "could not find ingredient in the list!");
    if (indx >= 0) {
      try { 
        ingredient_semaphores[indx].acquire();
      } catch (InterruptedException e) {}
    }
  }
  public void return_ingredient(Ingredient ingredient){
    int indx = -1;
    for (int i=0; i<ingredient_list.length; i++) {
      if (ingredient_list[i] == ingredient) {
        indx = i;
        break;
      }
    }
    logger.check(indx >= 0, "could not find ingredient in the list!");
    if (indx >= 0) {
      logger.log(ingredient + " is being released");
      ingredient_semaphores[indx].release();
    }
  }
}