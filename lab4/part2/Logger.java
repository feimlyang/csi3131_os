// interface
interface Logger {
  public void check(boolean chk, String msg);  
  public void log(String msg); 
}

class displayLooger implements Logger {
  displayLooger(){}
  public void check(boolean chk, String msg){
    if (!chk) System.out.println ("<<<<<<<<<< " + msg + ">>>>>>>>");
  }
  public void log(String msg){
    System.out.println (msg);
  }
}

class assertLooger implements Logger {
  assertLooger(){}
  public void check(boolean chk, String msg){
    assert (chk) : msg;
    if (!chk) System.out.println ("<<<<<<<<<< " + msg + ">>>>>>>>");
  }
  public void log(String msg){
    System.out.println (msg);
  }
}