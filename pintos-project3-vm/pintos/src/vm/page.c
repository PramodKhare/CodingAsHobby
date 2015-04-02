#include <string.h>
#include <stdio.h>

#include "vm/swap.h"
#include "vm/page.h"
#include "vm/frame.h"

#include "filesys/file.h"

#include "threads/vaddr.h"
#include "threads/thread.h"
#include "threads/malloc.h"

#include "userprog/pagedir.h"
#include "userprog/process.h"

//Maximum size of process stack, in bytes.
#define MAX_STACK_SIZE (1024 * 1024)


/* this function will get the page from the current process's page table
   and then it checks if the page has a frame,if yes then it will free the
   frame first and then free the page.So basically it is used as
   a callback to hash_destory function.*/
//----------------------------------------------------------------------
static void destroy_page (struct hash_elem *pg_, void *aux_entry UNUSED)
{
  struct page *pg = hash_entry (pg_, struct page, hash_elem);
  //lock the frame
  lock_curr_pg_frame (pg);
  //check if the page has a frame.If yes then free that frame first.
  if (pg->frame) {
    //free the frame 
	release_frame (pg->frame);
	}//end of if
  //free the page
  free (pg);
}//end of function


/* this function will delete the current process's page table entry
	by calling hash_destroy function*/ 
//----------------------------------------------------------------------
void delete_cur_page_pte (void) 
{
  struct thread *cur = thread_current();
  //get the current thread's page
  struct hash *hs = cur -> pages;
  //check if the hash entry is not null
  if (hs != NULL){
		//delete the page entry from hash table
		hash_destroy (hs, destroy_page);
		}//end of if	
}//end of function


/* this function will return the page that has the given virtual address
   and if the page does not exists it will return NULL.It also allocates
   the stack pages when it is necessary*/
//----------------------------------------------------------------------
static struct page *page_for_addr (const void *vir_addr) 
 {
  int val=NULL;
  //check if the virtual address is below PHYS_BASE.If not then return NULL
  if (vir_addr < PHYS_BASE) {
      struct page pg;
	  struct page *return_pg;
	  struct thread *cur = thread_current();
	  struct hash_elem *elem;
	  // look for existing page using hash_find function
      pg.user_vaddr = (void *) pg_round_down (vir_addr);
      elem = hash_find (cur->pages, &pg.hash_elem);
	  //if the page is not present
      if (elem == NULL){
			// we have to check the stack vir_addr and vir_addr for stack growth
			if (vir_addr >= PHYS_BASE - MAX_STACK_SIZE){
				if(vir_addr >= cur ->user_stack_ptr - 32){
				return_pg = allocation_of_page ((void *) vir_addr, false);
				return return_pg;
				}//end of if
			}//end of if
		}//end of if
	  // if the page is present then get the hash entry		
	  else
	  {	  
			return_pg = hash_entry (elem, struct page, hash_elem);
			return return_pg;
	   }//end of else
			
    }//end of if
  return val;
  }//end of function


/* this function will try to lock the frame for a page pg and pages it in.
   if this process is successful then return true and if not the return 
   false*/
//----------------------------------------------------------------------
static bool do_page_in (struct page *pg)
{
   bool	success=true;
  // first try to get a frame for this page
  pg->frame = frame_allocate (pg);
  //if not found then return false
  if (pg->frame == NULL){
		success = false;
		return success;
	}//end of if

  //once the frame is fetched, copy the whole data to the frame
  //check if the data has be be fetched from swap or from file
	if (pg->file != NULL) {
      // fetch the data from file
      off_t r_bytes = file_read_at (pg->file, 
					  pg->frame->fkbase,pg->byts_rw_f, pg->f_ofs);
		
      //calculate the zero bytes		
      off_t zbytes = PGSIZE - r_bytes;
      memset (pg->frame->fkbase + r_bytes, 0, zbytes);
      if (r_bytes != pg->byts_rw_f){
        printf (" the number of bytes read  are (%"PROTd
		") != number of bytes requested are (%"PROTd")\n",
                r_bytes, pg->byts_rw_f);}			
    }//end of if
	else if (pg->swap_sec != (block_sector_t) -1) {
	  //get the data from the swap
      page_swap_in (pg); 
	  }//end of else if
	else {
	  // just get the all zero page	  
      memset (pg->frame->fkbase, 0, PGSIZE);
	  }//end of else
  return success;
}//end of function

/* this page will fault when the address contains fault_address
	It will return true if it is successful else it returns false*/
//----------------------------------------------------------------------   
bool page_in (void *fault_address) 
{
  bool has_succeeded;
  struct page *pg;
  struct thread *cur = thread_current ();
  bool not_success = false;
  //if there is no hash table faults will not will be handled
  if (cur->pages == NULL) {
    return not_success;
  }//end of if
  // get the page from the given address  
  pg = page_for_addr (fault_address);
  //check if the page is null.If yes return false
  if (pg == NULL) {
    return not_success; 
  }//end of if
  //lock the frame
  lock_curr_pg_frame (pg);
  //check if the page frame is null
  if (pg->frame == NULL){
	  //try to lock the frame for a page pg and page it in and it is
	  //false then return false.
      if (!do_page_in (pg)){
        return not_success;
	  }//end of if
    }//end of if
	
  ASSERT (lock_held_by_current_thread (&pg->frame->lock));

  //insert the frame in the page table
  has_succeeded = pagedir_set_page (cur->pagedir, pg->user_vaddr,
									pg->frame->fkbase, !pg->ro_page);
  // unlock the frame
  unlock_curr_pg_frame (pg->frame);
  //return the result
  return has_succeeded;
}//end of fucntion


/* this function will evict the page pg.If the eviction is successful
   return true else return false */
//----------------------------------------------------------------------
bool page_out (struct page *pg) 
{
  bool success = false;
  bool dirty_pg;
 
  ASSERT (pg->frame != NULL);
  ASSERT (lock_held_by_current_thread (&pg->frame->lock));
  // get the page thread
  struct thread *t=pg->thread;
   
   //this will 1st mark the page as not present in the page table so that
   //all the accesses by the process will cause fault.
   //After doing this we check for dirty bit.
   //the checks are in this order so as to avoid a race condition between
   //a process who wants to evit a page and a process who wants to modify a
   //page
    pagedir_clear_page (t->pagedir, pg->user_vaddr);
    //check if the page is dirty i.e if page has been modified
   dirty_pg = pagedir_is_dirty (t->pagedir, pg->user_vaddr);
   
    // if the page has some contents,before evicting, we need to write the 
	// contents to a file
	if ( pg->file != NULL ){
		// again check if page is dirty
		if (dirty_pg){
			//check if the page contents are to be written to a file or
			//back to a swap
			if ( !pg->private) {
				// we have to write the contents to a file
				off_t total_write_amt;
				//get the total amount of bytes written
				total_write_amt = file_write_at (pg->file, pg->frame->fkbase,
							pg->byts_rw_f, pg->f_ofs);
				// check if the write to file was successful 
				if (total_write_amt == pg->byts_rw_f)
				{	
					success = true;
				}//end of if
				else
				{
					success=false;
				}//end of else
				//success = total_write_amt == pg->byts_rw_f;
			}//end of if	
			else{
				//contents are to be written back to a swap
				success = page_swap_out(pg);
			}//end of else
		}//end of if
		else{
			success = true;
		}//end of else	
	}//end of if
	//page does not have any contents
	else{
		success = page_swap_out(pg);
	}//end of else
  //if success is true, make the frame of the page null	
  if (success) {
      pg->frame = NULL; 
    }//end of if
  //return success	
  return success;
}//end of function

/* this function checks if the page pg has been accessed recently*/
//----------------------------------------------------------------------
bool is_page_recently_accessed (struct page *pg) 
{
  bool is_accessed;
  //check if the page frame is null
  ASSERT (pg->frame != NULL);
  ASSERT (lock_held_by_current_thread (&pg->frame->lock));
  //call function pagedir_is_accessed to check if it has been recently 
  //accessed
  is_accessed = pagedir_is_accessed (pg->thread->pagedir, pg->user_vaddr);
  //if the page contents have been accessed recently,then set it as accessed
  if (is_accessed)
  {
    pagedir_set_accessed (pg->thread->pagedir, pg->user_vaddr, false);
  }	//end of if
  //return true if accessed else false
  return is_accessed;
}//end of function

/* this function will map the user virtual address to the page hash table
   and if the address is already present then the allocation fails.
   also the process fails if the memory allocation fails*/
//----------------------------------------------------------------------   
struct page *allocation_of_page (void *vaddr, bool r_only)
{
  struct thread *cur = thread_current ();
  struct page *pg = malloc (sizeof *pg);
  //check if the pag is null
  if (pg != NULL) {
	  //we have to initialize all the page members with relevant details.
	  //initialize the page user virtual address with vaddr
      pg->user_vaddr = pg_round_down (vaddr);
	  //initialize the frame of the page and file of the page to null
      pg->frame = NULL;
      pg->file = NULL;
	  //set the offset and bytes to read/write as 0
	  pg->f_ofs = 0;
	  pg->byts_rw_f = 0;
      //initialize the swap_sec for Swap information.
	  pg->swap_sec = (block_sector_t) -1;
	  //initialize the page thread as current thread
      pg->thread = thread_current ();
	  pg->ro_page = r_only;
	  pg->private = !r_only;
	  //check if the address is already present in the hash table
	  // free the page pg and set it as null
      if (hash_insert (cur->pages, &pg->hash_elem) != NULL) {
          // address is already mapped.
          free (pg);
          pg = NULL;
        }//end of if
    }//end of if
  //return the page pg as initialized	
  return pg;
}//end of function


/* this function will evict the page at the given vaddr and take the page
   off from the page table. */
//----------------------------------------------------------------------  
void deallocation_of_page (void *vaddr) 
{
  struct page *pg = page_for_addr (vaddr);
  //check if the page is null
  ASSERT (pg != NULL);
  //lock the frame
  lock_curr_pg_frame (pg);
  if (pg->frame){
      struct frame *frm = pg->frame;
	  if(pg->file){
	    //check if the page contents have to be written to file or swap
		if(!pg->private){
			page_out (pg); 
		}//end of if
	  }//end of if
	  //free the frame
      release_frame (frm);
    }//end of if
  //delete the page from the table	
  hash_delete (thread_current ()->pages, &pg->hash_elem);
  //free the page i.e deallocate the page
  free (pg);
}//end of function


/* this function will just return the hash value for the page
	that hash_elem ele refers to. */
//----------------------------------------------------------------------
unsigned get_page_hash_value (const struct hash_elem *ele, void *auxillary UNUSED)
{
  //get the hash entry for the hash_elem ele
  const struct page *pg = hash_entry (ele, struct page, hash_elem);
  //get the hash value for that entry
  unsigned hash_val=((uintptr_t) pg->user_vaddr) >> PGBITS;
  //return that hash value
  return hash_val;
}//end of function

/* this function will check  if the which of entries a_elem and b_elem 
   of hash table precedes the other .If a_elem precedes b_elem then 
   return true else return false*/
//----------------------------------------------------------------------
bool check_hash_entries (const struct hash_elem *a_elem_, 
				const struct hash_elem *b_elem_,
				void *auxillary UNUSED) 
{
  //get the 1st hash entry
  const struct page *a_pg = hash_entry (a_elem_, struct page, hash_elem);
  //get the 2nd hash entry
  const struct page *b_pg = hash_entry (b_elem_, struct page, hash_elem);
  //get the result i.e  if a_pg precedes b_pg return true
  bool result=a_pg->user_vaddr < b_pg->user_vaddr;
  //return the result
  return result;
}//end of function

/* this function will unlock a page at the given address*/
//----------------------------------------------------------------------
void unlock_this_page (const void *addr) 
{
  //get the page at given address
  struct page *pg = page_for_addr (addr);
  //check if the page is null
  ASSERT (pg != NULL);
  // unlock the frame
  unlock_curr_pg_frame (pg->frame);
}//end of function
 
/* this function will lock the page at the address and write
   to physical memory.Also make a check if page is writeable r read
   only. */
//----------------------------------------------------------------------   
bool lock_this_page (const void *addr, bool to_write) 
{
  //get the frame at the given address
  struct page *pg = page_for_addr (addr);
  bool result=true;
  //check if the page fetched is null or it is not writeable
  if (pg == NULL || (pg->ro_page && to_write)){
	result=false;
    return result;
  }//end of if	
  //lock the frame
  lock_curr_pg_frame (pg);
  //check if the frame is null
  if (pg->frame == NULL){
	if(do_page_in (pg))
	{
		if(pagedir_set_page (thread_current ()->pagedir,
			pg->user_vaddr,pg->frame->fkbase, !pg->ro_page))
			{
				return true;
			}
		else
			{
				return false;	
			}
	}//end of if
	else	
	{
		return false;	
	}//end of else
   }//end of if							 
  else{
	result=true;
    return result;
  }//end of else	
}//end of function
