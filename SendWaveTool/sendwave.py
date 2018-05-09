import sys,os
import serial
import time

filename = sys.argv[0]
dirname = os.path.dirname(filename)
abspath = os.path.abspath(dirname)
ser = serial.Serial()
ser.baudrate = 38400

#change port ID here
ser.port = 'COM3'
ser
ser.open()
if ser.is_open:
    print('serial port is open')
    file=open(abspath+'\\leadii.bin','rb')
    binData = file.read()
    file.close()
    count=0
    while count<(len(binData)-2):
        ser.write(binData[count:count+4])
        count+=4
        time.sleep(0.02)
        
else:
    print('Nothing send')

ser.close()
os.system("pause")
