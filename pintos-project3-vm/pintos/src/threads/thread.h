#ifndef THREADS_THREAD_H
#define THREADS_THREAD_H

#include <debug.h>
#include <list.h>
#include <stdint.h>
#include <hash.h>

#include "threads/synch.h"

/* States in a thread's life cycle. */
enum thread_status
  {
    THREAD_RUNNING,     /* Running thread. */
    THREAD_READY,       /* Not running but ready to run. */
    THREAD_BLOCKED,     /* Waiting for an event to trigger. */
    THREAD_DYING        /* About to be destroyed. */
  };

/* Structure for a file which contains its descriptor
 * Used by system calls.
 */
struct file_structure {
    int fd;
    struct file *file;
    struct list_elem file_elem;
};

/* Structure for a map which has the address of the memory ,
 * file and number of pages associated.
 */
/*struct map_structure {
  int id;                     Id associated with the map
  uint8_t *address;           Start address of the memory map
  struct file *map_file;      File associated to this map
  struct list_elem map_elem;  Map list element
  size_t pages_count;         Count of the mapped pages
};*/

/* Map structure binds a map id to a specific region of memory and a file. */
struct map
{
    struct list_elem elem;      // List element
    struct file *file;          // File
    size_t num_of_pages;        //Number of pages mapped
    int map_id;                 // Mapping id
    uint8_t *start_addr;        //Start of memory mapping
};

/* Set a minimum value for File Descriptor */
#define FD_VALUE 2
#define THREAD_YET_TO_LOAD 0
#define THREAD_LOAD_POSITIVE 1
#define THREAD_LOAD_NEGATIVE 2

/* Thread identifier type.
   You can redefine this to whatever type you like. */
typedef int tid_t;
#define TID_ERROR ((tid_t) -1)          /* Error value for tid_t. */

/* Thread priorities. */
#define PRI_MIN 0                       /* Lowest priority. */
#define PRI_DEFAULT 31                  /* Default priority. */
#define PRI_MAX 63                      /* Highest priority. */

/* A kernel thread or user process.

   Each thread structure is stored in its own 4 kB page.  The
   thread structure itself sits at the very bottom of the page
   (at offset 0).  The rest of the page is reserved for the
   thread's kernel stack, which grows downward from the top of
   the page (at offset 4 kB).  Here's an illustration:

        4 kB +---------------------------------+
             |          kernel stack           |
             |                |                |
             |                |                |
             |                V                |
             |         grows downward          |
             |                                 |
             |                                 |
             |                                 |
             |                                 |
             |                                 |
             |                                 |
             |                                 |
             |                                 |
             +---------------------------------+
             |              magic              |
             |                :                |
             |                :                |
             |               name              |
             |              status             |
        0 kB +---------------------------------+

   The upshot of this is twofold:

      1. First, `struct thread' must not be allowed to grow too
         big.  If it does, then there will not be enough room for
         the kernel stack.  Our base `struct thread' is only a
         few bytes in size.  It probably should stay well under 1
         kB.

      2. Second, kernel stacks must not be allowed to grow too
         large.  If a stack overflows, it will corrupt the thread
         state.  Thus, kernel functions should not allocate large
         structures or arrays as non-static local variables.  Use
         dynamic allocation with malloc() or palloc_get_page()
         instead.

   The first symptom of either of these problems will probably be
   an assertion failure in thread_current(), which checks that
   the `magic' member of the running thread's `struct thread' is
   set to THREAD_MAGIC.  Stack overflow will normally change this
   value, triggering the assertion. */
/* The `elem' member has a dual purpose.  It can be an element in
   the run queue (thread.c), or it can be an element in a
   semaphore wait list (synch.c).  It can be used these two ways
   only because they are mutually exclusive: only a thread in the
   ready state is on the run queue, whereas only a thread in the
   blocked state is on a semaphore wait list. */
struct thread
  {
    /* Owned by thread.c. */
    tid_t tid;                          /* Thread identifier. */
    enum thread_status status;          /* Thread state. */
    char name[16];                      /* Name (for debugging purposes). */
    uint8_t *stack;                     /* Saved stack pointer. */
    int priority;                       /* Priority. */
    struct list_elem allelem;           /* List element for all threads list. */

    /* Shared between thread.c and synch.c. */
    struct list_elem elem;              /* List element. */

    int exit_status;                    /* Exit status of this thread */

    struct list_elem child_elem;        /* Child List element. */
    struct list children;               /* List of children of this thread */
    struct thread *parent;              /* Parent thread of this thread */

    struct condition wait_for_me_to_exit; /* Condition variable to wait for this thread*/
    struct condition wait_for_me_to_load; /* Condition variable to wait till thread loads*/
    struct file *exe_file;              /* Current thread's executable file handle*/
    struct list opened_files;           /* List of files which are opened by this thread */
    int thread_fd;                      /* File descriptor number */
    bool is_parent_waiting;             /* Boolean flag to indicate if parent process
                                           is already waiting on this thread */
    bool is_thread_exited;              /* Is this already dead / exited */
    int is_loaded;                      /* Gives the load status of thread */

#ifdef USERPROG
    /* Owned by userprog/process.c. */
    uint32_t *pagedir;                  /* Page directory. */
#endif

    /* Owned by thread.c. */
    unsigned magic;                     /* Detects stack overflow. */

    //user stack pointer
    void *user_stack_ptr;

    /*Owned by page table*/
    struct list map_list;       //list of memory mappped files
    struct hash *pages;         // page table
  };

/* If false (default), use round-robin scheduler.
   If true, use multi-level feedback queue scheduler.
   Controlled by kernel command-line option "-o mlfqs". */
extern bool thread_mlfqs;

void thread_init (void);
void thread_start (void);

void thread_tick (void);
void thread_print_stats (void);

typedef void thread_func (void *aux);
tid_t thread_create (const char *name, int priority, thread_func *, void *);

void thread_block (void);
void thread_unblock (struct thread *);

struct thread *thread_current (void);
tid_t thread_tid (void);
const char *thread_name (void);

void thread_exit (void) NO_RETURN;
void thread_yield (void);

/* Performs some operation on thread t, given auxiliary data AUX. */
typedef void thread_action_func (struct thread *t, void *aux);
void thread_foreach (thread_action_func *, void *);

int thread_get_priority (void);
void thread_set_priority (int);

int thread_get_nice (void);
void thread_set_nice (int);
int thread_get_recent_cpu (void);
int thread_get_load_avg (void);

struct thread *get_child_thread_by_tid (tid_t input_tid);
struct thread *get_alive_thread_by_tid (tid_t input_tid);
void release_thread_resources (struct thread *t);

#endif /* threads/thread.h */
