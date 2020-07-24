#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <sys/wait.h>


/**
 * pipe stdout to stdin and read from stdin  
 *
 */

int main(int argc, char **argv)
{
  int pipefd[2];
  int pid;
  int pid2;
  char *ls_args[] = {"ls", "-l", NULL};

  pipe(pipefd);

  if (fork() == 0)
  {
      close(pipefd[0]);    // close reading end in the child

      dup2(pipefd[1], 1);  // send stdout to the pipe
      dup2(pipefd[1], 2);  // send stderr to the pipe

      close(pipefd[1]);    // this descriptor is no longer needed

      // exec(...);
      execvp("ls", ls_args);
  }
  else
  {
      // parent

      // char buffer[1024];
      char *buffer = (char *)malloc(1024);
      char **ptr = &buffer;
      size_t bufsize = 1024;
      int rdSize;

      close(pipefd[1]);  // close the write end of the pipe in the parent
      dup2(pipefd[0], 0); // redirect to stdin
      close(pipefd[0]);

      while (rdSize = getline(&buffer, &bufsize, stdin) > 0)
      {
        printf("buffer %s", buffer);
      }
  }
}
