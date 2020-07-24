import java.util.Random;

class pasta1 extends menu_item{
  public pasta1(){
    add_ingredient(Ingredient.SPAGHETTI);
    add_ingredient(Ingredient.GROUND_BEEF);
    add_ingredient(Ingredient.TOMATO_SAUCE);
    add_ingredient(Ingredient.SPICE);
    add_ingredient(Ingredient.GARLIC);
    add_ingredient(Ingredient.SALT);
    add_ingredient(Ingredient.BLACK_PEPPER);
    add_ingredient(Ingredient.ZUCCHINI);
    name = "SPAGHETTI POLONEZ";
  }
}
class pasta2 extends menu_item{
  public pasta2(){
    add_ingredient(Ingredient.PENNE);
    add_ingredient(Ingredient.CHICKEN);
    add_ingredient(Ingredient.ROSE_SAUCE);
    add_ingredient(Ingredient.ZUCCHINI);
    add_ingredient(Ingredient.SPICE);
    add_ingredient(Ingredient.GARLIC);
    add_ingredient(Ingredient.BLACK_PEPPER);
    add_ingredient(Ingredient.SALT);
    name = "CHICKEN PENNE";
  }
}
class pasta3 extends menu_item{
  public pasta3(){
    add_ingredient(Ingredient.LASAGNE);
    add_ingredient(Ingredient.TOMATO_SAUCE);
    add_ingredient(Ingredient.SPINACH);
    add_ingredient(Ingredient.BLACK_PEPPER);
    add_ingredient(Ingredient.SALT);
    add_ingredient(Ingredient.GARLIC);
    add_ingredient(Ingredient.SPICE);
    name = "Veggie LASAGNE";
  }
}
class lamb1 extends menu_item{
  public lamb1(){
    add_ingredient(Ingredient.LAMB);
    add_ingredient(Ingredient.RICE);
    add_ingredient(Ingredient.ZUCCHINI);
    add_ingredient(Ingredient.CARROT);
    add_ingredient(Ingredient.GREEN_PEPPER);
    add_ingredient(Ingredient.GARLIC);
    add_ingredient(Ingredient.BLACK_PEPPER);
    add_ingredient(Ingredient.SALT);
    name = "LAMB1";
  }
}
class lamb2 extends menu_item{
  public lamb2(){
    add_ingredient(Ingredient.RICE);
    add_ingredient(Ingredient.LAMB);
    add_ingredient(Ingredient.BROCCOLI);
    add_ingredient(Ingredient.TOMATO);
    add_ingredient(Ingredient.SALT);
    add_ingredient(Ingredient.GARLIC);
    add_ingredient(Ingredient.GREEN_PEPPER);
    add_ingredient(Ingredient.BLACK_PEPPER);
    name = "LAMB2";
  }
}
class shrimp1 extends menu_item{
  public shrimp1(){
    add_ingredient(Ingredient.RICE);
    add_ingredient(Ingredient.SHRIMP);
    add_ingredient(Ingredient.BUTTER);
    add_ingredient(Ingredient.SALT);
    add_ingredient(Ingredient.GARLIC);
    add_ingredient(Ingredient.GREEN_PEPPER);
    add_ingredient(Ingredient.BLACK_PEPPER);
    name = "SHRIMP";
  }
}
class misc extends menu_item{
  public misc(){
    Random rand = new Random(); 
    Ingredient[] vector = Ingredient.values();
    boolean [] chosen = new boolean[vector.length];
    for (int i=0; i<vector.length; i++) chosen[i] = false;
    int n = rand.nextInt(vector.length-10);
    for (int i=0; i<n; i++) {
      // choose ingredient a random
      int ii = rand.nextInt(vector.length);
      if (chosen[ii]) continue;
      add_ingredient(vector[ii]);
      chosen[ii] = true;
    }
    name = "MISC";
  }
}

class menu {
  Random rand; 
  public menu(){
    rand = new Random();
  }
  menu_item get_next_order(){
    int n = rand.nextInt(10);
    menu_item m;
    switch (n) {
      case 0: m = new pasta1(); break;
      case 1: m = new pasta2(); break;
      case 2: m = new pasta3(); break;
      case 3: m = new lamb1(); break;
      case 4: m = new lamb2(); break;
      case 5: m = new shrimp1(); break;
      default: m = new misc(); break;
    }
    return m;
  }
}


