#include <stdio.h>
#include <memory.h>
#include "rc4.h"

void print_hex(BYTE str[], int len)
{
    int idx;

    for (idx = 0; idx < len; idx++)
        printf("%02x", str[idx]);
}

int main()
{
    char text[] = "14564561313213215665423";
    Rc4EncryptText(text);
    return 0;
}