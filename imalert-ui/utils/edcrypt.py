# import base64
# from cryptography.fernet import Fernet
# from cryptography.hazmat.primitives import hashes
# from cryptography.hazmat.backends import default_backend
# from cryptography.hazmat.primitives.kdf.pbkdf2 import PBKDF2HMAC

"""
def generate_fernet_key(key):
    salt = b'salt_'
    kdf = PBKDF2HMAC(
        algorithm=hashes.SHA256(),
        length=32,
        salt=salt,
        iterations=100000,
        backend=default_backend())

    fernet_key = base64.urlsafe_b64encode(kdf.derive(key.encode('utf-8')))
    return fernet_key


def encrypt(key, message):
    fernet = Fernet(generate_fernet_key(key))
    return fernet.encrypt(message.encode()).decode("utf-8")


def decrypt(key, message):
    fernet = Fernet(generate_fernet_key(key))
    return fernet.decrypt(str.encode(message)).decode('utf-8')

"""