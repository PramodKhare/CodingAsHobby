#ifndef VM_SWAP_H
#define VM_SWAP_H 1
#include <stdbool.h>

//---------------------------------------------------------------------------
bool page_swap_out (struct page *);
void page_swap_in (struct page *);
void swap_init (void);
//---------------------------------------------------------------------------
struct page;
//---------------------------------------------------------------------------

#endif /* vm/swap.h */