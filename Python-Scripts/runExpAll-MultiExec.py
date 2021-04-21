import csv
import random
from subprocess import check_output

fileName = 'configCombinations-v2.csv'
totalExecutions = 3

print(check_output("mkdir testReportsMult", shell="True").decode('cp850'))

with open(fileName) as csv_file:
    csv_reader = csv.reader(csv_file, delimiter=',')
    line_count = 0
 
    allConfig = {}

    for row in csv_reader:
        config = {'sensors':False,'location':False,'wifi':False,'mobiledata':False,'bluetooth':False,'batterysaver':False,'autorotate':False,'donotdisturb':False} 

        for column in row:
        	if (column != ''):
        		config[column] = True

        allConfig[f"c{line_count}"] = config

        keys = list(allConfig.keys())
        
        line_count += 1

    #print(allConfig)
    print(f'Processed {line_count} combinations.')

    execFile = open("executionLog.txt","a")

    currentExec = 1
    while currentExec <= totalExecutions:
        print(f"Execution:{currentExec}")
        print(check_output(f"mkdir testReportsMult/execution{currentExec}", shell="True").decode('cp850'))
        random.shuffle(keys)
        
        execFile.write(f"Execution {currentExec} configurations \n")
        str1 = '-'.join(keys)
        execFile.write(str1)
        execFile.write("\n")
        execFile.flush()

        for k in keys:
            cf = allConfig[k]	
            print(f"config: {cf}")
            command = f"./gradlew -Pandroid.testInstrumentationRunnerArguments.sensorEnabled={cf['sensors']} \
                -Pandroid.testInstrumentationRunnerArguments.locationEnabled={cf['location']} \
                -Pandroid.testInstrumentationRunnerArguments.wifiEnabled={cf['wifi']} \
                -Pandroid.testInstrumentationRunnerArguments.mobiledataEnabled={cf['mobiledata']} \
                -Pandroid.testInstrumentationRunnerArguments.bluetoothEnabled={cf['bluetooth']} \
                -Pandroid.testInstrumentationRunnerArguments.batterysaverEnabled={cf['batterysaver']} \
                -Pandroid.testInstrumentationRunnerArguments.autoRotateEnabled={cf['autorotate']} \
                -Pandroid.testInstrumentationRunnerArguments.doNotDisturbEnabled={cf['donotdisturb']} \
                connectedDebugAndroidTest"	
            
            #print(command)
            print(check_output(command, shell=True).decode('cp850'))
            print(check_output(f"mkdir testReportsMult/execution{currentExec}/report{k.replace('c','')}", shell="True").decode('cp850'))
            print(check_output(f"cp -Rf AnkiDroid/build/reports testReportsMult/execution{currentExec}/report{k.replace('c','')}", shell="True").decode('cp850'))
        
        #print(keys)
        currentExec += 1

    #print(allConfig[keys[0]])
    execFile.close()
