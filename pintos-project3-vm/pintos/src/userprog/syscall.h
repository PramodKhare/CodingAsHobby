#ifndef USERPROG_SYSCALL_H
#define USERPROG_SYSCALL_H

void syscall_init (void);
void exit_thread (int status);

typedef int pid_t;
#define PID_ERROR ((pid_t) -1)

typedef int mmapid_t;


#endif /* userprog/syscall.h */

