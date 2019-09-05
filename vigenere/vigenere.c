// 维吉尼亚加密实现，基于凯撒加密

// demo: ./demo-test "A" "B"

#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

#define ALPHABETA_MIN 'A'
#define ALPHABETA_NUM 26

char table[ALPHABETA_NUM][ALPHABETA_NUM];

void caesar_encode_single(char *p, char *c, char key)
{
    if (*p >= 'a' && *p <= 'z')
    { /// 小写字母
        *c = (*p - 'a' + key) % 26 + 'a';
    }
    else if (*p >= 'A' && *p <= 'Z')
    { /// 大写字母
        *c = (*p - 'A' + key) % 26 + 'A';
    }
    else
    { /// 其它转换成空格
        *c = ' ';
    }
}

void caesar_decode_single(char *c, char *p, char key)
{
    char offset = (*c) - key;
    if (*c >= 'a' && *c <= 'z')
    { /// 小写字母
        offset = (offset >= 'a') ? offset : offset + 26;
        *p = offset;
    }
    else if (*c >= 'A' && *c <= 'Z')
    { /// 大写字母
        offset = (offset >= 'A') ? offset : offset + 26;
        *p = offset;
    }
    else
    { /// 其它转换成空格
        *p = ' ';
    }
}

int getkey(char key)
{
    if (key >= 'a' && key <= 'z')
    { /// 小写字母
        return key - 'a';
    }
    else
    {
        return key - 'A';
    }
}

void init_vigenere()
{
    for (int i = 0; i < ALPHABETA_NUM; ++i)
    {
        for (int j = 0; j < ALPHABETA_NUM; ++j)
        {
            table[i][j] = ALPHABETA_MIN + (i + j) % ALPHABETA_NUM;
        }
    }
}

void print_vigenere()
{
    for (int i = 0; i < ALPHABETA_NUM; ++i)
    {
        for (int j = 0; j < ALPHABETA_NUM; ++j)
        {
            printf("%c ", table[i][j]);
        }
        printf("\n");
    }
}

bool vigenere_encode(char *key, char *source, char *dest)
{
    char *tempSource = source;
    char *tempKey = key;
    char *tempDest = dest;

    for (char *c = source; (*c) != '\0'; c++)
    {
        caesar_encode_single(c, tempDest, getkey(*tempKey));
        tempDest++;
        if (*c == ' ')
        {
            continue;
        }
        if (!(*(++tempKey)))
        {
            tempKey = key;
        }
    }

    dest[strlen(source)] = '\0';
    return true;
}

bool vigenere_decode(char *key, char *source, char *dest)
{
    char *tempSource = source;
    char *tempKey = key;
    char *tempDest = dest;
    for (char *c = source; (*c) != '\0'; c++)
    {
        caesar_decode_single(c, tempDest, getkey(*tempKey));
        tempDest++;
        if (*c == ' ')
        {
            continue;
        }
        if (!(*(++tempKey)))
        {
            tempKey = key;
        }
    }
    dest[strlen(source)] = '\0';
    return true;
}

int main(int argc, char **argv)
{
    if (argc < 3)
    {
        printf("usage: %s plain key\n", argv[0]);
        return -1;
    }

    init_vigenere();
    print_vigenere();

    char key[256];
    char plain[256];
    char plain2[256];
    char cipher[256];

    memset(key, 0, sizeof(key));
    memset(plain, 0, sizeof(plain));
    memset(plain2, 0, sizeof(plain2));
    memset(cipher, 0, sizeof(cipher));

    strncpy(plain, argv[1], sizeof(plain));
    strncpy(key, argv[2], sizeof(key));
    printf("the plaintext is \n%s\n", plain);

    vigenere_encode(key, plain, cipher);
    printf("the ciphertext is \n%s\n", cipher); /// 输出密文

    vigenere_decode(key, cipher, plain2);
    printf("decode ciphertext is\n%s\n", plain2); /// 输出解码明文

    return 0;
}