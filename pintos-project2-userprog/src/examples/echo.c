#include <stdio.h>
#include <syscall.h>

int
main (int argc, char **argv)
{
  int i;

printf("***strt****");
  for (i = 0; i < argc; i++)	
    printf ("%s ", argv[i]);
printf("*****end****");
  printf ("\n");

  return EXIT_SUCCESS;
}
