
#include <stdio.h>
#include <stdlib.h> 
#include <signal.h> // sigaction(), sigsuspend(), sig*()
#include <unistd.h> // alarm()
#include <string.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <sys/types.h>
#include "tic_tac_toe.h"


/* Usage example
 * 
 * First, compile and run this program:
 *     $ gcc np_tic_tac_toe.c -o np_tic_tac_toe
 *     if not already created, create a named pipe (FIFO)
 *     $ mkfifo my_pipe 
 *
 *     In two separe shells run:
 *     $ ./np_tic_tac_toe X 
 *     $ ./np_tic_tac_toe O 
 * 
 *
 */

bool chk_file_exists (char *filename) {
  struct stat buffer;   
  return (stat (filename, &buffer) == 0);
}
 
int main(int argc, char **argv) {
  char player;
  if (argc != 2) {
    printf ("Usage: sig_tic_tac_toe [X|O] \n");
    return (-1);
  }
  player = argv[1][0];
  if (player != 'X' && player != 'O') {
    printf ("Usage: player names must be either X or Y");
    return (-2);
  }

  char tic_named_pipe[128] = "my_pipe";
  if(!chk_file_exists(tic_named_pipe)) {
    printf("You need to create a named pipe called my_pipe first.\n");
    return (-3);
  }

  const char turns[2] = {'X', 'O'};
  // player X will start first.
  int turn_counts = 0;

  tic_tac_toe gameplay;
  // while the game is ongoing
  while(gameplay.game_result() == '-')
  {
    // it is my turn to move
    if(turns[turn_counts % 2] == player)
    {
      gameplay.get_player_move(player);
      const char *message = gameplay.convert2string();
      int fd = open(tic_named_pipe, O_WRONLY);
      write(fd, message, strlen(message) + 1);
      close(fd);
    }
    // otherwise wait opponent.
    else
    {
      int fd = open(tic_named_pipe, O_RDONLY);
      char message[16];
      read(fd, message, sizeof(message));
      close(fd);
      gameplay.set_game_state(message);
      gameplay.display_game_board();
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
  
  return (0);
}
