#include <stdio.h>

#include "vm/page.h"
#include "vm/frame.h"

#include "devices/timer.h"

#include "threads/malloc.h"
#include "threads/palloc.h"
#include "threads/init.h"
#include "threads/vaddr.h"
#include "threads/synch.h"
//----------------------------------------------------------------------
static struct frame *s_frame;
static size_t no_of_frames;
static size_t f_hand;
static struct lock s_lock;
//----------------------------------------------------------------------
// Initialize the frame manager.
//----------------------------------------------------------------------
void frame_init (void) 
{
  void *fkbase;
  
  lock_init (&s_lock);
  //get s_frame
  s_frame = malloc (sizeof *s_frame * init_ram_pages);
  //check if s_frame is NULL
  if (s_frame == NULL){
		PANIC ("Page frames out of Memory allocation");
	}// end of if

  while ((fkbase = palloc_get_page (PAL_USER)) != NULL) {
	  //get the frame
      struct frame *frm = &s_frame[no_of_frames++];
	  //initialize the parameters of frame
	  frm->page = NULL;
      frm->fkbase = fkbase;
	  //initialize the frame lock
      lock_init (&frm->lock);
    }// end of while
}// end of func


/* this function tries to allocate and lock any free frame for the page*/
//------------------------------------------------------------------------
static struct frame *try_frame_alloc_and_lock (struct page *page) 
{
  size_t len;
  //acquire the lock before scanning for free frame
  lock_acquire (&s_lock);

  // look for free frame
  for (len = 0; len < no_of_frames; len++){
      struct frame *frm = &s_frame[len];
      if (!lock_try_acquire (&frm->lock))
		continue;
		
	  // if page of the frame is null
      if (frm->page == NULL){
          frm->page = page;
		  //release the lock and return frame
          lock_release (&s_lock);
          return frm;
        }// end of if 
	  // release the lock
      lock_release (&frm->lock);
    }// end of for

  // evict a frame since no free frame is present 
  // used clock algorithm for eviction
  for (len = 0; len < no_of_frames * 2; len++){
      // get a frame
      struct frame *frm = &s_frame[f_hand];
      if (++f_hand >= no_of_frames)
		f_hand = 0;
	  
	  //continue if the frame is not locked
      if (!lock_try_acquire (&frm->lock))
			continue;
      
	  //check if the page is null
      if (frm->page == NULL){
          frm->page = page;
		  // release the lock and return frame
          lock_release (&s_lock);return frm;
        }// end of if
      //check if the page has been accessed recently
      if (is_page_recently_accessed (frm->page)){
			// release the lock and continue
			lock_release (&frm->lock);continue;
        }// end of if
      // release the lock   
      lock_release (&s_lock);
      
      // evict this frame. 
      if (!page_out (frm->page)){ 
			// release the lock and evict the frame
			lock_release (&frm->lock);return NULL;
        }// end of if
      frm->page = page;return frm;
    }// end of for
  // release the lock
  lock_release (&s_lock);  return NULL;
}// end of func


/* this function will lock the page's frame to the memory */
//-----------------------------------------------------------
void lock_curr_pg_frame (struct page *pg) 
{
  //Removal of frame is async.But insertion is never async
  struct frame *frm = pg->frame;
  //check if the frame is null
  if (frm != NULL){
	  //acquire the frame lock
      lock_acquire (&frm->lock);
      if (frm != pg->frame){
		  // release the frame lock
          lock_release (&frm->lock);
		  //check if the page frame is null
          ASSERT (pg->frame == NULL); 
        }// end of if 
    }// end of if
}// end of func


/* this function will try to allocate and lock frame for the page */
//-------------------------------------------------------------------------
struct frame *frame_allocate (struct page *page) 
{
  size_t len;
  int sleep_val= 1000;
  int val = NULL;
  for (len = 0; len < 3; len++) {
	  //get a frame for the page
      struct frame *frm = try_frame_alloc_and_lock (page);
      if (frm != NULL) {
		  //check if the frame lock has been held by current thread
          ASSERT (lock_held_by_current_thread (&frm->lock));return frm; 
        }timer_msleep (sleep_val);
    }return val;
}// end of func


/* this function will free the frame f so that any other page can use it*/   
//----------------------------------------------------------------------
void release_frame (struct frame *frm)
{
  //check if the frame lock has been held by current thread
  ASSERT (lock_held_by_current_thread (&frm->lock));
  //assign the page of the frame as null        
  frm->page = NULL;
  // release the frame lock
  lock_release (&frm->lock);
}// end of func


 /* this function will unlock the frame so that it can be evicted*/
//----------------------------------------------------------------------
 void unlock_curr_pg_frame (struct frame *frm) 
{
  //check if the frame lock has been held by current thread
  ASSERT (lock_held_by_current_thread (&frm->lock));
  // release the frame lock
  lock_release (&frm->lock);
}// end of func 


   
