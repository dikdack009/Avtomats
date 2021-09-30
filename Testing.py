import Statistics
import AppClass
import random
import time
import Dialog

class Testing:
    def __init__(self):
        self.statistics = Statistics.Statistics()
    def startTesting(self):

        f = open("2", "w")
        for i in range(1, 500000):
            a = random.choice([True, True, False, False, False])
            if a:
                f.write(self.genAcceptableString() + "\n")
            else:
                f.write(self.genUnacceptableString() + "\n")
        start_time_5 = time.time()
        self.workFile_test("2")
        print("500.000 -- %s msec" % ((time.time() - start_time_5)*1000))
        f.close()
        f = open("3", "w")
        for i in range(1, 100000):
            a = random.choice([True, True, False, False, False])
            if a:
                f.write(self.genAcceptableString() + "\n")
            else:
                f.write(self.genUnacceptableString() + "\n")
        start_time_1 = time.time()
        self.workFile_test("3")
        print("100.000 -- %s msec" % ((time.time() - start_time_1)*1000))
        f.close()
        f = open("4", "w")
        for i in range(1, 50000):
            a = random.choice([True, True, False, False, False])
            if a:
                f.write(self.genAcceptableString() + "\n")
            else:
                f.write(self.genUnacceptableString() + "\n")

        start_time_6 = time.time()
        self.workFile_test("4")
        print("50.000 -- %s msec" % ((time.time() - start_time_6)*1000))
        f.close()
        f = open("5", "w")
        for i in range(1, 10000):
            a = random.choice([True, True, False, False, False])
            if a:
                f.write(self.genAcceptableString() + "\n")
            else:
                f.write(self.genUnacceptableString() + "\n")
        start_time_2 = time.time()
        self.workFile_test("5")
        print("10.000 -- %s msec" % ((time.time() - start_time_2)*1000))
        f.close()
        f = open("5", "w")
        for i in range(1, 1000):
            a = random.choice([True, True, False, False, False])
            if a:
                f.write(self.genAcceptableString() + "\n")
            else:
                f.write(self.genUnacceptableString() + "\n")
        start_time_3 = time.time()
        self.workFile_test("6")
        print("1.000 -- %s msec" % ((time.time() - start_time_3)*1000))

    def genAcceptableString(self):
        return "ed2k://|server|132.67.0.0|234|/"
    def genUnacceptableString(self):
        return "ed2k://|server|300.67.0.0|234|/"

    def workFile_test(self, s):
        #print("Введите имя файла --> ", end="")
        fileName = s
        f = open(fileName, "r")
        while True:
            line = f.readline().strip()
            if not line:
                break
            self.testLine(line)
        self.statistics.printStat()
        f.close()

    def testLine(self, line):
        appObject = AppClass.AppClass()

        guess = appObject.CheckString(line)

        if guess:
            self.statistics.orgStatistics(line)