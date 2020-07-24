#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <stdlib.h>
#include <sys/wait.h>
#include <fcntl.h>

/**
 * pipe stdout to stdin and read from stdin  
 *
 */

int main(int argc, char **argv)
{
  int pipefd[2];
  // NULL-terminated required
  char *tee_args[] = {"tee", argv[1], NULL};

  pipe(pipefd);
  if (fork() == 0)
  {
      close(pipefd[0]);    // close reading end in the child
      // pass std output to its parent process.
      dup2(pipefd[1], 1);  // send stdout to the pipe
      dup2(pipefd[1], 2);  // send stderr to the pipe
      close(pipefd[1]);    // this descriptor is no longer needed
      // exec(...);
      execvp("tee", tee_args);
  }
  else
  {
      // parent process
      // automatic storage is feasible
      char buffer[2048];
      // for the first parameter of getline.
      char *buffer_start = &buffer[0];
      size_t bufsize = 2048;
      int rdSize = -1;
      int line_no = 1;
      close(pipefd[1]);  // close the write end of the pipe in the parent
      dup2(pipefd[0], 0); // redirect to stdin.
      close(pipefd[0]);
      while ((rdSize = getline(&buffer_start, &bufsize, stdin)) > 0)
      {
        printf("%d %s", line_no++, buffer);
      }
  }
  return 0;
}