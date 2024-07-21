'''
Created on Jun 30, 2022

@author: Benjamin Strauss
'''

class NullPointerError(Exception):
    message: str
    def __init__(self, message: str = None):
        self.message = message

def requireNotNone(_object, message: str = None):
    if(_object is None):
        raise NullPointerError(message)
