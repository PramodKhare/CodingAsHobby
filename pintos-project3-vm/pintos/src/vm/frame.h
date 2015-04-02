#ifndef VM_FRAME_H
#define VM_FRAME_H
#include <stdbool.h>
#include "threads/synch.h"

//----------------------------------------------------------------------
// A physical frame structure
//----------------------------------------------------------------------
// unlocks the frame from the current page
void unlock_curr_pg_frame (struct frame *);
// locks frames from the pages
void lock_curr_pg_frame (struct page *);
// allocate the frame
struct frame *frame_allocate (struct page *);
// free the frame
void release_frame (struct frame *);
// initialization of frames
void frame_init (void);

// Structure of the frame 
struct frame 
  {	
	void *fkbase;               // Will represent the virtual address 
								// of the kernel. 
	struct page *page;          // Maps process pages.
	
    struct lock lock;           // This will ensure that no access will 
								// simultaneous. 
  };
//----------------------------------------------------------------------
#endif /* vm/frame.h */