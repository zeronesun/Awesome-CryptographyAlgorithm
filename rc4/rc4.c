#include <stdio.h>
#include <memory.h>
#include "rc4.h"

int S[256];         //向量S
char T[256];        //向量T
int Key[256];       //随机生成的密钥
int KeyStream[MAX]; //密钥
char PlainText[MAX];
char CryptoText[MAX];
const char *WordList = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
void init_S()
// 初始化S;
{
    for (int i = 0; i < 256; i++)
    {
        S[i] = i;
    }
}

void init_Key()
{
    // 初始密钥
    int index;
    int keylen;
    double RAND_MAX;
    srand(time(NULL));                  //根据当前时间，作为种子
    keylen = random() / RAND_MAX * 256; //随机获取一个密钥的长度
    for (int i = 0; i < keylen; i++)
    {
        index = random() / RAND_MAX * 63; //生产密钥数组
        Key[i] = WordList[index];
    }
    int d;
    for (int i = 0; i < 256; i++)
    { //初始化T[]
        T[i] = Key[i % keylen];
    }
}

void permute_S()
{
    // 置换S;
    int temp;
    int j = 0;
    for (int i = 0; i < 256; i++)
    {
        j = (j + S[i] + T[i]) % 256;
        temp = S[i];
        S[i] = S[j];
        S[j] = temp;
    }
}

void create_key_stream(char *text, int textLength)
{
    // 生成密钥流
    int i, j;
    int temp, t, k;
    int index = 0;
    i = j = 0;
    while (textLength--)
    { //生成密钥流
        i = (i + 1) % 256;
        j = (j + S[i]) % 256;
        temp = S[i];
        S[i] = S[j];
        S[j] = temp;
        t = (S[i] + S[j]) % 256;
        KeyStream[index] = S[t];
        index++;
    }
}

void Rc4EncryptText(char *text)
{
    //加密 && 解密
    int textLength = strlen(text);
    init_S();
    init_Key();
    permute_S();
    create_key_stream(text, textLength);
    int plain_word;
    printf("============开始加密============:\n 密文：");
    for (int i = 0; i < textLength; i++)
    {
        CryptoText[i] = KeyStream[i] ^ text[i]; //加密
    }
    for (int i = 0; i < textLength; i++)
    {
        printf("%c", CryptoText[i]);
    }
    printf("\n============加密完成============\n============开始解密============\n明文：");
    for (int i = 0; i < textLength; i++)
    {
        PlainText[i] = KeyStream[i] ^ CryptoText[i]; //解密
    }
    for (int i = 0; i < textLength; i++)
    {
        printf("%c", PlainText[i]);
    }
    printf("\n============解密完成============\n");
    printf("\n");
}
