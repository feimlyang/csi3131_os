/**
 * More info?
 * a.dotreppe@aspyct.org
 * http://aspyct.org
 * @aspyct (twitter)
 *
 * Hope it helps :)
 */
// $ kill -l
 // 1) SIGHUP       2) SIGINT       3) SIGQUIT      4) SIGILL       5) SIGTRAP
 // 6) SIGABRT      7) SIGEMT       8) SIGFPE       9) SIGKILL     10) SIGBUS
// 11) SIGSEGV     12) SIGSYS      13) SIGPIPE     14) SIGALRM     15) SIGTERM
// 16) SIGURG      17) SIGSTOP     18) SIGTSTP     19) SIGCONT     20) SIGCHLD
// 21) SIGTTIN     22) SIGTTOU     23) SIGIO       24) SIGXCPU     25) SIGXFSZ
// 26) SIGVTALRM   27) SIGPROF     28) SIGWINCH    29) SIGPWR      30) SIGUSR1
// 31) SIGUSR2     32) SIGRTMIN    33) SIGRTMIN+1  34) SIGRTMIN+2  35) SIGRTMIN+3
// 36) SIGRTMIN+4  37) SIGRTMIN+5  38) SIGRTMIN+6  39) SIGRTMIN+7  40) SIGRTMIN+8
// 41) SIGRTMIN+9  42) SIGRTMIN+10 43) SIGRTMIN+11 44) SIGRTMIN+12 45) SIGRTMIN+13
// 46) SIGRTMIN+14 47) SIGRTMIN+15 48) SIGRTMIN+16 49) SIGRTMAX-15 50) SIGRTMAX-14
// 51) SIGRTMAX-13 52) SIGRTMAX-12 53) SIGRTMAX-11 54) SIGRTMAX-10 55) SIGRTMAX-9
// 56) SIGRTMAX-8  57) SIGRTMAX-7  58) SIGRTMAX-6  59) SIGRTMAX-5  60) SIGRTMAX-4
// 61) SIGRTMAX-3  62) SIGRTMAX-2  63) SIGRTMAX-1  64) SIGRTMAX


#include <stdio.h>
#include <stdlib.h> 
#include <signal.h> // sigaction(), sigsuspend(), sig*()
#include <unistd.h> // alarm()
#include <string.h>

#include "tic_tac_toe.h"

void handle_signal(int signal);
void handle_sigalrm(int signal);
void do_sleep(int seconds);

bool opponent_moved = false;

/* Usage example
 * 
 * First, compile and run this program:
 *     $ gcc signal.c
 *     $ ./a.out
 * 
 * It will print out its pid. Use it from another terminal to send signals
 *     $ kill -HUP <pid>
 *     $ kill -USR1 <pid>
 *     $ kill -ALRM <pid>
 *
 * Exit the process with ^C ( = SIGINT) or SIGKILL, SIGTERM
 */
int main(int argc, char **argv) {
  struct sigaction sa;
  char player;
  FILE *inFile, *outFile;
  char buffer1[128] = {0}, buffer2[128] = {0};
  char x_file_name[] = "xmove.txt";
  char o_file_name[] = "omove.txt";
  char *my_filename = NULL, *oponent_filename = NULL;
  int my_pid = -1, oponent_pid = -1;
  int size = 0;

  if (argc != 2) {
    printf ("Usage: sig_tic_tac_toe [X|O] \n");
    return (-1);
  }
  player = argv[1][0];
  if (player != 'X' && player != 'O') {
    printf ("Usage: player names must be either X or Y");
    return (-2);
  }
  if (player == 'X') {
    my_filename = x_file_name;
    oponent_filename = o_file_name;
  }
  else {
    my_filename = o_file_name;
    oponent_filename = x_file_name;
  }
  // Setup the sighub handler
  sa.sa_handler = &handle_signal;

  // Restart the system call, if at all possible
  sa.sa_flags = SA_RESTART;

  // Block every signal during the handler
  sigfillset(&sa.sa_mask);

  // Intercept SIGUSR1 and SIGINT
  if (sigaction(SIGUSR1, &sa, NULL) == -1) {
      perror("Error: cannot handle SIGUSR1"); // Should not happen
  }

  if (sigaction(SIGINT, &sa, NULL) == -1) {
      perror("Error: cannot handle SIGINT"); // Should not happen
  }
  // Print pid, so that we can send signals from other shells
  my_pid = getpid();
  printf("My pid is: %d\n", my_pid);
  
  outFile = fopen (my_filename, "w");
  fprintf (outFile, "%d", my_pid);
  fclose(outFile);
  
  printf ("trying to open input file:\n .");
  do {
    sleep(1);
    inFile = fopen (oponent_filename, "r");
  } while (inFile == NULL);
  
  fgets( buffer1, 128, inFile );
  oponent_pid = atoi (buffer1);
  printf (" done. \noponent_pid %d \n", oponent_pid);
  fclose(inFile);
  
  tic_tac_toe gameplay;
  const char turns[2] = {'X', 'O'};
  int turn_counts = 0;

  // sleep 2 seconds for each player to be ready
  sleep(2);

  // while game is still ongoing
  while(gameplay.game_result() == '-') {
    // it is my turn
    if(turns[turn_counts % 2] == player)
    {
      printf ("The current game state is: \n");
      gameplay.display_game_board();
      gameplay.get_player_move(player);
      const char *message = gameplay.convert2string();
      // write the move to my_file
      outFile = fopen (my_filename, "w");
      fprintf (outFile, "%s", message);
      fclose(outFile);
      // notify the opponent that the move has been placed
      kill(oponent_pid, SIGUSR1);
    }
    // otherwise wait opponent's move
    else
    {
      while(!opponent_moved) 
        sleep(1);
      inFile = fopen (oponent_filename, "r");
      fgets(buffer2, 128, inFile);
      gameplay.set_game_state(buffer2);
      printf ("The current game state is: \n");
      gameplay.display_game_board();
      fclose(inFile);
      opponent_moved = false;
    }
    ++turn_counts;
  }
  char result = gameplay.game_result();
  if(result == 'X')
  {
    printf("X wins\n");
  }
  else if(result== 'O')
  {
    printf("O wins\n");
  }
  else if(result == 'd')
  {
    printf("It is a draw game.\n");
  }
  gameplay.display_game_board();

  // for (;;) {
      // printf("\nSleeping for ~3 seconds\n");
      // sleep(3); // Later to be replaced with a SIGALRM
  // }

  remove (oponent_filename);  // need to remove file for next time we run the game
  return 0;
}

void handle_signal(int signal) {
  if (signal == SIGUSR1){
    opponent_moved = true;
  }
  else if (signal == SIGINT) exit(0); // CTRL C exit
  else return;
}

