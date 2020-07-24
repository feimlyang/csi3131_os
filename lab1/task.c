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
#include <unistd.h>
#include <stdlib.h> 
#include <sys/wait.h>
#include <sys/stat.h>
#include <pthread.h>

char ID;
char *cfg_fname;
pthread_t rcv_thread_id;

/* Prototypes */
void* readMsg(void *args);

/*-------------------------------------------------------------
Function: main
Parameters:
    int ac - number of arguments on the command line
    char **av - array of pointers to the arguments
Description:
    
-------------------------------------------------------------*/


int main(int argc, char **argv)
{
  int num_sec = 10;
  // for (int i=0; i<argc; i++) printf ("%s ", argv[i]); printf("\n");
  if (argc > 2) num_sec = atoi(argv[2]);
  printf ("%s %s start ... will end in %d sec  \n", argv[0], argv[1], num_sec);
  sleep(num_sec);
  printf ("%s %s end\n", argv[0], argv[1]);
  return(0);  // All is done.
}

