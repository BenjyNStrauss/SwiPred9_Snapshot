'''
Created on Jun 30, 2022

@author: Benjamin Strauss
'''

class InternalError(Exception):
    message: str
    def __init__(self, message: str = None):
        self.message = message
