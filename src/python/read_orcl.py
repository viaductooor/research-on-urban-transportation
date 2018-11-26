import cx_Oracle as oracle
import pandas as pd
import utils
import datetime

tables = {int(i): 'TR_GPSTRACK_CZC_201408' + i for i in
          ('05', '06', '07', '12', '13', '14', '19', '20', '26', '27', '28')}
columns = ('ID', 'DEVICE_ID', 'VEHICLECOLOR', 'ENCRYPT',
           'DATE_GPS', 'LON', 'LAT', 'DERECTION', 'SPEED_GPS',
           'SPEED_TRCK', 'ALTITUDE', 'DATE_INSERT', 'GID10',
           'GID100', 'GID1000', 'ACCESSCODE', 'OLD_LON', 'OLD_LAT',
           'GPS_TYPE', 'ZHUJIAN')
table_1 = 'TR_GPSTRACK_0700_0710'


def time_range(d):
    t1 = datetime.datetime(year=2014, month=8, day=d, hour=7, minute=0, second=0)
    t2 = datetime.datetime(year=2014, month=8, day=d, hour=7, minute=9, second=59)
    return t1, t2

# get connection
conn = utils.getConnection()
cursor = conn.cursor()

# SELECT and insert
for key, value in tables.items():
    time = time_range(key)
    cursor.prepare('SELECT ID,LON,LAT,SPEED_GPS,STATE,DATE_GPS FROM ' + tables[key] + ' WHERE DATE_GPS BETWEEN :t1 AND :t2')
    cursor.execute(None, t1=time[0], t2=time[1])
    rows = cursor.fetchall()
    cursor.executemany('INSERT INTO ' + table_1 + '(ID,LON,LAT,SPEED_GPS,STATE,DATE_GPS) VALUES(:1,:2,:3,:4,:5,:6)',rows)

cursor.close()
conn.commit()
conn.close()
