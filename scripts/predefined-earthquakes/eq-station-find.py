from obspy.clients.fdsn import Client
from obspy import UTCDateTime
from obspy.geodetics import locations2degrees
from math import sqrt
from eqh import *
import sys

assertError(len(sys.argv) == 3 or len(sys.argv) == 4, 'Usage: python eq-station-find.py <latitude> <longitude> [host]')

host = 'IRIS'
if len(sys.argv) == 4:
    host = sys.argv[3].strip()

lat = float(sys.argv[1])
lon = float(sys.argv[2])

printStatus('Connecting to %s...' % host)
client = Client(host)

printStatus('Downloading station list. This might take a while...')
metadata = client.get_stations(level='station')

printStatus('Done, processing and outputting...')

stationList = []

i = 0
for network in metadata.networks:
    for station in network.stations:
        distance = locations2degrees(lat, lon, station.latitude, station.longitude)
        stationList += [(distance, i, network, station)]
        i += 1

stationList.sort()

for x in stationList[0:100]:
    print('%4.2f: %4s.%3s; %3.4f %3.4f %3.4f' % (x[0], x[2].code, x[3].code, x[3].latitude, x[3].longitude, x[3].elevation))
