/*********************************************************************
* Filename:   rc4.h
* Details:    Defines the API for the corresponding rc4 implementation.
*********************************************************************/
#ifndef RC4_H
#define RC4_H

#include <stdio.h>
#include <time.h>
#include <string.h>

#define MAX 65534

typedef unsigned char BYTE; // 8-bit byte

void Rc4EncryptText(char *text);

#endif // RC4_H