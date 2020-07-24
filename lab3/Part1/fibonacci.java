import java.util.Random;
import java.util.concurrent.Semaphore;

interface fibonacci_seq{
    public double next_item();
}

class classic_fib implements fibonacci_seq
{
  double a = 0;
  double b = 1;
  public  double next_item() {
    final double val = a + b;
    a = b;
    b = val;
    System.out.println("next item: " + val);
    return val;
  }
}

class fib_class implements fibonacci_seq {
  static double a = 0;
  static double b = 1;
  static Semaphore sema = new Semaphore(1);
  Random rand;

  fib_class() {
    rand = new Random(); // instance of random class
  }

  public double next_item() {
    final int upperbound = 100;
    try {
      Thread.sleep(rand.nextInt(upperbound));
    } catch (final InterruptedException ie) {
    }

    try {
      sema.acquire();
    } catch (InterruptedException exc){
      System.out.println(exc);
    }
    final double val = a + b;

    try {
      Thread.sleep(rand.nextInt(upperbound));
    } catch (final InterruptedException ie) {
    }

    a = b;
    b = val;
    System.out.println("next item: " + val);
    sema.release();
  
    return val;
  }
}

class fibonacci_thrd implements Runnable {
  fibonacci_seq fib;
  int id;

  fibonacci_thrd(final fibonacci_seq _fib, final int _id) {
    fib = _fib;
    id = _id;
  }

  public void run() {
    System.out.println("thrd " + id + " output: " + fib.next_item());
  }
}

public class fibonacci {
  public static void main(final String args[]) {
    final fibonacci_seq seq = new fib_class();
    final classic_fib seq1 = new classic_fib();
    for (int i = 0; i < 100; i++) {
      seq1.next_item();
    }

    for (int i = 0; i < 100; i++) {
      final Runnable fb = new fibonacci_thrd(seq, i);
      final Thread thrd = new Thread(fb);
      thrd.start();
    }

    System.out.println("main done");
  }
}

