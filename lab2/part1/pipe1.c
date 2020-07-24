#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <sys/wait.h>


/**
 * pipe stdout to a buffer  
 *
 */

int main(int argc, char **argv)
{
  int pipefd[2];
  int pid;
  int pid2;

  pipe(pipefd);

  if (fork() == 0)
  {
      close(pipefd[0]);    // close reading end in the child

      dup2(pipefd[1], 1);  // send stdout to the pipe
      dup2(pipefd[1], 2);  // send stderr to the pipe

      close(pipefd[1]);    // this descriptor is no longer needed

      // exec(...);
      for (char c='a'; c<='z'; c++) printf ("%c", c);
      printf("%c", '\0'); // end of string char 
  }
  else
  {
      // parent

      char buffer[1024];

      close(pipefd[1]);  // close the write end of the pipe in the parent

      while (read(pipefd[0], buffer, sizeof(buffer)) != 0)
      {
        printf("buffer %s", buffer);
      }
  }
}
