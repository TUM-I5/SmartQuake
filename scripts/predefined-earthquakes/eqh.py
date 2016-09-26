import sys

# Help methods
def printStatus(msg):
    print(msg, file=sys.stderr)

def printError(msg):
    printStatus(msg)
    sys.exit(1)

def assertError(cond, msg):
    if not cond:
        printError(msg)
        