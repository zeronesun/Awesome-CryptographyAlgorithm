# Base64 encoding and decoding - 纯Python实现Base64编解码
# 实现 RCMP 1424 和 RFC 2045 标准


# Base64 character table
BASE64_TABLE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"

# Reverse lookup table for decoding
TABLE = [0] * 128
for i, c in enumerate(BASE64_TABLE):
    TABLE[ord(c)] = i


def base64_encode(str_data):
    """
    Encode binary data to Base64 string
    将二进制数据编码为Base64字符串
    """
    # 接受 str 或 bytes 输入
    if isinstance(str_data, str):
        str_bytes = str_data.encode('latin1')
    else:
        str_bytes = bytes(str_data)
    str_len = len(str_bytes)
    
    # Calculate encoded length - 计算编码后的长度
    if str_len % 3 == 0:
        encoded_len = str_len // 3 * 4
    else:
        encoded_len = (str_len // 3 + 1) * 4
    
    result = [''] * encoded_len
    
    # Encode 3 bytes at a time - 每3个字节为1组进行编码
    i = 0
    j = 0
    while j < str_len:
        b1 = str_bytes[j]
        b2 = str_bytes[j+1] if j+1 < str_len else 0
        b3 = str_bytes[j+2] if j+2 < str_len else 0
        
        result[i] = BASE64_TABLE[b1 >> 2]
        result[i+1] = BASE64_TABLE[(b1 & 0x3) << 4 | (b2 >> 4)]
        result[i+2] = BASE64_TABLE[(b2 & 0xf) << 2 | (b3 >> 6)]
        result[i+3] = BASE64_TABLE[b3 & 0x3f]
        
        j += 3
        i += 4
    
    # Add padding characters - 添加填充字符
    remainder = str_len % 3
    if remainder == 1:
        result[i-2] = '='
        result[i-1] = '='
    elif remainder == 2:
        result[i-1] = '='
    
    return ''.join(result)


def base64_decode(code):
    """
    Decode Base64 string to binary data
    将Base64字符串解码为二进制数据
    """
    code_len = len(code)
    
    # Calculate decoded length based on padding - 根据填充计算解码长度
    if '==' in code:
        decoded_len = code_len // 4 * 3 - 2
    elif '=' in code:
        decoded_len = code_len // 4 * 3 - 1
    else:
        decoded_len = code_len // 4 * 3
    
    result = bytearray(decoded_len)
    
    # Decode 4 characters at a time - 每4个字符为1组进行解码
    i = 0
    j = 0
    while i < code_len:
        val0 = TABLE[ord(code[i])] if i < code_len and code[i] != '=' else 0
        val1 = TABLE[ord(code[i+1])] if i+1 < code_len and code[i+1] != '=' else 0
        val2 = TABLE[ord(code[i+2])] if i+2 < code_len and code[i+2] != '=' else 0
        val3 = TABLE[ord(code[i+3])] if i+3 < code_len and code[i+3] != '=' else 0
        
        # Only add bytes if we have room in the result
        if j < decoded_len:
            result[j] = ((val0 << 2) | (val1 >> 4)) & 0xFF
        if j+1 < decoded_len:
            result[j+1] = ((val1 << 4) | (val2 >> 2)) & 0xFF
        if j+2 < decoded_len:
            result[j+2] = ((val2 << 6) | val3) & 0xFF
        
        j += 3
        i += 4
    
    # Convert bytes back to string (assuming latin1 for byte-for-byte mapping)
    return result.decode('latin1')


if __name__ == '__main__':
    import sys
    
    # Test with common strings - 测试常用字符串
    test_strings = [
        "HELLO",
        "HOME",
        "ABC123",
        ""
    ]
    
    for test_str in test_strings:
        encoded = base64_encode(test_str)
        decoded = base64_decode(encoded)
        print(f"Original: {test_str}")
        print(f"Encoded:  {encoded}")
        print(f"Decoded:  {decoded}")
        print(f"Match: {test_str == decoded}")
        print()
