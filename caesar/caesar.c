// 凯撒密码实现
// 将明文字母变为它后面的三个字母，后面的循环到前面
// 公式 f(a) = (f(a) + 3) % 26

// demo:./demo-test "HOME" 3

#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>

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

void caesar_encode(char *plain, char *cipher, char key)
{
    for (int i = 0; plain[i] != '\0'; ++i)
    { /// 逐个加密
        caesar_encode_single(&plain[i], &cipher[i], key);
    }
}

void caesar_decode(char *cipher, char *plain, char key)
{
    for (int i = 0; cipher[i] != '\0'; ++i)
    { /// 逐个解密
        caesar_decode_single(&cipher[i], &plain[i], key);
    }
}

int main(int argc, char **argv)
{
    if (argc < 3)
    {
        printf("usage: %s plain key\n", argv[0]);
        return -1;
    }

    char P[128];
    char P2[128];
    char C[128];

    memset(P, 0, sizeof(P));
    memset(P2, 0, sizeof(P2));
    memset(C, 0, sizeof(C));
    strncpy(P, argv[1], sizeof(P));

    int K = atoi(argv[2]);
    caesar_encode(P, C, K);
    printf("the ciphertext is \n%s\n", C); /// 输出密文

    caesar_decode(C, P2, K);
    printf("decode ciphertext is\n%s\n", P2); /// 输出解码明文

    return 0;
}