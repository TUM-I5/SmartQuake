echo Downloading data...
python %0\..\eq-dump.py JP.ASAJ..BHZ 2011-03-11T05:47:50 2011-03-11T05:56:00 | python %0\..\eq-normalize.py 10 > tohoku-2011-03-11-hokkaido.earthquake
python %0\..\eq-dump.py JP.JHJ2..BHE 2011-03-11T05:47:50 2011-03-11T05:52:00 | python %0\..\eq-normalize.py 10 > tohoku-2011-03-11-hachijojima.earthquake
python %0\..\eq-dump.py GE.MATE..HHN 2016-08-24T01:37:25 2016-08-24T01:41:30 GFZ | python %0\..\eq-normalize.py 10 > accumoli-2016-08-24-u-basilicata.earthquake
echo Note: The next one does not seem to be accurate at all. (if non-normalized)
python %0\..\eq-dump.py C.NICH..SHE 2010-02-27T06:34:45 2010-02-27T06:39:45 | python %0\..\eq-normalize.py 10 > chile-2010-02-27-los-niches.earthquake
echo Done!
