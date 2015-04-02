#include <bitmap.h>
#include <debug.h>
#include <stdio.h>

#include "threads/synch.h"
#include "threads/vaddr.h"

#include "vm/frame.h"
#include "vm/page.h"
#include "vm/swap.h"

#include "devices/block.h"
//---------------------------------------------------------------------------
// Set the number of sectors per page
#define PAGE_SECTORS (PGSIZE / BLOCK_SECTOR_SIZE)

// Define a lock that protects sw_bitmap.
static struct lock sw_lock;

// Define a structure bitmap for used swap pages.
static struct bitmap *sw_bitmap;

// Define a structure block for the swap device.
static struct block *sw_device;

/* Initialize the swap */
//---------------------------------------------------------------------------
void swap_init (void) 
{
  sw_device = block_get_role (BLOCK_SWAP);
  //check if the device is null
  if (sw_device != NULL) {
		sw_bitmap = bitmap_create (block_size (sw_device)/ PAGE_SECTORS);
    }// end of if
	else{
		printf ("No swap present, swapping disabled\n");
		sw_bitmap = bitmap_create (0);
	}// end of else
  //check if the swap bitmap is null	
  if (sw_bitmap == NULL){
		PANIC ("Swap bitmap cannot be created");
	}// end of for
  // initialize the swap lock
  lock_init (&sw_lock);
}// end of func


//---------------------------------------------------------------------------
/* This function will swap a page P that has locked frame.*/
//---------------------------------------------------------------------------
void page_swap_in (struct page *pg) 
{
  size_t len;
  //check if the page frame is null
  ASSERT (pg->frame != NULL);
  //check if the lock of the frame of the current page is held by current 
  //thread
  ASSERT (lock_held_by_current_thread (&pg->frame->lock));
  // check the pg swap if not equal to the block sector -1
  ASSERT (pg->swap_sec != (block_sector_t) -1);

  //perform block read
  for (len = 0; len < PAGE_SECTORS; len++){
		block_read (sw_device, pg->swap_sec + len,pg->frame->fkbase + len
		* BLOCK_SECTOR_SIZE);
	}// end of for
  //reset the bitmap	
  bitmap_reset (sw_bitmap, pg->swap_sec / PAGE_SECTORS);
  
  pg->swap_sec = (block_sector_t) -1;
}// end of func


//---------------------------------------------------------------------------
/* this function will swap a page P out that has locked frame */
//---------------------------------------------------------------------------
bool page_swap_out (struct page *pg) 
{
  //initialize the size_t variables
  size_t len;
  size_t sw_slot;
  bool success = true;
  // check for page frame equal to NULL or not.
  // If equal to NULL throw an assert error.
  ASSERT (pg->frame != NULL);
  // check for lock held by the present thread.
  // if not equal to the throw an assert error.
  ASSERT (lock_held_by_current_thread (&pg->frame->lock));
  //acquire the swap lock
  lock_acquire (&sw_lock);
  sw_slot = bitmap_scan_and_flip (sw_bitmap, 0, 1, false);
  //release the swap lock
  lock_release (&sw_lock);
  
  //check if there is bitmap error in swap slot
  if (sw_slot == BITMAP_ERROR){
		success = false;
		return success; 
	}// end of if

  pg->swap_sec = sw_slot * PAGE_SECTORS;
  
  //perform block write
  for (len = 0; len < PAGE_SECTORS; len++){
		block_write (sw_device, pg->swap_sec + len,pg->frame->fkbase + len
		* BLOCK_SECTOR_SIZE);
	}// end of for
  
  bitmap_mark (sw_bitmap, pg->swap_sec / PAGE_SECTORS); 
  
  //set the page members
  pg->file = NULL;
  pg->private = false;
  pg->byts_rw_f = 0;
  pg->f_ofs = 0;
  
  return success;
}// end of func
