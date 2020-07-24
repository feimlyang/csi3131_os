/*------------------------------------------------------------
File: stn.c   (CSI3131 Assignment 1)

Student Name: Manlin Guo
Student Number: 6602848

Student Name: Yuejia Cara Li
Student Number: 300049083

Description:  This program creates the station processes
     (A, B, C, and D) and then acts as a Ethernet/802.3 hub.
-------------------------------------------------------------*/
#include <stdio.h>
#include <stdlib.h> 
#include <unistd.h>
#include <sys/wait.h>


/*-------------------------------------------------------------
Function: main
Parameters:
    int ac - number of arguments on the command line
    char **av - array of pointers to the arguments
Description:
-------------------------------------------------------------*/


int main(int argc, char **argv)
{
  int pid1, pid2, pid3, pid;
  int status;
  int time_period[4] = {5,6,7,10};
  char str[128];
  
  if (argc > 1) time_period[0] = atoi(argv[1]);
  if (argc > 2) time_period[1] = atoi(argv[2]);
  if (argc > 3) time_period[2] = atoi(argv[3]);
  if (argc > 4) time_period[3] = atoi(argv[4]);

  pid1 = fork();
  if (pid1 < 0) {
    fprintf(stderr, "Fork Failed");
    exit(-1);
  }
  else if (pid1 == 0) { // child 1
    // printf("in child 1, pid: %d %d %d\n", pid1, pid2, pid3);
    sprintf(str, "%d", time_period[0]);
    execlp ("./task", "task", "pid1", str, NULL);
  }
  pid2 = fork();
  if (pid2 < 0) {
  fprintf(stderr, "Fork Failed");
  exit(-1);
  }
  if (pid2 == 0) { // child 2
    sleep(1);
    // printf("in child 2, pid: %d %d %d\n", pid1, pid2, pid3);
    sprintf(str, "%d", time_period[1]);
    execlp ("./task", "task", "pid2", str, NULL);
  }
  pid3 = fork();
  if (pid3 < 0) {
  fprintf(stderr, "Fork Failed");
  exit(-1);
  }
  if (pid3 == 0) {  // child 3
    sleep(1);
    // printf("in child 3, pid: %d %d %d\n", pid1, pid2, pid3);
    sprintf(str, "%d", time_period[2]);
    execlp ("./task", "task", "pid3", str, NULL);
  }
  // main process
  pid = wait(&status); printf ("pid %d ended\n" ,pid);

  sprintf(str, "./task pid_parent %d", time_period[3]);
  system(str);

  pid = wait(&status); printf ("pid %d ended\n" ,pid);
  pid = wait(&status); printf ("pid %d ended\n" ,pid);

  return(0);  // All is done.
}               

