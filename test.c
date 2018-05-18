#include <stdlib.h>
#include <stdio.h>

typedef struct {
  int x;
} A;

void f(
	const char *msg
//	, A **res
) {
//  *res = malloc(sizeof(A));
  puts(msg);
}
