import java.util.concurrent.Semaphore;
//import sun.security.util.Length;
import java.util.LinkedList; 
import java.util.Queue; 
import java.util.Iterator;
import java.util.HashMap;


class kitchen {
  private Semaphore [] ingredient_semaphores;
  private Logger logger;
  Ingredient [] ingredient_list;
  private Semaphore acuquire_ingredient_semaphore;

  HashMap<Integer, Semaphore> chef_mutexes= new HashMap<Integer, Semaphore>();
  HashMap<Integer, menu_item> menus = new HashMap<Integer, menu_item>();

  public kitchen (Logger _log) {
    logger = _log;
    ingredient_list = Ingredient.values();
    ingredient_semaphores = new Semaphore[ingredient_list.length];
    for (int i=0; i<ingredient_list.length; i++) ingredient_semaphores[i] = new Semaphore(1);
    acuquire_ingredient_semaphore = new Semaphore(1);
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

  public boolean check_ingredient(Ingredient ingredient){
    int idx = 0;
    for (int i = 0; i < ingredient_list.length; i++){
      if (ingredient_list[i] == ingredient){
        idx = i;
        break;
      }
    }
    if (ingredient_semaphores[idx].availablePermits() > 0){
      return true;
    }
    return false;
  }

  public boolean check_recipe_ingredients(menu_item recipe){
    boolean flag = true;
    Ingredient ingredient = recipe.get_next_ingredient();
    while(ingredient != null && flag == true){
      flag = check_ingredient(ingredient);
      ingredient = recipe.get_next_ingredient();
    }
    recipe.reset_iterator();
    return flag;
  }
  public void acquire_recipe_ingredients(menu_item recipe, int chef){
    Ingredient ingredient = null;
    try{
      acuquire_ingredient_semaphore.acquire();
      while(!check_recipe_ingredients(recipe))
      {
        this.menus.put(chef, recipe);
        this.chef_mutexes.put(chef, new Semaphore(0));
        acuquire_ingredient_semaphore.release();
        this.chef_mutexes.get(chef).acquire();
        acuquire_ingredient_semaphore.acquire();
      }
      recipe.reset_iterator();
      do {
        ingredient = recipe.get_next_ingredient();
        logger.log("chef " + chef + " requesting " + ingredient + " from kitchen ...");
        this.get_ingredient(ingredient);
        logger.log("chef " + chef + " got " + ingredient + " from kitchen");
      } while (ingredient != null);
      acuquire_ingredient_semaphore.release();
      recipe.reset_iterator();
    } catch(Exception e) {}
  }
  synchronized public void release_recipe_ingredients(menu_item recipe, int chef){
    Ingredient ingredient;
    do {
      ingredient = recipe.get_next_ingredient();
      logger.log("chef " + chef + " returning " + ingredient + " to kitchen ...");
      this.return_ingredient(ingredient);
      logger.log("chef " + chef + " returned " + ingredient + " to kitchen");
    } while (ingredient != null);
    
    for(int chefid : this.menus.keySet())
    {
      try{
      menu_item recipe_check = this.menus.get(chefid);
      acuquire_ingredient_semaphore.acquire();

      if(chefid != chef && !check_recipe_ingredients(recipe_check))
      {
        acuquire_ingredient_semaphore.release();
        continue;
      }
      if(this.chef_mutexes.get(chefid).getQueueLength() > 0)
      {
        this.chef_mutexes.get(chefid).release();
      }
      acuquire_ingredient_semaphore.release();
      }catch(Exception e){}
    }
  }
}