import Statistics
import LexManager
import Testing

class Dialog:
    def __init__(self):
        self.statistics = Statistics.Statistics()
        self.lex = LexManager.LexManager()

    def startDialog(self):
        test = Testing.Testing()
        while True:
            self.statistics.clear()
            print("Хотите считать из файла? (y/n or q) или хотите тестирование? (t)--> ", end="")
            answer = input()
            if answer == "y":
                self.workFile()
            elif answer == "n":
                self.workConsole()
            elif answer == "q":
                print("До свидания!")
                break
            elif answer == "t":
                test.startTesting()
            else:
                print("Ещё разок...")
                self.startDialog()

    def workFile(self):
        print("Введите имя файла --> ", end="")
        fileName = input()
        f = open(fileName, "r")
        while True:
            line = f.readline().strip()
            if not line:
                break
            self.lex.testLine(line, self.statistics)
        self.statistics.printStat()
        f.close()

    def workConsole(self):
        lines = []
        while True:
            print("Хотите ввести срочку? (y/n) --> ", end="")
            answer = input()
            if answer == "y":
                print("Вводите --> ", end="")
                buf = input()
                lines.append(buf)
            elif answer == "n":
                if len(lines) == 0:
                    self.startDialog()
                else:
                    break
            else:
                print("???")
                continue
        for line in lines:
            self.lex.testLine(line, self.statistics)
        self.statistics.printStat()





