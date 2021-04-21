# artifacts

SensorConditionsTest folder contains the code for test suite extension for Java and Kotlin. They were implemented usin UIAutomator and AndroidX test library. There are two versions: for Galaxy M30 and Galaxy S10.

Scripts folder contains Python code:

runExpMultAll -> the Test Execution Manager

inspectResultFilesGenerate Details - Reads the test reports for identifying the failures. Generates a file named configTestResult.csv with Failure Descriptions and the configuration associated. Uses as input the configuration set file configCombinations-v2.csv
