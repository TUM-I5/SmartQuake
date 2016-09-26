from obspy.clients.fdsn import Client
from obspy import UTCDateTime
from obspy.geodetics import locations2degrees
from math import sqrt
import sys

# Help methods (TODO place them into a separate file)
def printStatus(msg):
    print(msg, file=sys.stderr)

def printError(msg):
    printStatus(msg)
    sys.exit(1)

def assertError(cond, msg):
    if not cond:
        printError(msg)

assertError(len(sys.argv) == 5 or len(sys.argv) == 4, 'Usage: python eq-station-find.py <latitude> <longitude> <altitude> [host]')

host = 'IRIS'
if len(sys.argv) == 5:
    host = sys.argv[4].strip()

lat = float(sys.argv[1])
lon = float(sys.argv[2])
alt = float(sys.argv[3])

printStatus('Connecting to %s...' % host)
client = Client(host)

printStatus('Downloading station list. This might take a while...')
metadata = client.get_stations(level='station')

printStatus('Done, processing and outputting...')

stationList = []

i = 0
for network in metadata.networks:
    for station in network.stations:
        flatDistance = locations2degrees(lat, lon, station.latitude, station.longitude)
        heightDiff = (station.elevation - alt) * .001
        distance = sqrt(flatDistance * flatDistance + heightDiff * heightDiff)
        stationList += [(distance, i, network, station)]
        i += 1

stationList.sort()

for x in stationList:
    print('%4.2f: %4s.%3s; %3.4f %3.4f %3.4f' % (x[0], x[2].code, x[3].code, x[3].latitude, x[3].longitude, x[3].elevation))
