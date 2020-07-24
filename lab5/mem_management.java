import java.util.concurrent.Semaphore;

enum search_strtegy {
  FIRST_FIT, NEXT_FIT, BEST_FIT, WORST_FIT
}

class mem_segment {
  public int start_addr;
  public int end_addr;
  mem_segment next;
  int get_size() { return end_addr - start_addr; }
  public String toString(){ 
    String str = String.format("mem_segment (0x%x - 0x%x)", start_addr, end_addr);
    return str;
  }
}

class mmu_monitor {
  public mem_segment head;
  public mem_segment current;
  public mem_segment benchmark = head;
  private Semaphore mmu_mutex;
  private search_strtegy strategy;
  private Logger logger;

  mmu_monitor (search_strtegy _strategy, Logger _log) {
    mmu_mutex = new Semaphore(1);
    strategy = _strategy;
    logger = _log;
    head = new mem_segment();
    head.start_addr = 0;
    head.end_addr = 0x400000; // 4 GB
  }
  public String toString(){ 
    String str = "MMU: ";
    mem_segment m = head;
    while (m != null) {
      str += String.format("mem_segment (0x%x - 0x%x)->", m.start_addr, m.end_addr);
      m = m.next;
    }
    str += "|||.";
    return str;
  }
  public mem_segment allocate(int size) {
    mem_segment s = null;
    try {
      mmu_mutex.acquire();
      switch (strategy) {
        case FIRST_FIT: s = allocate_first_fit(size); break;
        case NEXT_FIT: s = allocate_next_fit(size); break;
        case BEST_FIT: s = allocate_best_fit(size); break;
        case WORST_FIT: s = allocate_worst_fit(size); break;
      }
    } catch (InterruptedException e) {}
    if (s != null) logger.log(String.format("%s ", this));
    mmu_mutex.release();
    check();
    return s;
  }
  public boolean check() {
    boolean err= false;
    mem_segment left;
    mem_segment right;
    try {
      mmu_mutex.acquire();
      left = head;
      right = head.next;
      if (left.start_addr >= left.end_addr) err = true;
      while (right != null) {
        if (right.start_addr >= right.end_addr) { err = true; break; }
        if (left.end_addr >= right.start_addr) { err= true; break; }
        left = right;
        right = right.next;
      }
    } catch (InterruptedException e) {}
    if (err) logger.check(false, String.format("%s ", this));
    mmu_mutex.release();
    return err;
  }
  public void free (mem_segment segment) {
    check();
    try{
      mmu_mutex.acquire();
      current = head;
      if(current == null){
        current = segment;
      }
      //case1
      else if (segment.end_addr < head.start_addr){
        segment.next = head;
        head = segment;
      }
      //case2
      else if (segment.end_addr == head.start_addr){
        head.start_addr = segment.start_addr;
      }
      //segment is after head but head = tail
      else if (current.next == null && segment.start_addr == current.end_addr){
        current.end_addr = segment.end_addr;
      }
      else if (current.next == null && segment.start_addr > current.end_addr){
        current.next = segment;
        segment.next = null;
      }
      //segment is after the head and head != tail
      else{
        System.out.println(current);
        System.out.print(segment);
        while(current.next.start_addr < segment.start_addr && current.next.next != null){
          current = current.next;
        }
        if (current.next.next == null && segment.start_addr == current.next.end_addr){
          //case7
          current.next.end_addr = segment.end_addr;
        }
        else if (current.next.next == null && segment.start_addr > current.next.end_addr){
          //case8
          current.next.next = segment;
          segment.next = null;
        }
        //case 3
        else if (segment.start_addr == current.end_addr && segment.end_addr < current.next.start_addr){
          current.end_addr = segment.end_addr;
        }
        //case 4
        else if (segment.start_addr > current.end_addr && segment.end_addr < current.next.start_addr){
          segment.next = current.next;
          current.next = segment;
        }
        //case 5
        else if (segment.start_addr > current.end_addr && segment.end_addr == current.next.start_addr){
          current.next.start_addr = segment.start_addr;
        }
        //case6
        else{
          current.end_addr = current.next.end_addr;
          current.next = current.next.next;
        }
      }
      benchmark = current;
    }catch (InterruptedException e) {}
    mmu_mutex.release();
    logger.log(String.format("%s ", this));
  }
  public mem_segment allocate_first_fit(int size) {
    mem_segment s;
    s = head;
    while (s != null && s.get_size() < size) s = s.next;
    if (s != null) {
      current = s;
      s = new mem_segment();
      s.start_addr = current.start_addr;
      s.end_addr = s.start_addr + size;
      current.start_addr = s.end_addr;
      if (current.get_size() == 0) { // delete current
        if (current == head) {
          if (head.next == null) {
            head.start_addr = 0;
            head.end_addr = 0;
          }
          else {
            head = head.next;
          }
        }
        else {
          mem_segment tmp = head;
          while (tmp.next != current) tmp = tmp.next;
          tmp.next = current.next;  // remove current from the linked list
        }
      }
    }
    return s;
    }
  public mem_segment allocate_next_fit(int size) {
    mem_segment s;
    if (benchmark == null) benchmark = head;
    if (head == null) return null;
    s = benchmark;
    while (s != null && s.get_size() < size){
      s = s.next;
    }
    if (s == null){
      s = head;
      if ( benchmark == head) return null;
      while (s != benchmark && s.get_size() < size){
        s = s.next;
      }
      if (s == benchmark){
        return null;
      }
    }
    if (s != null) {
      current = s;
      benchmark = current.next;
      s = new mem_segment();
      s.start_addr = current.start_addr;
      s.end_addr = s.start_addr + size;
      current.start_addr = s.end_addr;
      if (current.get_size() == 0) { // delete current
        if (current == head) {
          if (head.next == null) {
            head.start_addr = 0;
            head.end_addr = 0;
          }
          else {
            head = head.next;
          }
        }
        else {
          mem_segment tmp = head;
          while (tmp.next != current) tmp = tmp.next;
          tmp.next = current.next;  // remove current from the linked list
        }
      }
    }
    return s;
  }

  public mem_segment allocate_best_fit(int size) {
    mem_segment bestnode = current = head;
    int bestsize = 0x800000;
    if(bestnode.get_size() > size)
    {
      bestsize = bestnode.get_size();
    }
    while (current != null){
      if (current.get_size() >= size && current.get_size() < bestsize){
        bestsize = current.get_size();
        bestnode = current;
      }
      current = current.next;
    }
    //s has the minimal size
    if(bestnode.get_size() < size) return null;
    mem_segment s = new mem_segment();
    s.start_addr = bestnode.start_addr;
    s.end_addr = s.start_addr + size;
    bestnode.start_addr = s.end_addr;
    if (bestnode.get_size() == 0) { // delete current
      if (bestnode == head) {
        if (head.next == null) {
          head.start_addr = 0;
          head.end_addr = 0;
        }
        else {
          head = head.next;
        }
      }
      else {
        mem_segment tmp = head;
        while (tmp.next != bestnode) tmp = tmp.next;
        tmp.next = bestnode.next;  // remove current from the linked list
      }
    }
    return s;
  }


  public mem_segment allocate_worst_fit(int size) {
    mem_segment bestnode = current = head;
    int bestsize = 0x800000;
    if(bestnode.get_size() > size)
    {
      bestsize = bestnode.get_size();
    }
    while (current != null){
      if (current.get_size() > bestsize){
        bestsize = current.get_size();
        bestnode = current;
      }
      current = current.next;
    }
    //s has the maximal size
    if(bestnode.get_size() < size) return null;
    mem_segment s = new mem_segment();
    s.start_addr = bestnode.start_addr;
    s.end_addr = s.start_addr + size;
    bestnode.start_addr = s.end_addr;
    if (bestnode.get_size() == 0) { // delete current
      if (bestnode == head) {
        if (head.next == null) {
          head.start_addr = 0;
          head.end_addr = 0;
        }
        else {
          head = head.next;
        }
      }
      else {
        mem_segment tmp = head;
        while (tmp.next != bestnode) tmp = tmp.next;
        tmp.next = bestnode.next;  // remove current from the linked list
      }
    }
    return s;
  }
}