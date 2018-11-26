import pandas as pd
import cx_Oracle as oracle


def format_dataframe(dataframe, cursor):
    head = [i[0] for i in cursor.description]
    d = {i: head[i] for i in range(len(head))}
    return dataframe.rename(columns=d)


def formatone(cursor):
    head = [i[0] for i in cursor.description]
    d = {i: head[i] for i in range(len(head))}
    df = pd.DataFrame(cursor.fetchone())
    return df.rename(columns=d)


def formatall(cursor):
    head = [i[0] for i in cursor.description]
    d = {i: head[i] for i in range(len(head))}
    df = pd.DataFrame(cursor.fetchall())
    return df.rename(columns=d)


def formatmany(cursor, n):
    head = [i[0] for i in cursor.description]
    d = {i: head[i] for i in range(len(head))}
    df = pd.DataFrame(cursor.fetchmany(n))
    return df.rename(columns=d)


def getConnection():
    return oracle.connect('system/Greenpeace1@orcl')
