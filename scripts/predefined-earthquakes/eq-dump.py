# Script by David Schneller.
# Written for extracting earthquake data into a time-acceleration graph-like. Or so. But so that it can be read by the program.

from obspy.clients.fdsn import Client
from obspy import UTCDateTime
from csv import writer
from eqh import *
import sys

# Basic outline:
# * we read in our args
# * we query metadata (maily gain and input unit, for output unit we assume digital ticks)
# * we query the wave data
# * we transform the wave data (derive, if necessary, and scale it)
# * we finally output the data

# Guard for wrong option count.
assertError(len(sys.argv) == 5 or len(sys.argv) == 4, 'Usage: python eq-dump.py <network>.<station>.<location>.<channel> <start time> <end time> [host]')

locationString = sys.argv[1].split('.')
host = 'IRIS'
if len(sys.argv) == 5:
    host = sys.argv[4].strip()

# Strip our location and so on out of the string we got.
network = locationString[0].strip()
station = locationString[1].strip()
location = locationString[2].strip()
channel = locationString[3].strip()

startTime = UTCDateTime(sys.argv[2])
endTime = UTCDateTime(sys.argv[3])

# Now, let's connect to our host.
printStatus('Establishing connection to %s...' % host)
client = Client(host)

# We then get the metadata.
printStatus('Getting metadata from %s.%s.%s.%s...' % (network, station, location, channel))
metadata = client.get_stations(network=network, station=station, location=location, channel=channel, starttime=startTime, endtime=endTime, level='channel')

# In the XML, we need to navigate through network and station to the channels.
assertError(len(metadata.networks) == 1, 'Network %s is not unique. Please check if it exists or if there are multiple instances.' % network)

networkdata = metadata.networks[0]

assertError(len(networkdata.stations) == 1, 'Station %s is not unique. Please check if it exists or if there are multiple instances.' % station)

stationdata = networkdata.stations[0]

assertError(len(stationdata.channels) == 1, 'Channel %s is not unique. Please check if it exists or if there are multiple instances.' % station)

channeldata = stationdata.channels[0]

response = channeldata.response

# Retrieving important variables.

sensitivity = response.instrument_sensitivity

gain = sensitivity.value

unit = sensitivity.input_units

# Now, we may download our data.
printStatus('Got all necessary metadata: We got data in [%s], the gain is %f' % (unit, gain))
printStatus('Downloading data...')
dataResponse = client.get_waveforms(network, station, location, channel, startTime, endTime)

# This might have taken a while, depending on the size.
printStatus('Fetched data, processing...')

data = []

# Deriving if needed (by time).
if unit == 'M':
    printStatus('Deriving data twice...')
    dataResponse = dataResponse.differentiate().differentiate()

if unit == 'M/S':
    printStatus('Deriving data once...')
    dataResponse = dataResponse.differentiate()

# Then, we transform our data for output.
printStatus('Transforming data...')
for x in dataResponse.traces:
    baseTime = x.stats.starttime
    # The timestamp increments linearly, the actual value is divided through our gain.
    data += [((baseTime + i / x.stats.sampling_rate).timestamp * 1000000000, j / gain) for i, j in zip(range(len(x.data)), x.data)]

# Then, we sort for nicer later data processing.
printStatus('Sorting data...')
data.sort()

# After that, we base the timestamps to zero.
printStatus('Adjusting timestamps...')
if len(data) > 0:
    baseTimestamp = data[0][0]
    data = [(i - baseTimestamp, j) for i, j in data]

# Finally, we print to stdout.
printStatus('Outputting...')
for x in data:
    print("%d;%.15f;0;0;9.81" % (x[0], x[1]))

# That's it!
printStatus('Done!')
