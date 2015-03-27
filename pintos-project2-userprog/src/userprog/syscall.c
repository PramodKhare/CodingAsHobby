#include <stdio.h>
#include <syscall-nr.h>
#include "userprog/syscall.h"
#include "userprog/process.h"
#include "userprog/pagedir.h"
#include "threads/interrupt.h"
#include "threads/thread.h"
#include "threads/vaddr.h"
#include "threads/palloc.h"
#include "threads/malloc.h"
#include "devices/shutdown.h"
#include "devices/input.h"
#include "filesys/filesys.h"
#include "filesys/file.h"
#include "../lib/user/syscall.h"

#define STDIN_FILENO    0       /* standard input file descriptor */
#define STDOUT_FILENO   1       /* standard output file descriptor */

/* Lock for FileSystem Operations */
static struct lock file_op_lock;
static void syscall_handler (struct intr_frame *);
void close_all_opened_files (struct thread *current);
static int close_files (int fd);
int exec_process (char * file_name);

void syscall_init (void)
{
  lock_init (&file_op_lock);
  intr_register_int (0x30, 3, INTR_ON, syscall_handler, "syscall");
}

/**
 * Check if pointer value is in user-virtual memory space
 * The code segment in Pintos starts at user virtual address 0x08084000,
 * approximately 128 MB from the bottom of the address space,
 * and expands till PHYS_BASE 0xc0000000
 *
 * Ref: http://w3.cs.jmu.edu/kirkpams/450-f14/projects/process_project.shtml
 */
static void is_ptr_in_user_vm_space (const void *vaddr)
{
  if (vaddr >= PHYS_BASE || vaddr < ( (void *) 0x08048000 ))
  {
    // Exit the thread as it is trying to access invalid memory location
    exit_thread (-1);
  }
}

/**
 * Validate if each pointer in buffer is in valid User Virtual Memory Space.
 */
static void validate_buffer_pointers (const void* buffer_, unsigned size)
{
  const void *buffer = buffer_;
  is_ptr_in_user_vm_space (buffer);
  unsigned i;
  for (i = 0; i < size; i++)
  {
    is_ptr_in_user_vm_space ((const void*) buffer);
    (char *) buffer++;
  }
}

/**
 * Get given number (i.e. limit) of  arguments from stack frame,
 * and check if their pointers are in the user virtual space.
 */
static void extract_args (struct intr_frame *f, int *args, int limit)
{
  int i;
  int *ptr;
  for (i = 0; i < limit; i++)
  {
    ptr = (int *) f->esp + i + 1;
    is_ptr_in_user_vm_space ((const void *) ptr);
    args [i] = *ptr;
  }
}

/**
 * Just shut down the OS
 */
void halt (void)
{
  shutdown_power_off ();
}

/**
 *  SYS_EXIT call handler
 *  Terminates the current user program by exiting
 *  the current thread.
 */
void exit_thread (int status)
{
  struct thread *cur_thread = thread_current ();
  printf ("%s: exit(%d)\n", cur_thread->name, status);
  cur_thread->_state->exit_status = status;
  cur_thread->exit_status = status;
  thread_exit ();
}

/**
 * Wait for a particular process (given by pid) to complete.
 */
int wait (pid_t pid)
{
  return process_wait (pid);
}

/*
 * Execute a process with a given file name.
 * Return -1 if the process did not load.
 * Return the thread's id if the new process is load.
 */
int exec_process (char * file_name)
{
  struct lock temp_lock;
  tid_t tid_returned = process_execute ((const char *) file_name);
  struct thread *t = get_alive_thread_by_tid (tid_returned);
  struct _child *child = t->_state;
  lock_init (&temp_lock);
  lock_acquire (&temp_lock);
  //if the process has not yet loaded. Wait for it.
  while (t != NULL && child->is_loaded == THREAD_YET_TO_LOAD)
  {
    cond_wait (&child->wait_for_me_to_load, &temp_lock);
  }
  lock_release (&temp_lock);
  //After the process has been loaded, return.
  if (t == NULL || child->is_loaded == THREAD_LOAD_NEGATIVE)
  {
    return -1;
  } else
  {
    // If process loaded successfully then only add it to children list and assign the parent
    /*if (strcmp (t->name, "main") != 0)
    {
      t->parent = thread_current ();
    }
    list_push_front (&thread_current ()->children, &child->child_elem);*/
    return tid_returned;
  }
}

/**
 * Find the file with given file-descriptor from the opened_files
 * list of current thread and return the file structure if present
 * else null.
 */
static struct file_structure* get_file_structure (int fd)
{
  struct thread *current = thread_current ();
  struct list_elem *file_elem;

  for (file_elem = list_begin (&current->opened_files);
      file_elem != list_end (&current->opened_files);
      file_elem = list_next (file_elem))
  {
    struct file_structure *file_struct_local = list_entry(file_elem,
        struct file_structure, file_elem);
    if (fd == file_struct_local->fd)
    {
      return file_struct_local;
    }
  }
  return NULL;
}

/**
 * Writes given number of bytes (i.e. size) from buffer to the open file fd.
 * Returns 0 if size invalid, return size if fd is 1 i.e. STDOUT_FILENO and
 * the number of bytes actually written if written to a file.
 */
static int write_file (int fd, const void *buffer, unsigned size)
{
  // For invalid buffer sizes, just don't do anything return 0
  if (size < 1)
  {
    return 0;
  }
  // If file-descriptor is console then just dump buffer to console
  else if (fd == STDOUT_FILENO)
  {
    putbuf (buffer, size);
    return size;
  } else
  {
    // Else it is a file - use filesys write api to write to file
    lock_acquire (&file_op_lock);
    struct file_structure *file_struct = get_file_structure (fd);
    if (!file_struct)
    {
      lock_release (&file_op_lock);
      return -1;
    }
    int bytes_written = (int) file_write (file_struct->file, buffer, size);
    lock_release (&file_op_lock);
    return bytes_written;
  }
}

/**
 * Read bytes into buffer from file with given file-descriptor.
 * Returns 0 if size is less than 1 else number of bytes
 * read.
 */
static int read_file (int fd, void *buffer, unsigned size)
{
  // For invalid buffer sizes, just don't do anything return 0
  if (size < 1)
  {
    return 0;
  }
  // If file-descriptor is STDIN_FILENO then read given number of characters
  // from STDIN_FILENO
  if (fd == STDIN_FILENO)
  {
    return (int) input_getc ();
  }
  lock_acquire (&file_op_lock);
  struct file_structure *file_struct = get_file_structure (fd);
  if (!file_struct)
  {
    lock_release (&file_op_lock);
    return -1;
  }
  int bytes_read = (int) file_read (file_struct->file, buffer, size);
  lock_release (&file_op_lock);
  return bytes_read;
}

/**
 * Open the file with given file name.
 * Returns -1 if file not present else the
 * file descriptor of opened file.
 */
static int open_file (const char *file_name)
{
  lock_acquire (&file_op_lock);
  struct file *file = filesys_open (file_name);
  if (!file)
  {
    lock_release (&file_op_lock);
    return -1;
  }
  struct thread *current = thread_current ();
  // struct file_structure *file_struct = palloc_get_page (PAL_ZERO);
  struct file_structure *file_struct = (struct file_structure *) malloc (
      sizeof(struct file_structure));
  file_struct->fd = current->thread_fd;
  file_struct->file = file;
  list_push_back (&current->opened_files, &file_struct->file_elem);
  current->thread_fd++;
  lock_release (&file_op_lock);
  return file_struct->fd;
}

/**
 * Closes the file with given file-descriptor
 */
static int close_files (int fd)
{
  struct file_structure *file_struct = get_file_structure (fd);
  int status = 0;
  if (file_struct != NULL)
  {
    lock_acquire (&file_op_lock);
    file_close (file_struct->file);
    file_struct->file = NULL;
    lock_release (&file_op_lock);
    list_remove (&file_struct->file_elem);
    free (file_struct);
    file_struct = NULL;
  }
  return status;
}

/**
 * Close all opened files by this thread, this is done when thread is dying
 */
void close_all_opened_files (struct thread *current)
{
  struct list_elem *file_elem;

  for (file_elem = list_begin (&current->opened_files);
      file_elem != list_end (&current->opened_files);
      file_elem = list_next (file_elem))
  {
    struct file_structure *f = list_entry(file_elem, struct file_structure,
        file_elem);
    // Close the file
    lock_acquire (&file_op_lock);
    file_close (f->file);
    f->file = NULL;
    lock_release (&file_op_lock);
    list_remove (&f->file_elem);
    // free (f);
    // palloc_free_page(f);
    f = NULL;
  }
}

/**
 * Calculates file size for the file with the given file-descriptor.
 */
int filesize (int fd)
{
  struct file_structure *file_struct = get_file_structure (fd);
  if (!file_struct)
  {
    return -1;
  }
  lock_acquire (&file_op_lock);
  int size = (int) file_length (file_struct->file);
  lock_release (&file_op_lock);
  file_struct = NULL;
  return size;
}

/**
 * Sets the file-pointer to given position of the file with the given file-descriptor.
 */
static int fileseek (int fd, unsigned ps)
{
  struct file_structure *file_struct = get_file_structure (fd);
  int status = 0;
  if (!file_struct)
  {
    return -1;
  }
  lock_acquire (&file_op_lock);
  file_seek (file_struct->file, ps);
  lock_release (&file_op_lock);
  file_struct = NULL;
  return status;
}

/**
 * Returns the current position of the pointer in the file having the given descriptor,
 * else returns -1 if file not present.
 */
static int filetell (int fd)
{
  unsigned tell_pointer;
  struct file_structure *file_struct = get_file_structure (fd);
  if (!file_struct)
  {
    return -1;
  }
  lock_acquire (&file_op_lock);
  tell_pointer = (unsigned) file_tell (file_struct->file);
  lock_release (&file_op_lock);
  file_struct = NULL;
  return tell_pointer;
}

/**
 * Method to handle all the system calls.
 */
static void syscall_handler (struct intr_frame *f UNUSED)
{
  int args [3];
  is_ptr_in_user_vm_space ((const void*) f->esp);
  switch (*(int *) f->esp)
  {
    case SYS_HALT:
    {
      halt ();
      break;
    }
    case SYS_EXIT:
    {
      extract_args (f, &args [0], 1);
      exit_thread (args [0]);
      break;
    }
    case SYS_EXEC:
    {
      extract_args (f, &args [0], 1);
      f->eax = exec_process ((const char *) args [0]);
      break;
    }
    case SYS_WAIT:
    {
      extract_args (f, &args [0], 1);
      f->eax = wait (args [0]);
      break;
    }
    case SYS_CREATE:
    {
      extract_args (f, &args [0], 2);
      args [0] = (int) pagedir_get_page (thread_current ()->pagedir,
          (const void *) args [0]);
      if (!args [0])
      {
        exit_thread (-1);
      }
      f->eax = filesys_create ((const char *) args [0], args [1]);
      break;
    }
    case SYS_REMOVE:
    {
      extract_args (f, &args [0], 1);
      f->eax = filesys_remove ((const char *) args [0]);
      break;
    }
    case SYS_OPEN:
    {
      extract_args (f, &args [0], 1);
      args [0] = (int) pagedir_get_page (thread_current ()->pagedir,
          (const void *) args [0]);
      if (!args [0])
      {
        f->eax = -1;
        exit_thread (-1);
      }
      f->eax = open_file ((const char *) args [0]);
      break;
    }
    case SYS_FILESIZE:
    {
      extract_args (f, &args [0], 1);
      if ((int) args [0] < 2)
      {
        f->eax = -1;
        exit_thread (-1);
      }
      f->eax = filesize (args [0]);
      break;
    }
    case SYS_READ:
    {
      extract_args (f, &args [0], 3);
      validate_buffer_pointers ((const void *) args [1], (unsigned) args [2]);
      args [1] = (int) pagedir_get_page (thread_current ()->pagedir,
          (const void *) args [1]);
      if (!args [1])
      {
        f->eax = -1;
        exit_thread (-1);
      }
      f->eax = read_file (args [0], (void *) args [1], (unsigned) args [2]);
      break;
    }
    case SYS_WRITE:
    {
      extract_args (f, &args [0], 3);
      validate_buffer_pointers ((const void *) args [1], (unsigned) args [2]);
      args [1] = (int) pagedir_get_page (thread_current ()->pagedir,
          (const void *) args [1]);
      if (!args [1])
      {
        f->eax = -1;
        exit_thread (-1);
      }
      f->eax = write_file (args [0], (const void *) args [1],
          (unsigned) args [2]);
      break;
    }
    case SYS_SEEK:
    {
      extract_args (f, &args [0], 2);
      f->eax = (uint32_t) fileseek (args [0], (unsigned) args [1]);
      break;
    }
    case SYS_TELL:
    {
      extract_args (f, &args [0], 1);
      f->eax = (uint32_t) filetell (args [0]);
      break;
    }
    case SYS_CLOSE:
    {
      extract_args (f, &args [0], 1);
      if ((int) args [0] == 0 || (int) args [0] == 1)
      {
        f->eax = 0;
      } else
      {
        f->eax = close_files (args [0]);
      }
      break;
    }
  }
}
