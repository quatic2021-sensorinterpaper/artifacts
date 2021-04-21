import csv
import glob
from pyquery import PyQuery

#----Reading all lines from result------------------------------
inputFileName = 'configTestResult.csv'

with open(inputFileName) as csv_file:
	csv_reader = csv.reader(csv_file, delimiter=',')
	idCode = 0
	failureCodes = {}

	next(csv_reader)

	for row in csv_reader:
		if (row[10] not in failureCodes.keys()):
			idCode += 1
		
			failureId = 'F' + str(idCode)
			failureCodes[row[10]] = failureId 	

with open(inputFileName) as csv_file:
	csv_reader = csv.reader(csv_file, delimiter=',')
	line_count = 0
	allLines = {}

	next(csv_reader)

	for row in csv_reader:
		line = {'EXECUTION':-1,'CONFIG_ID':-1,'FAILURE_ID':''} 
		
		line['EXECUTION'] = row[0]
		line['CONFIG_ID'] = row[1]
		line['FAILURE_ID'] = failureCodes[row[10]]	

		allLines[line_count] = line
		line_count += 1

#print(allLines)
#----Reading all lines from result------------------------------

#----Tabulating failures ---------------------------------------
with open('tabulatedFailures.csv', mode='w') as tabFailures_file:
	tabFailures_file_writer = csv.writer(tabFailures_file, delimiter=',', quotechar='"', quoting=csv.QUOTE_MINIMAL)
	tabFailures_file_header = ["CONFIG_ID","FAILURE ID","EXEC1","EXEC2","EXEC3"]	
	tabFailures_file_writer.writerow(tabFailures_file_header)
	allFailures = {}
	failureNames = {}
	execInf = {}

	for x in range(line_count):
		l = allLines[x]
		failureName = 'C' + l['CONFIG_ID'] + '-' + l['FAILURE_ID']
		allFailures[failureName] = [0,0,0]

		failureNames[x] = failureName
		execInf[x] = l['EXECUTION']


	for k in failureNames.keys():

		failureName = failureNames.get(k)

		if (execInf.get(k) == '1'):
			y = allFailures.get(failureName)
			y[0] = 1
			allFailures[failureName] = y
		elif (execInf.get(k) == '2'):
			y = allFailures.get(failureName)
			y[1] = 1
			allFailures[failureName] = y
		else:
			y = allFailures.get(failureName)
			y[2] = 1
			allFailures[failureName] = y

	#for z in allFailures.keys():
	# 	failureNameList = []
	# 	failureNameList.append(z)
	# 	tabFailures_file_writer.writerow(failureNameList+allFailures[z])

	for z in allFailures.keys():
		configIdList = []
		failureIdList = []
		posHiphen = z.index('-')
		configIdList.append(z[0:posHiphen])
		failureIdList.append(z[posHiphen+1:])
		tabFailures_file_writer.writerow(configIdList+failureIdList+allFailures[z])
			
	print(allFailures)
		

#----Tabulating failures ---------------------------------------



