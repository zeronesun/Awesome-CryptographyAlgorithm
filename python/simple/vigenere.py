# Vigenere cipher implementation - 纯Python实现维吉尼亚密码
# 基于凯撒密码的多表密码
# demo: ./demo-test "A" "B"


ALPHABETA_MIN = 'A'
ALPHABETA_NUM = 26

# Vigenere table initialization
table = [[None for _ in range(ALPHABETA_NUM)] for _ in range(ALPHABETA_NUM)]


def caesar_encode_single(plain_char, key):
    """
    Encode a single character using Caesar cipher
    对单个字符进行凯撒加密
    """
    if 'a' <= plain_char <= 'z':  # Lowercase letter
        return chr((ord(plain_char) - ord('a') + key) % 26 + ord('a'))
    elif 'A' <= plain_char <= 'Z':  # Uppercase letter
        return chr((ord(plain_char) - ord('A') + key) % 26 + ord('A'))
    else:
        return ' '  # Non-letter characters become spaces


def caesar_decode_single(cipher_char, key):
    """
    Decode a single character using Caesar cipher
    对单个字符进行凯撒解密
    """
    if 'a' <= cipher_char <= 'z':  # Lowercase letter
        offset = chr(ord(cipher_char) - key)
        if 'a' <= offset <= 'z':
            return offset
        else:
            return chr(ord(offset) + 26)
    elif 'A' <= cipher_char <= 'Z':  # Uppercase letter
        offset = chr(ord(cipher_char) - key)
        if 'A' <= offset <= 'Z':
            return offset
        else:
            return chr(ord(offset) + 26)
    else:
        return ' '  # Non-letter characters become spaces


def getkey(key_char):
    """
    Get the shift value for a key character
    获取密钥字符的移位值
    """
    if 'a' <= key_char <= 'z':
        return ord(key_char) - ord('a')
    else:
        return ord(key_char) - ord('A')


def init_vigenere():
    """
    Initialize Vigenere table
    初始化维吉尼亚表
    """
    for i in range(ALPHABETA_NUM):
        for j in range(ALPHABETA_NUM):
            table[i][j] = chr(ord(ALPHABETA_MIN) + (i + j) % ALPHABETA_NUM)


def print_vigenere():
    """
    Print Vigenere table
    打印维吉尼亚表
    """
    for i in range(ALPHABETA_NUM):
        row = ' '.join(table[i][j] for j in range(ALPHABETA_NUM))
        print(row)


def vigenere_encode(key, source):
    """
    Encode a string using Vigenere cipher
    对字符串进行维吉尼亚加密
    """
    result = [''] * len(source)
    temp_key = key
    key_index = 0
    
    for i, char in enumerate(source):
        shift = getkey(temp_key[key_index])
        result[i] = caesar_encode_single(char, shift)
        
        # Move to next key character only if char is not space
        if char != ' ':
            key_index = (key_index + 1) % len(temp_key)
    
    return ''.join(result)


def vigenere_decode(key, source):
    """
    Decode a string using Vigenere cipher
    对字符串进行维吉尼亚解密
    """
    result = [''] * len(source)
    temp_key = key
    key_index = 0
    
    for i, char in enumerate(source):
        shift = getkey(temp_key[key_index])
        result[i] = caesar_decode_single(char, shift)
        
        # Move to next key character only if char is not space
        if char != ' ':
            key_index = (key_index + 1) % len(temp_key)
    
    return ''.join(result)


if __name__ == '__main__':
    import sys
    
    if len(sys.argv) < 3:
        print(f"usage: {sys.argv[0]} plain key")
        sys.exit(-1)
    
    # Initialize and print Vigenere table
    init_vigenere()
    print_vigenere()
    print()
    
    plain_text = sys.argv[1]
    key = sys.argv[2]
    
    print(f"the plaintext is\n{plain_text}")
    
    cipher_text = vigenere_encode(key, plain_text)
    print(f"the ciphertext is\n{cipher_text}")
    
    decoded_text = vigenere_decode(key, cipher_text)
    print(f"decode ciphertext is\n{decoded_text}")
