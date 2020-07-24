public class mmuApp
{
  
  public static void main(String args[]) {
    Logger logger = new assertLooger();
    mmu_monitor mmu;
    search_strtegy[] strategies = search_strtegy.values();
    int indx = 0;
    int num_processes = 100;
    
    System.out.println ("num args: " + args.length);
    if (args.length > 0) {
      try {
        indx = Integer.parseInt(args[0]);
      } catch (NumberFormatException e) { indx = 0; }
    }
    System.out.println (indx);
    mmu = new mmu_monitor (strategies[indx], logger);
    
    if (args.length > 1) {
      try {
        num_processes = Integer.parseInt(args[1]);
      } catch (NumberFormatException e) { num_processes = 100; }
    }
    checker chk = new checker (logger, mmu);
    Thread thrd = new Thread(chk);
    thrd.start();
    for (int i=0; i<num_processes; i++) {
      process p = new process (i, logger, mmu);
      thrd = new Thread(p);
      thrd.start();
    }
  }
}