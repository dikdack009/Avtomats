import Statistics
import AppClass
import random
import time
import RandomLineGenerator

class Testing:
    def __init__(self):
        self.statistics = Statistics.Statistics()
        self.gen = RandomLineGenerator.RandomLineGenerator()
    def startTesting(self):
        f = open("2", "w")
        f.seek(0)
        for i in range(500_000):
            ch = random.choice([True, True, True, False, False])
            if ch:
                f.write(self.gen.genAcceptableLine() + "\n")
            else:
                f.write(self.gen.genUnacceptableLine()+ "\n")
        print("500.000 -- %s msec" % self.workFile_test("2"))
        f.close()
        f = open("3", "w")
        f.seek(0)
        for i in range(100_000):
            ch = random.choice([True, True, True, False, False])
            if ch:
                f.write(self.gen.genAcceptableLine()+ "\n")
            else:
                f.write(self.gen.genUnacceptableLine()+ "\n")
        print("100.000 -- %s msec" % self.workFile_test("3"))
        f.close()
        f = open("4", "w")
        f.seek(0)
        for i in range(50_000):
            ch = random.choice([True, True, True, False, False])
            if ch:
                f.write(self.gen.genAcceptableLine()+ "\n")
            else:
                f.write(self.gen.genUnacceptableLine()+ "\n")
        print("50.000 -- %s msec" % self.workFile_test("4"))
        f.close()
        f = open("5", "w")
        f.seek(0)
        for i in range(10_000):
            ch = random.choice([True, True, True, False, False])
            if ch:
                f.write(self.gen.genAcceptableLine()+ "\n")
            else:
                f.write(self.gen.genUnacceptableLine()+ "\n")
        print("10.000 -- %s msec" % self.workFile_test("5"))
        f.close()
        f = open("6", "w")
        f.seek(0)
        for i in range(1_000):
            ch = random.choice([True, True, True, False, False])
            if ch:
                f.write(self.gen.genAcceptableLine()+ "\n")
            else:
                f.write(self.gen.genUnacceptableLine()+ "\n")
        print("1.000 -- %s msec" % self.workFile_test("6"))
        f.close()

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