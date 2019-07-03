import serial
import firebase_admin
import re
from firebase_admin import credentials
from firebase_admin import db

port = "/dev/ttyACM0"
serialFromArduino = serial.Serial(port,9600)
serialFromArduino.flushInput()

# FireBase Setting
# Fetch the service account key JSON file contents
cred = credentials.Certificate('/home/pi/WeatherSeeker/weatherseeker.json')

# Initialize the app with a service account, granting admin privileges
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://weatherseeker-40afb.firebaseio.com/'
})

print("start")
# Output
a=1
while True:
    try:
        print("Arduino Start")
        input = serialFromArduino.readline()
        print("Sensor Start")
        temp = re.sub('t<|<|t|>.*|\\n|\\..*','',input.decode('utf-8'))
        humi = re.sub('.*h<|h|>.*|\\n|\\..*','',input.decode('utf-8'))
        dust = re.sub('.*d<|d|>.*|\\n|\\..*','',input.decode('utf-8'))
        discom = (9/5*float(temp))-(0.55*(1-float(humi)/100)*(9/5*float(temp)-26))+32
        discom = re.sub('\\..*','',str(discom))
        ref = db.reference() #db locate
        ref.update({'Temp' : temp}) #create
        ref.update({'Dust' : dust}) #create
        ref.update({'Humidity' : humi}) #create
        ref.update({'Discom' : discom}) #create
        print(temp + "==" + humi + "==" + dust)
    except Exception as ex:
        print("error:", ex)
        
