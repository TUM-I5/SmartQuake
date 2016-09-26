from eqh import *
import sys

assertError(len(sys.argv) == 2, 'Usage (stdin > stdout): python eq-normalize.py <maximum>')

factor = float(sys.argv[1])

rdata = sys.stdin.readlines()

data = []
mdata = []

for x in rdata:
    y = x.split(';')
    y[1] = float(y[1])
    data += [y]
    mdata += [y[1]]

maximum = max(mdata)
norm = factor / maximum

for x in data:
    x[1] *= norm
    x[1] = '%.15f' % x[1]
    print(';'.join(x), end='')
