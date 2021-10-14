import Statistics
import AppClass
import random
import time
import Dialog

class Testing:
    def __init__(self):
        self.statistics = Statistics.Statistics()
    def startTesting(self):
        print("500.000 -- %s msec" % self.workFile_test("2"))
        print("100.000 -- %s msec" % self.workFile_test("3"))
        print("50.000 -- %s msec" % self.workFile_test("4"))
        print("10.000 -- %s msec" % self.workFile_test("5"))
        print("1.000 -- %s msec" % self.workFile_test("6"))

    def genAcceptableString(self):
        return "ed2k://|server|132.67.0.0|234|/"

    def genUnacceptableString(self):
        return "ed2k://|server|300.67.0.0|234|/"

    def genUnacceptableStringBla(self):
        return "ef2k//|serer|132.67..0|2366664|/"

    def workFile_test(self, s):
        fileName = s
        f = open(fileName, "r")
        currentTime = 0
        while True:
            line = f.readline().strip()
            if not line:
                break
            start_time = time.time()
            self.testLine(line)
            currentTime += (time.time() - start_time)*1000
        f.close()
        return currentTime

    def testLine(self, line):
        appObject = AppClass.AppClass()

        guess = appObject.CheckString(line)

        if guess:
            self.statistics.orgStatistics(line)