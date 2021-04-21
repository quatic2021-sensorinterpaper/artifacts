# artifacts

SensorConditionsTest folder contains the code for test suite extension for Java and Kotlin. They were implemented usin UIAutomator and AndroidX test library. There are two versions: for Galaxy M30 and Galaxy S10.

Scripts folder contains Python code:

runExpMultAll.py -> the Test Execution Manager

inspectResultFilesGenerateDetails.py - Reads the test reports for identifying the failures. Generates a file named configTestResult.csv with the failure occurences descriptions and the configuration associated. Uses as input the configuration set file configCombinations-v2.csv

inspectResultFileGenerateFailureDetails.py - Reads the file configTestResults.csv. Generates a file named tabulatedFailures.csv. With a failure identifier
and the executions where the failures occurred. 

itemset_dataset.ipynb - A Jupyter Notebook with code for frequent item set mining using an implementation of the Apriori algorithm.

