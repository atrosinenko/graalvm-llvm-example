#include <polyglot.h>

void *fromCString(const char *str) {
  return polyglot_from_string(str, "UTF-8");
}

void copy_to_array_from_pointers(void *arr, void **ptrs) {
  int size = polyglot_get_array_size(arr);
  for(int i = 0; i < size; ++i) {
    polyglot_set_array_element(arr, i, ((uintptr_t)ptrs[i]) ^ 1 /* prevent from detecting Polyglot value O_O */);
  }
}
