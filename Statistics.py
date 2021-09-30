class Statistics:
    def __init__(self):
        self.goodPorts = {}
        self.ports = set()
    def orgStatistics(self, string):
        copyString = string[15:]
        index = copyString.index("|")
        tail = copyString[index+1:]
        index = tail.index("|")
        port = tail[0:index]
        self.ports.add(port)
        #print(port)
        try:
            value = self.goodPorts[port]
            newValue = value + 1
            self.goodPorts[port] = newValue
        except KeyError:
            self.goodPorts[port] = 1
        except Exception:
            print("WTF")

    def printStat(self):
        for port in self.ports:
            print(f"{port} - {self.goodPorts[port]}")

    def clear(self):
        self.goodPorts = {}
        self.ports = set()