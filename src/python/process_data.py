import pandas as pd
import cx_Oracle as oracle
import utils
import matplotlib.pyplot as plt
from io import StringIO

conn = utils.getConnection()
cur = conn.cursor()
cur.execute('SELECT LON,LAT,AVG(SPEED_GPS) FROM TR_GPSTRACK_0700_0710 GROUP BY LON,LAT')
rows = cur.fetchall()
cur.executemany('INSERT INTO TR_GPSTRACK_0700_0710_AVGSPEED(LON,LAT,SPEED_GPS_AVG) VALUES(:1,:2,:3)',rows)
conn.commit()

cur.execute('SELECT * FROM TR_GPSTRACK_0700_0710_AVGSPEED')
data = utils.formatall(cur)
data.to_csv('avg_speed.csv')

# cm = plt.cm.get_cmap('RdYlBu')
# sc = plt.scatter(data.LON,data.LAT,c=data.SPEED_GPS_AVG,vmin=0,vmax=120,s=2,cmap=cm)
# plt.colorbar(sc)
# plt.show()

cur.close()
conn.close()
