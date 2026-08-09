/*********************************************************************
* Filename:   des_test.c
* Details:    Performs known-answer tests on the corresponding DES
	          implementation. These tests do not encompass the full
	          range of available test vectors, however, if the tests
	          pass it is very, very likely that the code is correct
	          and was compiled properly. This code also serves as
	          example usage of the functions.
*********************************************************************/

/*************************** HEADER FILES ***************************/
#include <stdio.h>
#include <memory.h>
#include "des.h"

/*********************** FUNCTION DEFINITIONS ***********************/
void print_hex(BYTE str[], int len)
{
	int idx;

	for(idx = 0; idx < len; idx++)
		printf("%02x", str[idx]);
}


int des_test()
{
	printf("function des_test() include:des,3des,using dif key\n");
	
	BYTE pt1[DES_BLOCK_SIZE] = {0x01,0x23,0x45,0x67,0x89,0xAB,0xCD,0xE7};
	BYTE pt2[DES_BLOCK_SIZE] = {0x01,0x23,0x45,0x67,0x89,0xAB,0xCD,0xEF};
	BYTE pt3[DES_BLOCK_SIZE] = {0x54,0x68,0x65,0x20,0x71,0x75,0x66,0x63};

	BYTE ct1[DES_BLOCK_SIZE] = {0xc9,0x57,0x44,0x25,0x6a,0x5e,0xd3,0x1d};
	BYTE ct2[DES_BLOCK_SIZE] = {0x85,0xe8,0x13,0x54,0x0f,0x0a,0xb4,0x05};
	BYTE ct3[DES_BLOCK_SIZE] = {0xc9,0x57,0x44,0x25,0x6a,0x5e,0xd3,0x1d};
	BYTE ct4[DES_BLOCK_SIZE] = {0xA8,0x26,0xFD,0x8C,0xE5,0x3B,0x85,0x5F};


	BYTE key1[DES_BLOCK_SIZE] = {0x01,0x23,0x45,0x67,0x89,0xAB,0xCD,0xEF};
	BYTE key2[DES_BLOCK_SIZE] = {0x13,0x34,0x57,0x79,0x9B,0xBC,0xDF,0xF1};


	BYTE three_key1[DES_BLOCK_SIZE * 3] = {0x01,0x23,0x45,0x67,0x89,0xAB,0xCD,0xEF,
	                                       0x01,0x23,0x45,0x67,0x89,0xAB,0xCD,0xEF,
	                                       0x01,0x23,0x45,0x67,0x89,0xAB,0xCD,0xEF};
	BYTE three_key2[DES_BLOCK_SIZE * 3] = {0x01,0x23,0x45,0x67,0x89,0xAB,0xCD,0xEF,
	                                       0x23,0x45,0x67,0x89,0xAB,0xCD,0xEF,0x01,
	                                       0x45,0x67,0x89,0xAB,0xCD,0xEF,0x01,0x23};


	BYTE schedule[16][6];
	BYTE three_schedule[3][16][6];
	BYTE buf[DES_BLOCK_SIZE];
	int pass = 1;
//1
	printf("\n1.key1 and plaintext1:");
	
	//encryt
	//key setup
	des_key_setup(key1, schedule, DES_ENCRYPT);
	printf	("\nKey1		: ");
	print_hex(key1, DES_BLOCK_SIZE);
	
	//encrypt
	des_crypt(pt1, buf, schedule);
	printf("\nPlaintext1	: ");
	print_hex(pt1,DES_BLOCK_SIZE);
	printf("\n-encrypted to	: ");
	print_hex(buf,DES_BLOCK_SIZE);
	
	//compare	
	pass = pass && !memcmp(ct1, buf, DES_BLOCK_SIZE);
	if (pass != 1)
	{
		printf("\nDES_ENCRYPT failed\n");
		}else
		{
			printf("\nCiphertext1	: ");
			print_hex(buf,DES_BLOCK_SIZE);
		}
	
	//decrypt
	//key setup
	des_key_setup(key1, schedule, DES_DECRYPT);
	
	//decrypt
	des_crypt(ct1, buf, schedule);
	printf("\nCiphertext1	: ");
	print_hex(ct1,DES_BLOCK_SIZE);
	printf("\n-decrypted to	: ");
	print_hex(buf,DES_BLOCK_SIZE);
	
	//compare
	pass = pass && !memcmp(pt1, buf, DES_BLOCK_SIZE);
	if (pass != 1)
	{
		printf("\nDES_DECRYPT failed\n");
		}else
		{
			printf("\nPlaintext1	: ");
			print_hex(buf,DES_BLOCK_SIZE);
		}
		
//2
	printf("\n\n2.key2 and plaintext2:");
	
	//encryt
	//key setup
	des_key_setup(key2, schedule, DES_ENCRYPT);
	printf	("\nKey2		: ");
	print_hex(key2, DES_BLOCK_SIZE);
	
	//encrypt
	des_crypt(pt2, buf, schedule);
	printf("\nPlaintext2	: ");
	print_hex(pt2,DES_BLOCK_SIZE);
	printf("\n-encrypted to	: ");
	print_hex(buf,DES_BLOCK_SIZE);
	//compare
	pass = pass && !memcmp(ct2, buf, DES_BLOCK_SIZE);
	if (pass != 1)
	{
		printf("DES_ENCRYPT failed\n");
		}else
		{
			printf("\nCiphertext2	: ");
			print_hex(buf,DES_BLOCK_SIZE);
		}
		
	//decrypt
	//key setup
	des_key_setup(key2, schedule, DES_DECRYPT);

	//decrypt
	des_crypt(ct2, buf, schedule);
	printf("\nCiphertext2	: ");
	print_hex(ct2,DES_BLOCK_SIZE);
	printf("\n-decrypted to	: ");
	print_hex(buf,DES_BLOCK_SIZE);
	
	//compare
	pass = pass && !memcmp(pt2, buf, DES_BLOCK_SIZE);
	if (pass != 1)
	{
		printf("DES_DECRYPT failed\n");
		}else
		{
			printf("\nPlaintext2	: ");
			print_hex(buf,DES_BLOCK_SIZE);
		}
		

//3
	printf("\n\n3.three_key1 and plaintext1:");
	
	//encryt
	//key setup
	three_des_key_setup(three_key1, three_schedule, DES_ENCRYPT);
	printf	("\nThree_Key1	: ");
	print_hex(three_key1, DES_BLOCK_SIZE);
	//encrypt
	three_des_crypt(pt1, buf, three_schedule);
	printf("\nPlaintext1	: ");
	print_hex(pt1,DES_BLOCK_SIZE);
	printf("\n-encrypted to	: ");
	print_hex(buf,DES_BLOCK_SIZE);
	//compare
	pass = pass && !memcmp(ct3, buf, DES_BLOCK_SIZE);
	if (pass != 1)
	{
		printf("\nDES_ENCRYPT failed\n");
		}else
		{
			printf("\nCiphertext1	: ");
			print_hex(buf,DES_BLOCK_SIZE);
		}
	//decrypt
	//key setup
	three_des_key_setup(three_key1, three_schedule, DES_DECRYPT);
	//decrypt
	three_des_crypt(ct3, buf, three_schedule);
	printf("\nCiphertext1	: ");
	print_hex(ct3,DES_BLOCK_SIZE);
	printf("\n-decrypted to	: ");
	print_hex(buf,DES_BLOCK_SIZE);
	//compare
	pass = pass && !memcmp(pt1, buf, DES_BLOCK_SIZE);
	if (pass != 1)
	{
		printf("\nDES_DECRYPT failed\n");
		}else
		{
			printf("\nPlaintext1	: ");
			print_hex(buf,DES_BLOCK_SIZE);
		}
//4
	printf("\n\n4.three_key2 and plaintext3:");
	
	//encryt
	//key setup
	three_des_key_setup(three_key2, three_schedule, DES_ENCRYPT);
	printf	("\nThree_Key1	: ");
	print_hex(three_key2, DES_BLOCK_SIZE);
	//encrypt
	three_des_crypt(pt3, buf, three_schedule);
	printf("\nPlaintext3	: ");
	print_hex(pt3,DES_BLOCK_SIZE);
	printf("\n-encrypted to	: ");
	print_hex(buf,DES_BLOCK_SIZE);
	//compare
	pass = pass && !memcmp(ct4, buf, DES_BLOCK_SIZE);
	if (pass != 1)
	{
		printf("\nDES_ENCRYPT failed\n");
		}else
		{
			printf("\nCiphertext4	: ");
			print_hex(buf,DES_BLOCK_SIZE);
		}
	//decrypt
	//key setup
	three_des_key_setup(three_key2, three_schedule, DES_DECRYPT);
	//decrypt
	three_des_crypt(ct4, buf, three_schedule);
	printf("\nCiphertext4	: ");
	print_hex(ct4,DES_BLOCK_SIZE);
	printf("\n-decrypted to	: ");
	print_hex(buf,DES_BLOCK_SIZE);
	
	pass = pass && !memcmp(pt3, buf, DES_BLOCK_SIZE);
	if (pass != 1)
	{
		printf("\nDES_DECRYPT failed\n");
		}else
		{
			printf("\nPlaintext3	: ");
			print_hex(buf,DES_BLOCK_SIZE);
		}
	printf("\n\n");
	return(pass);
}

int main()
{
	printf("---DES test demo.\n");
	printf("---DES test: %s\n", des_test() ? "SUCCEEDED" : "FAILED");

	return(0);
}