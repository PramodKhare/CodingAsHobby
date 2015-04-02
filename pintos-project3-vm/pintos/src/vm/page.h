#ifndef VM_PAGE_H
#define VM_PAGE_H
#include <hash.h>
#include "userprog/process.h"
#include "threads/synch.h"
#include "filesys/off_t.h"
#include "devices/block.h"
//---------------------------------------------------------------------------
/* Structure for virtual page */


struct page 
  {
	// Set only in owning process context with frame->lock held.
    //Cleared only with s_lock and frame->lock held. 
	struct frame *frame;        // frame that is associated with this page. 
	struct thread *thread;      // thread that is associated with this page.
	//content process accesses
	struct hash_elem hash_elem; // struct thread `pages' hash element. 
	//file info for memory mapped files 
	// secured by frame->lock
	struct file *file;          // File that is associated with this page. 
	bool private;               // If False to write back to file, and if
                                // true to write back to swap. 
																
	//Swap information, which is protected by frame->lock.
	block_sector_t swap_sec; 	// This variable will either have starting 
								// sector of the swap area  or -1.
	
    // these Immutable members. 
    void *user_vaddr;       	// User virtual address that is associated with 
								// this page.
    bool ro_page;          		// it true if this page is read only page
           
	// this has information of memory-mapped
    off_t f_ofs;          		// Offset in file.
    off_t byts_rw_f;      		// Bytes to read or write, 1 - PGSIZE.
  };
//---------------------------------------------------------------------------

void unlock_this_page (const void *);
bool lock_this_page (const void *, bool will_write);
hash_less_func check_hash_entries;
hash_hash_func get_page_hash_value;
void deallocation_of_page (void *vaddr);
struct page *allocation_of_page (void *, bool read_only);
bool page_out (struct page *);
bool is_page_recently_accessed (struct page *);
bool page_in (void *fault_addr);
void delete_cur_page_pte (void);
#endif /* vm/page.h */ 