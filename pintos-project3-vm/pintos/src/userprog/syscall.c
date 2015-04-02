#include <stdio.h>
#include <syscall-nr.h>
#include "userprog/syscall.h"
#include "userprog/process.h"
#include "userprog/pagedir.h"
#include "threads/interrupt.h"
#include "threads/thread.h"
#include "threads/vaddr.h"
#include "threads/palloc.h"
#include "devices/shutdown.h"
#include "devices/input.h"
#include "filesys/filesys.h"
#include "filesys/file.h"
#include "../lib/user/syscall.h"
#include "vm/page.h"

#define STDIN_FILENO    0       /* standard input file descriptor */
#define STDOUT_FILENO   1       /* standard output file descriptor */

/* Lock for FileSystem Operations */
static struct lock file_op_lock;
static void syscall_handler (struct intr_frame *);
void close_all_opened_files (struct thread *current);
static void close_files (int fd);
int exec_process (char * file_name);
static int memory_map (int id, void *address);
static int memory_unmap (int mapping);

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
 *  Terminates the current user program by exiting
 *  the current thread.
 */
void exit_thread (int status)
{
  struct thread *cur_thread = thread_current ();
  cur_thread->exit_status = status;
  // printf ("%s: exit(%d)\n", cur_thread->name, status);
  thread_exit ();
}

/**
 * Wait for a particular process (given by pid) to complete.
 */
int wait (pid_t pid)
{
  return process_wait (pid);
}

/**
 * Copies given commandline argument string to kernel virtual memory page,
 * and returns its pointer
 */
/*char * copy_commandline_arguments_to_kernel_page(char *commandline_args)
{
  char * copy_args, *uvaddr_page;
  int i = 0;
  bool user_vm_page_lock_status = false;
  // If kernel memory page allocation fails meaning something wrong - exit the thread
  if ( ( copy_args = palloc_get_page (0) ) == NULL)
  {
    // Kill this thread
    exit_thread (-1);
  }

  // Get the page from user virtual memory address
  uvaddr_page = pg_round_down (commandline_args);
  user_vm_page_lock_status = lock_and_write_page (uvaddr_page, false);
  // Get the lock over this user-virtual memory page
  if (user_vm_page_lock_status == true)
  {
    for (i = 0; i < PGSIZE; commandline_args++, i++)
    //should be less than - commandline_args < PGSIZE + uvaddr_page
    {
      copy_args [i] = *commandline_args;
      if (*commandline_args == '\0')
      {
        // Meaning whole arguments string is copied into kernel virtual memory
        break;
      }
    }
    unlock_page (uvaddr_page);
    return copy_args;
  } else
  {
    palloc_free_page (copy_args);
    // Kill this thread
    exit_thread (-1);
  }
  return NULL;
}*/
static char * copy_commandline_arguments_to_kernel_page (const char *user_str)
{
  char *kernel_str;
  size_t len;
  char *usr_pg;
  kernel_str = palloc_get_page (0);
  if (kernel_str == NULL){
    // exit thread
    thread_exit ();
  }// end of if

  len = 0;
  while(1)
    {
      usr_pg = pg_round_down (user_str);
      if (!lock_this_page (usr_pg, false)){
        goto page_not_lock;
    }// end of if

      for (; user_str < usr_pg + PGSIZE; user_str++) {
      kernel_str[len++] = *user_str;
      if (*user_str == '\0'){
        // unlock the page
        unlock_this_page (usr_pg);
        //return the kernel string after unlocking the page
        return kernel_str;
      }// end of if
      else if (len >= PGSIZE){
        goto len_long;
      }// end of else
        }// end of for
    // unlock the page
      unlock_this_page(usr_pg);
    }// end of while

len_long:
  unlock_this_page (usr_pg); // unlock kernel page

page_not_lock:
  palloc_free_page (kernel_str); // free kernel page
  thread_exit ();   // exit the thread
}
/*
 * Execute a process with a given file name.
 * Return -1 if the process did not load.
 * Return the thread's id if the new process is load.
 */
int exec_process (char * file_name)
{
  struct lock temp_lock;
  char * args = copy_commandline_arguments_to_kernel_page (file_name);
  tid_t tid_returned = process_execute ((const char *) args);
  struct thread *t = get_alive_thread_by_tid (tid_returned);
  lock_init (&temp_lock);
  lock_acquire (&temp_lock);
  //if the process has not yet loaded. Wait for it.
  while (t != NULL && t->is_loaded == THREAD_YET_TO_LOAD)
  {
    cond_wait (&t->wait_for_me_to_load, &temp_lock);
  }
  lock_release (&temp_lock);
  palloc_free_page (args);
  //After the process has been loaded, return.
  if (t == NULL || t->is_loaded == THREAD_LOAD_NEGATIVE)
  {
    return -1;
  } else
  {
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
 * Find the map with given id from the map
 * list of current thread and return the map structure if present
 * else null.
 */
/*static struct map_structure* extract_map (int id)
{
  struct thread *current = thread_current ();
  struct list_elem *map_elem;

  for (map_elem = list_begin (&current->maps);
      map_elem != list_end (&current->maps);
      map_elem = list_next (map_elem))
  {
    struct map_structure *map_struct_local = list_entry(map_elem,
        struct map_structure, map_elem);
    if (id == map_struct_local->id)
    {
      return map_struct_local;
    }
  }
  return NULL;
}*/

static struct map * get_map (int id)
{
  struct thread *cur = thread_current ();
  struct list_elem *ele;

  for (ele = list_begin (&cur->map_list); ele != list_end (&cur->map_list);
       ele = list_next (ele)){
      struct map *mp = list_entry (ele, struct map, elem);
      if (mp->map_id == id){
        return mp;
    }//end of if
    }//end of for
  //exit
  thread_exit ();
}//end of get_map

/**
 * Writes given number of bytes (i.e. size) from buffer to the open file fd.
 * Returns 0 if size invalid, return size if fd is 1 i.e. STDOUT_FILENO and
 * the number of bytes actually written if written to a file.
 */
/*static int write_file (int fd, const void *buffer, unsigned size)
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
}*/

/**
 * Read bytes into buffer from file with given file-descriptor.
 * Returns 0 if size is less than 1 else number of bytes
 * read.
 */
/*
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
*/


// Read system call
//----------------------------------------------------------------------
int read_file (int fd, void *buffer_, unsigned size)
{
  struct file_structure *f_d;
  uint8_t *read_buffer = buffer_;
  int total_bytes_read = 0;
  //$$--------------------------------------------------
  f_d = get_file_structure (fd);
  //$$--------------------------------------------------

  while (size > 0) {
    //page read, amount to read in this

    off_t retval;
    // check the space left on the current page
    size_t rem_page_size;
    rem_page_size= PGSIZE - pg_ofs(read_buffer);
    //check the amount to be read and then start reading accordingly.
    size_t r_vol ;

    if(size<rem_page_size){
      r_vol=size;
    }//end of if
    else{
      r_vol=rem_page_size;
    }//end of else

    if(fd!= STDIN_FILENO){
      if (!lock_this_page (read_buffer, true)) {
        thread_exit ();
      }//end of if
      //acquire the file lock to start reading
      lock_acquire (&file_op_lock);
      retval = file_read (f_d->file, read_buffer, r_vol);
      lock_release (&file_op_lock);
      unlock_this_page(read_buffer);
    }//end of if
    else{
      size_t itr;
      for(itr =0;itr<r_vol;itr++){
        char ip_c=input_getc();
        if(!lock_this_page(read_buffer,true)){
          thread_exit ();
        }//end of if

        read_buffer[itr]=ip_c;
        unlock_this_page(read_buffer);
      }//end of for
      total_bytes_read=r_vol;
    }//end of else

    // start reading from file to a page
    if (retval < 0){
      //set the total_bytes_read to -1 if it is 0
      if (total_bytes_read == 0){
        total_bytes_read = -1;
      }//end of if
      break;
        }//end of if
    //increment the total_bytes_read to retval
        total_bytes_read= total_bytes_read  + retval;

    // If all the bytes have been read then break
    if (retval != (off_t) r_vol){
      break;
    }//end of if

    // update the size and read_buffer values according to the retval
    size =size- retval;
    read_buffer =read_buffer + retval;
    }//end of wjile
  //return the total total_bytes_read
  return total_bytes_read;
}//end of read


// Write system call.
//----------------------------------------------------------------------
int write_file (int fd, void  *buffer_, unsigned size)
{
  struct file_structure *f_d;
  f_d =  NULL;
  uint8_t *write_buffer = buffer_;
  int total_bytes_written = 0;

  // Lookup up file descriptor.
  if (fd != 1){
    f_d = get_file_structure (fd);
  }//end of if

  //loop till all the bytes have been written
  while (size > 0)
    {
    off_t retval;
    // check how much space is left on the current page
    size_t rem_page_size;
    rem_page_size = PGSIZE - pg_ofs(write_buffer);
    //check the amount to be written and then start reading accordingly.
    size_t w_vol;

    if(size < rem_page_size){
      w_vol=size;
    }//end of if
    else{
      w_vol=rem_page_size;
    }//end of else

    // write into file from page
    if (!lock_this_page(write_buffer, false)){
      // if user addr not valid exit thread
      thread_exit ();
    }//end of if


    lock_acquire (&file_op_lock);
    // perform write operation by call putbuf() function
    if (fd == 1){
      //write the amount
      putbuf ((char *)write_buffer, w_vol);
      retval = w_vol;
    }//end of if
    else{
      retval = file_write (f_d->file, write_buffer, w_vol);
    }//end of else
    lock_release (&file_op_lock);


    /* Release user page. */
    unlock_this_page(write_buffer);

    if (retval < 0){
      //set the total_bytes_read to -1 if it is 0
      if (total_bytes_written == 0){
        total_bytes_written = -1;
      }//end of if
      break;
    }//end of if

    //increment the total_bytes_written to retval
    total_bytes_written =total_bytes_written + retval;

    // If all the bytes have been written then break
    off_t check_write_amt=(off_t)w_vol;

    if (retval != check_write_amt){
      break;
    }//end of if

    // update the size and write_buffer values according to the retval
    write_buffer = write_buffer + retval;
    size = size - retval;
    }//end of while

  return total_bytes_written;
}//end of write


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
  struct file_structure *file_struct = palloc_get_page (PAL_ZERO);
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
static void close_files (int fd)
{
  struct file_structure *file_struct = get_file_structure (fd);
  if (file_struct)
  {
    lock_acquire (&file_op_lock);
    file_close (file_struct->file);
    list_remove (&file_struct->file_elem);
    palloc_free_page (file_struct);
    lock_release (&file_op_lock);
  }
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
    lock_release (&file_op_lock);
    list_remove (&f->file_elem);
    palloc_free_page (f);
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
  return size;
}

/**
 * Sets the file-pointer to given position of the file with the given file-descriptor.
 */
static void fileseek (int fd, unsigned ps)
{
  struct file_structure *file_struct = get_file_structure (fd);
  if (!file_struct)
  {
    return;
  }
  lock_acquire (&file_op_lock);
  file_seek (file_struct->file, ps);
  lock_release (&file_op_lock);
}

/**
 * Returns the current position of the pointer in the file having the given descriptor,
 * else returns -1 if file not present.
 */
static unsigned filetell (int fd)
{
  struct file_structure *file_struct = get_file_structure (fd);
  if (!file_struct)
  {
    return -1;
  }
  lock_acquire (&file_op_lock);
  unsigned tell_pointer = (unsigned) file_tell (file_struct->file);
  lock_release (&file_op_lock);
  return tell_pointer;
}

/*void unmap_memory(struct map_structure *map)
{
  size_t i;
  void * start_addr = (void *) map ->address;
  for(i = 0 ; i< map -> pages_count ; i++)
  {
    deallocate_sup_page(start_addr);
    start_addr += PGSIZE;
  }
  list_remove(&map ->map_elem);
  free(map);
}*/

/* Remove map M from the virtual address space,
   writing back any pages that have changed. */
static void
unmap (struct map *mapping)
{
  //get the virtual start addr
  void *vaddr=(void *) mapping->start_addr;
  size_t iter;
  // iterate through the number of pages
  for(iter = 0 ; iter < mapping->num_of_pages ; iter++){
    // deallocate the address from the page
    deallocation_of_page (vaddr);
    // add the page size to the address
    vaddr=vaddr + PGSIZE;
  }//end of for
  // remove all the list mappings
  list_remove (&mapping->elem);
  // call function free to free mappings
  free(mapping);
}//

/*
 * Maps the memory
 */
/*static int memory_map(int fd, void *addr)
{
  size_t offset = 0;
  off_t file_len;
  struct file_structure *file_struct = get_file_structure (fd);
  struct map_structure *map = malloc(sizeof *map);

  if(!file_struct || addr == NULL || pg_ofs(addr)!=0 || map == NULL)
    return -1;

  struct thread *current = thread_current();
  map -> id = current -> thread_fd;
  current -> thread_fd ++ ;

  lock_acquire(&file_op_lock);
  map -> map_file = file_reopen(file_struct -> file);
  lock_release(&file_op_lock);

  if(map -> map_file == NULL)
  {
    free(map);
    return -1;
  }

  lock_acquire(&file_op_lock);
  file_len = file_length(map -> map_file);
  lock_release(&file_op_lock);

  if(file_len < 0)
    return -1;


  map -> address = addr;
  map -> pages_count = 0;

  while(file_len > 0)
  {

    struct sup_page *page = allocate_sup_page(offset + (uint8_t *)addr, true);
    if(page == NULL)
    {
      unmap_memory(map);
      return -1;
    }

    map -> pages_count += 1;

    if(file_len < PGSIZE)
      page -> rw_bytes = file_len;
    else
      page -> rw_bytes = PGSIZE;

    page -> file_offset = offset;
    page -> swap_write = false;
    page -> sup_file = map -> map_file;

    file_len = file_len - page -> rw_bytes;
    offset = offset + page -> rw_bytes;

  }

  return map -> id;
}*/

//----------------------------------------------------------------------
static int memory_unmap (int mapping)
{
  int success = 0;
  struct map *mp=get_map (mapping);
  if(mp == NULL){
    success = -1;
    return success;
  }//end of if

  unmap(mp);
  return success;
}// end of Mummap


static int memory_map(int id, void *address)
{
  int success = 0;
  struct map *mp = malloc (sizeof *mp);
  // struct file_descriptor *f_d = get_file_desc (id);
  struct file_structure *f_d = get_file_structure (id);

  off_t len;
  size_t offs;

//check if mp is null or the address is null or the page offset
//of address is 0
  if (mp == NULL || address == NULL || pg_ofs (address) != 0)
  {
    success = -1;
    return success;
  }  //end of if

  struct thread *cur = thread_current ();
  mp->map_id = cur->thread_fd++;
//acquire the file lock
  lock_acquire (&file_op_lock);
//reopen the file
  mp->file = file_reopen (f_d->file);
//release the file lock
  lock_release (&file_op_lock);
//check if the file reopend is null is yes then free the map
  if (mp->file == NULL)
  {
    free (mp);
    success = -1;
    return success;
  }  //end of if

  mp->num_of_pages = 0;
  mp->start_addr = address;
//insert the map in the current threads map list and map's elem
  list_push_front (&cur->map_list, &mp->elem);
  offs = 0;
//acquire the file lock
  lock_acquire (&file_op_lock);
//get the file length
  len = file_length (mp->file);
//release the file lock
  lock_release (&file_op_lock);

//check if length is greater than 0
  while (len > 0)
  {
    //allocate the page
    struct page *pg = allocation_of_page ((uint8_t *) address + offs, false);

    //check if the page allocated is null
    if (pg == NULL)
    {
      //if page is null then unmap the map
      unmap (mp);
      success = -1;
      return success;
    }

    pg->file = mp->file;
    pg->private = false;
    pg->f_ofs = offs;

    //check if the length is greater than PGSIZE .Set the byts_rw_f
    //accordingly
    if (len >= PGSIZE)
    {
      pg->byts_rw_f = PGSIZE;
    } else
    {
      pg->byts_rw_f = len;
    }

    //update the length and offset
    offs += pg->byts_rw_f;
    len -= pg->byts_rw_f;
    //update the number of pages mapped in the map
    mp->num_of_pages = mp->num_of_pages + 1;
  }
  //return the map id
  return mp->map_id;
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
      char *filename = NULL;
      extract_args (f, &args [0], 2);
      args [0] = (int) pagedir_get_page (thread_current ()->pagedir,
          (const void *) args [0]);
      if (!args [0])
      {
        exit_thread (-1);
      }
      filename = copy_commandline_arguments_to_kernel_page ((char *) args [0]);
      f->eax = filesys_create (filename, args [1]);
      palloc_free_page (filename);
      break;
    }
    case SYS_REMOVE:
    {
      char *filename = NULL;
      extract_args (f, &args [0], 1);
      filename = copy_commandline_arguments_to_kernel_page ((char *) args [0]);
      f->eax = filesys_remove ((const char *) filename);
      palloc_free_page (filename);
      break;
    }
    case SYS_OPEN:
    {
      char *filename = NULL;
      extract_args (f, &args [0], 1);
      args [0] = (int) pagedir_get_page (thread_current ()->pagedir,
          (const void *) args [0]);
      if (!args [0])
      {
        f->eax = -1;
        exit_thread (-1);
      }
      filename = copy_commandline_arguments_to_kernel_page ((char *) args [0]);
      f->eax = open_file ((const char *) filename);
      palloc_free_page (filename);
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
      f->eax = (uint32_t) read_file (args [0], (void *) args [1], (unsigned) args [2]);
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
      f->eax = (uint32_t) write_file (args [0], (const void *) args [1],
          (unsigned) args [2]);
      break;
    }
    case SYS_SEEK:
    {
      extract_args (f, &args [0], 2);
      fileseek (args [0], (unsigned) args [1]);
      break;
    }
    case SYS_TELL:
    {
      extract_args (f, &args [0], 1);
      f->eax = filetell (args [0]);
      break;
    }
    case SYS_CLOSE:
    {
      extract_args (f, &args [0], 1);
      if ((int) args [0] == 0 || (int) args [0] == 1)
      {
        f->eax = 0;
        break;
      }
      close_files (args [0]);
      break;
    }
    case SYS_MMAP:
    {
      extract_args (f, &args [0], 2);
      f->eax = memory_map(args[0], (void *) args[1]);
      break;
    }
    case SYS_MUNMAP:
    {
      extract_args (f, &args [0], 1);
      /*struct map_structure *map = extract_map(args[0]);
      if(map != NULL)
        unmap_memory(map);
       break;*/
      memory_unmap (args [0]);
      break;
    }
    default:
    {
      printf ("This system call is not implemented!");
      exit_thread (-1);
      break;
    }
  }
}
