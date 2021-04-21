import csv
import glob
from pyquery import PyQuery

#----Reading all configurations ------------------------------
inputFileName = 'configCombinations-v2.csv'

with open(inputFileName) as csv_file:
	csv_reader = csv.reader(csv_file, delimiter=',')
	line_count = 0
	allConfig = {}

	for row in csv_reader:
		config = {'sensors':False,'location':False,'wifi':False,'mobiledata':False,'bluetooth':False,'batterysaver':False,'autorotate':False,'donotdisturb':False} 
		
		for column in row:
			if (column != ''): 
				config[column] = True

		allConfig[line_count]=config
		line_count += 1

#print(allConfig)
#----Reading all configurations ------------------------------

#----Finding failures on reports -----------------------------
with open('configTestResult.csv', mode='w') as configTest_file:
	configTest_file_writer = csv.writer(configTest_file, delimiter=',', quotechar='"', quoting=csv.QUOTE_MINIMAL)


	totalReports = 256
	totalExec = 3
	
	configTest_file_header = ["EXECUTION","CONFIG_ID","SENSORS","LOCATION","WIFI","MOBILEDATA","BLUETOOTH","BATTERYSAVER","AUTOROTATE","DONOTDISTURB","FAILED TEST"]
	configTest_file_writer.writerow(configTest_file_header)

	for exec in range(1,totalExec+1):
		print(f"Execution: {exec}")

		for x in range(totalReports):
			reportFile = glob.glob(f"/.../testReportsMult/execution{exec}/report{x}/reports/androidTests/connected/flavors/debugAndroidTest/index.html")
			with open(reportFile[0]) as file_object:
				html = file_object.read()
				#print(html)
				doc = PyQuery(html)
				
				tagtext = doc("div").text()
				#chunks = tagtext.split('\n')
				chunks = tagtext.split()
				#print(chunks)
				print("TOTAL FAILURES IN TEST REPORT " + str(x) + ": " + chunks[4])

				#table = doc("tr").find("td.failures")
				tables = doc("div.tab")
				tab0 = tables.eq(0) 
				tab0text = tables.eq(0).text()
				#chunks2 = tab0text.split('\n')
				chunks2 = tab0text.split()
				
				#print(chunks2)
				
				configVals = allConfig[x].values()
			
				if (chunks2[0] == "Failed"):
					print("FAILED TESTS:")
					print(chunks2[4:])
					#Concateneting Test Class with Test Name
					list_length = len(chunks2)
					paired_list = [chunks2[i] + "." + chunks2[i+1] for i in range(4, list_length-1, 2)]
					list_length2 = len(paired_list)
					print("PAIRED LIST:")
					print(paired_list)  
					
					for y in range(list_length2):
						failedTestList = []
						failedTestList.append(paired_list[y])
						configTest_file_writer.writerow([exec]+[x]+list(configVals)+failedTestList)


			#print(chunks2)
#----Finding failures on reports -----------------------------



